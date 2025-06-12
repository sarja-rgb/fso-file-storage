package listeners;

import java.io.File;
/**
 * Base Event Listener for file operations.
 * 
 * This interface provides callback methods that can be implemented by 
 * any class that wants to respond to events related to file system actions 
 * such as creating folders, deleting files, uploading, etc.
 */
public interface FileEventListener {

    /**
     * This method is triggered when a new folder is created.
     *
     * @param fileFolder The File object representing the newly created folder.
     */
    void onCreateFolder(File fileFolder);

    /**
     * This method is triggered when a folder is deleted.
     *
     * @param fileFolder The File object representing the folder that was deleted.
     */
    void onDeleteFolder(File fileFolder);

    /**
     * This method is triggered when a file is successfully uploaded.
     *
     * @param fileTarget The File object representing the uploaded file.
     */
    void onUploadFile(File fileTarget);

    /**
     * This method is triggered when a file is deleted.
     *
     * @param fileTarget The File object representing the file that was deleted.
     */
    void onDeleteFile(File fileTarget);
}
