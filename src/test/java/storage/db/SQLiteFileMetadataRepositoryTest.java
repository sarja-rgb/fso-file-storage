package storage.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import storage.FileObject;

/**
 * Unit tests for SQLiteFileMetadataRepository.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SQLiteFileMetadataRepositoryTest {

    private Connection connection;
    private FileMetadataRepository repository;

    @BeforeAll
    public void setup() throws Exception {
        // Use in-memory SQLite DB for testing
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        repository = new SQLiteFileMetadataRepository(connection);
    }

    @AfterEach
    public void cleanup() throws Exception {
        connection.createStatement().execute("DELETE FROM file_metadata");
    }

    @Test
    public void testSaveAndRetrieve() {
        FileObject file = FileObject.builder()
                .setFileName("sample.txt")
                .setFilePath("/data/sample.txt")
                .setFileSize(5120L)
                .setCheckSum("abc123")
                .setBucketName("main")
                .setVersion("1")
                .setLastModifiedDate(new Date())
                .build();

        repository.saveOrUpdate(file);
        FileObject retrieved = repository.findByName("sample.txt");
        System.out.println("##### Retried file "+retrieved);
        assertNotNull(retrieved);
        assertEquals(file.getFileName(), retrieved.getFileName());
        assertEquals(file.getChecksum(), retrieved.getChecksum());
        assertEquals("1", retrieved.getVersion());
    }

      @Test
    public void testSaveAllAndRetrieve() { 
        List<FileObject> fileObjects = new ArrayList<>();
        for(int index = 0; index < 5; index++){
            String fileName = "sample_file "+index + ".txt";
            FileObject file = FileObject.builder()
                                .setFileName(fileName)
                                .setFilePath("/data/"+fileName)
                                .setFileSize(5120L)
                                .setCheckSum("abc123")
                                .setBucketName("main")
                                .setLastModifiedDate(new Date())
                                .setVersion("1")
                                .build();
            fileObjects.add(file);
        }
      
        repository.saveOrUpdateFiles(fileObjects);
        FileObject fileObject = fileObjects.get(0);
        FileObject retrieved = repository.findByName(fileObject.getFileName());
        System.out.println("##### Retried file "+retrieved);
        assertNotNull(retrieved);
        assertEquals(fileObject.getFileName(), retrieved.getFileName());
        assertEquals(fileObject.getChecksum(), retrieved.getChecksum());
        assertEquals("1", retrieved.getVersion());
    }

    @Test
    public void testExistsAndDelete() {
        FileObject file = FileObject.builder()
                .setFileName("delete_me.txt")
                .setFilePath("/data/delete_me.txt")
                .setFileSize(1024L)
                .setCheckSum("delete123")
                .setBucketName("trash")
                .setVersion("1")
                .setLastModifiedDate(new Date())
                .build();

        repository.saveOrUpdate(file);
        assertTrue(repository.exists("delete_me.txt"));

        repository.delete("delete_me.txt");
        assertFalse(repository.exists("delete_me.txt"));
    }

    @Test
    public void testUpdateFile() {
        FileObject original = FileObject.builder()
                .setFileName("update.txt")
                .setFilePath("/data/update.txt")
                .setFileSize(2048L)
                .setCheckSum("oldsum")
                .setBucketName("bucket")
                 .setVersion("1")
                .setLastModifiedDate(new Date())
                .build();

        repository.saveOrUpdate(original);

        FileObject updated = FileObject.builder()
                .setFileName("update.txt")
                .setFilePath("/data/update.txt")
                .setFileSize(2048L)
                .setCheckSum("newsum")
                .setBucketName("bucket")
                 .setVersion("2")
                .setLastModifiedDate(new Date())
                .build();

        repository.saveOrUpdate(updated);

        FileObject retrieved = repository.findByName("update.txt");
        assertEquals("newsum", retrieved.getChecksum());
        assertEquals("2", retrieved.getVersion());
    }

    @Test
    public void testFindAll() {
        FileObject file1 = FileObject.builder()
                .setFileName("file1.txt")
                .setFilePath("/data/file1.txt")
                .setFileSize(2048L)
                .setCheckSum("f1")
                .setBucketName("bucket1")
                .setVersion("1")
                .setLastModifiedDate(new Date())
                .build();

        FileObject file2 = FileObject.builder()
                .setFileName("file2.txt")
                .setFilePath("/data/file2.txt")
                .setFileSize(4096L)
                .setCheckSum("f2")
                .setBucketName("bucket2")
                .setLastModifiedDate(new Date())
                .setVersion("1")
                .build();

        repository.saveOrUpdate(file1);
        repository.saveOrUpdate(file2);

        List<FileObject> allFiles = repository.findAll();
        assertEquals(2, allFiles.size());
        assertTrue(allFiles.stream().anyMatch(f -> f.getFileName().equals("file1.txt")));
        assertTrue(allFiles.stream().anyMatch(f -> f.getFileName().equals("file2.txt")));
    }
}
