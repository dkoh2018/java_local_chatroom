package src;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // Map for active users (username -> ClientHandler)
    private static final Map<String, ClientHandler> activeUsers = new HashMap<>();

    // Map for private rooms (roomID -> List of participants)
    private static final Map<String, List<ClientHandler>> privateRooms = new HashMap<>();

    // Invitations (user -> PriorityQueue of invitations)
    private static final Map<String, PriorityQueue<Invitation>> pendingInvitations = new HashMap<>();

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
    public static synchronized void registerUser(String username, ClientHandler handler) {
        activeUsers.put(username, handler);
        pendingInvitations.put(username, new PriorityQueue<>());
        broadcastMessage(username + " has joined the chat.");  // Broadcast once
    }

    // Broadcast a message to all users
    public static synchronized void broadcastMessage(String message) {
        for (ClientHandler client : activeUsers.values()) {
            client.sendMessage(message);
        }
    }

    // Send active users to a newly connected user
    public static synchronized void sendHistoryAndUsers(ClientHandler client) {
        // No need to show previous messages, just send active users
        client.sendMessage("Active users: " + String.join(", ", listUsers()));
    }

    // Create a private room between two users
    public static synchronized String createPrivateRoom(ClientHandler user1, ClientHandler user2) {
        String roomId = UUID.randomUUID().toString(); // Generate unique room ID
        List<ClientHandler> participants = Arrays.asList(user1, user2);
        privateRooms.put(roomId, participants);
        return roomId;
    }

    // Handle invitation sending
    public static synchronized void handleInvitation(String fromUser, String toUser) {
        ClientHandler sender = activeUsers.get(fromUser);
        ClientHandler receiver = activeUsers.get(toUser);

        if (sender != null && receiver != null) {
            Invitation invitation = new Invitation(fromUser, toUser, System.currentTimeMillis());
            sender.sendMessage("Invitation sent to " + toUser);
            receiver.sendMessage("You have an invitation from " + fromUser + ". Type /accept or /decline");
            pendingInvitations.get(toUser).add(invitation);
        } else {
            if (sender != null) {
                sender.sendMessage("User " + toUser + " does not exist or is not online.");
            }
        }
    }

    // Accept an invitation
    public static synchronized void acceptInvitation(String receiverUsername) {
        PriorityQueue<Invitation> invitations = pendingInvitations.get(receiverUsername);
        if (invitations != null && !invitations.isEmpty()) {
            Invitation invitation = invitations.poll();
            String senderUsername = invitation.getFromUser();
            ClientHandler sender = activeUsers.get(senderUsername);
            ClientHandler receiver = activeUsers.get(receiverUsername);

            if (sender != null && receiver != null) {
                String roomId = createPrivateRoom(sender, receiver);
                sender.sendMessage("Private room created with " + receiverUsername);
                receiver.sendMessage("Private room created with " + senderUsername);

                // Notify users they are now in a private room
                broadcastMessageToRoom(roomId, "Private chat started between " + senderUsername + " and " + receiverUsername);
            }
        } else {
            ClientHandler receiver = activeUsers.get(receiverUsername);
            if (receiver != null) {
                receiver.sendMessage("No pending invitations to accept.");
            }
        }
    }

    // Send message to all users in a private room
    public static synchronized void broadcastMessageToRoom(String roomId, String message) {
        List<ClientHandler> participants = privateRooms.get(roomId);
        if (participants != null) {
            for (ClientHandler participant : participants) {
                participant.sendMessage(message);
            }
        }
    }

    // List all active users
    public static synchronized List<String> listUsers() {
        return new ArrayList<>(activeUsers.keySet());
    }

    // Handle user disconnection
    public static synchronized void removeUser(String username) {
        activeUsers.remove(username);
        pendingInvitations.remove(username);
        broadcastMessage(username + " has left the chat.");
    }
}
