package app;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger logger = LogManager.getLogger(S3CloudManagerImpl.class);
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
                   logger.info("#### File event listener write file: {}",fileObject);
                   fileEventListener.onSave(fileObject);
                }
			} catch (FileStoreException ex1) {
				appUI.showAlertMessage("Upload Error: \n" + ex1.getMessage());
                logger.error("File Upload Error: error: {}", ex1.getMessage());
            } catch (FileEventExceptions ex2) {
                logger.error("File Upload event error: {}", ex2.getMessage());
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
        } catch (FileStoreException ex1) {
           appUI.showAlertMessage("Error deleting file");
           logger.error("Error deleteing file {}", ex1.getMessage());
        } catch (FileEventExceptions ex2) {
            logger.error("File delete event error: {}", ex2.getMessage());
        }
    }

    @Override
    public void listFiles() {
        try {
            logger.info("Listing files in S3");
            List<FileObject> fileObjects = fileOperations.loadAll();
            appUI.updateFileTable(fileObjects);
        } catch (FileStoreException ex) {
           logger.error("List file errors: {}", ex.getMessage());
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
             logger.info("Sync up local meta data and cloud storage");
              List<FileObject> fileObjects = fileOperations.loadAll();
              fileSyncHandle.syncFiles(fileObjects);
              appUI.showAlertMessage("File storage sync up completed");
            } 
        } catch (Exception ex) {
             logger.error("File sync errors {}", ex.getMessage());
        }
        
    }
}