package handles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import storage.FileObject;
import storage.FileStoreException;
import storage.FileStoreOperations;
import storage.db.FileMetadataRepository;

public class S3LocalFileSyncHandle implements FileSyncHandle {
    private final FileMetadataRepository fileMetadataRepository;
    private final FileStoreOperations fileStoreOperations;
    private final List<FileObject> unresolvedFiles;
    private final List<FileObject> conflictedFiles;

    public S3LocalFileSyncHandle(FileMetadataRepository fileMetadataRepository,
            FileStoreOperations fileStoreOperations) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.fileStoreOperations = fileStoreOperations;
        unresolvedFiles = new ArrayList<>();
        conflictedFiles = new ArrayList<>();
    }


      /**
     * Synchronizes a list of file objects by comparing them with the metadata tracker.
     * If the file exists and differs, resolves conflicts. Otherwise, adds new file entry.
     */
    @Override
    public void syncFiles(List<FileObject> fileObjects) {
        conflictedFiles.clear();
        unresolvedFiles.clear();

        for (FileObject remote : fileObjects) {
            FileObject local = fileMetadataRepository.findByName(remote.getFileName());

            if (local == null) {
                unresolvedFiles.add(remote); // could be optionally inserted instead
            } else if (isConflict(local, remote)) {
                FileObject resolved = resolveConflict(local, remote);
                if (resolved != null) {
                    fileMetadataRepository.saveOrUpdate(resolved);
                    conflictedFiles.add(remote);
                } else {
                    unresolvedFiles.add(remote);
                }
            }
        }

        if(!unresolvedFiles.isEmpty()){
          System.out.println("Save and update unresolve files, count : "+unresolvedFiles.size());
          fileMetadataRepository.saveOrUpdateFiles(fileObjects);
          unresolvedFiles.clear();
        }
    }

  
    /**
     * Conflict resolution strategy: keeps the most recently modified file.
     */
    @Override
    public FileObject resolveConflict(FileObject localFile, FileObject remoteFile) {
       // Strategy: keep the newer one
    return remoteFile.getLastModifiedDate().after(localFile.getLastModifiedDate())
            ? remoteFile
            : localFile;
    }

    @Override
    public List<FileObject> getConflictedFiles() {
         return conflictedFiles;
    }   

     /**
     * Determines if there is a conflict between two file metadata versions.
     * Conflict is detected by checksum or modified date mismatch.
     */
    private boolean isConflict(FileObject localFileObject, FileObject remoteFileObject) {
        System.out.println("\n-----------------------");
        System.out.println("localFileObject.checkSum "+localFileObject.getChecksum());
        System.out.println("localFileObject.getLastModifiedDate "+localFileObject.getLastModifiedDate());

        System.out.println("remoteFileObject.checkSum "+remoteFileObject.getChecksum());
        System.out.println("remoteFileObject.getLastModifiedDate "+remoteFileObject.getLastModifiedDate());
        System.out.println("-------------------------\n");

        // Conflict if checksums differ or last modified timestamps disagree
    return !Objects.equals(localFileObject.getChecksum(), remoteFileObject.getChecksum())
        || !Objects.equals(localFileObject.getLastModifiedDate(), remoteFileObject.getLastModifiedDate());
    }


     /**
     * Returns a list of files that are not resolved.
     * Unresolved files are:
     * - Present in the S3 cloud but missing or mismatched in the local file metadata repository.
     * - Present in the local repository but missing in S3.
     *
     * This method should be called *after* syncFiles(List<FileObject>) is executed.
     *
     * @return list of unresolved FileObject instances
     * @throws  FileStoreException
     */
     @Override
     public List<FileObject> unResolveFiles() throws FileStoreException{
        List<FileObject> unresolved = new ArrayList<>();
        // Get all S3 files that were last synced
        List<FileObject> cloudFiles = fileStoreOperations.loadAll();

        // 1. Find files that are in cloud but missing or mismatched locally
        for (FileObject remote : cloudFiles) {
            FileObject local = fileMetadataRepository.findByName(remote.getFileName());

            if (local == null || isConflict(local, remote)) {
                unresolved.add(remote);
            }
        }

        // 2. Find files that are in local metadata but not in cloud
        // List<FileObject> allLocalFiles = fileMetadataRepository.findAll();
        // for (FileObject local : allLocalFiles) {
        //     boolean existsInCloud = cloudFiles.stream()
        //             .anyMatch(cloud -> cloud.getFileName().equals(local.getFileName()));

        //     if (!existsInCloud) {
        //         unresolved.add(local);
        //     }
        // }

        return unresolved;
     }
}
