import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.netology.homework.ChatServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class ChatServerTest {
    @Test
    public void testSaveClientInfo() {
        Socket mockSocket = new Socket();
        String clientName = "TestUser";

        ChatServer.saveClientInfo(mockSocket, clientName);

        assertEquals(clientName, ChatServer.clientInfo.get(mockSocket));
    }

    @Test
    public void testGetPortFromSettings() {
        try {
            Files.write(Paths.get("settings_test.txt"), "port=8090".getBytes());
        } catch (IOException e) {
            fail("Failed to create temporary settings file.");
        }

        int port = ChatServer.getPortFromSettings();
        assertEquals(8090, port);
        try {
            Files.delete(Paths.get("settings_test.txt"));
        } catch (IOException e) {
            fail("Failed to delete temporary settings file.");
        }
    }
}
