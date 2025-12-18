/*
this shows the Stats for the user for all their multiplayer games
 */
package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.*;

public class StatsScene {
    public final Scene scene;

    public StatsScene(String username) {
        this(username, ClientConnection.getStats(username));
    }

    public StatsScene(String username, String[] statsArr) {
        int wins = 0;
        int losses = 0;
        int draws = 0;
        try {
            String[] stats = ClientConnection.getStats(username);
            if (stats.length == 3) {
                wins = Integer.parseInt(stats[0]);
                losses = Integer.parseInt(stats[1]);
                draws = Integer.parseInt(stats[2]);
            }
        } catch (Exception e) {
            File dir = new File("user_stats");
            if (!dir.exists()) dir.mkdirs();
            File statsFile = new File("user_stats/" + username + ".txt");
            if (!statsFile.exists()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(statsFile))) {
                    writer.println("WINS:0");
                    writer.println("LOSSES:0");
                    writer.println("DRAWS:0");
                } catch (IOException ignored) {}
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(statsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("WINS:")) wins = Integer.parseInt(line.substring(5).trim());
                    else if (line.startsWith("LOSSES:")) losses = Integer.parseInt(line.substring(7).trim());
                    else if (line.startsWith("DRAWS:")) draws = Integer.parseInt(line.substring(6).trim());
                }
            } catch (IOException ignored) {}
        }

        BorderPane root = new BorderPane();
        root.setPrefSize(950, 700);
        root.setStyle("-fx-background-color: " + "linear-gradient(to bottom, #E0F7FF, #C5E8FF)" + ";");

        Label title = new Label("Connect-4");
        title.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 60));
        title.setTextFill(Color.web("#2A3990"));
        Reflection reflection = new Reflection();
        reflection.setFraction(0.3);
        title.setEffect(reflection);

        Rectangle line = new Rectangle(300, 4);
        line.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[]{
                new Stop(0, Color.web("#2A3990", 0.1)),
                new Stop(0.5, Color.web("#4BD0F5")),
                new Stop(1, Color.web("#2A3990", 0.1))
        }));
        line.setArcWidth(4);
        line.setArcHeight(4);

        VBox titleBox = new VBox(5, title, line);
        titleBox.setAlignment(Pos.CENTER);
        StackPane topPane = new StackPane(titleBox);
        topPane.setPadding(new Insets(40, 0, 20, 0));
        root.setTop(topPane);

        VBox centerBox = new VBox(30);
        centerBox.setPadding(new Insets(20));
        centerBox.setAlignment(Pos.CENTER);

        HBox mainCard = new HBox(30);
        mainCard.setAlignment(Pos.CENTER);
        mainCard.setPadding(new Insets(40));
        mainCard.setMaxWidth(800);
        mainCard.setMinHeight(400);
        mainCard.setStyle("-fx-background-color: " + "linear-gradient(to bottom right, #2A3990, #1A2970)" + ";" + "-fx-background-radius: 20;" + "-fx-border-radius: 20;" + "-fx-border-color: rgba(255,255,255,0.3);" + "-fx-border-width: 1;" + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");

        Label statsFor = new Label("Multiplayer Stats for " + username);
        statsFor.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 41));
        statsFor.setTextFill(Color.SKYBLUE);
        Rectangle underline = new Rectangle(statsFor.getText().length() * 11, 3);
        underline.setFill(Color.web("#4BD0F5"));
        underline.setArcWidth(3);
        underline.setArcHeight(3);

        VBox userBox = new VBox(5, statsFor, underline);
        userBox.setAlignment(Pos.CENTER);

        VBox logoBox = new VBox(20);
        logoBox.setAlignment(Pos.CENTER);
        ImageView logo;

        logo = new ImageView(new Image(getClass().getResourceAsStream("/stats.png")));

        logo.setFitHeight(220);
        logo.setFitWidth(220);
        logoBox.getChildren().add(logo);

        VBox cardBox = new VBox(15);
        cardBox.setAlignment(Pos.CENTER);

        String[][] stats = {
                {"Wins", String.valueOf(wins), "#2CB67D"}, {"Losses", String.valueOf(losses), "#FF5757"}, {"Draws", String.valueOf(draws), "#F9C846"}
        };

        for (String[] stat : stats) {
            HBox card = new HBox(15);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(10));
            card.setMinWidth(220);
            card.setMaxWidth(220);
            card.setStyle("-fx-background-color: rgba(255,255,255,0.15);" + "-fx-background-radius: 15;" + "-fx-border-radius: 15;" + "-fx-border-color: " + stat[2] + ";" + "-fx-border-width: 2;");

            Rectangle indicator = new Rectangle(6, 40);
            indicator.setFill(Color.web(stat[2]));
            indicator.setArcWidth(6);
            indicator.setArcHeight(6);

            Label label = new Label(stat[0]);
            label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
            label.setTextFill(Color.WHITE);

            Label value = new Label(stat[1]);
            value.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 28));
            value.setTextFill(Color.web(stat[2]));

            VBox statText = new VBox(2, label, value);
            statText.setAlignment(Pos.CENTER_LEFT);
            card.getChildren().addAll(indicator, statText);
            cardBox.getChildren().add(card);
        }

        BorderPane statsPanel = new BorderPane();
        statsPanel.setTop(userBox);
        BorderPane.setMargin(userBox, new Insets(0, 0, 30, 0));
        statsPanel.setLeft(logoBox);
        BorderPane.setMargin(logoBox, new Insets(0, 30, 0, 20));
        statsPanel.setRight(cardBox);
        BorderPane.setAlignment(userBox, Pos.CENTER);
        BorderPane.setMargin(cardBox, new Insets(0, 20, 0, 30));

        mainCard.getChildren().add(statsPanel);
        centerBox.getChildren().add(mainCard);
        root.setCenter(centerBox);

        Button home = new Button("Home");
        Button exit = new Button("Exit");

        String homeStyle = "-fx-background-color: #305BAB; -fx-text-fill: white; -fx-background-radius: 20;";
        String homeHover = "-fx-background-color: #3d6bd9;";
        String exitStyle = "-fx-background-color: #8B0000; -fx-text-fill: white; -fx-background-radius: 20;";
        String exitHover = "-fx-background-color: #a40000;";

        home.setStyle(homeStyle);
        home.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        home.setPrefSize(150, 45);
        home.setOnMouseEntered(e -> home.setStyle(homeHover + " -fx-text-fill: white; -fx-background-radius: 20;"));
        home.setOnMouseExited(e -> home.setStyle(homeStyle));
        home.setOnAction(e -> ClientMain.mainStage.setScene(SceneManager.getHomeScene(username)));

        exit.setStyle(exitStyle);
        exit.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        exit.setPrefSize(150, 45);
        exit.setOnMouseEntered(e -> exit.setStyle(exitHover + " -fx-text-fill: white; -fx-background-radius: 20;"));
        exit.setOnMouseExited(e -> exit.setStyle(exitStyle));
        exit.setOnAction(e -> System.exit(0));

        HBox buttonBox = new HBox(20, home, exit);
        buttonBox.setAlignment(Pos.CENTER);
        BorderPane bottom = new BorderPane(buttonBox);
        bottom.setPadding(new Insets(30));
        root.setBottom(bottom);

        this.scene = new Scene(root);
    }
}

