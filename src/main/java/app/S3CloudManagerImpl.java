package app;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import handles.FileSyncHandle;
import listeners.FileEventListener;
import storage.FileObject;
import storage.FileStoreException;
import storage.FileStoreOperations;
import util.FileEventExceptions;
import util.FileUtil;

/**
 * S3FileManagerImpl implements FileManager interface and uploads, deletes, and lists files/folders to/from AWS S3.
 */
public class S3CloudManagerImpl implements FileManager {
    private final BaseFileStorageUI appUI;
    private final FileStoreOperations fileOperations;
    private FileEventListener fileEventListener;
    private final FileSyncHandle fileSyncHandle;

    public S3CloudManagerImpl(BaseFileStorageUI appUI, FileStoreOperations fileOperations) {
        this(appUI,fileOperations,null);
    }

    public S3CloudManagerImpl(BaseFileStorageUI appUI, FileStoreOperations fileOperations,
       FileEventListener  fileEventListener) {
        this(appUI,fileOperations,fileEventListener,null);
    }

     public S3CloudManagerImpl(BaseFileStorageUI appUI, FileStoreOperations fileOperations,
       FileEventListener  fileEventListener, FileSyncHandle fileSyncHandle) {
        this.appUI = appUI;
        this.fileOperations = fileOperations;
        this.fileEventListener = fileEventListener;
        this.fileSyncHandle = fileSyncHandle;
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
                FileObject fileObject = this.fileOperations.save(selectedFile);
                appUI.showAlertMessage("File upload completed");
                listFiles();
                if(fileEventListener != null){
                   System.out.println("#### File event listener write file"+fileObject);
                   fileEventListener.onSave(fileObject);
                }
			} catch (FileStoreException e) {
				appUI.showAlertMessage("Upload Error: \n" + e.getMessage());
            } catch (FileEventExceptions e) {
                e.printStackTrace();
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
            if(fileEventListener != null){
                fileEventListener.onDelete(fileObject);
             }
            listFiles();
        } catch (FileStoreException e) {
           appUI.showAlertMessage("Error deleting file");
           System.err.println("Error deleteing file");
        } catch (FileEventExceptions e) {
            e.printStackTrace();
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

    @Override
    public void syncFile() {
        try {
           if(fileSyncHandle != null){
              System.out.println("Sync up local meta data and cloud storage");
              List<FileObject> fileObjects = fileOperations.loadAll();
              fileSyncHandle.syncFiles(fileObjects);
              appUI.showAlertMessage("File storage sync up completed");
            } 
        } catch (Exception ex) {
            System.out.println("File sync errors "+ex.getMessage());
            ex.printStackTrace();
        }
        
    }
}