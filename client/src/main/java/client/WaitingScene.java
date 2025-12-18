/*
this is the waiting room scene where in multiplayer the user will wait if there isn't a second
client on the server and ready to play. once both player have entered the game starts right away.
there is an option to cancel and go back to home scene
 */
package client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class WaitingScene {
    public final Scene scene;
    private boolean keepChecking = true;

    public WaitingScene(String username) {
        BorderPane root = new BorderPane();
        root.setPrefSize(950, 700);
        root.setStyle("-fx-background-color: #dceefb;");


        Label title = new Label("Connect-4");
        title.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 80));
        title.setTextFill(Color.web("#305BAB"));
        VBox topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(40, 0, -10, 0));
        root.setTop(topBox);

        Label status = new Label("Waiting for Opponent...");
        status.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 40));
        status.setTextFill(Color.web("#305BAB"));
        VBox centerBox = new VBox(status);
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 18));
        cancelBtn.setPrefSize(150, 40);
        String cancelStyle = "-fx-background-color: #e2ecfb; -fx-border-color: #305BAB; "
                + "-fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: #BD0A0A;";
        String cancelHover = "-fx-background-color: #f5c6cb; -fx-border-color: #305BAB; "
                + "-fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: #BD0A0A;";
        cancelBtn.setStyle(cancelStyle);
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(cancelHover));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(cancelStyle));
        cancelBtn.setOnAction(e -> {
            keepChecking = false;
            ClientConnection.send("CANCEL_WAIT");
            ClientConnection.flushMessages();
            SceneManager.setPlayerId(0);
            SceneManager.setPlayerNames("Player 1", "Player 2");
            ClientMain.mainStage.setScene(SceneManager.getHomeScene(username));
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 16));
        exitBtn.setPrefSize(120, 40);
        String exitStyle  = "-fx-background-color: #8B0000; -fx-text-fill: white; -fx-background-radius: 20;";
        String exitHover  = "-fx-background-color: #a40000; -fx-text-fill: white; -fx-background-radius: 20;";
        exitBtn.setStyle(exitStyle);
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(exitHover));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(exitStyle));
        exitBtn.setOnAction(e -> System.exit(0));

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(0, 20, 40, 20));
        bottom.setPrefWidth(950);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bottom.getChildren().addAll(cancelBtn, spacer, exitBtn);
        bottom.setAlignment(Pos.CENTER);
        root.setBottom(bottom);

        this.scene = new Scene(root);

        ClientConnection.flushMessages();
        ClientConnection.send("HOME");
        ClientConnection.flushMessages();

        ClientConnection.send("WAIT_FOR_GAME");

        new Thread(() -> {
            while (keepChecking) {
                String msg = ClientConnection.read();
                if (msg != null && msg.startsWith("START:")) {
                    keepChecking = false;

                    String[] parts = msg.split(":");
                    int playerId = Integer.parseInt(parts[1]);
                    String p1 = "Player 1";
                    if (parts.length > 2) {
                        p1 = parts[2];
                    }
                    String p2 = "Player 2";
                    if (parts.length > 3) {
                        p2 = parts[3];
                    }
                    SceneManager.setPlayerId(playerId);
                    SceneManager.setPlayerNames(p1, p2);

                    Platform.runLater(() ->
                            ClientMain.mainStage.setScene(SceneManager.getMultiplayerScene(username))
                    );
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ignored) {

                }
            }
        }).start();
    }
}
