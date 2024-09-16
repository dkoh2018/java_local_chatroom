package src;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // Map for active users (username -> ClientHandler)
    private static Map<String, ClientHandler> activeUsers = new HashMap<>();
    
    // Map for private rooms (roomID -> List of participants)
    private static Map<String, List<ClientHandler>> privateRooms = new HashMap<>();
    
    // Invitations (user -> PriorityQueue of invitations)
    private static Map<String, PriorityQueue<Invitation>> pendingInvitations = new HashMap<>();
    
    // Message history stored as a LinkedList
    private static LinkedList<String> messageHistory = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 10; // Limit the history size

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Chat server started on port 12345...");

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(socket);
            new Thread(clientHandler).start();
        }
    }

    // Register a user when they join
    public static void registerUser(String username, ClientHandler handler) {
        activeUsers.put(username, handler);
        pendingInvitations.put(username, new PriorityQueue<>());
        broadcastMessage(username + " has joined the chat.");
    }

    // Broadcast a message to all users
    public static void broadcastMessage(String message) {
        messageHistory.add(message);  // Store message in history
        if (messageHistory.size() > MAX_HISTORY_SIZE) {
            messageHistory.removeFirst(); // Remove oldest message if size exceeds limit
        }
        for (ClientHandler client : activeUsers.values()) {
            client.sendMessage(message);
        }
    }

    // Send message history to a newly connected user
    public static void sendHistory(ClientHandler client) {
        for (String message : messageHistory) {
            client.sendMessage(message);
        }
    }

    // Create a private room between two users
    public static String createPrivateRoom(ClientHandler user1, ClientHandler user2) {
        String roomId = UUID.randomUUID().toString(); // Generate unique room ID
        List<ClientHandler> participants = Arrays.asList(user1, user2);
        privateRooms.put(roomId, participants);
        return roomId;
    }

    // Handle invitation acceptance
    public static void handleInvitation(String fromUser, String toUser) {
        ClientHandler sender = activeUsers.get(fromUser);
        ClientHandler receiver = activeUsers.get(toUser);

        if (sender != null && receiver != null) {
            Invitation invitation = new Invitation(fromUser, toUser, System.currentTimeMillis());
            sender.sendMessage("Invitation sent to " + toUser);
            receiver.sendMessage("You have an invitation from " + fromUser + ". Type /accept or /decline");
            pendingInvitations.get(toUser).add(invitation);
        }
    }

    // Accept an invitation
    public static void acceptInvitation(String receiverUsername) {
        PriorityQueue<Invitation> invitations = pendingInvitations.get(receiverUsername);
        if (!invitations.isEmpty()) {
            Invitation invitation = invitations.poll();
            String senderUsername = invitation.getFromUser();
            ClientHandler sender = activeUsers.get(senderUsername);
            ClientHandler receiver = activeUsers.get(receiverUsername);

            String roomId = createPrivateRoom(sender, receiver);
            sender.sendMessage("Private room created with " + receiverUsername);
            receiver.sendMessage("Private room created with " + senderUsername);

            // Notify users they are now in a private room
            broadcastMessageToRoom(roomId, "Private chat started between " + senderUsername + " and " + receiverUsername);
        }
    }

    // Send message to all users in a private room
    public static void broadcastMessageToRoom(String roomId, String message) {
        List<ClientHandler> participants = privateRooms.get(roomId);
        if (participants != null) {
            for (ClientHandler participant : participants) {
                participant.sendMessage(message);
            }
        }
    }

    // List all active users
    public static List<String> listUsers() {
        return new ArrayList<>(activeUsers.keySet());
    }
}
