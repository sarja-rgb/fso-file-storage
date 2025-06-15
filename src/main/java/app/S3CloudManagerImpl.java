package app;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import storage.FileObject;
import storage.FileStoreOperations;

/**
 * S3FileManagerImpl implements FileManager interface and uploads, deletes, and lists files/folders to/from AWS S3.
 */
public class S3CloudManagerImpl implements FileManager {
    private final BaseFileStorageUI appUI;
    private final FileStoreOperations fileOperations;
    //private final List<FileEventListener> fileEventListeners;

    public S3CloudManagerImpl(BaseFileStorageUI appUI, FileStoreOperations fileOperations) {
        this.appUI = appUI;
        this.fileOperations = fileOperations;
        //fileEventListeners = new ArrayList<>();
    }

    @Override
    public void createFolder(String folderName) {
  
    }

    @Override
    public void uploadFileToSelectedFolder() {
        String folderPath = getSelectedFilePath();
        if (folderPath == null) {
            appUI.showAlertMessage("No folder selected.");
            return;
        }
        uploadFileToSelectedFolder(folderPath);
    }

    @Override
    public void uploadFileToSelectedFolder(String folderPath) {
        JFileChooser chooser = new JFileChooser();
		int result = chooser.showOpenDialog(appUI.getComponent());
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
                File selectedFile = chooser.getSelectedFile();
                this.fileOperations.save(selectedFile);
				appUI.showAlertMessage("File uploaded to folder.");
				//fileEventListeners.stream().forEach(event->event.onUploadFile(selectedFile));
				listFiles();
			} catch (Exception e) {
				appUI.showAlertMessage("Upload failed: \n" + e.getMessage());
			}
		}
    }

    @Override
    public void deleteSelectedFile(String filePath) {
        // if (filePath == null || filePath.isEmpty()) {
        //     app.showAlertMessage("No file selected.");
        //     return;
        // }
        // s3Manager.deleteFile(filePath);
        // s3Manager.delete(file);
        // appUI.showAlertMessage("File deleted from S3.");
        // listFiles();
    }

    @Override
    public void listFiles() {
        System.out.println("Listing files in S3:");
        List<FileObject> fileObjects = fileOperations.loadAll();
        appUI.updateFileTable(fileObjects);
        // For UI, you can adapt this section to reflect dummy file objects in the table
        //appUI.updateFileTable(dummyList);
        //appUI.updateFolderTree();
    }

    @Override
    public String getSelectedFilePath() {
        return appUI.getSelectedFilePath();
    }
}
