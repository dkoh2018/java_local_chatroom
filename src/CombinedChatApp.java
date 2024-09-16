package src;

import java.net.ServerSocket;

public class CombinedChatApp {
    public static void main(String[] args) {
        // Check if the server is already running
        if (!isServerRunning(12345)) {
            // Start the server in a separate thread
            new Thread(() -> {
                try {
                    ChatServer.startServer();  // Try to start the chat server
                } catch (Exception e) {
                    if (!e.getMessage().contains("Address already in use")) {
                        System.err.println("Failed to start server: " + e.getMessage());  // Show error if it's something else
                    }
                }
            }).start();
        } else {
            System.out.println("Server is already running. Connecting as a client...");
        }

        // Start the client
        try {
            ChatClient.startClient();  // Start the chat client
        } catch (Exception e) {
            System.err.println("Unable to start the client: " + e.getMessage());
        }
    }

    // Method to check if the server is already running on the given port
    private static boolean isServerRunning(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            return false;  // If we can bind to the port, the server is not running
        } catch (Exception e) {
            return true;  // If the port is already in use, the server is running
        }
    }
}
