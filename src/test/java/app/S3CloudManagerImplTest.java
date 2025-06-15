package app;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import storage.FileObject;
import storage.FileStoreException;
import storage.FileStoreOperations;

public class S3CloudManagerImplTest {

    private BaseFileStorageUI mockUI;
    private FileStoreOperations mockOperations;
    private S3CloudManagerImpl manager;

    @BeforeEach
    public void setUp() {
        mockUI = mock(BaseFileStorageUI.class);
        mockOperations = mock(FileStoreOperations.class);
        manager = new S3CloudManagerImpl(mockUI, mockOperations);
        // Simulate getComponent() to avoid null in JFileChooser call
        when(mockUI.getComponent()).thenReturn(mock(JComponent.class));
    }

    @Test
    public void testDeleteSelectedFile_Success() throws FileStoreException {
        FileObject fileObject = FileObject.builder().setFileName("test.txt").build();

        manager.deleteSelectedFile(fileObject);

        verify(mockOperations).delete(fileObject);
        verify(mockUI).updateFileTable(any());
    }

    @Test
    public void testDeleteSelectedFile_NullFile() {
        manager.deleteSelectedFile(null);
        verify(mockUI).showAlertMessage("No file selected.");
        verifyNoInteractions(mockOperations);
    }

    @Test
    public void testDeleteSelectedFile_Failure() throws FileStoreException {
        FileObject fileObject = FileObject.builder().setFileName("fail.txt").build();
        doThrow(new FileStoreException("delete failed")).when(mockOperations).delete(fileObject);

        manager.deleteSelectedFile(fileObject);

        verify(mockUI).showAlertMessage("Error deleting file");
    }

    @Test
    public void testListFiles_Success() throws FileStoreException {
        List<FileObject> files = Arrays.asList(
            FileObject.builder().setFileName("f1.txt").build(),
            FileObject.builder().setFileName("f2.txt").build()
        );

        when(mockOperations.loadAll()).thenReturn(files);

        manager.listFiles();

        verify(mockUI).updateFileTable(files);
    }

    @Test
    public void testListFiles_Failure() throws FileStoreException {
        when(mockOperations.loadAll()).thenThrow(new FileStoreException("load error"));

        manager.listFiles();

        // Should not throw, only log
        verify(mockUI, never()).updateFileTable(any());
    }

    @Test
    public void testGetSelectedFile() {
        FileObject file = FileObject.builder().setFileName("sample.txt").build();
        when(mockUI.getSelectedFile()).thenReturn(file);

        FileObject result = manager.getSelectedFile();

        assertEquals("sample.txt", result.getFileName());
    }
}
