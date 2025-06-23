package handles;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import storage.FileObject;
import storage.FileStoreException;
import storage.FileStoreOperations;
import storage.db.FileMetadataRepository;

public class S3LocalFileSyncHandleTest {

    private FileMetadataRepository fileMetadataRepository;
    private FileStoreOperations fileStoreOperations;
    private S3LocalFileSyncHandle syncHandle;

    private FileObject localFile;
    private FileObject remoteFileNewer;
    private FileObject unmatchedRemoteFile;

    @BeforeEach
    public void setUp() {
        fileMetadataRepository = mock(FileMetadataRepository.class);
        fileStoreOperations = mock(FileStoreOperations.class);
        syncHandle = new S3LocalFileSyncHandle(fileMetadataRepository, fileStoreOperations);

        // Local file
        localFile = FileObject.builder()
                .setFileName("example.txt")
                .setFilePath("/local/example.txt")
                .setFileSize(100)
                .setCheckSum("abc123")
                .setVersion("1")
                .setBucketName("local")
                .setLastModifiedDate(new Date(System.currentTimeMillis() - 10000))
                .build();

        // Newer remote file (conflict)
        remoteFileNewer = FileObject.builder()
                .setFileName("example.txt")
                .setFilePath("/cloud/example.txt")
                .setFileSize(100)
                .setCheckSum("def456")
                .setVersion("1")
                .setBucketName("cloud")
                .setLastModifiedDate(new Date())
                .build();

        // File present only in cloud
        unmatchedRemoteFile = FileObject.builder()
                .setFileName("only_in_s3.txt")
                .setFilePath("/cloud/only_in_s3.txt")
                .setFileSize(200)
                .setCheckSum("xyz789")
                .setVersion("1")
                .setBucketName("cloud")
                .setLastModifiedDate(new Date())
                .build();
    }

    @Test
    public void testSyncFiles_ConflictDetectedAndResolved() {
        when(fileMetadataRepository.findByName("example.txt")).thenReturn(localFile);

        syncHandle.syncFiles(List.of(remoteFileNewer));

        verify(fileMetadataRepository, times(1)).saveOrUpdate(remoteFileNewer);
        List<FileObject> conflicts = syncHandle.getConflictedFiles();
        assertEquals(1, conflicts.size());
        assertTrue(conflicts.contains(remoteFileNewer));
    }

    @Test
    public void testSyncFiles_FileMissingInLocalAddedToUnresolved() throws FileStoreException {
        // Cloud has the file, repo has nothing
        when(fileStoreOperations.loadAll()).thenReturn(List.of(remoteFileNewer));
        when(fileMetadataRepository.findByName("only_in_s3.txt")).thenReturn(null);
        when(fileMetadataRepository.findAll()).thenReturn(List.of());
        List<FileObject> unresolved = syncHandle.unResolveFiles();
        assertTrue(unresolved.stream().anyMatch(f -> f.getFileName().equals(remoteFileNewer.getFileName())));
    }

    @Test
    public void testUnresolveFiles_ReturnsMissingAndMismatchedFiles() throws FileStoreException {
        FileObject cloudOnly = unmatchedRemoteFile;

        FileObject outdatedLocal = FileObject.builder()
                .setFileName("example.txt")
                .setFilePath("/local/example.txt")
                .setFileSize(100)
                .setCheckSum("abc123")
                .setVersion("1")
                .setBucketName("local")
                .setLastModifiedDate(new Date(System.currentTimeMillis() - 50000))
                .build();

        when(fileStoreOperations.loadAll()).thenReturn(List.of(cloudOnly, remoteFileNewer));
        when(fileMetadataRepository.findByName("only_in_s3.txt")).thenReturn(null);
        when(fileMetadataRepository.findByName("example.txt")).thenReturn(outdatedLocal);
        when(fileMetadataRepository.findAll()).thenReturn(List.of(outdatedLocal));

        List<FileObject> unresolved = syncHandle.unResolveFiles();

        assertEquals(2, unresolved.size());
        assertTrue(unresolved.contains(cloudOnly));
        assertTrue(unresolved.contains(outdatedLocal));
    }
}
