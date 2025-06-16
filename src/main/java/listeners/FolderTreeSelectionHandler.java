package listeners;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import app.BaseFileStorageUI;
import util.FileUtil;

/**
 * FolderTreeSelectionHandler handles selection changes in the JTree and updates the file table accordingly.
 */
public class FolderTreeSelectionHandler implements TreeSelectionListener {
    private final BaseFileStorageUI fileStorageUI;

    public FolderTreeSelectionHandler(BaseFileStorageUI fileStorageUI ) {
        this.fileStorageUI = fileStorageUI;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        TreePath path = e.getPath();
        if (path != null) {
            String filePath = FileUtil.createFilePath(path);
            System.out.println("file Path created "+filePath);
            //List<File> files = List.of(new File(filePath));
            // List<FileObject> fileObjects = files.stream().map(f->{
            //                                     return  FileObject.builder().build();
            //                                 }).collect(Collectors.toList());
            // Console.log(e)
            //fileStorageUI.updateFileTable(fileObjects);
        }
    }
}