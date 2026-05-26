package Socket;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {
    public static void main(String[] args) {
        String serverIP = "localhost";
        int port = 5000;

        System.out.println("Connecting to the server...");

        try (
            Socket socket = new Socket(serverIP, port);
            DataInputStream input = new DataInputStream(socket.getInputStream())
        ) {
            System.out.println("Connected to the server.");

            while (true) {
                String message = input.readUTF();
                System.out.println("Message from server: " + message);

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Client closed the connection.");
                    break;
                }
            }
        } catch (EOFException e) {
            System.out.println("Connection closed by the server.");
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
