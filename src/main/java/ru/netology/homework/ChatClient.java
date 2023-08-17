package ru.netology.homework;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Properties settings = loadSettings();
            int port = Integer.parseInt(settings.getProperty("port", "8080"));
            Socket socket = new Socket("127.0.0.1", port);
            System.out.println("Подключен к серверу.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            // Ввод никнейма пользователя
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите имя пользователя");
            String nickName = scanner.nextLine();
            writer.println(nickName);
            // Запускаем отдельный поток для приема сообщений от сервера.
            System.out.println("Теперь можете писать сообщения");
            Thread messageReceiverThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                        logClientMessage("Received: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            messageReceiverThread.start();

            // Отправляем сообщения на сервер.
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
                    logClientMessage("Sent: " + message);
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
    public static void logClientMessage(String message) {
        try (PrintWriter out = new PrintWriter(new FileWriter("file.log", true))) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Properties loadSettings() {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream("settings.txt")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

}
