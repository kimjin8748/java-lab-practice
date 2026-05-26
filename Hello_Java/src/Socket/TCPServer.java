package Socket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPServer {
    public static void main(String[] args) {
        int port = 5000;

        System.out.println("Server started.");
        System.out.println("Waiting for a client...");

        try (
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            Scanner scanner = new Scanner(System.in);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client connected.");
            System.out.println("Type a message and press Enter.");
            System.out.println("Type \"exit\" to close the connection.");

            while (true) {
                String message = scanner.nextLine();
                output.writeUTF(message);
                output.flush();

                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Server closed the connection.");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
