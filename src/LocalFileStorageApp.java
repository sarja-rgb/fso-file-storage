import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * FileStorageApp is the main GUI application class.
 * It provides a Swing-based user interface for managing files and folders in a storage directory.
 * Users can upload files, create new folders, delete selected items,
 * and refresh the file list to reflect the current state of the local storage directory.
 */
public class LocalFileStorageApp extends JFrame {
    // Manager to handle all local file operations
    private LocalStorageManager localStorageManager;

    // Table to display files and folders
    private JTable fileTable;
    private DefaultTableModel tableModel;

    // Input field for creating new folders
    private JTextField folderNameField;

    /**
     * Constructor initializes the GUI components and hooks up event listeners.
     */
    public LocalFileStorageApp() {
        // Set basic window properties
        setTitle("Local File Storage App");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // Initialize the file manager
        localStorageManager = new LocalStorageManager(this);

        // Main panel with BorderLayout to organize UI components
        JPanel panel = new JPanel(new BorderLayout());

        // Top panel contains all control buttons and input fields
        JPanel topPanel = new JPanel(new FlowLayout());

        // Control buttons
        JButton uploadButton = new JButton("Upload File");
        JButton deleteButton = new JButton("Delete Selected File");
        JButton listButton = new JButton("Refresh Files");

        // Folder creation field and button
        folderNameField = new JTextField(15);
        JButton createFolderButton = new JButton("Create Folder");

        // Add controls to top panel
        topPanel.add(uploadButton);
        topPanel.add(deleteButton);
        topPanel.add(listButton);
        topPanel.add(new JLabel("Folder Name:"));
        topPanel.add(folderNameField);
        topPanel.add(createFolderButton);

        // Add top panel to the main panel
        panel.add(topPanel, BorderLayout.NORTH);

        // Initialize the file table and scroll pane
        tableModel = new DefaultTableModel(new Object[]{"Name", "Path", "Type"}, 0);
        fileTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(fileTable);

        // Add scroll pane with file table to center of main panel
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add the main panel to the JFrame
        add(panel);

        // Hook up button actions to respective methods in LocalStorageManager
        uploadButton.addActionListener(e -> localStorageManager.uploadFile());
        deleteButton.addActionListener(e -> localStorageManager.deleteSelectedFile(getSelectedFilePath()));
        listButton.addActionListener(e -> localStorageManager.listFiles());
        createFolderButton.addActionListener(e -> localStorageManager.createFolder(folderNameField.getText().trim()));

        // Load initial file list from local storage
        localStorageManager.listFiles();
    }

    /**
     * Updates the JTable to display the list of files.
     *
     * @param files List of File objects to display.
     */
    public void updateFileTable(List<File> files) {
        tableModel.setRowCount(0); // Clear existing rows
        for (File file : files) {
            // Add file data to table: name, absolute path, and type (File or Folder)
            tableModel.addRow(new Object[]{file.getName(), file.getAbsolutePath(), file.isDirectory() ? "Folder" : "File"});
        }
    }

    /**
     * Returns the absolute path of the selected file in the JTable.
     *
     * @return String path of the selected file or null if nothing is selected.
     */
    private String getSelectedFilePath() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow == -1) return null; // No selection
        return (String) tableModel.getValueAt(selectedRow, 1); // Get file path from table
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
