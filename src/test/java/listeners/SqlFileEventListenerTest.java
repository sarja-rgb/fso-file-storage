package listeners;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import storage.FileObject;
import storage.db.FileMetadataRepository;
import util.FileEventExceptions;

public class SqlFileEventListenerTest {

    private FileMetadataRepository repository;
    private SqlFileEventListener listener;
    private FileObject sampleFile;

    @BeforeEach
    public void setup() {
        repository = mock(FileMetadataRepository.class);
        listener = new SqlFileEventListener(repository);

        sampleFile = FileObject.builder()
                .setFileName("test.txt")
                .setCheckSum("1233")
                .setFilePath("/tmp/test.txt")
                .build();
    }

    @Test
    public void testOnSave_success() {
        assertDoesNotThrow(() -> listener.onSave(sampleFile));
        verify(repository, times(1)).saveOrUpdate(sampleFile);
    }

    @Test
    public void testOnSave_failure_throwsException() {
        doThrow(new RuntimeException("DB failure")).when(repository).saveOrUpdate(sampleFile);

        FileEventExceptions ex = assertThrows(FileEventExceptions.class, () -> listener.onSave(sampleFile));
        assertTrue(ex.getMessage().contains("Failed to save file metadata"));
    }

    @Test
    public void testOnUpdate_success() {
        when(repository.exists("test.txt")).thenReturn(true);

        assertDoesNotThrow(() -> listener.onUpdate(sampleFile));
        verify(repository).saveOrUpdate(sampleFile);
    }

    // @Test
    // public void testOnUpdate_fileNotFound_throwsException() {
    //     when(repository.exists("test.txt")).thenReturn(false);

    //     FileEventExceptions ex = assertThrows(FileEventExceptions.class, () -> listener.onUpdate(sampleFile));
    //     assertTrue(ex.getMessage().contains("File not found for update"));
    // }

    @Test
    public void testOnUpdate_failure_throwsException() {
        when(repository.exists("test.txt")).thenReturn(true);
        doThrow(new RuntimeException("DB error")).when(repository).saveOrUpdate(sampleFile);

        FileEventExceptions ex = assertThrows(FileEventExceptions.class, () -> listener.onUpdate(sampleFile));
        assertTrue(ex.getMessage().contains("Failed to update file metadata"));
    }

    @Test
    public void testOnDelete_success() {
        when(repository.exists("test.txt")).thenReturn(true);

        assertDoesNotThrow(() -> listener.onDelete(sampleFile));
        verify(repository).delete("test.txt");
    }

    // @Test
    // public void testOnDelete_fileNotFound_throwsException() {
    //     when(repository.exists("test.txt")).thenReturn(false);

    //     FileEventExceptions ex = assertThrows(FileEventExceptions.class, () -> listener.onDelete(sampleFile));
    //     assertTrue(ex.getMessage().contains("File not found for deletion"));
    // }

    @Test
    public void testOnDelete_failure_throwsException() {
        when(repository.exists("test.txt")).thenReturn(true);
        doThrow(new RuntimeException("DB delete error")).when(repository).delete("test.txt");

        FileEventExceptions ex = assertThrows(FileEventExceptions.class, () -> listener.onDelete(sampleFile));
        assertTrue(ex.getMessage().contains("Failed to delete file metadata"));
    }
}
