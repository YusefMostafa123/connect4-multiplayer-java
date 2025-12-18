/*
this is the logging the server events to both the consoles and the log file
and we use time stamps
 */
package server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String LOG_FILE = "server_log.txt";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static synchronized void log(String message) {
        String timestamp = timeFormat.format(new Date());
        String logMessage = "[" + timestamp + "] " + message;

        System.out.println(logMessage);

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(logMessage);
        } catch (IOException e) {
        }
    }
}