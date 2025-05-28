package app;
import javax.swing.*;

/**
 * FileMenuBar defines the main menu bar with file actions, which are connected
 * to the FileManager.
 */
public class FileMenuBar {
	private final JMenuBar menuBar;

	public FileMenuBar(FileManager fileManager) {
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem uploadToFolderItem = new JMenuItem("Upload File to Selected Folder");
		JMenuItem createFolderItem = new JMenuItem("Create New Folder");
		JMenuItem deleteFolderItem = new JMenuItem("Delete Selected Folder");
		JMenuItem refreshItem = new JMenuItem("Refresh");
		uploadToFolderItem.addActionListener(e -> fileManager.uploadFileToSelectedFolder());

		/**
		 * Create folder
		 */
		createFolderItem.addActionListener(e -> {
			JTextField input = new JTextField();
			int option = JOptionPane.showConfirmDialog(null, input, "Enter folder name:", JOptionPane.OK_CANCEL_OPTION);
			if (option == JOptionPane.OK_OPTION) {
				fileManager.createFolder(input.getText().trim());
			}
		});

		/**
		 * Delete selected file path
		 */
		deleteFolderItem.addActionListener(e -> {
			String filePath = fileManager.getSelectedFilePath();
			fileManager.deleteSelectedFile(filePath);
		});
        /**
		* Refresh file list
	    */
		refreshItem.addActionListener(e -> fileManager.listFiles());
		
		fileMenu.add(uploadToFolderItem);
		fileMenu.add(createFolderItem);
		fileMenu.add(deleteFolderItem);
		fileMenu.addSeparator();
		fileMenu.add(refreshItem);
		menuBar.add(fileMenu);
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}
}
