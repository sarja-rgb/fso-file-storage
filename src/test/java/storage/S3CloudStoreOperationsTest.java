
package storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3CloudStoreOperationsTest {
    private AmazonS3 mockS3Client;
    private AwsS3Credential mockCredential;
    private S3CloudStoreOperations s3CloudStoreOperations;

    @BeforeEach
    public void setUp() {
        mockCredential = new AwsS3Credential("mockAccess", "mockSecret", "us-east-1", "mockBucket");
        mockS3Client = mock(AmazonS3.class);
        s3CloudStoreOperations = new S3CloudStoreOperations(mockS3Client,mockCredential);
    }

    @Test
    public void testSaveFile() throws FileStoreException {
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        when(file.getName()).thenReturn("file.txt");

        s3CloudStoreOperations.save(file);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(mockS3Client).putObject(captor.capture());

        PutObjectRequest request = captor.getValue();
        assertEquals(request.getKey(),"file.txt");
        assertEquals(request.getBucketName(),mockCredential.getBucketName());
    }

    @Test
    public void testDeleteFile() throws FileStoreException {
        FileObject fileObject = FileObject.builder()
                                           .setFileName("delete-me.txt")
                                           .build();
        s3CloudStoreOperations.delete(fileObject);

        verify(mockS3Client).deleteObject(mockCredential.getBucketName(), "delete-me.txt");
    }

    @Test
    public void testSaveAllFiles() throws FileStoreException {
        File file1 = mock(File.class);
        when(file1.exists()).thenReturn(true);
        when(file1.getName()).thenReturn("file1.txt");
        when(file1.isFile()).thenReturn(true);

        File file2 = mock(File.class);
        when(file2.exists()).thenReturn(true);
        when(file2.getName()).thenReturn("file2.txt");
        when(file2.isFile()).thenReturn(true);

        s3CloudStoreOperations.saveAll(List.of(file1,file2));

        verify(mockS3Client, times(2)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testLoadAll() throws FileStoreException {
        ListObjectsV2Result result = mock(ListObjectsV2Result.class);
        List<S3ObjectSummary> summaries = new ArrayList<>();
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setKey("file1.txt");
        summary.setSize(123);
        summaries.add(summary);

        when(result.getObjectSummaries()).thenReturn(summaries);
        when(mockS3Client.listObjectsV2(mockCredential.getBucketName())).thenReturn(result);

        List<FileObject> fileObjects = s3CloudStoreOperations.loadAll();

        verify(mockS3Client).listObjectsV2(mockCredential.getBucketName());

        assertTrue(!fileObjects.isEmpty());
    }

     @Test
    public void testDownloadFileSuccess() throws Exception {
        String dummyContent = "File content from mock S3!";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(dummyContent.getBytes());

        S3Object mockS3Object = new S3Object();
        mockS3Object.setObjectContent(inputStream);

        when(mockS3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);
        FileObject testFileObject = FileObject.builder()
                .setFileName("mock-test.txt")
                .build();

        File downloadedFile = s3CloudStoreOperations.downloadFile(testFileObject);
        // Assert
        assertNotNull(downloadedFile);
        assertTrue(downloadedFile.exists());
        String content = Files.readString(downloadedFile.toPath());
        assertEquals(dummyContent, content);

        ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);
        verify(mockS3Client).getObject(requestCaptor.capture());
        assertEquals("mock-test.txt", requestCaptor.getValue().getKey());
        assertEquals(mockCredential.getBucketName(), requestCaptor.getValue().getBucketName());
    }

    @Test
    public void testDownloadThrowsFileStoreException() {
        FileObject testFileObject = FileObject.builder()
                .setFileName("mock-test.txt")
                .build();
        when(mockS3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("S3 error"));

        assertThrows(FileStoreException.class, () -> {
            s3CloudStoreOperations.downloadFile(testFileObject);
        });

    }
}
