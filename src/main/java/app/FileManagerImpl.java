package app;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;
import util.FileUtil;

/**
 * FileOperationsManagerImpl implements FileOperations interface. It provides
 * core file operations for creating folders, uploading files, deleting
 * files/folders, and listing files inside the local storage directory.
 */
public class FileManagerImpl implements FileManager {

	private final LocalFileStorageApp app;

	public FileManagerImpl(LocalFileStorageApp app) {
		this.app = app;
		initializeStorageDirectory();
	}

	/**
	 * Creates the base local storage directory if it does not exist.
	 */
	private void initializeStorageDirectory() {
		File dir = new File(FileUtil.STORAGE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	/**
	 * Upload a file to the currently selected folder in the UI.
	 */
	@Override
	public void uploadFileToSelectedFolder() {
		String folderPath = getSelectedFilePath();
		if (folderPath == null) {
			JOptionPane.showMessageDialog(app, "No folder selected.");
			return;
		}
		File folder = new File(folderPath);
		if (!folder.exists() || !folder.isDirectory()) {
			JOptionPane.showMessageDialog(app, "Selected item is not a folder.");
			return;
		}
		uploadFileToSelectedFolder(folder.getAbsolutePath());
	}

	/**
	 * Upload a file to a specified folder path.
	 * 
	 * @param folderPath the path to upload the file into.
	 */
	@Override
	public void uploadFileToSelectedFolder(String folderPath) {
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showOpenDialog(app);
		if (result == JFileChooser.APPROVE_OPTION) {
			File source = chooser.getSelectedFile();
			Path targetPath = Paths.get(folderPath, source.getName());
			try {
				Files.copy(source.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
				JOptionPane.showMessageDialog(app, "File uploaded to folder.");
				listFiles();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(app, "Upload failed: " + e.getMessage());
			}
		}
	}

	/**
	 * Create a new folder inside the local storage directory.
	 * 
	 * @param folderName the name of the folder to create
	 */
	@Override
	public void createFolder(String folderName) {
		if (folderName == null || folderName.isEmpty()) {
			JOptionPane.showMessageDialog(app, "Please enter a folder name.");
			return;
		}
        String selectedFilePath = app.getSelectedFilePath();
		if(selectedFilePath != null &&!FileUtil.isDirectory(selectedFilePath)){
		   JOptionPane.showMessageDialog(app, "Failed to create folder. Select a directory");
           return;
		}
        String newFolderFilePath = FileUtil.createNewFolderFilePath(selectedFilePath, folderName);
		File folder = new File(newFolderFilePath);
		if (!folder.exists()) {
			if (folder.mkdirs()) {
				JOptionPane.showMessageDialog(app, "Folder created.");
				listFiles();
			} else {
				JOptionPane.showMessageDialog(app, "Failed to create folder.");
			}
		} else {
			JOptionPane.showMessageDialog(app, "Folder already exists.");
		}
	}

	/**
	 * Delete a selected file or folder using its absolute path.
	 * 
	 * @param filePath the full path of the file/folder to delete
	 */
	@Override
	public void deleteSelectedFile(String filePath) {
		if (filePath == null || filePath.isEmpty()) {
			JOptionPane.showMessageDialog(app, "Please select a file.");
			return;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			JOptionPane.showMessageDialog(app, "File not found.");
			return;
		}

		if(FileUtil.isRootDirectory(file)){
			JOptionPane.showMessageDialog(app, "Root file directory cannot be deleted");
			return;
		}

		try {
			if (file.isDirectory()) {
				Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} else {
				file.delete();
			}
			JOptionPane.showMessageDialog(app, "Deleted successfully.");
			listFiles();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(app, "Delete failed: " + e.getMessage());
		}
	}

	/**
	 * Refresh and list all files/folders inside the local storage directory.
	 */
	@Override
	public void listFiles() {
		File dir = new File(FileUtil.STORAGE_DIR);
		List<File> files = new ArrayList<>();
		if (dir.exists()) {
			File[] fileArray = dir.listFiles();
			if (fileArray != null) {
				for (File file : fileArray) {
					files.add(file);
				}
			}
		}
		app.updateFileTable(files);
		app.updateFolderTree();
	}

	/**
	 * Get the absolute path of the file selected in the UI table.
	 * 
	 * @return selected file's absolute path or null if nothing is selected
	 */
	@Override
	public String getSelectedFilePath() {
		return app.getSelectedFilePath();
	}
}
