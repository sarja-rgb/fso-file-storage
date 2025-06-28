package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import storage.FileObject;
import storage.db.FileMetadataRepository;
import util.FileEventExceptions;

/**
 * FileEventListener implementation that persists file events
 * using a SQL-based file metadata repository.
 */
public class SqlFileEventListener implements FileEventListener {
    private static final Logger logger = LogManager.getLogger(SqlFileEventListener.class);
    private final FileMetadataRepository fileMetadataRepository;

    public SqlFileEventListener(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    /**
     * Handles saving a new file into the metadata store.
     */
    @Override
    public void onSave(FileObject fileObject) throws FileEventExceptions {
        try {
            fileMetadataRepository.saveOrUpdate(fileObject);
        } catch (Exception ex) {
            logger.error("Failed to save file metadata, error: {}",ex.getMessage());
            throw new FileEventExceptions("Failed to save file metadata", ex);
        }
    }

    /**
     * Handles updating a file entry if it exists.
     */
    @Override
    public void onUpdate(FileObject fileObject) throws FileEventExceptions {
        try {
            if (!fileMetadataRepository.exists(fileObject.getFileName())) {
                throw new FileEventExceptions("File not found for update: " + fileObject.getFileName());
            }
            fileMetadataRepository.saveOrUpdate(fileObject);
        } catch (Exception ex) {
            logger.error("Failed to update file metadata , error: {}",ex.getMessage());
            throw new FileEventExceptions("Failed to update file metadata", ex);
        }
    }

    /**
     * Handles deleting a file from the metadata store.
     */
    @Override
    public void onDelete(FileObject fileObject) throws FileEventExceptions {
        try {
            if (!fileMetadataRepository.exists(fileObject.getFileName())) {
                throw new FileEventExceptions("File not found for deletion: " + fileObject.getFileName());
            }
            fileMetadataRepository.delete(fileObject.getFileName());
        } catch (Exception ex) {
            logger.error("Failed to delete file metadata, error: {}",ex.getMessage());
            throw new FileEventExceptions("Failed to delete file metadata", ex);
        }
    }
}
