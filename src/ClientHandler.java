package src;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Map;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private String username;
    private ChatRoom chatRoom;

    private static final String RESET = "\u001B[0m";  // Reset color to default
    private static final String[] COLORS = {
        "\u001B[34m", // Blue
        "\u001B[31m", // Red
        "\u001B[33m", // Yellow
        "\u001B[32m", // Green
        "\u001B[35m", // Purple
        "\u001B[36m"  // Cyan
    };

    private String color;  // Color assigned to this client

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        assignColor();  // Assign a random color to the user
    }

    // Randomly assign a color to the user
    private void assignColor() {
        Random random = new Random();
        this.color = COLORS[random.nextInt(COLORS.length)];
    }

    public String getColor() {
        return color;  // Return the user's assigned color
    }

    @Override
    public void run() {
        try {
            out.println("\n=== Welcome to the Main Menu ===\n");
            out.println("Please enter your username:");
            username = in.readLine();

            while (true) {
                displayMainMenu();
                String choice = in.readLine();

                switch (choice) {
                    case "1":
                        createChatRoom();
                        break;
                    case "2":
                        listChatRooms();
                        break;
                    case "3":
                        listChatRooms();  // Show available chatrooms first
                        joinChatRoom();    // Then allow the user to join
                        break;
                    case "/exit":
                        return;  // Exit the chatroom and stop the thread
                    default:
                        out.println("\nInvalid choice. Please try again.\n");
                }
            }

        } catch (IOException e) {
            System.err.println("Error handling client (" + username + "): " + e.getMessage());
        } finally {
            if (chatRoom != null) {
                chatRoom.removeMember(this);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Display main menu
    private void displayMainMenu() {
        out.println("\n----------------------------");
        out.println("          Main Menu          ");
        out.println("----------------------------");
        out.println(" 1. Create a new chatroom");
        out.println(" 2. List available chatrooms");
        out.println(" 3. Join an existing chatroom");
        out.println("/exit - Leave the chatroom");
        out.println("----------------------------");
        out.println("Enter your choice:\n");
    }

    private void createChatRoom() throws IOException {
        out.println("\n--- Create a New Chatroom ---\n");
        out.println("Enter a name for your chatroom:");
        String roomName = in.readLine();
        out.println("Set a password for your chatroom:");
        String password = in.readLine();

        ChatRoom chatRoom = ChatServer.createChatRoom(roomName, password);
        out.println("\nChatroom created!");
        out.println("Chatroom ID: " + chatRoom.getName() + " | Port: " + chatRoom.getPort() + "\n");
    }

    private void listChatRooms() throws IOException {
        out.println("\n--- Available Chatrooms ---");
        for (Map.Entry<String, ChatRoom> entry : ChatServer.listChatRooms().entrySet()) {
            out.println("ID: " + entry.getKey() + " | Name: " + entry.getValue().getName());
        }
        if (ChatServer.listChatRooms().isEmpty()) {
            out.println("\nNo chatrooms available.\n");
        } else {
            out.println("\n----------------------------\n");
        }
    }

    private void joinChatRoom() throws IOException {
        out.println("\n--- Join a Chatroom ---");
        out.println("Enter the chatroom ID:");
        String roomID = in.readLine();
        out.println("Enter the chatroom password:");
        String password = in.readLine();

        ChatRoom chatRoom = ChatServer.joinChatRoom(roomID, password);
        if (chatRoom != null) {
            out.println("\nSuccessfully joined the chatroom: " + chatRoom.getName() + " (ID: " + roomID + ")");
            this.chatRoom = chatRoom;
            chatRoom.addMember(this);  // Add this client to the chatroom

            // Start receiving and sending messages within the chatroom
            handleChatRoomMessages();
        } else {
            out.println("\nInvalid chatroom ID or password. Please try again.\n");
        }
    }

    private void handleChatRoomMessages() throws IOException {
        out.println("\n--- Chatroom ---\n");
        String message;
        while ((message = in.readLine()) != null) {
            if (message.equalsIgnoreCase("/exit")) {
                out.println("\nExiting the chatroom...\n");
                break;
            }
            if (message.trim().isEmpty()) {
                continue;  // Skip sending empty messages
            }
            chatRoom.broadcastMessage(username + ": " + message, this);  // Pass sender reference
        }
        chatRoom.removeMember(this);  // Remove user when they exit the chatroom
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        // Send the message to the client with their assigned color
        out.println(color + message + RESET);
    }
}