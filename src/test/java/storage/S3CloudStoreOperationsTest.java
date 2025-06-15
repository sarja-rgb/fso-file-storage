
package storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
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
    public void testSaveFile() {
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
    public void testDeleteFile() {
        File file = new File("delete-me.txt");
        s3CloudStoreOperations.delete(file);

        verify(mockS3Client).deleteObject(mockCredential.getBucketName(), "delete-me.txt");
    }

    @Test
    public void testSaveAllSkipsNonExistentDir() {
        File dir = mock(File.class);
        when(dir.exists()).thenReturn(false);
        when(dir.isDirectory()).thenReturn(true);

        s3CloudStoreOperations.saveAll(dir);

        verify(mockS3Client, never()).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testLoadAll() {
        ListObjectsV2Result result = mock(ListObjectsV2Result.class);
        List<S3ObjectSummary> summaries = new ArrayList<>();
        S3ObjectSummary summary = new S3ObjectSummary();
        summary.setKey("file1.txt");
        summary.setSize(123);
        summaries.add(summary);

        when(result.getObjectSummaries()).thenReturn(summaries);
        when(mockS3Client.listObjectsV2(mockCredential.getBucketName())).thenReturn(result);

        s3CloudStoreOperations.loadAll();

        verify(mockS3Client).listObjectsV2(mockCredential.getBucketName());
    }
}
