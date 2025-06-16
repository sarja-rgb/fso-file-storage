package util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import storage.AwsS3Credential;

/**
 * Unit test for JsonUtil
 */
public class JsonUtilTest {

    @Test
    public void testObjectToJson() throws JsonProcessingException {
        AwsS3Credential credential = new AwsS3Credential("access123", "secret456", "us-east-1", "my-bucket");
        String json = JsonUtil.objectToJson(credential);

        assertNotNull(json);
        assertTrue(json.contains("access123"));
        assertTrue(json.contains("my-bucket"));
    }

    @Test
    public void testJsonToObject() throws JsonProcessingException {
        String json = "{\"accessKey\":\"access123\",\"secretKey\":\"secret456\",\"region\":\"us-east-1\",\"bucketName\":\"my-bucket\"}";
        AwsS3Credential credential = JsonUtil.jsonToObject(json, AwsS3Credential.class);

        assertNotNull(credential);
        assertEquals("access123", credential.getAccessKey());
        assertEquals("secret456", credential.getSecretKey());
        assertEquals("us-east-1", credential.getRegion());
        assertEquals("my-bucket", credential.getBucketName());
    }

    @Test
    public void testRoundTripSerialization() throws JsonProcessingException {
        AwsS3Credential original = new AwsS3Credential("accessABC", "secretXYZ", "eu-west-1", "bucket-test");

        String json = JsonUtil.objectToJson(original);
        AwsS3Credential deserialized = JsonUtil.jsonToObject(json, AwsS3Credential.class);

        assertEquals(original.getAccessKey(), deserialized.getAccessKey());
        assertEquals(original.getSecretKey(), deserialized.getSecretKey());
        assertEquals(original.getRegion(), deserialized.getRegion());
        assertEquals(original.getBucketName(), deserialized.getBucketName());
    }
}
