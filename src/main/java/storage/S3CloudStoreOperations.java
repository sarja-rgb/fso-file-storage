package storage;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import util.AwsS3Util;

/**
 * S3CloudStorage implements the CloudStorage interface and handles file operations
 * to AWS S3, such as upload, delete, and sync. This implementation provides
 * encrypted credential storage and integrates with a Swing UI for login.
 */
public class S3CloudStoreOperations implements FileStoreOperations, S3ClientHandle {
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
        try {
          connectAwsS3Client();
        } catch (AmazonS3Exception ex) {
           System.out.println("Error loading credentials "+ex.getMessage());
        }
    }

    @Override
    public void save(File file) throws FileStoreException{
        try{
            if (!file.exists()) return;
            PutObjectRequest request = new PutObjectRequest(awsS3Credential.getBucketName(), file.getName(), file);
            PutObjectResult objectResult = s3Client.putObject(request);       
            System.out.println("Save object result ###"+objectResult);
        }
        catch(NullPointerException | AmazonServiceException ex){
             throw new FileStoreException("Failed to save AWS S3 Object. Check your credentials",ex);   
        }
    }

    @Override
    public void delete(FileObject fileObject) throws FileStoreException{
        if (fileObject == null || fileObject.getFileName().isEmpty()){
            return;
        }
        try {
            s3Client.deleteObject(awsS3Credential.getBucketName(), fileObject.getFileName()); 
        } catch (AmazonServiceException ex) {
             throw new FileStoreException("Failed to Remove AWS S3 Object. Check your credentials",ex); 
        }
    }

    @Override
    public List<FileObject> saveAll(File localDir) throws FileStoreException{
        for (File file : localDir.listFiles()) {
            if (file.isFile()) {
                save(file);
            } else if (file.isDirectory()) {
                saveAll(file);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<FileObject> loadAll() throws FileStoreException{
        try {
                ListObjectsV2Result result = s3Client.listObjectsV2(awsS3Credential.getBucketName());
                List<S3ObjectSummary> objects = result.getObjectSummaries();
                return objects.stream().map(object->{
                    return  FileObject.builder()
                    .setFileName(object.getKey())
                    .setBucketName(object.getBucketName())
                    .setFileSize(object.getSize())
                    .setLastModifiedDate(object.getLastModified())
                    //.setFileType(object.get)
                    .build();
                }).collect(Collectors.toList());
        }
        catch( NullPointerException | AmazonServiceException ex){
            throw new FileStoreException("AWS Credentials Error. Make sure AWS credentials are configured correctly.",ex);
        }  
    }

    @Override
    public AmazonS3 connectAwsS3Client() throws AmazonS3Exception{
        try {
           awsS3Credential = AwsS3Util.loadCredential();
        } catch (IOException ex) {
           System.out.println("Error loading to AWS credentials "+ex.getMessage());
        }
        s3Client = connectAwsS3Client(awsS3Credential);
        return s3Client;
    }

    @Override
    public AmazonS3 connectAwsS3Client(AwsS3Credential s3Credential) throws AmazonS3Exception {
        if(s3Credential != null){
            this.awsS3Credential = s3Credential;
            this.s3Client = createAmazonS3Client(awsS3Credential);
        }
        return s3Client;
    }

    @Override
    public AmazonS3 getAwsS3Client(){
        return s3Client;
    }

    @Override
    public AwsS3Credential getAwsS3Credential() {
        return awsS3Credential;
    }

    private AmazonS3 createAmazonS3Client(AwsS3Credential s3Credential) throws AmazonS3Exception{
        try {
            System.out.print("createAmazonS3Client->s3Credential "+s3Credential);
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(s3Credential.getAccessKey(), s3Credential.getSecretKey());
            return AmazonS3ClientBuilder.standard()
                       .withRegion(s3Credential.getRegion())
                        .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                        .build();
        } catch (Exception ex) {
            throw new AmazonS3Exception("Failed to create AWS S3 Client",ex);
        }
    }

}
