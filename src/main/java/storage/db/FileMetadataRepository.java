package storage.db;

import java.util.List;

import storage.FileObject;

/**
 * FileMetadataRepository provides methods to manage file metadata
 * in a persistent storage (e.g., SQL database).
 */
public interface FileMetadataRepository {
    /**
     * Saves a new file metadata entry or updates an existing one.
     *
     * @param file the FileObject to save or update
     */
    public void saveOrUpdate(FileObject file);

    /**
     * Save list of file object metadata entries or update an existing files.
     *
     * @param files the list of File objects to save or update
     */
    public void saveOrUpdateFiles(List<FileObject> files);

    /**
     * Finds a file metadata entry by its name.
     *
     * @param name the name of the file
     * @return the matching FileObject, or null if not found
     */
    public FileObject findByName(String name);

    /**
     * Deletes a file metadata entry by its name.
     *
     * @param name the name of the file to delete
     */
    public void delete(String name);

    /**
     * Retrieves all file metadata entries.
     *
     * @return a list of all FileObjects
     */
    public List<FileObject> findAll();

    /**
     * Checks if a file metadata entry exists by name.
     *
     * @param name the name of the file
     * @return true if exists, false otherwise
     */
    public boolean exists(String name);
}
