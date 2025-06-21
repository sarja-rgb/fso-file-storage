package storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * LocalFileStoreOperations implements FileStoreOperations to manage files locally.
 * It handles saving, deleting, and loading files within a defined root directory.
 */
public class LocalFileStoreOperations implements FileStoreOperations {

    private final File rootDir;

    public LocalFileStoreOperations(String storagePath) {
        this.rootDir = new File(storagePath);
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
    }

    /**
     * Saves a file to the local storage directory.
     */
    @Override
    public void save(File file) throws FileStoreException {
        try {
            File dest = new File(rootDir, file.getName());
            Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStoreException("Failed to save file locally", e);
        }
    }

    /**
     * Saves multiple files to the local storage directory.
     */
    @Override
    public void saveAll(List<File> files) throws FileStoreException {
        for (File file : files) {
            save(file);
        }
    }

    /**
     * Deletes a file from the local storage directory.
     */
    @Override
    public void delete(FileObject fileObject) throws FileStoreException {
        File file = new File(rootDir, fileObject.getFileName());
        if (!file.exists() || !file.delete()) {
            throw new FileStoreException("Failed to delete local file: " + fileObject.getFileName());
        }
    }

    /**
     * Loads all files from the local storage directory.
     * Converts them to FileObject representations.
     */
    @Override
    public List<FileObject> loadAll() throws FileStoreException {
        List<FileObject> fileObjects = new ArrayList<>();
        File[] files = rootDir.listFiles(File::isFile);

        if (files != null) {
            for (File file : files) {
                FileObject obj = FileObject.builder()
                    .setFileName(file.getName())
                    .setFilePath(file.getAbsolutePath())
                    .setFileSize(file.length())
                    .setLastModifiedDate(new Date(file.lastModified()))
                    .build();
                fileObjects.add(obj);
            }
        }
        return fileObjects;
    }
}
