package storage;

import java.io.File;
import java.util.List;

/**
 * Base File Storage Operations interface
 */
public interface FileStoreOperations {
    public void save(File file);

    public void delete(File file);

    public List<FileObject> saveAll(File localDir);

    public List<FileObject> loadAll();
}
