package server.ui;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatRoom {
    private ServerSocket serverSocket;
    private List<ClientHandler> clients = new ArrayList<>();

    public ChatRoom(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        System.out.println("ChatRoom is running on port " + serverSocket.getLocalPort());

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                clientHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ChatRoom chatRoom = new ChatRoom(8080);
        chatRoom.start();
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name;
        private boolean isRunning = true;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        }

        public void run() {
            try {
                // Get client name
                out.println("Enter your name: ");
                name = in.readLine();
                out.println("Welcome to the chat room, " + name + "!");

                // Broadcast message to all clients
                broadcast(name + " joined the chat room");

                while (isRunning) {
                    String input = in.readLine();
                    if (input == null) {
                        break;
                    }
                    if (input.equalsIgnoreCase("quit")) {
                        isRunning = false;
                        break;
                    }

                    // Handle private message
                    if (input.startsWith("/pm")) {
                        String[] split = input.split(" ", 3);
                        String recipientName = split[1];
                        String message = split[2];

                        sendPrivateMessage(name, recipientName, message);
                    }
                    // Handle file sending
                    else if (input.startsWith("/sendfile")) {
                        String[] split = input.split(" ", 2);
                        String filePath = split[1];

                        sendFile(filePath);
                    }
                    // Handle normal message
                    else {
                        broadcast(name + ": " + input);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(this);
                broadcast(name + " left the chat room");
            }
        }

        private void broadcast(String message) {
            synchronized (clients) {
                for (ClientHandler clientHandler : clients) {
                    if (clientHandler != this) {
                        clientHandler.out.println(message);
                    }
                }
            }
        }

        private void sendPrivateMessage(String senderName, String recipientName, String message) {
            synchronized (clients) {
                for (ClientHandler clientHandler : clients) {
                    if (clientHandler.name.equals(recipientName)) {
                        clientHandler.out.println(senderName + " whispered to you: " + message);
                        out.println("You whispered to " + recipientName + ": " + message);
                        return;
                    }
                }
                out.println("Error: user " + recipientName + " not found");
            }
        }

        private void sendFile(String filePath) throws IOException {
            // Get file name
            File
