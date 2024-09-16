package src;

import java.io.*;
import java.net.*;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private String username;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            // Prompt for the username
            out.println("Welcome! Please enter your username:");
            username = in.readLine();

            // Send message history and list of active users to the new user
            ChatServer.sendHistoryAndUsers(this);

            // Register the user (broadcasts join message)
            ChatServer.registerUser(username, this);

            out.println("Type /help for available commands.");
            displayMenu();

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/")) {
                    handleCommand(message);
                } else {
                    ChatServer.broadcastMessage(username + ": " + message);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client (" + username + "): " + e.getMessage());
        } finally {
            if (username != null) {
                ChatServer.removeUser(username);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayMenu() {
        out.println("----------------------------");
        out.println("Menu:");
        out.println("/list - List all online users");
        out.println("/invite [username] - Send a private room invitation");
        out.println("/accept - Accept a private room invitation");
        out.println("/decline - Decline a private room invitation");
        out.println("/exit - Leave the chatroom");
        out.println("----------------------------");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }

    // Handle commands like /list, /invite, /accept, /decline
    private void handleCommand(String command) throws IOException {
        if (command.equals("/list")) {
            out.println("Active users: " + String.join(", ", ChatServer.listUsers()));
        } else if (command.startsWith("/invite ")) {
            String[] parts = command.split(" ", 2);
            if (parts.length == 2) {
                String targetUser = parts[1];
                ChatServer.handleInvitation(username, targetUser);
            } else {
                out.println("Usage: /invite [username]");
            }
        } else if (command.equals("/accept")) {
            ChatServer.acceptInvitation(username);
        } else if (command.equals("/decline")) {
            out.println("You declined the invitation.");
        } else if (command.equals("/exit")) {
            out.println("Goodbye " + username + "!");
            socket.close();
        } else {
            out.println("Unknown command. Type /help for available commands.");
        }
    }
}
