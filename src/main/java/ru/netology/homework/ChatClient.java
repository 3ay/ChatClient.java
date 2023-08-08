package ru.netology.homework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8080);
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
                    writer.println(message);
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
}
