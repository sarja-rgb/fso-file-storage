package app;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import util.FileUtil;

@TestInstance(Lifecycle.PER_CLASS)
public class FileManagerImplUnitTest {
    public static String TEST_FILE_STORAGE_DIR = "test_storage";
    @Mock
    private LocalFileStorageApp mockApp;
    private FileManager fileManager;
    private File rootFolder;

    @BeforeAll
    public  void setup() {
        rootFolder = FileUtil.createFileDirectory("test_storage");
        MockitoAnnotations.openMocks(this);
        fileManager = new FileManagerImpl(mockApp);
    }

    @AfterAll
    public void clean() throws IOException{
       FileUtil.deleteFolderDirectory(rootFolder);
    }

    @Test
    public void testCreateFolder() {
        when(mockApp.getSelectedFilePath()).thenReturn(rootFolder.getPath());
        fileManager.createFolder("mockFolder");
        File folder = new File(rootFolder.getPath() +"/mockFolder");
        assertTrue(folder.exists() && folder.isDirectory());
    }

    @Test
    public void testFailToCreateExistingFolder() {
        when(mockApp.getSelectedFilePath()).thenReturn(rootFolder.getPath());
        fileManager.createFolder("mockFolder1");
        File folder = new File(rootFolder.getPath() +"/mockFolder1");
        assertTrue(folder.exists() && folder.isDirectory());

        fileManager.createFolder("mockFolder1");
        File folder2 = new File(rootFolder.getPath() +"/mockFolder1");
        assertTrue(folder2.exists() && folder2.isDirectory());
    }

    @Test
    public void testGetSelectedFilePath() {
        when(mockApp.getSelectedFilePath()).thenReturn(rootFolder.getPath());
        String path = fileManager.getSelectedFilePath();
        assertEquals("test_storage", path);
    }
}
