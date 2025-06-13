package app;
/**
 * FileManager is an interface that defines core file operations to be
 * implemented for managing local file and folder storage.
 */
public interface FileManager {

	/**
	 * Creates a new folder with the given name in the storage directory.
	 *
	 * @param folderName the name of the new folder
	 */
	public void createFolder(String folderName);

	/**
	 * Uploads a file into the currently selected folder in the UI.
	 */
	public void uploadFileToSelectedFolder();

	/**
	 * Uploads a file to a specific folder path provided.
	 *
	 * @param folderName the path to upload the file into
	 */
	public void uploadFileToSelectedFolder(String folderName);

	/**
	 * Deletes the file or folder specified by its full path.
	 *
	 * @param filePath the absolute path to the file/folder to delete
	 */
	public void deleteSelectedFile(String filePath);

	/**
	 * Lists all files and folders in the local storage directory and updates the UI
	 * table view.
	 */
	public void listFiles();

	/**
	 * Retrieves the absolute path of the selected file or folder from the UI table.
	 *
	 * @return the path as a string, or null if nothing is selected
	 */
	public String getSelectedFilePath();
}
