package src;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    // HashMap to store message history locally for each user
    private static Map<String, LinkedList<String>> messageHistory = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345); // Connect to server
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Handle server messages in a separate thread
        new Thread(() -> {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    synchronized (System.out) {  // Ensure messages are printed in a synchronized way
                        // Print server message
                        System.out.println(serverMessage);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Wait for the server to ask for the username
        String userInput;
        while ((userInput = consoleReader.readLine()) != null) {
            out.println(userInput);  // Send message to server
        }
    }

    // Removed code for handling username and local history initialization here,
    // because the server handles that logic.
}
