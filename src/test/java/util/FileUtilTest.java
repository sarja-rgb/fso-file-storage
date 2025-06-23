package util;

import java.io.File;
import java.io.FileWriter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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

}
