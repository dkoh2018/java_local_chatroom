package src;

import java.util.*;
import java.io.*;

public class ChatRoom {
    private final String name;
    private final List<ClientHandler> members = new ArrayList<>();
    private final int port;
    private final Map<String, String> userColors = new HashMap<>();  // Track colors by username

    public ChatRoom(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    // Add a member to the chatroom
    public synchronized void addMember(ClientHandler client) {
        members.add(client);
        // Assign color to user if not already done
        if (!userColors.containsKey(client.getUsername())) {
            userColors.put(client.getUsername(), client.getColor());
        }
        broadcastMessage(client.getUsername() + " has joined the chatroom " + name, null);
    }

    // Remove a member from the chatroom
    public synchronized void removeMember(ClientHandler client) {
        members.remove(client);
        broadcastMessage(client.getUsername() + " has left the chatroom " + name, null);
    }

    // Broadcast a message to all members except the sender
    public synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler member : members) {
            if (sender == null || !member.equals(sender)) {  // Skip the sender if they are broadcasting
                member.sendMessage(message);
            }
        }
    }

    // List all active members in the chatroom
    public synchronized List<String> listMembers() {
        List<String> usernames = new ArrayList<>();
        for (ClientHandler member : members) {
            usernames.add(member.getUsername());
        }
        return usernames;
    }
}