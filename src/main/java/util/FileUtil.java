package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import javax.swing.tree.TreePath;

/**
 * File util helper class
 */
public class FileUtil {
    /**
     * Default file storage directo
     */
    public static final String STORAGE_DIR = "local_storage";

    /**
     * Tree path to FilePath
     * @param treePath
     * @return
     */
    public static String createFilePath(TreePath treePath) {
        Object[] paths = treePath.getPath();
        return createFilePath(paths);
    }

    /**
     * Create file path TreeNode paths
     * @param paths
     * @return
     */
    public static String createFilePath(Object[] paths){
        StringBuilder sb = new StringBuilder();
        for(int index = 0; index < paths.length; index++){
            if(index > 0){
              sb.append(File.separatorChar);
             }
            sb.append(paths[index].toString()); 
         }
         return sb.toString();
    }

    /**
     * Check if filepath is directory
     * @param filePath
     * @return
     */
    public static boolean isDirectory(String filePath){
        return (filePath == null)? false: new File(filePath).isDirectory(); 
    }

    /**
     * Create new folder file path
     * @param selectedFilePath
     * @param folderName
     * @return
     */
    public static String createNewFolderFilePath(String selectedFilePath, String folderName){
        StringBuilder sb = new StringBuilder();
       if(selectedFilePath != null && !selectedFilePath.isEmpty()){
           sb.append(selectedFilePath);
       }
       else{
          sb.append(FileUtil.STORAGE_DIR);
       }
       return sb.append(File.separator)
                .append(folderName)
                .toString();
   }

   /**
    * Check if the given file path is the root storage directory
    * @param file
    * @return
    */
   public static boolean isRootDirectory(File file){
        return file != null && file.equals(new File(STORAGE_DIR));	
   }

   /**
    * Create fiel directory
    * @param fileDirName
    * @return File
    */
   public static File createFileDirectory(String fileDirName){
        File dir = new File(fileDirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
   }

   /**
    * Delete file directory
    * @param file
    * @throws IOException
    */
   public static void deleteFolderDirectory(File file) throws IOException{
    	if (file.isDirectory()) {
				Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			} else {
				file.delete();
		}
   }
}