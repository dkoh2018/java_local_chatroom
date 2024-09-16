package src;

import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Thread to handle incoming messages from the server
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        synchronized (System.out) {  // Ensure messages are printed in a synchronized way
                            System.out.println(serverMessage);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Connection closed.");
                }
            }).start();

            // Handle user input
            String userInput;
            while ((userInput = consoleReader.readLine()) != null) {
                out.println(userInput);  // Send message to server
                if (userInput.equalsIgnoreCase("/exit")) {
                    break; // Exit the loop if user types /exit
                }
            }

        } catch (IOException e) {
            System.err.println("Unable to connect to the server: " + e.getMessage());
        }
    }
}
