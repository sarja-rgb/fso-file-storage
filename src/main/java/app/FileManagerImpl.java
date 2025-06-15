// package app;
// import java.io.File;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

// import javax.swing.JFileChooser;

// import listeners.FileEventListener;
// import util.FileUtil;

// /**
//  * FileOperationsManagerImpl implements FileOperations interface. It provides
//  * core file operations for creating folders, uploading files, deleting
//  * files/folders, and listing files inside the local storage directory.
//  */
// public class FileManagerImpl implements FileManager {

// 	private final BaseFileStorageUI app;
// 	private final List<FileEventListener> fileEventListeners;

// 	public FileManagerImpl(BaseFileStorageUI app) {
// 		this.app = app;
// 		initializeStorageDirectory();
// 		fileEventListeners = new ArrayList<>();
// 	}

// 	/**
// 	 * Creates the base local storage directory if it does not exist.
// 	 */
// 	private void initializeStorageDirectory() {
// 		File dir = new File(FileUtil.STORAGE_DIR);
// 		if (!dir.exists()) {
// 			dir.mkdirs();
// 		}
// 	}

// 	/**
// 	 * Upload a file to the currently selected folder in the UI.
// 	 */
// 	@Override
// 	public void uploadFileToSelectedFolder() {
// 		String folderPath = getSelectedFilePath();
// 		if (folderPath == null) {
// 			app.showAlertMessage("No folder selected.");
// 			return;
// 		}
// 		File folder = new File(folderPath);
// 		if (!folder.exists() || !folder.isDirectory()) {
// 			app.showAlertMessage("Selected item is not a folder.");
// 			return;
// 		}
// 		uploadFileToSelectedFolder(folder.getAbsolutePath());
// 	}

// 	/**
// 	 * Upload a file to a specified folder path.
// 	 * 
// 	 * @param folderPath the path to upload the file into.
// 	 */
// 	@Override
// 	public void uploadFileToSelectedFolder(String folderPath) {
// 		JFileChooser chooser = new JFileChooser();
// 		int result = chooser.showOpenDialog(app.getComponent());
// 		if (result == JFileChooser.APPROVE_OPTION) {
// 			File source = chooser.getSelectedFile();
// 			Path targetPath = Paths.get(folderPath, source.getName());
// 			try {
// 				Files.copy(source.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
// 				app.showAlertMessage("File uploaded to folder.");
// 				fileEventListeners.stream().forEach(event->event.onUploadFile(targetPath.toFile()));
// 				listFiles();
// 			} catch (IOException e) {
// 				app.showAlertMessage("Upload failed: " + e.getMessage());
// 			}
// 		}
// 	}

// 	/**
// 	 * Create a new folder inside the local storage directory.
// 	 * 
// 	 * @param folderName the name of the folder to create
// 	 */
// 	@Override
// 	public void createFolder(String folderName) {
// 		if (folderName == null || folderName.isEmpty()) {
// 			app.showAlertMessage("Please enter a folder name.");
// 			return;
// 		}
//         String selectedFilePath = app.getSelectedFilePath();
// 		if(selectedFilePath != null &&!FileUtil.isDirectory(selectedFilePath)){
// 		   app.showAlertMessage("Failed to create folder. Select a directory");
//            return;
// 		}
//         String newFolderFilePath = FileUtil.createNewFolderFilePath(selectedFilePath, folderName);
// 		File folder = new File(newFolderFilePath);
// 		if (!folder.exists()) {
// 			if (folder.mkdirs()) {
// 				app.showAlertMessage( "Folder created.");
// 				fileEventListeners.stream().forEach(event->event.onCreateFolder(folder));
// 				listFiles();
// 			} else {
// 				app.showAlertMessage("Failed to create folder.");
// 			}
// 		} else {
// 			app.showAlertMessage("Folder already exists.");
// 		}
// 	}

// 	/**
// 	 * Delete a selected file or folder using its absolute path.
// 	 * 
// 	 * @param filePath the full path of the file/folder to delete
// 	 */
// 	@Override
// 	public void deleteSelectedFile(String filePath) {
// 		if (filePath == null || filePath.isEmpty()) {
// 			app.showAlertMessage("Please select a file.");
// 			return;
// 		}
// 		File file = new File(filePath);
// 		if (!file.exists()) {
// 			app.showAlertMessage("File not found.");
// 			return;
// 		}

// 		if(FileUtil.isRootDirectory(file)){
// 			app.showAlertMessage("Root file directory cannot be deleted");
// 			return;
// 		}

// 		try {
// 			FileUtil.deleteFolderDirectory(file);
// 			app.showAlertMessage( "Deleted successfully.");
// 			fileEventListeners.stream().forEach(event->event.onDeleteFile(file));
// 			listFiles();
// 		} catch (IOException e) {
// 			app.showAlertMessage( "Delete failed: " + e.getMessage());
// 		}
// 	}

// 	/**
// 	 * Refresh and list all files/folders inside the local storage directory.
// 	 */
// 	@Override
// 	public void listFiles() {
// 		File dir = new File(FileUtil.STORAGE_DIR);
// 		List<File> files = new ArrayList<>();
// 		if (dir.exists()) {
// 			File[] fileArray = dir.listFiles();
// 			if (fileArray != null) {
// 				files = Arrays.asList(fileArray);
// 			}
// 		}
// 		files.stream().t
// 		app.updateFileTable(files);
// 		app.updateFolderTree();
// 	}

// 	/**
// 	 * Get the absolute path of the file selected in the UI table.
// 	 * 
// 	 * @return selected file's absolute path or null if nothing is selected
// 	 */
// 	@Override
// 	public String getSelectedFilePath() {
// 		return app.getSelectedFilePath();
// 	}

// 	/**
// 	 * Add FileEventListener to observe and listen to file operations
// 	 * @param eventListener
// 	 */
// 	public void addFileEventListener(FileEventListener eventListener){
//         this.fileEventListeners.add(eventListener);
// 	}
// }
