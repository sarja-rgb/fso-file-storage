package app;

import java.awt.Component;
import java.util.List;
import javax.swing.JTable;
import storage.FileObject;

/**
 * Interface defining the base contract for any File Storage UI.
 * This interface decouples the UI behavior from its implementation,
 * allowing back-end logic, listeners, and test utilities to interact
 * with any UI that conforms to this contract.
 */
public interface BaseFileStorageUI {

    /**
     * Returns the currently selected file or folder represented as a FileObject.
     * This method allows external logic to determine the user's selection for operations
     * like open, delete, rename, or upload.
     *
     * @return the selected FileObject or null if no selection is made.
     */
    public FileObject getSelectedFile();

    /**
     * Refreshes and displays the given list of files/folders in the file table component.
     * Typically used when navigating a folder or after sync/upload operations.
     *
     * @param files the list of FileObject instances to be shown in the file table.
     */
    public void updateFileTable(List<FileObject> files);

    /**
     * Refreshes the folder tree component, often used after creating or deleting folders.
     * This ensures that the left-side directory navigation reflects the current state.
     */
    public void updateFolderTree();

    /**
     * Provides access to the JTable component that lists files and folders.
     * This can be used by listeners, decorators, or custom UI actions.
     *
     * @return the JTable instance representing the file list.
     */
    public JTable getFileTable();

    /**
     * Returns the root UI component for embedding this file manager inside another container
     * or frame. This is especially useful when creating reusable or pluggable UI modules.
     *
     * @return the base Swing Component.
     */
    public Component getComponent();

    /**
     * Displays an alert message (e.g. popup, dialog, or banner) to the user.
     * Can be used to show errors, warnings, or success messages in a unified manner.
     *
     * @param message the message content to display.
     */
    public void showAlertMessage(String message);

    /**
     * Opens a dialog for the user to enter or view their cloud account credentials.
     * This may be AWS S3, Azure, Google Cloud, etc., depending on the implementation.
     */
    public void showCloudAccountDialog();
}
