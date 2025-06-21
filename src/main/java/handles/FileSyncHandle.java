package handles;

import java.util.List;

import storage.FileObject;

/**
 * FileSyncHandle defines the contract for synchronizing files between
 * local and remote storage systems (e.g., local disk and cloud storage).
 */
public interface FileSyncHandle {
     /**
     * Synchronizes files between local and remote storage.
     *
     * @param localFiles  list of files available in the local storage
     * @param remoteFiles list of files available in the remote/cloud storage
     */
    public void syncFiles( List<FileObject> localFiles, List<FileObject> remoteFiles);

     /**
     * Resolves a conflict between two versions of a file using a selected strategy.
     *
     * @param localFile  the local file version
     * @param remoteFile the remote file version
     * @return the resolved file to keep
     */
     public FileObject resolveConflict(FileObject localFile, FileObject remoteFile);

    /**
     * Returns a list of files that had conflicts during the last sync.
     *
     * @return list of conflicted FileObject pairs
     */
    public List<FileObject> getConflictedFiles();
}
