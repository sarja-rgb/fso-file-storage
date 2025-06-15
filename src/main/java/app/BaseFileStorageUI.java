package app;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.tree.DefaultMutableTreeNode;

import storage.FileObject;

/**
 * Interface defining the base contract for any File Storage UI.
 * This allows FileManager, tests, or listeners to interact with the UI
 * without depending on the concrete Swing implementation.
 */
public interface BaseFileStorageUI {
    /**
     * Returns the absolute path of the selected file or folder.
     * This is used by file operation logic to target the appropriate path.
     *
     * @return the absolute path of the selected file/folder or null if not selected.
     */
    public String getSelectedFilePath();

    /**
     * Updates the file table (right panel) with a list of files/folders.
     *
     * @param files the list of files and folders to display.
     */
    public void updateFileTable(List<FileObject> files);

    /**
     * Updates and reloads the folder tree (left panel).
     */
    public void updateFolderTree();

    /**
     * Returns the JTable component used to display files and folders.
     * Useful for adding listeners or UI extensions.
     *
     * @return JTable instance representing the file table.
     */
    public JTable getFileTable();

    /**
     * Get base UI component
     * @return
     */
    public Component getComponent();
    /**
     * Updates the currently selected node in the folder tree.
     * This is used when the user selects a new folder in the UI.
     *
     * @param selectedTreeNode the selected tree node in the JTree
     */
    public void setSelectedTreeNode(DefaultMutableTreeNode selectedTreeNode);

    /**
     * Display Alert message
     * @param message
     */
    public void showAlertMessage(String message);


    public void showCloudAccountDialog();
}
