package storage;

import java.io.File;
import java.util.List;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * S3CloudStorage implements the CloudStorage interface and handles file operations
 * to AWS S3, such as upload, delete, and sync. This implementation provides
 * encrypted credential storage and integrates with a Swing UI for login.
 */
public class S3CloudStorage implements CloudStorage {
    private AmazonS3 s3Client;
    private AwsS3Credential awsS3Credential;

    public S3CloudStorage(AwsS3Credential awsS3Credential) {
        this.awsS3Credential = awsS3Credential;
        init();
    }

     public S3CloudStorage(AmazonS3 s3Client, AwsS3Credential awsS3Credential) {
       this.s3Client = s3Client;
       this.awsS3Credential = awsS3Credential;
    }

    private void init() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsS3Credential.getAccessKey(), awsS3Credential.getSecretKey());
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(awsS3Credential.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();

    }

    @Override
    public void save(File file) {
        if (!file.exists()) return;
        PutObjectRequest request = new PutObjectRequest(awsS3Credential.getBucketName(), file.getName(), file);
        s3Client.putObject(request);
    }

    @Override
    public void delete(File file) {
        if (file == null || file.getName().isEmpty()) return;
        s3Client.deleteObject(awsS3Credential.getBucketName(), file.getName());
    }

    @Override
    public void saveAll(File localDir) {
        if (!localDir.exists() || !localDir.isDirectory()) return;
        for (File file : localDir.listFiles()) {
            if (file.isFile()) {
                save(file);
            } else if (file.isDirectory()) {
                saveAll(file); // Recursively sync
            }
        }
    }

    @Override
    public void loadAll() {
        ListObjectsV2Result result = s3Client.listObjectsV2(awsS3Credential.getBucketName());
        System.err.println("loadAll result "+result);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("- " + os.getKey() + " (" + os.getSize() + " bytes)");
        }
    }

    public AmazonS3 getAwsS3Client(){
        return s3Client;
    }
}
