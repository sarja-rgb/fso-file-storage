package storage;

import java.io.File;
import java.util.List;

/**
 * Base File Storage Operations interface.
 * 
 * This interface defines the contract for saving, deleting, and loading
 * files from a file storage system, whether local or cloud-based (e.g., AWS S3).
 */
public interface FileStoreOperations {

    /**
     * Saves a single file to the storage system.
     *
     * @param file the file to be saved
     * @throws FileStoreException if the save operation fails
     */
    public void save(File file) throws FileStoreException;

    /**
     * Saves multiple files to the storage system.
     *
     * @param files the list of files to be saved
     * @throws FileStoreException if any of the file save operations fail
     */
    public void saveAll(List<File> files) throws FileStoreException;

    /**
     * Deletes a file from the storage system.
     *
     * @param fileObject the file object representing the file to be deleted
     * @throws FileStoreException if the delete operation fails
     */
    public void delete(FileObject fileObject) throws FileStoreException;

    /**
     * Loads all files currently stored in the storage system.
     *
     * @return list of all file objects available in the storage
     * @throws FileStoreException if the load operation fails
     */
    public List<FileObject> loadAll() throws FileStoreException;
}
