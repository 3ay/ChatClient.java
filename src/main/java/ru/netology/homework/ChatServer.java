package ru.netology.homework;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static ArrayList<Socket> clientSockets = new ArrayList<>();
    private static ConcurrentHashMap clientInfo = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void saveClientInfo(Socket clientSocket, String clientName) {
        clientInfo.put(clientSocket, clientName);
    }
}
