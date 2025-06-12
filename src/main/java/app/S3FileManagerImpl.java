// package app;

// import util.FileUtil;
// import java.io.File;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;
// import storage.S3StorageManager;

// /**
//  * S3FileManagerImpl implements FileManager interface and uploads, deletes, and lists files/folders to/from AWS S3.
//  */
// public class S3FileManagerImpl implements FileManager {

//     private final BaseFileStorageUI app;
//     private final S3StorageManager s3Manager;

//     public S3FileManagerImpl(BaseFileStorageUI app, S3StorageManager s3Manager) {
//         this.app = app;
//         this.s3Manager = s3Manager;
//     }

//     @Override
//     public void createFolder(String folderName) {
//         if (folderName == null || folderName.isEmpty()) {
//             app.showAlertMessage("Please enter a folder name.");
//             return;
//         }
//         String folderKey = folderName.endsWith("/") ? folderName : folderName + "/";
//         File dummy = new File("temp.txt");
//         try {
//             if (!dummy.exists()) dummy.createNewFile();
//             s3Manager.uploadFile(folderKey + ".keep", dummy.getAbsolutePath());
//             app.showAlertMessage("Folder created in S3.");
//             listFiles();
//         } catch (IOException e) {
//             app.showAlertMessage("Failed to create folder: " + e.getMessage());
//         } finally {
//             dummy.delete();
//         }
//     }

//     @Override
//     public void uploadFileToSelectedFolder() {
//         String folderPath = getSelectedFilePath();
//         if (folderPath == null) {
//             app.showAlertMessage("No folder selected.");
//             return;
//         }
//         uploadFileToSelectedFolder(folderPath);
//     }

//     @Override
//     public void uploadFileToSelectedFolder(String folderPath) {
//         File selectedFile = FileUtil.chooseFile(app.getComponent());
//         if (selectedFile == null) return;
//         String key = folderPath.endsWith("/") ? folderPath + selectedFile.getName() : folderPath + "/" + selectedFile.getName();
//         s3Manager.uploadFile(key, selectedFile.getAbsolutePath());
//         app.showAlertMessage("File uploaded to S3.");
//         listFiles();
//     }

//     @Override
//     public void deleteSelectedFile(String filePath) {
//         if (filePath == null || filePath.isEmpty()) {
//             app.showAlertMessage("No file selected.");
//             return;
//         }
//         s3Manager.deleteFile(filePath);
//         app.showAlertMessage("File deleted from S3.");
//         listFiles();
//     }

//     @Override
//     public void listFiles() {
//         List<File> dummyList = new ArrayList<>();
//         System.out.println("Listing files in S3:");
//         s3Manager.listFiles();
//         // For UI, you can adapt this section to reflect dummy file objects in the table
//         app.updateFileTable(dummyList);
//         app.updateFolderTree();
//     }

//     @Override
//     public String getSelectedFilePath() {
//         return app.getSelectedFilePath();
//     }
// }
