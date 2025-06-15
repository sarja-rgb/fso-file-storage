package storage;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import util.AwsS3Util;

/**
 * S3CloudStorage implements the CloudStorage interface and handles file operations
 * to AWS S3, such as upload, delete, and sync. This implementation provides
 * encrypted credential storage and integrates with a Swing UI for login.
 */
public class S3CloudStoreOperations implements FileStoreOperations {
    private AmazonS3 s3Client;
    private AwsS3Credential awsS3Credential;

     public S3CloudStoreOperations(){
        this(null);
    }

    public S3CloudStoreOperations(AwsS3Credential awsS3Credential) {
        this.awsS3Credential = awsS3Credential;
        init();
    }

     public S3CloudStoreOperations(AmazonS3 s3Client, AwsS3Credential awsS3Credential) {
       this.s3Client = s3Client;
       this.awsS3Credential = awsS3Credential;
    }

    private void init() {
        if(awsS3Credential == null){
           awsS3Credential = AwsS3Util.loadCredential();
        }
        connectAwsS3Client();
    }

    private void connectAwsS3Client(){
        try {
          if(awsS3Credential != null){
             BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsS3Credential.getAccessKey(), awsS3Credential.getSecretKey());
             this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(awsS3Credential.getRegion())
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();
            }
        } catch (Exception ex) {
            System.out.println("Error connecting to AWS S3 Client "+ex.getMessage());
        }
  
    }

    @Override
    public void save(File file) {
        try{
            if (!file.exists()) return;
            PutObjectRequest request = new PutObjectRequest(awsS3Credential.getBucketName(), file.getName(), file);
            PutObjectResult objectResult = s3Client.putObject(request);
            
            System.out.println("Save object result ###"+objectResult);
        }
        catch(AmazonServiceException ex){
             ex.printStackTrace();
        }
    }

    @Override
    public void delete(File file) {
        if (file == null || file.getName().isEmpty()) return;
        // s3Client.deleteObject(awsS3Credential.getBucketName(), file.getName());
    }

    @Override
    public List<FileObject> saveAll(File localDir) {
        //if (!localDir.exists() || !localDir.isDirectory()) return;
        for (File file : localDir.listFiles()) {
            if (file.isFile()) {
                save(file);
            } else if (file.isDirectory()) {
                saveAll(file); // Recursively sync
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<FileObject> loadAll() {
        // try {
        //     ListObjectsV2Result result = s3Client.listObjectsV2(awsS3Credential.getBucketName());
        //     List<S3ObjectSummary> objects = result.getObjectSummaries();
        
        //     for (S3ObjectSummary os : objects) {
        //         System.out.println("- " + os.getKey() + " (" + os.getSize() + " bytes)");
        //     }

        //     objects.stream().map(object->{
        //         return  FileObject.builder()
        //         .setFileName(object.getKey())
        //         .setBucketName(object.getBucketName())
        //         .setFileSize(object.getSize())
        //         //.setFileType(object.get)
        //         .build();
        //     });
        // } catch (Exception ex) {
        //     ex.printStackTrace();
        // }
  
        return Collections.emptyList();
    }

    public AmazonS3 getAwsS3Client(){
        return s3Client;
    }
}
