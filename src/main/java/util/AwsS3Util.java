package util;

import java.io.File;
import java.io.FileWriter;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import storage.AwsS3Credential;

/**
 * AWS S3 Util helper classs
 */
public class AwsS3Util {
    private static final String ENCRYPTION_KEY = "1234567890123456"; // 16-char AES key
    /**
     * Encrypted S3 credential secret file - 
     * The AWS credentails are encrypted and stored in the "s3_secrets.txt" file
     */
    public static final String CREDENTIAL_FILE = "s3_secrets";
    private AwsS3Util(){}

    public static AwsS3Credential getCredential(){
      return new AwsS3Credential();
    }

    /**
     * Encrypt and save AWS credentials on local secret file
     * @param accessKey
     * @param secretKey
     * @param region
     * @param bucketName
     * @return
     */
     public static AwsS3Credential saveCredential(String accessKey, String secretKey, String region, String bucketName)
     {
       try {
            AwsS3Credential credential = new AwsS3Credential(accessKey,secretKey, region, bucketName);
            String credentialData = JsonUtil.objectToJson(credential);
            SecretKeySpec secretKeySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(credentialData.getBytes());
            String encoded = Base64.getEncoder().encodeToString(encrypted);
            File file = new File(CREDENTIAL_FILE);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(encoded);
            }
            return credential;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to save credentials: " + ex.getMessage(), ex);
        }
    }
}
