import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.*;

/**
 * LocalStorageManager handles all file operations for local storage such as
 * uploading, creating folders, listing and deleting files.
 */
public class LocalStorageManager {
    private static final String STORAGE_DIR = "local_storage";
    private final LocalFileStorageApp app;

    /**
     * Constructor initializes the storage directory and stores reference to the app UI.
     *
     * @param app Instance of the UI application.
     */
    public LocalStorageManager(LocalFileStorageApp app) {
        this.app = app;
        initializeStorageDirectory();
    }

    /**
     * Creates the base storage directory if it does not exist.
     */
    private void initializeStorageDirectory() {
        File dir = new File(STORAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Opens a file chooser to select and copy a file into the storage directory.
     */
    public void uploadFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(app);
        if (result == JFileChooser.APPROVE_OPTION) {
            File source = chooser.getSelectedFile();
            Path targetPath = Paths.get(STORAGE_DIR, source.getName());
            try {
                Files.copy(source.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(app, "File uploaded locally.");
                listFiles();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(app, "Upload failed: " + e.getMessage());
            }
        }
    }

    /**
     * Creates a new folder under the storage directory.
     *
     * @param folderName The name of the new folder.
     */
    public void createFolder(String folderName) {
        if (folderName == null || folderName.isEmpty()) {
            JOptionPane.showMessageDialog(app, "Please enter a folder name.");
            return;
        }

        File folder = new File(STORAGE_DIR + File.separator + folderName);
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
     * Deletes a selected file or folder from the storage directory.
     *
     * @param filePath The absolute path of the file or folder to delete.
     */
    public void deleteSelectedFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            JOptionPane.showMessageDialog(app, "Please select a file to delete.");
            return;
        }

        File file = new File(filePath);
        if (file.exists() && file.getAbsolutePath().startsWith(new File(STORAGE_DIR).getAbsolutePath())) {
            try {
                if (file.isDirectory()) {
                    // Recursively delete directory and its contents
                    Files.walk(file.toPath())
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } else {
                    file.delete();
                }
                JOptionPane.showMessageDialog(app, "File deleted successfully.");
                listFiles();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(app, "Error deleting file: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(app, "Invalid file selection.");
        }
    }

    /**
     * Lists all files and folders currently stored in the storage directory.
     */
    public void listFiles() {
        File dir = new File(STORAGE_DIR);
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
    }
}
