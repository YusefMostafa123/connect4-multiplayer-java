/*
this manages the servers by accepting client connections dealing with the active users,
and coordinating the client threads
 */
package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ServerMain {
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private volatile boolean running = false;
    final Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());

    public ServerMain() {
        threadPool = Executors.newCachedThreadPool();
    }

    public void startServer() throws IOException {
        serverSocket = new ServerSocket(12555);
        running = true;
        Logger.log("Server started on port 12555");
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                Logger.log("New connection from " + clientSocket.getInetAddress().getHostAddress());
                HandleClient handler = new HandleClient(clientSocket, activeUsers);
                threadPool.execute(handler);
            } catch (IOException e) {
                if (running) {
                    Logger.log("couldn't accepting the client connection: " + e.getMessage());
                }
            }
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            Logger.log("Couldn't close the server socket: " + e.getMessage());
        }

        threadPool.shutdownNow();
        Logger.log("server stopped");
    }


    public static void main(String[] args) {
        try {
            ServerMain server = new ServerMain();
            server.startServer();
        } catch (IOException e) {
            Logger.log("couldn't start the server: " + e.getMessage());
        }
    }
}
