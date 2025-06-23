package listeners;

import storage.FileObject;
import storage.db.FileMetadataRepository;
import util.FileEventExceptions;

/**
 * FileEventListener implementation that persists file events
 * using a SQL-based file metadata repository.
 */
public class SqlFileEventListener implements FileEventListener {

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
        } catch (Exception e) {
            throw new FileEventExceptions("Failed to save file metadata", e);
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
        } catch (Exception e) {
            throw new FileEventExceptions("Failed to update file metadata", e);
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
        } catch (Exception e) {
            throw new FileEventExceptions("Failed to delete file metadata", e);
        }
    }
}
