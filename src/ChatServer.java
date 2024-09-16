package src;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final Map<String, ChatRoom> chatRooms = new HashMap<>();  // ChatRoom ID -> ChatRoom
    private static final int BASE_PORT = 20000;  // Starting port for chatrooms
    private static int nextPort = BASE_PORT;
    private static final Map<String, String> chatRoomPasswords = new HashMap<>();  // ChatRoom ID -> Password

    public static void startServer() throws IOException {
        System.out.println("Chat server (Main Menu) started on port 12345...");

        ServerSocket serverSocket = new ServerSocket(12345);

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            new Thread(clientHandler).start();
        }
    }

    // Create a new chatroom with a 6-digit ID and password
    public static synchronized ChatRoom createChatRoom(String roomName, String password) {
        String chatRoomID = generateChatRoomID();
        int port = nextPort++;
        ChatRoom chatRoom = new ChatRoom(roomName, port);
        chatRooms.put(chatRoomID, chatRoom);
        chatRoomPasswords.put(chatRoomID, password);

        System.out.println("Created chatroom '" + roomName + "' with ID: " + chatRoomID + " on port: " + port);
        return chatRoom;
    }

    // List all chatrooms with their IDs and names
    public static synchronized Map<String, ChatRoom> listChatRooms() {
        return new HashMap<>(chatRooms);  // Return chatroom ID -> ChatRoom map
    }

    // Verify chatroom password and return the chatroom if correct
    public static synchronized ChatRoom joinChatRoom(String chatRoomID, String password) {
        if (chatRooms.containsKey(chatRoomID) && chatRoomPasswords.get(chatRoomID).equals(password)) {
            return chatRooms.get(chatRoomID);
        }
        return null;  // Invalid ID or password
    }

    // Generate a unique 6-digit ID for each chatroom
    private static String generateChatRoomID() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
