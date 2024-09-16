package src;

import java.io.*;
import java.net.*;

public class CombinedChatApp {
    public static void main(String[] args) {
        // Start the server in a separate thread
        new Thread(() -> {
            try {
                ChatServer.startServer();  // This starts the chat server
            } catch (IOException e) {
                System.err.println("Failed to start server: " + e.getMessage());
            }
        }).start();

        // Start the client
        try {
            ChatClient.startClient();  // This starts the chat client
        } catch (IOException e) {
            System.err.println("Unable to start the client: " + e.getMessage());
        }
    }
}
