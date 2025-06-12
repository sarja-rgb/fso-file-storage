package util;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import storage.AwsS3Credential;

public class AwsS3UtilTest {
    private static final String TEST_ACCESS_KEY = "AKIA_TEST_KEY";
    private static final String TEST_SECRET_KEY = "SECRET_TEST_KEY";
    private static final String TEST_REGION = "us-west-1";
    private static final String TEST_BUCKET = "test-bucket";

    private static final File CREDENTIAL_FILE = new File(AwsS3Util.CREDENTIAL_FILE);

    @BeforeEach
    public void setup() {
        if (CREDENTIAL_FILE.exists()) {
            CREDENTIAL_FILE.delete();
        }
    }

    @Test
    public void testSaveCredential_createsEncryptedFile() throws Exception {
        AwsS3Credential saved = AwsS3Util.saveCredential(TEST_ACCESS_KEY, TEST_SECRET_KEY, TEST_REGION, TEST_BUCKET);
        assertTrue(CREDENTIAL_FILE.exists(), "Credential file should be created");
        assertEquals(TEST_ACCESS_KEY, saved.getAccessKey());
    }

    @Test
    public void testSaveCredential_fileContentIsEncrypted() throws Exception {
        AwsS3Util.saveCredential(TEST_ACCESS_KEY, TEST_SECRET_KEY, TEST_REGION, TEST_BUCKET);
        String content = Files.readString(CREDENTIAL_FILE.toPath());
        assertFalse(content.contains(TEST_ACCESS_KEY), "Access key should be encrypted");
        assertFalse(content.contains(TEST_SECRET_KEY), "Secret key should be encrypted");
    }

    @AfterEach
    public void cleanup() {
        if (CREDENTIAL_FILE.exists()) {
            CREDENTIAL_FILE.delete();
        }
    }
}
