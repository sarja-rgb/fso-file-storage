package listeners;

import storage.FileObject;
import util.FileEventExceptions;

/**
 * FileEventListener defines a contract for handling file-related events,
 * such as saving, updating, and deleting metadata or content associated
 * with files in a storage or metadata tracking system.
 */
public interface FileEventListener {

    /**
     * Invoked when a new file is added and should be persisted or tracked.
     *
     * @param fileObject the file object to save
     * @throws FileEventExceptions if an error occurs during the save operation
     */
    public void onSave(FileObject fileObject) throws FileEventExceptions;

    /**
     * Invoked when an existing file's metadata or content is updated.
     *
     * @param fileObject the file object with updated information
     * @throws FileEventExceptions if the file does not exist or update fails
     */
    public void onUpdate(FileObject fileObject) throws FileEventExceptions;

    /**
     * Invoked when a file is deleted and its metadata should be removed.
     *
     * @param fileObject the file object to delete
     * @throws FileEventExceptions if deletion fails or file is not found
     */
    public void onDelete(FileObject fileObject) throws FileEventExceptions;
}
