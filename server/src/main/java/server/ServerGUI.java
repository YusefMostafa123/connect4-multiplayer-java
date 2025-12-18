/*
this is the UI for the server we can turn ON/OFF the server,
see the port we are using the uptime and all the log that are happening
all communication between the client and server
 */
package server;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

public class ServerGUI extends Application {

    private ServerMain serverInstance;
    private Thread serverThread;
    private volatile boolean serverRunning = false;
    private LocalDateTime serverStartTime;

    private Button toggleServerButton;
    private TextArea logTextArea;
    private Label runningStatus;
    private Label uptimeLabel;

    private Set<String> seenLogLines = new LinkedHashSet<>();
    private ScheduledExecutorService scheduler;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Connect 4 Server Control Panel");


        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setStyle("-fx-background-color: #D6E7F7;");

        Label titleLabel = new Label("Connect 4 Server Status");
        titleLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#3D5A80"));

        toggleServerButton = createStyledButton("Start Server");
        toggleServerButton.setOnAction(e -> {
            if (!serverRunning) {
                startServer();
            } else {
                stopServer();
            }
        });
        toggleServerButton.setPrefWidth(150);

        VBox statusPanel = new VBox(10);
        statusPanel.setPadding(new Insets(15));
        statusPanel.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #3D5A80; -fx-border-width: 2;");

        HBox statusBox = new HBox(20);
        statusBox.setAlignment(Pos.CENTER);

        Label statusLabel = new Label("Server Status:");
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        statusLabel.setTextFill(Color.web("#293241"));

        runningStatus = new Label("Offline");
        runningStatus.setFont(Font.font("System", FontWeight.BOLD, 14));
        runningStatus.setTextFill(Color.web("#8B0000"));

        statusBox.getChildren().addAll(statusLabel, runningStatus);

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(10);

        Label portLabel = new Label("Port:");
        portLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label portValue = new Label("12555");

        Label uptimeTextLabel = new Label("Uptime:");
        uptimeTextLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        uptimeLabel = new Label("00:00:00");

        infoGrid.add(portLabel, 0, 0);
        infoGrid.add(portValue, 1, 0);
        infoGrid.add(uptimeTextLabel, 0, 1);
        infoGrid.add(uptimeLabel, 1, 1);

        statusPanel.getChildren().addAll(statusBox, new Separator(), infoGrid);

        VBox logSection = new VBox(10);
        logSection.setPadding(new Insets(15));
        logSection.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #3D5A80; -fx-border-width: 2;");

        Label logLabel = new Label("Server Activity Log");
        logLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        logLabel.setTextFill(Color.web("#293241"));

        logTextArea = new TextArea();
        logTextArea.setEditable(false);
        logTextArea.setPrefHeight(300);
        logTextArea.setWrapText(true);
        logTextArea.setStyle("-fx-control-inner-background: #FFFFFF;");

        Button clearLogButton = createStyledButton("Clear Log");
        clearLogButton.setOnAction(e -> logTextArea.clear());

        HBox logControlBox = new HBox(10, clearLogButton);
        logControlBox.setAlignment(Pos.CENTER_RIGHT);

        logSection.getChildren().addAll(logLabel, logTextArea, logControlBox);

        mainContent.getChildren().addAll(titleLabel, toggleServerButton, statusPanel, logSection);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #D6E7F7;");
        root.setCenter(mainContent);
        HBox footer = new HBox();
        Button exitButton = new Button("Exit");
        exitButton.setStyle(
                "-fx-background-color: #8B0000;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-background-radius: 30;"
        );
        exitButton.setOnMouseEntered(e ->
                exitButton.setStyle(
                        "-fx-background-color: #a40000;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-background-radius: 30;"
                )
        );
        exitButton.setOnMouseExited(e ->
                exitButton.setStyle(
                        "-fx-background-color: #8B0000;" + "-fx-text-fill: white;" + "-fx-font-weight: bold;" + "-fx-background-radius: 30;"
                )
        );
        exitButton.setOnAction(e -> closeApplication());
        footer.getChildren().add(exitButton);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(10));
        root.setBottom(footer);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> closeApplication());
        primaryStage.show();

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (serverRunning) {
                    File logFile = new File("server_log.txt");
                    if (logFile.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(logFile));
                        String line;
                        List<String> newLines = new ArrayList<>();


                        while ((line = reader.readLine()) != null) {
                            if (!seenLogLines.contains(line)) {
                                seenLogLines.add(line);
                                newLines.add(line);
                            }
                        }
                        reader.close();


                        if (!newLines.isEmpty()) {
                            Platform.runLater(() -> {
                                for (String logLine : newLines) {
                                    logTextArea.appendText(logLine + "\n");
                                }
                            });
                        }
                    }

                    if (serverStartTime != null) {
                        Duration uptime = Duration.between(serverStartTime, LocalDateTime.now());
                        Platform.runLater(() -> uptimeLabel.setText(String.format("%02d:%02d:%02d",
                                uptime.toHours(), uptime.toMinutesPart(), uptime.toSecondsPart())));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
        startServer();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3D5A80; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #3D5A80; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3D5A80; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;"));
        return button;
    }

    private void startServer() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("server_log.txt", false));
            writer.print("");
            writer.close();
        } catch (IOException ignored) {}

        serverInstance = new ServerMain();
        serverThread = new Thread(() -> {
            try {
                serverInstance.startServer();
            } catch (Exception ignored) {
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        serverRunning = true;
        serverStartTime = LocalDateTime.now();
        seenLogLines.clear();

        Platform.runLater(() -> {
            toggleServerButton.setText("Stop Server");
            runningStatus.setText("Online");
            runningStatus.setTextFill(Color.web("#006400"));
            logTextArea.clear();
        });

        addToLog("Starting server...");
    }

    private void stopServer() {
        if (serverInstance != null) serverInstance.stopServer();
        if (serverThread != null && serverThread.isAlive()) serverThread.interrupt();
        serverRunning = false;

        Platform.runLater(() -> {
            toggleServerButton.setText("Start Server");
            runningStatus.setText("Offline");
            runningStatus.setTextFill(Color.web("#8B0000"));
            uptimeLabel.setText("00:00:00");
        });

        addToLog("Server stopped.");
    }

    private void addToLog(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logLine = "[" + timestamp + "] " + message;
        System.out.println(logLine);
        Platform.runLater(() -> logTextArea.appendText(logLine + "\n"));
    }

    private void closeApplication() {
        if (serverRunning) stopServer();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}