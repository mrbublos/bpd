package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Server {

    public static void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            Semaphore semaphore = new Semaphore(availableProcessors);
            while (true) {
                try {
                    semaphore.acquire();
                    new Thread(() -> {
                        try (Socket socket = serverSocket.accept();
                             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                            String response = Stats.getStats();
                            out.println("HTTP/1.1 200 OK");
                            out.println("Content-Type: application/json");
                            out.println("Content-Length: " + response.length());
                            out.println();
                            out.println(response);
                        } catch (IOException e) {
                            System.out.println("Error " + e.getMessage());
                        }
                        semaphore.release();
                    }).start();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
