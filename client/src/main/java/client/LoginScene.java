/*
this is the login scene where the user can enter their username in and if it's valid(not being used by someone else)
they will be able to enter and connect to the server
 */
package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginScene {
    public final Scene scene;

    public LoginScene() {
        BorderPane root = new BorderPane();
        root.setPrefSize(950, 700);
        root.setStyle("-fx-background-color: #dceefb;");

        VBox topBox = new VBox(20);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(30, 0, 10, 0));

        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER);
        Label welcome = new Label("WELCOME ");
        welcome.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 60));
        welcome.setTextFill(Color.web("#BD0A0A"));

        Label to = new Label("TO ");
        to.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 60));
        to.setTextFill(Color.web("#3366CC"));

        Label connect = new Label("CONNECT ");
        connect.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 60));
        connect.setTextFill(Color.web("#BD0A0A"));

        Label four = new Label("4");
        four.setFont(Font.font("Comic Sans MS", FontWeight.EXTRA_BOLD, 60));
        four.setTextFill(Color.web("#3366CC"));

        titleBox.getChildren().addAll(welcome, to, connect, four);

        Image logoImage = new Image(getClass().getResourceAsStream("/logoconnect.png"));
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(300);
        logoView.setFitHeight(300);
        logoView.setPreserveRatio(true);

        Circle clip = new Circle(150);
        clip.setCenterX(150);
        clip.setCenterY(150);
        logoView.setClip(clip);

        topBox.getChildren().addAll(titleBox, logoView);
        root.setTop(topBox);

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Enter Username:");
        usernameLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 40));
        usernameLabel.setTextFill(Color.web("#3366CC"));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter a username");
        usernameField.setPrefWidth(250);
        usernameField.setMaxWidth(250);
        usernameField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20; -fx-padding: 5;");

        Label feedbackLabel = new Label();
        feedbackLabel.setTextFill(Color.RED);
        feedbackLabel.setVisible(false);

        Button loginBtn = new Button("LOG IN");
        loginBtn.setPrefWidth(200);
        loginBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        loginBtn.setStyle("-fx-background-color: #dceefb; -fx-border-color: #3366CC; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: #3366CC;");

        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #c0d9ff; -fx-border-color: #3366CC; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: #3366CC;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #dceefb; -fx-border-color: #3366CC; -fx-border-radius: 20; -fx-background-radius: 20; -fx-text-fill: #3366CC;"));

        loginBtn.setOnAction(e -> {
            String name = usernameField.getText().trim();
            if (name.isEmpty()) {
                feedbackLabel.setText("username can't be empty");
                feedbackLabel.setVisible(true);
                return;
            }

            boolean connected = ClientConnection.connect("localhost", 12555);
            if (!connected) {
                feedbackLabel.setText("the server is not available");
                feedbackLabel.setVisible(true);
                return;
            }

            boolean loginSuccess = ClientConnection.sendUsername(name);
            if (loginSuccess) {
                ClientMain.mainStage.setScene(SceneManager.getHomeScene(name));
            } else {
                feedbackLabel.setText("the username is already taken");
                feedbackLabel.setVisible(true);
            }
        });

        centerBox.getChildren().addAll(usernameLabel, usernameField, loginBtn, feedbackLabel);
        root.setCenter(centerBox);

        Button exitBtn = new Button("Exit");
        exitBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
        exitBtn.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-background-radius: 20;");
        exitBtn.setPrefWidth(150);
        exitBtn.setPrefHeight(40);

        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle("-fx-background-color: #a40000; -fx-text-fill: white; -fx-background-radius: 20;"));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-background-radius: 20;"));

        exitBtn.setOnAction(e -> System.exit(0));

        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(0, 20, 40, 0));
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        bottomBox.getChildren().add(exitBtn);
        root.setBottom(bottomBox);

        this.scene = new Scene(root, 950, 700);
    }
}
