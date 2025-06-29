package app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import handles.FileSyncHandle;
import handles.S3LocalFileSyncHandle;
import listeners.FileEventListener;
import listeners.FolderTreeSelectionHandler;
import listeners.SqlFileEventListener;
import storage.AwsLoginDialog;
import storage.AwsS3Credential;
import storage.FileObject;
import storage.FileStoreException;
import storage.S3CloudStoreOperations;
import storage.db.FileMetadataRepository;
import storage.db.SQLiteFileMetadataRepository;
import storage.db.SqlConnectionManager;
import util.AwsS3Util;
import util.FileUtil;

/**
 * CloudFileStorageUI is the main Swing-based graphical interface for
 * interacting with a cloud-backed file system (AWS S3). It supports
 * file browsing, syncing, and folder navigation via a split-pane layout
 * showing a tree view and a file table.
 */
public class CloudFileStorageUI extends JFrame implements BaseFileStorageUI {
    private static final Logger logger = LogManager.getLogger(S3CloudStoreOperations.class);

    // Handles file logic and operations (upload, sync, delete)
    private FileManager fileManager;

    // Table component to display files/folders
    private JTable fileTable;
    private DefaultTableModel tableModel;

    // Root node of the folder tree on the left panel
    private DefaultMutableTreeNode rootTreeNode;

    // Tree view component for folder structure
    private JTree folderTree;

    // Cloud storage operations handler (AWS S3 implementation)
    private S3CloudStoreOperations cloudStoreOperations;

	private AwsS3Credential awsS3Credential;

    private Connection connection;
    private FileMetadataRepository fileMetadataRepository;
    private FileSyncHandle fileSyncHandle;

    private boolean isWindowOpened = false;
    /**
     * Constructor initializes and builds the GUI layout and components.
     */
    public CloudFileStorageUI() {
        init();
    }

    /**
     * Initializes layout, UI components, and default event handlers.
     */
    private void init() {
        setTitle("Cloud File Storage App");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new Object[] { "Name", "Path", "Modified Date", "Type", "Size" }, 0);
        fileTable = new JTable(tableModel);
		try {
			awsS3Credential = AwsS3Util.loadCredential();
		} catch (IOException ex) {
            logger.error("Error loading AWS credentials. {}", ex.getMessage());
		}
        initFileMetaRepository();
        cloudStoreOperations = new S3CloudStoreOperations(awsS3Credential);
        fileSyncHandle  = new S3LocalFileSyncHandle(fileMetadataRepository, cloudStoreOperations);
        FileEventListener fileEventListener = new SqlFileEventListener(fileMetadataRepository);
        fileManager = new S3CloudManagerImpl(this, cloudStoreOperations,fileEventListener,fileSyncHandle);

        FileMenuBar menuBar = new FileMenuBar(this, fileManager);
        setJMenuBar(menuBar.getMenuBar());

        setupMainPanel();
        fileManager.listFiles(); // Load initial file list
		addWindowListener(new WindowAdapter() {
               @Override
               public void windowOpened(WindowEvent e) {
                  logger.info("GUI application window  opended");
				  if(awsS3Credential == null){
                     showAlertMessage("AWS S3 Credentials not found. Please login the S3 credentials.");
				  }
                  else{
                    showUnResolveFiles();
                  }
                  isWindowOpened = true;
               }

               @Override
               public void windowActivated(WindowEvent e) {
                   logger.info("GUI application window Activated.....");
               }
        });
        logger.info("GUI application initialized.....");
    }

    /**
     * Initialize File Meta tracker repository
     */
    private void initFileMetaRepository()  {
        try {
            logger.info("Connecting SQL database conenction.....");
            connection = SqlConnectionManager.getConnection();
            fileMetadataRepository = new SQLiteFileMetadataRepository(connection);
        } catch (SQLException ex) {
            logger.error("Database connection failed. {}", ex.getMessage());
        }
    }

    /**
     * Initializes the left-right split pane layout and embeds tree + table.
     */
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
     * Updates the right-side file table with a list of FileObjects.
     */
    @Override
    public void updateFileTable(List<FileObject> files) {
        tableModel.setRowCount(0); // Clear previous content
        for (FileObject fileObject : files) {
            addFileToTableRecursive(fileObject);
        }

        showUnResolveFiles();
    }

    /**
     * Helper to populate the JTable with file data from a FileObject.
     */
    private void addFileToTableRecursive(FileObject fileObject) {
        Object[] fileItem = new Object[] {
            fileObject.getFileName(),
            fileObject.getFilePath(),
            fileObject.getLastModifiedDate(),
            fileObjectType(fileObject),
            fileObject.getFileSize()
        };
        tableModel.addRow(fileItem);
    }

    /**
     * Initializes the left-side folder tree view.
     */
    private void initFolderTree() {
        File fileRoot = new File(FileUtil.STORAGE_DIR);
        rootTreeNode = createRootTreeNode();
        createChildren(fileRoot, rootTreeNode);
        folderTree = new JTree(rootTreeNode, true);
        folderTree.addTreeSelectionListener(new FolderTreeSelectionHandler(this));
    }

    /**
     * Reloads the folder tree by clearing and rebuilding it.
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
     * Creates the root node for the folder tree view.
     */
    private DefaultMutableTreeNode createRootTreeNode() {
        File root = new File(FileUtil.STORAGE_DIR);
        return new DefaultMutableTreeNode(root);
    }

    /**
     * Recursively adds subfolders to the folder tree view.
     */
    private void createChildren(File parent, DefaultMutableTreeNode parentNode) {
        File[] files = FileSystemView.getFileSystemView().getFiles(parent, true);
        for (File file : files) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName(), file.isDirectory());
            parentNode.add(childNode);
            if (file.isDirectory()) {
                createChildren(file, childNode);
            }
        }
    }

    /**
     * Returns the selected file from the JTable in FileObject format.
     */
    @Override
    public FileObject getSelectedFile() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow == -1)
            return null;
        return FileObject.builder()
                .setFileName((String) tableModel.getValueAt(selectedRow, 0))
                .build();
    }

    /**
     * Provides access to the file table for listeners or external access.
     */
    @Override
    public JTable getFileTable() {
        return fileTable;
    }

    /**
     * Shows a dialog with a message alert to the user.
     */
    @Override
    public void showAlertMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * Returns the root UI component for embedding or referencing.
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     * Launches the AWS credentials login dialog and saves credentials if submitted.
     */
    @Override
    public void showCloudAccountDialog() {
        AwsLoginDialog dialog = new AwsLoginDialog(this);
        dialog.setVisible(true);
        if (dialog.isSubmitted()) {
            saveLoginCredentials(dialog);
        }
    }

    /**
     * Save and encrypt AWS credentials provided from the login dialog.
     * Also re-initializes the cloud store connection and refreshes files.
     */
    private void saveLoginCredentials(AwsLoginDialog dialog) {
        try {
            AwsS3Credential awsS3Credential = new AwsS3Credential();
            awsS3Credential.setAccessKey(dialog.getAccessKey());
            awsS3Credential.setSecretKey(dialog.getSecretKey());
            awsS3Credential.setRegion(dialog.getRegion());
            awsS3Credential.setBucketName(dialog.getBucketName());

            AwsS3Util.saveCredential(awsS3Credential);
            cloudStoreOperations.connectAwsS3Client(awsS3Credential);

            JOptionPane.showMessageDialog(this, "AWS credentials saved and encrypted.");
            logger.info("AWS credentials saved and encrypted");
            fileManager.listFiles();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to login");
            logger.error("Failed to save AWS credentials, error: {}", ex.getMessage());
        }
    }

	/**
	 * Get file object type. Default type is "File"
	 * @param fileObject
	 * @return String
	 */
	private String fileObjectType(FileObject fileObject){
		return (fileObject.getFileType() == null || fileObject.getFileType().isEmpty())? 
		            FileUtil.DEFAULT_OBJECT_TYPE: fileObject.getFileType();
	}

    /**
     * Show unresolve files alert message
     */
    private void showUnResolveFiles(){
        if(!this.isWindowOpened){
            return;
        }

        try {
            List<FileObject> unResolveFiles = fileSyncHandle.unResolveFiles();
            if(!unResolveFiles.isEmpty()){
              String msg = String.format("There is are %d unresolve files. Please sync the files", unResolveFiles.size());
              logger.info(msg);
              for(FileObject fileObject: unResolveFiles){
                logger.info("Unresolved file: {}",fileObject);
              }
              this.showAlertMessage(msg);
            }
            else{
             logger.info("All files are in sync.....");
            }
        } catch (FileStoreException ex) {
            logger.error("Unresolved files query error: {}", ex.getMessage());
        }
    }
}