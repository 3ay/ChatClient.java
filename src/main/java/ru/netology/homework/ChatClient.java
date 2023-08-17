package ru.netology.homework;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class ChatClient {
    private final Socket socket;
    private final InputStream input;
    public ChatClient(Socket socket, InputStream input) {
        this.socket = socket;
        this.input = input;
    }

    public static void main(String[] args) {
        try {
            Properties settings = loadSettings();
            int port = Integer.parseInt(settings.getProperty("port", "8080"));
            Socket socket = new Socket("127.0.0.1", port);
            System.out.println("Подключен к серверу.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите имя пользователя");
            String nickName = scanner.nextLine();
            writer.println(nickName);
            System.out.println("Теперь можете писать сообщения");
            Thread messageReceiverThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                        logClientMessage("Received: " + message, "file.log");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageReceiverThread.start();
            Thread messageSenderThread = new Thread(() -> {
                String message;
                while ((message = scanner.nextLine()) != null) {
                    if ("exit".equalsIgnoreCase(message)) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }
                    writer.println(message);
                    logClientMessage("Sent: " + message, "file.log");
                }
            });
            messageSenderThread.start();
            messageReceiverThread.join();
            messageSenderThread.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void logClientMessage(String message, String fileName) {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName, true))) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Properties loadSettings() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream("settings.txt")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
