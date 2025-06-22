package storage.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
                .setVersion("v1")
                .setBucketName("main")
                .setLastModifiedDate(new Date())
                .build();

        repository.saveOrUpdate(file);
        FileObject retrieved = repository.findByName("sample.txt");
        System.out.println("##### Retried file "+retrieved);
        assertNotNull(retrieved);
        assertEquals(file.getFileName(), retrieved.getFileName());
        assertEquals(file.getChecksum(), retrieved.getChecksum());
        assertEquals(file.getVersion(), retrieved.getVersion());
    }

}
