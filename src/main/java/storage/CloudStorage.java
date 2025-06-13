package storage;

import java.io.File;

/**
 * Base Cloud handle
 */
public interface CloudStorage {
    public void save(File file);

    public void delete(File file);

    public void saveAll(File localDir);

    public void loadAll();
}
