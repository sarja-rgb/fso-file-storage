package storage;

import java.io.File;
import java.util.List;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * S3StorageManager uploads, downloads, and synchronizes local files to an AWS S3 bucket.
 */
public class S3StorageManager {
    private final AmazonS3 s3Client;
    private final String bucketName;

    public S3StorageManager(String accessKey, String secretKey, String region, String bucketName) {
        this.bucketName = bucketName;
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
    }

    /**
     * Uploads a file to the specified S3 bucket.
     *
     * @param keyName  the S3 object key (file path in the bucket)
     * @param filePath the local file path
     */
    public void uploadFile(String keyName, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        PutObjectRequest request = new PutObjectRequest(bucketName, keyName, file);
        s3Client.putObject(request);
    }

    /**
     * Deletes a file from the S3 bucket.
     *
     * @param keyName the S3 object key to delete
     */
    public void deleteFile(String keyName) {
        s3Client.deleteObject(bucketName, keyName);
    }

    /**
     * Lists all files in the S3 bucket.
     */
    public void listFiles() {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("- " + os.getKey() + " (" + os.getSize() + " bytes)");
        }
    }

    /**
     * Downloads a file from the S3 bucket to a local path.
     *
     * @param keyName     the S3 object key to download
     * @param localTarget the target file path on local system
     */
    public void downloadFile(String keyName, String localTarget) {
        S3Object s3Object = s3Client.getObject(bucketName, keyName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            java.nio.file.Files.copy(inputStream, new File(localTarget).toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file: " + e.getMessage(), e);
        }
    }

    /**
     * Syncs local folder contents to the S3 bucket (upload only).
     *
     * @param localDir the local directory to sync
     */
    public void syncLocalToS3(File localDir) {
        if (!localDir.exists() || !localDir.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + localDir);
        }
        for (File file : localDir.listFiles()) {
            if (file.isFile()) {
                String key = file.getName();
                uploadFile(key, file.getAbsolutePath());
            } else if (file.isDirectory()) {
                syncLocalToS3(file); // Recursively sync subdirectories
            }
        }
    }
}
