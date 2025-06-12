package app;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * FileMenuBar defines the main menu bar with file actions, which are connected
 * to the FileManager.
 */
public class FileMenuBar {
	private final JMenuBar menuBar;

	public FileMenuBar(FileManager fileManager) {
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem uploadToFolderItem = new JMenuItem("Upload File");
		JMenuItem createFolderItem = new JMenuItem("Create Folder");
		JMenuItem deleteFolderItem = new JMenuItem("Delete");
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


		//Menu Item to sync local files to cloud storage
		JMenu cloudMenu = new JMenu("Cloud");
		JMenuItem syncToS3Item = new JMenuItem("Sync Cloud Storage"); 
		JMenuItem awsS3LoginDialogItem = new JMenuItem("Login to AWS S3"); 

		cloudMenu.add(syncToS3Item); 
		cloudMenu.add(awsS3LoginDialogItem);

		menuBar.add(fileMenu);
		menuBar.add(cloudMenu);

	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}
}
