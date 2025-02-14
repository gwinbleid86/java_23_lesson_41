package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoServer {
    private final int port;

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            try(Socket socket = server.accept()) {
                handle(socket);
            }
        } catch (IOException e){
            System.out.printf("Вероятнее всего порт %s занят.%n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(inputStream);

        try(Scanner scanner = new Scanner(isr)){
            while (true){
                String message = scanner.nextLine().strip();
                System.out.printf("Got: %s%n", message);
                if (message.equalsIgnoreCase("bye")){
                    System.out.println("Bye bye!");
                    return;
                }
            }
        } catch (NoSuchElementException e){
            System.out.println("Client dropped connection");
        }
    }
}
