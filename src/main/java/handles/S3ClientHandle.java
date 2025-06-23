package handles;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import storage.AwsS3Credential;

/**
 * S3ClientHandle defines the contract for managing AWS S3 client connections.
 * 
 * This interface provides methods for establishing a connection to the AWS S3 service,
 * retrieving the connected AmazonS3 client, and accessing the associated AWS credentials.
 * It abstracts the connection logic so that implementing classes can handle credential
 * storage, authentication, and client instantiation in a consistent and reusable way.
 */
public interface S3ClientHandle {

    /**
     * Connects to AWS S3 using internal or default credentials.
     * Implementations of this method are expected to initialize and return a valid AmazonS3 client.
     * 
     * @return AmazonS3 client instance connected to AWS
     * @throws AmazonS3Exception if the connection fails due to invalid credentials or configuration
     */
    public AmazonS3 connectAwsS3Client() throws AmazonS3Exception;

    /**
     * Connects to AWS S3 using the provided AwsS3Credential object.
     * This allows dynamic connections using user-supplied or runtime-loaded credentials.
     *
     * @param awsS3Credential the AWS access key, secret key, region, and bucket info
     * @return AmazonS3 client instance connected to AWS with the provided credentials
     * @throws AmazonS3Exception if connection fails due to invalid credentials or access errors
     */
    public AmazonS3 connectAwsS3Client(AwsS3Credential awsS3Credential) throws AmazonS3Exception;

    /**
     * Returns the current AmazonS3 client instance.
     * If the client is not connected, this may return null or throw an exception based on implementation.
     *
     * @return the currently connected AmazonS3 client
     */
    public AmazonS3 getAwsS3Client();

    /**
     * Returns the AWS credential object associated with the current connection.
     * This may be used for inspection or validation of the current session credentials.
     *
     * @return the AwsS3Credential object holding access key, secret key, and region
     */
    public AwsS3Credential getAwsS3Credential();
}
