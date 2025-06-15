package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import storage.AwsS3Credential;

/**
 * AWS S3 Util helper classs
 */
public class AwsS3Util {
    public static final String ENCRYPTION_KEY_FILE = "s3_encryption.key";
    /**
     * Encrypted S3 credential secret file - 
     * The AWS credentails are encrypted and stored in the "s3_secrets.txt" file
     */
    public static final String CREDENTIAL_FILE = "s3_credentials";
    private AwsS3Util(){}

    public static AwsS3Credential getCredential(){
      return new AwsS3Credential();
    }

    /* 
     * Generates and saves a random AES encryption key to the specified file.
     * @param filePath path to save the AES key file
     */
    public static void generateAndSaveEncryptionKey(String filePath) throws IOException {
        try {
            SecureRandom random = new SecureRandom();
            byte[] keyBytes = new byte[16]; // 128-bit AES key
            random.nextBytes(keyBytes);
            String encodedKey = Base64.getEncoder().encodeToString(keyBytes);

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(encodedKey);
            }
        } catch (IOException e) {
            throw new IOException("Failed to generate and save AES key: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the AES encryption key from file.
     * @param filePath path to the AES key file
     * @return SecretKeySpec for AES
     */
    public static SecretKeySpec loadEncryptionKey(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Encryption key file not found: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String encodedKey = reader.readLine();
            byte[] keyBytes = Base64.getDecoder().decode(encodedKey);
            return new SecretKeySpec(keyBytes, "AES");
        } catch (IOException e) {
            throw new IOException("Failed to read encryption key: " + e.getMessage(), e);
        }
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
     throws  IOException {
       return saveCredential(new AwsS3Credential(accessKey,secretKey, region, bucketName));
    }


        /**
     * Encrypt and save AWS credentials on local secret file
     * @param accessKey
     * @param secretKey
     * @param region
     * @param bucketName
     * @return
     */
     public static AwsS3Credential saveCredential(AwsS3Credential awsS3Credential) throws  IOException
     {
       try {
            SecretKeySpec secretKeySpec = loadEncryptionKey(ENCRYPTION_KEY_FILE);
            String credentialData = JsonUtil.objectToJson(awsS3Credential);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(credentialData.getBytes());
            String encoded = Base64.getEncoder().encodeToString(encrypted);
            File oldSecretFile = new File(CREDENTIAL_FILE);
            if (oldSecretFile.exists()) {
                oldSecretFile.delete();
            }
            File newSecretFile = new File(CREDENTIAL_FILE);
            try (FileWriter writer = new FileWriter(newSecretFile)) {
                writer.write(encoded);
            }
            return awsS3Credential;
            
        } catch (NoSuchAlgorithmException|  NoSuchPaddingException | IllegalBlockSizeException | 
                InvalidKeyException | BadPaddingException | IOException ex) {
            throw new IOException("Failed to save credentials: " + ex.getMessage(), ex);
        }
    }

    /**
     * Decrypt and load AWS credentials from the local file
     */
    public static AwsS3Credential loadCredential() throws IOException{
        try {
            File file = new File(CREDENTIAL_FILE);
            if (!file.exists()) {
                throw new IOException("Credential secret file not found. Please login");
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            String encoded = reader.readLine();
            reader.close();

            byte[] encrypted = Base64.getDecoder().decode(encoded);
            SecretKeySpec secretKeySpec = loadEncryptionKey(ENCRYPTION_KEY_FILE);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            String json = new String(decrypted);
            return JsonUtil.jsonToObject(json, AwsS3Credential.class);
        } catch (NoSuchAlgorithmException|  NoSuchPaddingException | IllegalBlockSizeException | 
                InvalidKeyException | BadPaddingException | IOException ex) {
            throw new IOException("Failed to load credentials: " + ex.getMessage(), ex);
        }
    }
}
