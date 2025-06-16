package app;

import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import storage.FileObject;
import storage.FileStoreException;
import storage.FileStoreOperations;
import util.FileUtil;

/**
 * S3FileManagerImpl implements FileManager interface and uploads, deletes, and lists files/folders to/from AWS S3.
 */
public class S3CloudManagerImpl implements FileManager {
    private final BaseFileStorageUI appUI;
    private final FileStoreOperations fileOperations;

    public S3CloudManagerImpl(BaseFileStorageUI appUI, FileStoreOperations fileOperations) {
        this.appUI = appUI;
        this.fileOperations = fileOperations;
    }

    @Override
    public void uploadFileToSelectedFolder() {
         uploadFileToSelectedFolder(FileUtil.STORAGE_DIR);
    }

    @Override
    public void uploadFileToSelectedFolder(String folderPath) {
        JFileChooser chooser = new JFileChooser();
		int result = chooser.showOpenDialog(appUI.getComponent());
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
                File selectedFile = chooser.getSelectedFile();
                this.fileOperations.save(selectedFile);
				appUI.showAlertMessage("File upload completed");
				listFiles();
			} catch (FileStoreException e) {
				appUI.showAlertMessage("Upload Error: \n" + e.getMessage());
			}
		}
    }

    @Override
    public void deleteSelectedFile(FileObject fileObject) {
        if (fileObject== null) {
            appUI.showAlertMessage("No file selected.");
            return;
        }
        try {
            this.fileOperations.delete(fileObject);
            listFiles();
        } catch (FileStoreException e) {
           appUI.showAlertMessage("Error deleting file");
           System.err.println("Error deleteing file");
        }
    }

    @Override
    public void listFiles() {
        try {
            System.out.println("Listing files in S3:");
            List<FileObject> fileObjects = fileOperations.loadAll();
            appUI.updateFileTable(fileObjects);
        } catch (FileStoreException ex) {
            System.out.println("List file errors "+ex.getMessage());
        }
    }

    @Override
    public FileObject getSelectedFile() {
        return appUI.getSelectedFile();
    }
}
