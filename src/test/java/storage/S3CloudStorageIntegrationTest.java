
package storage;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.s3.AmazonS3;

/**
 * Integration test for S3CloudStorage using real AWS credentials and bucket.
 * This will perform live S3 operations: upload, list, and delete.
 */
public class S3CloudStorageIntegrationTest {
    private static AwsS3Credential awsS3Credential;
    private static S3CloudStoreOperations s3CloudStoreOperations;
    private static File tempFile;
    private static final String TEST_FILE_NAME = "s3_test_file.txt";

    @BeforeAll
    public static void setup() throws Exception {
        String accessKey = System.getProperty("aws.accessKey");
        String secretKey = System.getProperty("aws.secretKey");
        String region = System.getProperty("aws.region");
        String bucketName = System.getProperty("aws.bucketName");
        awsS3Credential = new AwsS3Credential();
        awsS3Credential.setAccessKey(accessKey);
        awsS3Credential.setSecretKey(secretKey);
        awsS3Credential.setRegion(region);
        awsS3Credential.setBucketName(bucketName);
        s3CloudStoreOperations = new S3CloudStoreOperations(awsS3Credential);
        tempFile = new File(TEST_FILE_NAME);
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Integration test content.");
        }
    }

    @Test
    @Order(1)
    public void testUploadFile() throws FileStoreException {
        s3CloudStoreOperations.save(tempFile);
        AmazonS3 s3Client = s3CloudStoreOperations.getAwsS3Client();
        assertTrue(s3Client.doesObjectExist(awsS3Credential.getBucketName(), TEST_FILE_NAME));
        assertFalse(s3Client.doesObjectExist(awsS3Credential.getBucketName(), "dummy-file.txt"));
    }

    @Test
    @Order(2)
    public void testListFiles() throws FileStoreException {
        List<FileObject> fileObjects = s3CloudStoreOperations.loadAll();
        assertTrue(!fileObjects.isEmpty());
    }

    @Test
    @Order(3)
    public void testDownloadFileSuccess() throws Exception {
        FileObject fileObject = FileObject.builder()
                                .setFileName(TEST_FILE_NAME)
                                .build();
        File downloadedFile = s3CloudStoreOperations.downloadFile(fileObject);
        assertNotNull(downloadedFile);
        assertTrue(downloadedFile.exists());
    }


    @Test
    @Order(4)
    public void testDownloadNonExistingFileFails() {
        FileObject fileObject = FileObject.builder()
                .setFileName("non-existent-file.txt")
                .build();

        assertThrows(FileStoreException.class, () -> {
            s3CloudStoreOperations.downloadFile(fileObject);
        });
    }

    @Test
    @Order(5)
    public void testDeleteFile() throws FileStoreException {
        FileObject fileObject = FileObject.builder().setFileName(TEST_FILE_NAME).build();
        s3CloudStoreOperations.delete(fileObject);
        AmazonS3 s3Client = s3CloudStoreOperations.getAwsS3Client();
        assertFalse(s3Client.doesObjectExist(awsS3Credential.getBucketName(), fileObject.getFileName()));
    }

  

    @AfterAll
    public static void cleanup() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }
}
