import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.homework.ChatClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ChatClientTest {
    @BeforeEach
    public void setup() {
        try {
            Files.deleteIfExists(Paths.get("file_test.log"));
        } catch (IOException e) {
            fail("Failed to cleanup log file before test.");
        }
    }

    @AfterEach
    public void cleanup() {
        try {
            Files.deleteIfExists(Paths.get("file_test.log"));
        } catch (IOException e) {
            fail("Failed to cleanup log file after test.");
        }
    }

    @Test
    public void testLogClientMessage() {
        String testMessage = "Test log message";
        ChatClient.logClientMessage(testMessage, "file_test.log");
        try {
            List<String> logLines = Files.readAllLines(Paths.get("file_test.log"));
            assertEquals(1, logLines.size());
            assertEquals(testMessage, logLines.get(0));
        } catch (IOException e) {
            fail("Failed to read log file.");
        }
    }

    @Test
    public void testLoadSettings() {
        Properties settings = ChatClient.loadSettings();
        assertNotNull(settings);
        assertEquals("8090", settings.getProperty("port"));
    }
}
