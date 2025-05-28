package app;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import listeners.FolderTreeSelectionHandler;
import util.FileUtil;

/**
 * FileStorageApp is the main GUI application class. It provides a Swing-based
 * user interface for managing files and folders in a storage directory. Users
 * can upload files, create new folders, delete selected items, and refresh the
 * file list to reflect the current state of the local storage directory.
 */
public class LocalFileStorageApp extends JFrame {
	// Manager to handle all local file operations
	private final FileManager fileManager;
	// Table to display files and folders
	private JTable fileTable;
	private DefaultTableModel tableModel;
	private DefaultMutableTreeNode rootTreeNode;
    // Current selected Jtree node	
	private DefaultMutableTreeNode selectedTreeNode;

	// Input field for creating new folders
	private JTextField folderNameField;

	private FileContextMenu fileContextMenu;


	private JTree folderTree;

	/**
	 * Constructor initializes the GUI components and hooks up event listeners.
	 */
	public LocalFileStorageApp() {
		setTitle("Local File Storage App");
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		folderNameField = new JTextField(20);
		tableModel = new DefaultTableModel(new Object[] { "Name", "Path", "Type" }, 0);
		fileTable = new JTable(tableModel);

		fileManager = new FileManagerImpl(this);

		// Set Menu Bar
		FileMenuBar menuBar = new FileMenuBar(fileManager);
		setJMenuBar(menuBar.getMenuBar());

		// Set Context Menu
		fileContextMenu = new FileContextMenu(fileTable, fileManager);

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
	public void updateFileTable(List<File> files) {
		tableModel.setRowCount(0);
		for (File file : files) {
			addFileToTableRecursive(file);
		}
	}

	/**
	 * Add filter to table list recursively
	 * @param file
	 */
	private void addFileToTableRecursive(File file) {
		tableModel.addRow(
				new Object[] { file.getName(), file.getAbsolutePath(), file.isDirectory() ? "Folder" : "File" });
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					addFileToTableRecursive(child);
				}
			}
		}
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
	 * Returns the absolute path of the selected file in the JTable.
	 *
	 * @return String path of the selected file or null if nothing is selected.
	 */
	public String getSelectedFilePath() {
		if(selectedTreeNode != null){
			String selectedFilePath = FileUtil.createFilePath(selectedTreeNode.getUserObjectPath());
			if (selectedFilePath != null && !selectedFilePath.isEmpty()){
                return selectedFilePath;
			}
		}
		int selectedRow = fileTable.getSelectedRow();
		if (selectedRow == -1)
			return null; // No selection
		return (String) tableModel.getValueAt(selectedRow, 1); // Get file path from table
	}

	/**
	 * Set current selected Tree Node
	 * @param selectedTreeNode
	 */
	public void setSelectedTreeNode(DefaultMutableTreeNode selectedTreeNode){
	     this.selectedTreeNode = selectedTreeNode;
	}


	public JTable getFileTable() {
		return fileTable;
	}

	/**
	 * Main method to launch the application.
	 *
	 * @param args Command-line arguments (not used).
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new LocalFileStorageApp().setVisible(true));
	}
}
