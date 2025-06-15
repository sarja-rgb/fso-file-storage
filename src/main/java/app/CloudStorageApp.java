package app;

import java.io.IOException;

import javax.swing.SwingUtilities;

import util.AwsS3Util;
/**
 * Cloud StorageApp is the main GUI application class. It provides a Swing-based
 * user interface for managing files and folders in a storage directory. Users
 * can upload files, create new folders, delete selected items, and refresh the
 * file list to reflect the current state of the local storage directory.
 */
public class CloudStorageApp  {
	/**
	 * Main method to launch the application.
	 *
	 * @param args Command-line arguments (not used).
	 */
	public static void main(String[] args) {
		try {
			AwsS3Util.initializeEncryptionKey(); // Ensure AES key exists
		} catch (IOException e) {
			System.err.println("Failed to initialize encryption key: " + e.getMessage());
			System.exit(1);
		}
		SwingUtilities.invokeLater(() -> new CloudFileStorageUI().setVisible(true));
	}

}
