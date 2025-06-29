package app;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import storage.FileObject;

/**
 * FileMenuBar defines the main menu bar with file actions, which are connected
 * to the FileManager.
 */
public class FileMenuBar {
	private JMenuBar menuBar;
	private final BaseFileStorageUI parentUI;
	private final FileManager fileManager; 

	public FileMenuBar(FileManager fileManager) {
	     this(null,fileManager);
	}

	public FileMenuBar(BaseFileStorageUI parentUI, FileManager fileManager) {
		  this.parentUI = parentUI;
		  this.fileManager = fileManager;
		  init(fileManager);
	}

	private void init(FileManager fileManager){
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem uploadToFolderItem = new JMenuItem("Upload");
		JMenuItem deleteFileItem = new JMenuItem("Delete");
		JMenuItem downloadFileItem = new JMenuItem("Download");
		JMenuItem refreshItem = new JMenuItem("Refresh");
	
		uploadToFolderItem.addActionListener(e -> fileManager.uploadFileToSelectedFolder());
		/**
		 * Create folder
		 */
		// createFolderItem.addActionListener(e -> {
		// 	JTextField input = new JTextField();
		// 	int option = JOptionPane.showConfirmDialog(null, input, "Enter folder name:", JOptionPane.OK_CANCEL_OPTION);
		// 	if (option == JOptionPane.OK_OPTION) {
		// 		fileManager.createFolder(input.getText().trim());
		// 	}
		// });

		/**
		 * Delete selected file path
		 */
		deleteFileItem.addActionListener(e -> {
			FileObject fileObject = fileManager.getSelectedFile();
			fileManager.deleteSelectedFile(fileObject);
		});

		/**
		 * Download selected file from the cloud S3 to local directory
		 */
		downloadFileItem.addActionListener(e -> {
			FileObject fileObject = fileManager.getSelectedFile();
			fileManager.downloadSelectedFile(fileObject);
		});
        /**
		* Refresh file list
	    */
		refreshItem.addActionListener(e -> fileManager.listFiles());

		fileMenu.add(uploadToFolderItem);
		fileMenu.add(deleteFileItem);
		fileMenu.add(downloadFileItem);
		fileMenu.addSeparator();
		fileMenu.add(refreshItem);


		//Menu Item to sync local files to cloud storage
		JMenu cloudMenu = new JMenu("Cloud");
		JMenuItem awsS3LoginDialogItem = new JMenuItem("AWS S3 Login"); 
		JMenuItem s3FileSync = new JMenuItem("AWS S3 Sync"); 

		if(parentUI != null){
		   awsS3LoginDialogItem.addActionListener(e->parentUI.showCloudAccountDialog());
		}
		s3FileSync.addActionListener(e->fileManager.syncFile());
		cloudMenu.add(awsS3LoginDialogItem);
		cloudMenu.add(s3FileSync);
	
		menuBar.add(fileMenu);
		menuBar.add(cloudMenu);
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}
}