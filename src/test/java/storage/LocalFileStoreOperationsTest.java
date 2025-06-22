package storage;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalFileStoreOperationsTest {

    private static final String TEST_DIR = "test_folder";
    private LocalFileStoreOperations fileStore;

    @BeforeEach
    public void setup() {
        fileStore = new LocalFileStoreOperations(TEST_DIR);
    }

    private File createTempFile(String fileName, String content) throws Exception {
        File file = new File(TEST_DIR, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    @Test
    public void testSaveFile() throws Exception {
        File file = createTempFile("test1.txt", "Hello World");
        fileStore.save(file);
        File storedFile = new File(TEST_DIR, "test1.txt");
        assertTrue(storedFile.exists());
    }

    @Test
    public void testSaveAllFiles() throws Exception {
        File file1 = createTempFile("multi1.txt", "File 1");
        File file2 = createTempFile("multi2.txt", "File 2");
        fileStore.saveAll(Arrays.asList(file1, file2));
        assertTrue(new File(TEST_DIR, "multi1.txt").exists());
        assertTrue(new File(TEST_DIR, "multi2.txt").exists());
    }

    @Test
    public void testDeleteFile() throws Exception {
        File file = createTempFile("delete_me.txt", "To be deleted");
        fileStore.save(file);
        FileObject fileObject = FileObject.builder().setFileName("delete_me.txt").build();
        fileStore.delete(fileObject);
        assertFalse(new File(TEST_DIR, "delete_me.txt").exists());
    }

    @Test
    public void testLoadAllFiles() throws Exception {
        File file = createTempFile("load.txt", "Load this file");
        fileStore.save(file);
        List<FileObject> loadedFiles = fileStore.loadAll();
        assertFalse(loadedFiles.isEmpty());
        assertTrue(loadedFiles.stream().anyMatch(f -> f.getFileName().equals("load.txt")));
    }

    @AfterEach
    public void cleanup() {
        File dir = new File(TEST_DIR);
        for (File file : dir.listFiles()) {
            file.delete();
        }

        dir.delete();

    }
}
