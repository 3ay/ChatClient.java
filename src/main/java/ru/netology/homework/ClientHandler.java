package ru.netology.homework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String clientName = reader.readLine();
            ChatServer.saveClientInfo(clientSocket, clientName);
            String message;
            while ((message = reader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(message)) {
                    clientSocket.close();
                    ChatServer.clientSockets.remove(clientSocket);
                    break;
                }
                System.out.println("сообщение от '" + clientName + "': " + message);
                ChatServer.broadcastMessage(clientName + ": " + message, clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
