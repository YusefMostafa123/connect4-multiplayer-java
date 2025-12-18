/*
this is the home scene the user will see it after logging in and it has the options
that the suer can chose from in the game like doing multiplayer(playing with someone on the server),
doing single player( playing against the AI), Stats see you Stats for multiplayer games you've played,
and a logout button to log out and go back to the login scene
 */
package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HomeScene {
   public final Scene scene;

    public HomeScene(String username) {
        BorderPane root = new BorderPane();
        root.setPrefSize(950, 700);
        root.setStyle("-fx-background-color: #dceefb;");

        Label title = new Label("Connect-4");
        title.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 80));
        title.setTextFill(Color.web("#305BAB"));

        VBox topBox = new VBox(title);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(40, 0, 0, 0));
        root.setTop(topBox);

        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);

        Label welcome = new Label("Welcome " + username);
        welcome.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 50));
        welcome.setTextFill(Color.web("#BD0A0A"));
        welcome.setPadding(new Insets(0, 0, 60, 0));

        Button btnMultiplayer = new Button("Multiplayer");
        btnMultiplayer.setPrefWidth(300);
        btnMultiplayer.setPrefHeight(50);
        btnMultiplayer.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 20));
        String baseStyle = "-fx-background-color: #e2ecfb; -fx-border-color: #305BAB; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: #305BAB;";
        String hoverStyle = "-fx-background-color: #c7dcf7; -fx-border-color: #305BAB; -fx-border-radius: 15; -fx-background-radius: 15; -fx-text-fill: #305BAB;";
        btnMultiplayer.setStyle(baseStyle);
        btnMultiplayer.setOnMouseEntered(e -> btnMultiplayer.setStyle(hoverStyle));
        btnMultiplayer.setOnMouseExited(e -> btnMultiplayer.setStyle(baseStyle));

        Button btnSinglePlayer = new Button("Single Player (AI)");
        btnSinglePlayer.setPrefWidth(300);
        btnSinglePlayer.setPrefHeight(50);
        btnSinglePlayer.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 20));
        btnSinglePlayer.setStyle(baseStyle);
        btnSinglePlayer.setOnMouseEntered(e -> btnSinglePlayer.setStyle(hoverStyle));
        btnSinglePlayer.setOnMouseExited(e -> btnSinglePlayer.setStyle(baseStyle));

        Button btnStats = new Button("Stats");
        btnStats.setPrefWidth(300);
        btnStats.setPrefHeight(50);
        btnStats.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 20));
        btnStats.setStyle(baseStyle);
        btnStats.setOnMouseEntered(e -> btnStats.setStyle(hoverStyle));
        btnStats.setOnMouseExited(e -> btnStats.setStyle(baseStyle));

        centerBox.getChildren().addAll(welcome, btnMultiplayer, btnSinglePlayer, btnStats);
        root.setCenter(centerBox);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 16));
        logoutBtn.setPrefWidth(180);
        logoutBtn.setPrefHeight(40);
        String logoutStyle = "-fx-background-color: #305BAB; -fx-text-fill: white; -fx-background-radius: 20;";
        String logoutHover = "-fx-background-color: #3d6bd9; -fx-text-fill: white; -fx-background-radius: 20;";
        logoutBtn.setStyle(logoutStyle);
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutHover));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutStyle));
        logoutBtn.setOnAction(e -> {
            ClientConnection.send("LOGOUT");
            ClientMain.mainStage.setScene(SceneManager.getLoginScene());
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 16));
        exitBtn.setPrefWidth(120);
        exitBtn.setPrefHeight(40);
        String exitStyle = "-fx-background-color: #8B0000; -fx-text-fill: white; -fx-background-radius: 20;";
        String exitHover = "-fx-background-color: #a40000; -fx-text-fill: white; -fx-background-radius: 20;";
        exitBtn.setStyle(exitStyle);
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(exitHover));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(exitStyle));
        exitBtn.setOnAction(e -> System.exit(0));

        HBox bottomBox = new HBox(10, logoutBtn, exitBtn);
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        root.setBottom(bottomBox);


        this.scene = new Scene(root, 950, 700);

        btnMultiplayer.setOnAction(e -> ClientMain.mainStage.setScene(SceneManager.getWaitingScene(username)));
        btnSinglePlayer.setOnAction(e -> ClientMain.mainStage.setScene(SceneManager.getSinglePlayerScene(username)));
        btnStats.setOnAction(e -> ClientMain.mainStage.setScene(SceneManager.getStatsScene(username)));
    }
}
