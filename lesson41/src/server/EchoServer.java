package server;

import enums.Commands;
import org.w3c.dom.ls.LSOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class EchoServer {
    private final int port;

    private final ExecutorService pool = Executors.newCachedThreadPool();

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                Socket clientSocket = server.accept();
                pool.submit(() -> handle(clientSocket));
            }
        } catch (IOException e) {
            System.out.printf("Вероятнее всего порт %s занят.%n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        System.out.printf("Подключен клиент: %s%n", socket);

        try (
                socket;
                Scanner reader = getReader(socket);
                PrintWriter writer = getWriter(socket)
        ) {
            sendResponse("Привет " + socket, writer);
            while (true) {
                String message = reader.nextLine().strip();
                System.out.println("Got message: " + message);

                if (isEmptyMsg(message) || isQuitMsg(message)) {
                    break;
                }

                Commands command = Commands.findByValue(message.split(" ")[0].substring(1));
                if (command != null) {
                    String splitMsg = Arrays.stream(message.split(" "))
                            .skip(1)
                            .collect(Collectors.joining(" "));
                    message = command.runCommand(splitMsg);
                    sendResponse(message, writer);
                } else {
                    sendResponse(message.toUpperCase(), writer);
                }

            }
        } catch (NoSuchElementException e) {
            System.out.println("Client dropped connection");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Client [%s] disconnected.%n", socket);
    }

    private Scanner getReader(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new Scanner(inputStreamReader);
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream);
    }

    private void sendResponse(String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    private boolean isQuitMsg(String msg) {
        return "bye".equalsIgnoreCase(msg);
    }

    private boolean isEmptyMsg(String msg) {
        return msg == null || msg.isBlank();
    }
}
