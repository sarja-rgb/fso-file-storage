package storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import handles.S3ClientHandle;
import util.AwsS3Util;
import util.FileUtil;

/**
 * S3CloudStoreOperations handles file operations on AWS S3 using the Amazon S3 SDK.
 * 
 * It implements FileStoreOperations for file upload, delete, list, and sync
 * and also implements S3ClientHandle to manage client connection and credential handling.
 * 
 * It supports saving encrypted credentials to local storage and reading them back securely.
 */
public class S3CloudStoreOperations implements FileStoreOperations, S3ClientHandle {
    private static final Logger logger = LogManager.getLogger(S3CloudStoreOperations.class);
    private AmazonS3 s3Client;
    private AwsS3Credential awsS3Credential;

    /**
     * Default constructor; attempts to initialize with stored credentials.
     */
    public S3CloudStoreOperations() {
        this(null);
    }

    /**
     * Constructor with explicit credentials.
     * 
     * @param awsS3Credential AWS credentials including access key, secret key, region, and bucket name
     */
    public S3CloudStoreOperations(AwsS3Credential awsS3Credential) {
        this.awsS3Credential = awsS3Credential;
        init();
    }

    /**
     * Constructor used for injecting a mocked or existing AmazonS3 client.
     * 
     * @param s3Client an initialized AmazonS3 instance
     * @param awsS3Credential AWS credentials
     */
    public S3CloudStoreOperations(AmazonS3 s3Client, AwsS3Credential awsS3Credential) {
        this.s3Client = s3Client;
        this.awsS3Credential = awsS3Credential;
    }

    /**
     * Attempts to initialize the S3 client by loading encrypted credentials and connecting.
     */
    private void init() {
        try {
            if(awsS3Credential != null){
              connectAwsS3Client(awsS3Credential);
            }
            else{
              connectAwsS3Client();
            }
        } catch (AmazonS3Exception ex) {
           logger.error("Error loading credentials:  {}" , ex.getMessage());
        }
    }

    /**
     * Upload a single file to S3.
     * 
     * @param file file to upload
     * @throws FileStoreException on failure or credential error
     */
    @Override
    public FileObject save(File file) throws FileStoreException {
        try {
            PutObjectRequest request = new PutObjectRequest(awsS3Credential.getBucketName(), file.getName(), file);
            PutObjectResult objectResult = s3Client.putObject(request);
            Date modifiedDate = (objectResult != null && objectResult.getMetadata() != null && 
                                 objectResult.getMetadata().getLastModified() != null)? 
                                 objectResult.getMetadata().getLastModified() : new Date();
             
            String version = (objectResult != null && objectResult.getVersionId() != null)? 
                                objectResult.getVersionId(): "1";
            String checkSum = (objectResult != null)? objectResult.getETag(): "";
            return FileObject.builder()
                            .setFileName(file.getName())
                            .setLastModifiedDate(modifiedDate)
                            .setBucketName(awsS3Credential.getBucketName())
                            .setFilePath(file.getAbsolutePath())
                            .setFileSize(file.getFreeSpace())
                            .setVersion(version)
                            .setCheckSum(checkSum)
                            .build();
        } catch (NullPointerException | AmazonServiceException ex) {
            logger.error("Failed to save data to S3 cloud storage {}" , ex.getMessage());
            throw new FileStoreException("Failed to save AWS S3 object. Check your credentials", ex);
        }
    }

    /**
     * Delete a file object from the S3 bucket.
     * 
     * @param fileObject object representing the S3 file
     * @throws FileStoreException on failure
     */
    @Override
    public void delete(FileObject fileObject) throws FileStoreException {
        if (fileObject == null || fileObject.getFileName().isEmpty()) return;
        try {
            s3Client.deleteObject(awsS3Credential.getBucketName(), fileObject.getFileName());
        } catch (AmazonServiceException ex) {
            logger.error("Failed to delete file on AWS S3 cloud storage {}" , ex.getMessage());
            throw new FileStoreException("Failed to remove AWS S3 object. Check your credentials", ex);
        }
    }

    /**
     * Recursively upload all files the S3 bucket.
     * 
     * @param files list of files to upload
     * @throws FileStoreException on failure
     */
    @Override
    public void  saveAll(List<File> files) throws FileStoreException {
        for (File file : files) {
            if (file.isFile()) {
                save(file);
            }
        }
    }

    /**
     * Load and list all files from the S3 bucket.
     * 
     * @return list of FileObject representations from the bucket
     * @throws FileStoreException on failure
     */
    @Override
    public List<FileObject> loadAll() throws FileStoreException {
        try {
            ListObjectsV2Result result = s3Client.listObjectsV2(awsS3Credential.getBucketName());
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            return objects.stream().map(object -> FileObject.builder()
                    .setFileName(object.getKey())
                    .setBucketName(object.getBucketName())
                    .setFileSize(object.getSize())
                    .setLastModifiedDate(object.getLastModified())
                    .setCheckSum(object.getETag())
                    .build()).collect(Collectors.toList());

        } catch (NullPointerException | AmazonServiceException ex) {
            logger.error("Failed load files AWS S3 cloud storage {}" , ex.getMessage());
            throw new FileStoreException("AWS Credentials error. Ensure credentials are configured correctly.", ex);
        }
    }

    /**
     * Loads credentials and connects to AWS S3.
     * 
     * @return connected AmazonS3 client
     * @throws AmazonS3Exception on failure
     */
    @Override
    public AmazonS3 connectAwsS3Client() throws AmazonS3Exception {
        try {
            awsS3Credential = AwsS3Util.loadCredential();
        } catch (IOException ex) {
             logger.error("Error loading AWS credentials: {}" , ex.getMessage());
        }
        s3Client = connectAwsS3Client(awsS3Credential);
        return s3Client;
    }

    /**
     * Connects using provided AWS credentials.
     * 
     * @param s3Credential user-provided credential object
     * @return connected AmazonS3 client
     */
    @Override
    public AmazonS3 connectAwsS3Client(AwsS3Credential s3Credential) throws AmazonS3Exception {
        if (s3Credential != null) {
            this.awsS3Credential = s3Credential;
            this.s3Client = createAmazonS3Client(awsS3Credential);
        }
        return s3Client;
    }

    /**
     * Return the currently connected AmazonS3 client instance.
     */
    @Override
    public AmazonS3 getAwsS3Client() {
        return s3Client;
    }

    /**
     * Return the credentials currently used for S3 connection.
     */
    @Override
    public AwsS3Credential getAwsS3Credential() {
        return awsS3Credential;
    }

    /**
     * Creates and configures the AmazonS3 client using credentials.
     * 
     * @param s3Credential AWS credential object
     * @return AmazonS3 client
     * @throws AmazonS3Exception on failure
     */
    private AmazonS3 createAmazonS3Client(AwsS3Credential s3Credential) throws AmazonS3Exception {
        try {
            BasicAWSCredentials awsCreds = new BasicAWSCredentials(
                    s3Credential.getAccessKey(),
                    s3Credential.getSecretKey()
            );
            return AmazonS3ClientBuilder.standard()
                    .withRegion(s3Credential.getRegion())
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();
        } catch (Exception ex) {
            logger.error("Failed to create AWS credentials. Error {}" , ex.getMessage());
            throw new AmazonS3Exception("Failed to create AWS S3 client", ex);
        }
    }

    /**
     * Download S3 object to local directory
     */
    @Override
    public File downloadFile(FileObject fileObject) throws FileStoreException {
      try {          
            String filename = fileObject.getFileName();
            S3Object s3object = s3Client.getObject(new GetObjectRequest(awsS3Credential.getBucketName(),filename));
            if(!Files.exists(Paths.get(FileUtil.LOCAL_STORAGE_DIR))) {
            	FileUtil.createFileDirectory(FileUtil.LOCAL_STORAGE_DIR);
            }
            Path downloadPath = Paths.get(FileUtil.LOCAL_STORAGE_DIR, filename);
            File downloadFile = downloadPath.toFile();    
            try (OutputStream outputStream = new FileOutputStream(downloadFile)) {
                //ensure the parent directory folder is created
                downloadFile.getParentFile().mkdirs();
                // Create local file
                try (InputStream inputStream = s3object.getObjectContent()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
            logger.info("Download successful:{} " + downloadFile.getAbsolutePath());
            return downloadFile;
        } catch (Exception ex) {
            logger.error("Failed to create AWS credentials. Error {}" , ex.getMessage());
            throw new FileStoreException();
        }
    }
}
