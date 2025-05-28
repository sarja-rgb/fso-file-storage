package listeners;

import app.LocalFileStorageApp;
import java.io.File;
import java.util.List;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import util.FileUtil;

/**
 * FolderTreeSelectionHandler handles selection changes in the JTree and updates the file table accordingly.
 */
public class FolderTreeSelectionHandler implements TreeSelectionListener {

    private final LocalFileStorageApp app;

    public FolderTreeSelectionHandler(LocalFileStorageApp app) {
        this.app = app;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        TreePath path = e.getPath();

        if (path != null) {
            String filePath = FileUtil.createFilePath(path);
            File file = new File(filePath);
            app.updateFileTable(List.of(file));
        }

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        if(selectedNode != null){
            app.setSelectedTreeNode(selectedNode);
        }
    }
}