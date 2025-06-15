package app;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import listeners.FolderTreeSelectionHandler;
import storage.AwsLoginDialog;
import storage.AwsS3Credential;
import storage.FileObject;
import storage.S3CloudStoreOperations;
import util.AwsS3Util;
import util.FileUtil;

/**
 * FileStorageApp is the main GUI application class. It provides a Swing-based
 * user interface for managing files and folders in a storage directory. Users
 * can upload files, create new folders, delete selected items, and refresh the
 * file list to reflect the current state of the Cloud storage .
 */
public class CloudFileStorageUI extends JFrame implements  BaseFileStorageUI {
	// Manager to handle all local file operations
	private FileManager fileManager;
	// Table to display files and folders
	private JTable fileTable;
	private DefaultTableModel tableModel;
	private DefaultMutableTreeNode rootTreeNode;
	//private FileContextMenu fileContextMenu;
	private JTree folderTree;
	private S3CloudStoreOperations cloudStoreOperations;

	/**
	 * Constructor initializes the GUI components and hooks up event listeners.
	 */
	public CloudFileStorageUI() {
	    init();
	}

	private void init(){
		setTitle("Cloud File Storage App");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		tableModel = new DefaultTableModel(new Object[] { "Name", "Path",  "Modified Date", "Type", "Size" }, 0);
		fileTable = new JTable(tableModel);
        cloudStoreOperations = new S3CloudStoreOperations();
		fileManager = new S3CloudManagerImpl(this,cloudStoreOperations);

		// Set Menu Bar
		FileMenuBar menuBar = new FileMenuBar(this,fileManager);
		setJMenuBar(menuBar.getMenuBar());

		// Setup Main Panel
		setupMainPanel();

		// Load files
		fileManager.listFiles();
	}

	private void setupMainPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.3);

		initFolderTree();

		JScrollPane treeScroll = new JScrollPane(folderTree);
		JScrollPane tableScroll = new JScrollPane(fileTable);

		splitPane.setLeftComponent(treeScroll);
		splitPane.setRightComponent(tableScroll);

		panel.add(splitPane, BorderLayout.CENTER);
		add(panel);
	}

	/**
	 * Upload and refresh file table list
	 * @param files
	 */
	@Override
	public void updateFileTable(List<FileObject> files) {
		tableModel.setRowCount(0);
		for (FileObject fileObject : files) {

			addFileToTableRecursive(fileObject);
		}
	}
	/**
	 * Add filter to table list recursively
	 * @param file
	 */
	private void addFileToTableRecursive(FileObject fileObject) {
		System.out.println("# addFileToTableRecursive "+fileObject);
		Object[] fileItem = new Object[] { fileObject.getFileName(),
			                               fileObject.getFilePath(), 
										   fileObject.getLastModifiedDate(),
										   fileObject.getFileType(),
										   fileObject.getFileSize() };
		tableModel.addRow(fileItem);
	}

	private void initFolderTree() {
		File fileRoot = new File(FileUtil.STORAGE_DIR);
		rootTreeNode = createRootTreeNode();
		createChildren(fileRoot, rootTreeNode);
		folderTree = new JTree(rootTreeNode, true);
		folderTree.addTreeSelectionListener(new FolderTreeSelectionHandler(this));
	}

	/**
	 * Update and reload folder tree whenever there file or folder is modified (created, deleted)
	 */
	@Override
	public void updateFolderTree() {
		File fileRoot = new File(FileUtil.STORAGE_DIR);
		DefaultTreeModel treeModel = (DefaultTreeModel) folderTree.getModel();
		rootTreeNode.removeAllChildren();
		rootTreeNode.removeFromParent();
		createChildren(fileRoot, rootTreeNode);
		treeModel.reload();
	}

	/**
	 * Crate root Folder tree
	 * @return
	 */
	private DefaultMutableTreeNode createRootTreeNode() {
		File root = new File(FileUtil.STORAGE_DIR);
		return new DefaultMutableTreeNode(root);
	}

	private void createChildren(File parent, DefaultMutableTreeNode parentNode) {
		File[] files = FileSystemView.getFileSystemView().getFiles(parent, true);
		for (File file : files) {
			if (file.isDirectory()) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
				parentNode.add(childNode);
				createChildren(file, childNode);
			} else {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName(), false);
				parentNode.add(childNode);
			}
		}
	}

	/**
	 * Returns the name of the selected file in the JTable.
	 *
	 * @return String path of the selected file or null if nothing is selected.
	 */
	@Override
	public FileObject getSelectedFile() {
		int selectedRow = fileTable.getSelectedRow();
		if (selectedRow == -1)
			return null;
		return FileObject.builder()
		                 .setFileName((String)tableModel.getValueAt(selectedRow, 0))
		                 .build();
	}

	@Override
	public JTable getFileTable() {
		return fileTable;
	}
	
	/*
	 * Display UI Alert message
	 * 
	 * @param message
	 * 
	 */
	@Override
	public void showAlertMessage(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void showCloudAccountDialog() {
			AwsLoginDialog dialog = new AwsLoginDialog(this);
			dialog.setVisible(true);
		    if (dialog.isSubmitted()) {
	            saveLoginCredentials(dialog);
			}
	}

	private void saveLoginCredentials(AwsLoginDialog dialog){
		try {
			AwsS3Credential awsS3Credential = new AwsS3Credential();
			awsS3Credential.setAccessKey(dialog.getAccessKey());
			awsS3Credential.setSecretKey(dialog.getSecretKey());
			awsS3Credential.setRegion(dialog.getRegion());
			awsS3Credential.setBucketName(dialog.getBucketName());
			AwsS3Util.saveCredential(awsS3Credential);
			JOptionPane.showMessageDialog(this, "AWS credentials saved and encrypted.");	
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Failed to login");	
			System.out.println("Save login error "+ex.getMessage());
		}
	}
}
