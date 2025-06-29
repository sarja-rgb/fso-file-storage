package app;

import java.io.IOException;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import util.AwsS3Util;
/**
 * Cloud StorageApp is the main GUI application class. It provides a Swing-based
 * user interface for managing files and folders in a storage directory. Users
 * can upload files, create new folders, delete selected items, and refresh the
 * file list to reflect the current state of the local storage directory.
 */
public class CloudStorageApp  {
	private static final Logger logger = LogManager.getLogger(CloudStorageApp.class);

	/**
	 * Main method to launch the application.
	 *
	 * @param args Command-line arguments (not used).
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		try {
			AwsS3Util.initializeEncryptionKey(); // Ensure AES key exists
		} catch (IOException e) {
			logger.error("Failed to initialize encrypted AWS credentials {}" , e.getMessage());
			System.exit(1);
		}
		SwingUtilities.invokeLater(() -> new CloudFileStorageUI().setVisible(true));
	}

}
