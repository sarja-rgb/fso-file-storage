package handles;

import java.util.List;

import storage.FileObject;
import storage.FileStoreException;
import storage.FileStoreOperations;

/**
 * FileSyncHandle defines the contract for synchronizing files between
 * file tracker and remote storage systems (e.g., local disk and cloud storage).
 */
public interface FileSyncHandle {
     /**
     * Synchronizes files between cloud file storage and file tracker
     *
     * @param fileObjects  list of files available to synchronous
     */
    public void syncFiles( List<FileObject> fileObjects);

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

    /**
     * Returns a list of files that are not resolved 
     * @return
     * @throws  FileStoreOperations
     */
    public List<FileObject> unResolveFiles() throws FileStoreException;
}
