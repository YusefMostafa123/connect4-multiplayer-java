/*
this deals with  client's network communication with the server,
for login, messaging, and getting stats
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    public static boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean sendUsername(String username) {
        try {
            String prompt = in.readLine();
            if (!"ENTER_USERNAME".equals(prompt)) return false;

            out.println(username);
            String response = in.readLine();
            return "LOGIN_SUCCESS".equals(response);
        } catch (IOException e) {
            return false;
        }
    }

    public static void send(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public static String read() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public static void flushMessages() {
        try {
            while (in != null && in.ready()) {
                in.readLine();
            }
        } catch (IOException ignored) {}
    }

    public static String[] getStats(String username) {
        send("GET_STATS");
        try {
            String response = in.readLine();
            if (response.startsWith("STATS:")) {
                return response.substring(6).split(":");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{"0", "0", "0"};
    }
}

