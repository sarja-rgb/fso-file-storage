package storage;

import java.io.File;
import java.util.List;

/**
 * Base File Storage Operations interface
 */
public interface FileStoreOperations {
    public void save(File file) throws FileStoreException;
    public void saveAll(List<File> files) throws FileStoreException;
    public void delete(FileObject fileObject) throws FileStoreException;
    public List<FileObject> loadAll() throws FileStoreException;
}
