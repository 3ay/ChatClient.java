package ru.netology.homework;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    static ArrayList<Socket> clientSockets = new ArrayList<>();
    private static ConcurrentHashMap clientInfo = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(getPortFromSettings());
            System.out.println("Сервер запущен и ожидает подключений...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                System.out.println("Подключен клиент: " + clientSocket.getInetAddress());

                // Создаем новый поток для каждого клиента.
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для рассылки сообщения всем клиентам, кроме отправителя.
    public static void broadcastMessage(String message, Socket senderSocket) {
        for (Socket clientSocket : clientSockets) {
            if (clientSocket != senderSocket) {
                try {
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    writer.println(message);
                    String fullMessage = clientInfo.get(senderSocket) + ": " + message + " [" + LocalDateTime.now() + "]";
                    logMessage(fullMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveClientInfo(Socket clientSocket, String clientName) {
        clientInfo.put(clientSocket, clientName);
    }
    static int getPortFromSettings() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("settings.txt"));
            for (String line : lines) {
                if (line.startsWith("port=")) {
                    return Integer.parseInt(line.split("=")[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 8080;  // Default port
    }
    public static void logMessage(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter("file.log", true))) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
