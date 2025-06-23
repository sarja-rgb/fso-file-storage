package util;

import java.io.File;
import java.io.FileWriter;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileUtilTest {

    private File tempFile;

    @BeforeEach
    public void setup() throws Exception {
        tempFile = File.createTempFile("test-file", ".txt");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Hello, this is a checksum test!");
        }
    }

    @AfterEach
    public void cleanup() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testGenerateSHA256_validFile_returnsChecksum() {
        String checksum = FileUtil.generateSHA256(tempFile);
        assertNotNull(checksum);
        assertEquals(64, checksum.length()); // SHA-256 = 64 hex characters
    }

    @Test
    public void testGenerateChecksum_withMD5Algorithm_returnsExpectedLength() {
        String checksum = FileUtil.generateChecksum(tempFile, "MD5");
        assertNotNull(checksum);
        assertEquals(32, checksum.length()); // MD5 = 32 hex characters
    }

    @Test
    public void testGenerateChecksum_invalidAlgorithm_throwsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            FileUtil.generateChecksum(tempFile, "INVALID-ALG");
        });
        assertTrue(exception.getMessage().contains("Failed to compute checksum"));
    }

    @Test
    public void testGenerateChecksum_nonExistentFile_throwsException() {
        File fakeFile = new File("non_existent_file.xyz");
        Exception exception = assertThrows(RuntimeException.class, () -> {
            FileUtil.generateChecksum(fakeFile, "SHA-256");
        });
        assertTrue(exception.getMessage().contains("Failed to compute checksum"));
    }
}
