/*
this is the game over scene it appears right after a game ends whether its multiplayer or single player
it shows the game result and gives the option to do a rematch with the same player or go to home scene
 */
package client;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class GameOverScene {
    public final Scene scene;

    public GameOverScene(String resultText) {
        BorderPane root = new BorderPane();
        root.setPrefSize(950, 700);
        root.setStyle("-fx-background-color: #dceefb;");

        Label titleLabel = new Label("Connect-4");
        titleLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 60));
        titleLabel.setTextFill(Color.web("#3F6BB5"));

        BorderPane separator = new BorderPane();
        separator.setMaxWidth(650);
        separator.setMinHeight(3);
        separator.setStyle("-fx-background-color: " + String.format("#%02X%02X%02X", (int)(Color.web("#3F6BB5").getRed()*255), (int)(Color.web("#3F6BB5").getGreen()*255), (int)(Color.web("#3F6BB5").getBlue()*255)) + ";");

        VBox headerBox = new VBox(10, titleLabel, separator);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(25, 0, 15, 0));
        root.setTop(headerBox);

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 80));
        boolean isWin = resultText.toLowerCase().contains("win") || resultText.toLowerCase().contains("victory");
        Color resultColor;
        if (isWin) {
            resultColor = Color.web("#4CAF50");
        } else {
            resultColor = Color.web("#8B0000");
        }
        gameOverLabel.setTextFill(resultColor);

        Glow glow = new Glow();
        glow.setLevel(0.4);
        gameOverLabel.setEffect(glow);

        Region topSpacer = new Region();
        topSpacer.setPrefHeight(30);

        Label resultLabel = new Label(resultText);
        resultLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 38));
        resultLabel.setTextFill(Color.WHITE);
        resultLabel.setWrapText(true);
        resultLabel.setTextAlignment(TextAlignment.CENTER);
        resultLabel.setPadding(new Insets(20));

        VBox resultBox = new VBox(resultLabel);
        resultBox.setAlignment(Pos.CENTER);
        resultBox.setStyle("-fx-background-color: " + String.format("#%02X%02X%02X", (int)(Color.web("#3F6BB5").getRed()*255), (int)(Color.web("#3F6BB5").getGreen()*255), (int)(Color.web("#3F6BB5").getBlue()*255)) + "; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        resultBox.setPadding(new Insets(5));
        resultBox.setMaxWidth(600);

        Region bottomSpacer = new Region();
        bottomSpacer.setPrefHeight(120);

        VBox centerContent = new VBox(15, gameOverLabel, topSpacer, resultBox, bottomSpacer);
        centerContent.setAlignment(Pos.CENTER);
        root.setCenter(centerContent);

        Button homeBtn = new Button("Home");
        homeBtn.setPrefWidth(150);
        homeBtn.setPrefHeight(45);
        homeBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));
        String homeStyle = "-fx-background-color: #3F6BB5; -fx-text-fill: white; -fx-background-radius: 20;";
        String homeHover = "-fx-background-color: #5280ca; -fx-text-fill: white; -fx-background-radius: 20;";
        homeBtn.setStyle(homeStyle);
        homeBtn.setOnMouseEntered(e -> homeBtn.setStyle(homeHover));
        homeBtn.setOnMouseExited(e -> homeBtn.setStyle(homeStyle));
        homeBtn.setOnAction(e -> {
            SceneManager.setSinglePlayer(false);
            ClientConnection.send("CANCEL_WAIT");
            String playerName;
            if (SceneManager.getPlayerId() == 1) {
                playerName = SceneManager.getPlayerOneName();
            } else {
                playerName = SceneManager.getPlayerTwoName();
            }
            ClientMain.mainStage.setScene(SceneManager.getHomeScene(playerName));
        });

        Button rematchBtn = new Button("Rematch");
        rematchBtn.setPrefWidth(150);
        rematchBtn.setPrefHeight(45);
        rematchBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));
        String rematchStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 20;";
        String rematchHover = "-fx-background-color: #5dbd60; -fx-text-fill: white; -fx-background-radius: 20;";
        rematchBtn.setStyle(rematchStyle);
        rematchBtn.setOnMouseEntered(e -> rematchBtn.setStyle(rematchHover));
        rematchBtn.setOnMouseExited(e -> rematchBtn.setStyle(rematchStyle));
        rematchBtn.setOnAction(e -> {
            if (SceneManager.isSinglePlayer()) {
                SceneManager.setGameResult("");
                SceneManager.setSinglePlayer(true);
                String name;
                if (SceneManager.getPlayerId() == 1) {
                    name = SceneManager.getPlayerOneName();
                } else {
                    name = SceneManager.getPlayerTwoName();
                }
                ClientMain.mainStage.setScene(SceneManager.getSinglePlayerScene(name));
            } else {
                String opponentName;
                if (SceneManager.getPlayerId() == 1) {
                    opponentName = SceneManager.getPlayerTwoName();
                } else {
                    opponentName = SceneManager.getPlayerOneName();
                }
                ClientConnection.send("REMATCH:" + opponentName);

                String playerName;
                if (SceneManager.getPlayerId() == 1) {
                    playerName = SceneManager.getPlayerOneName();
                } else {
                    playerName = SceneManager.getPlayerTwoName();
                }
                ClientMain.mainStage.setScene(SceneManager.getWaitingScene(playerName));
            }
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setPrefWidth(150);
        exitBtn.setPrefHeight(45);
        exitBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));
        String exitStyle = "-fx-background-color: #8B0000; -fx-text-fill: white; -fx-background-radius: 20;";
        String exitHover = "-fx-background-color: #a40000; -fx-text-fill: white; -fx-background-radius: 20;";
        exitBtn.setStyle(exitStyle);
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(exitHover));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(exitStyle));
        exitBtn.setOnAction(e -> System.exit(0));

        HBox leftButtons = new HBox(20, homeBtn, rematchBtn);
        leftButtons.setAlignment(Pos.CENTER_LEFT);
        leftButtons.setPadding(new Insets(30));

        HBox rightButtons = new HBox(exitBtn);
        rightButtons.setAlignment(Pos.CENTER_RIGHT);
        rightButtons.setPadding(new Insets(30));

        BorderPane bottomPane = new BorderPane();
        bottomPane.setLeft(leftButtons);
        bottomPane.setRight(rightButtons);
        root.setBottom(bottomPane);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), centerContent);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(800), centerContent);
        scaleUp.setFromX(0.8);
        scaleUp.setFromY(0.8);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);

        SequentialTransition sequence = new SequentialTransition(fadeIn, scaleUp);
        sequence.play();

        scene = new Scene(root);
    }
}

