package src;

import java.io.*;
import java.net.*;
import java.util.PriorityQueue;

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private PriorityQueue<String> receivedMessages = new PriorityQueue<>();
    private PriorityQueue<String> sentMessages = new PriorityQueue<>();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            out.println("Welcome! Please enter your username:");
            username = in.readLine();
            ChatServer.registerUser(username, this);

            ChatServer.sendHistory(this);  // Send message history to the user
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
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enterPrivateRoom(String roomId, ClientHandler sender, ClientHandler receiver) throws IOException {
        out.println("==========================================");
        out.println("         Entering Private Room...         ");
        out.println("==========================================");

        new Thread(() -> {
            try {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    if (!receivedMessages.isEmpty()) {
                        out.println("          Received Messages:          ");
                        out.println("------------------------------------------");
                        while (!receivedMessages.isEmpty()) {
                            out.println(receivedMessages.poll());
                        }
                    }

                    out.println("------------------------------------------");
                    out.println("             Your Messages:              ");
                    out.println("------------------------------------------");

                    String userMessage = consoleReader.readLine();
                    sentMessages.add("You: " + userMessage);
                    receiver.receiveMessage(username + ": " + userMessage);
                    displayMessages();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void receiveMessage(String message) {
        receivedMessages.add(message);
    }

    private void displayMessages() {
        out.println("          Received Messages:          ");
        out.println("------------------------------------------");
        for (String message : receivedMessages) {
            out.println(message);
        }
        out.println("------------------------------------------");
        out.println("             Your Messages:              ");
        out.println("------------------------------------------");
        for (String message : sentMessages) {
            out.println(message);
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
