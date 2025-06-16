package storage;

/**
 * Aws S3 Account credentials
 */
public class AwsS3Credential {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;

    public AwsS3Credential() {}
    
    public AwsS3Credential(String accessKey, String secretKey, String region, String bucketName) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
    public String getAccessKey() {
        return accessKey;
    }
    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }
    public String getSecretKey() {
        return secretKey;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    
}
