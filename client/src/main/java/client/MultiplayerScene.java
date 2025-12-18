/*
this is where the multiplayer scene is maneged and actual game is played.
this scene has the game board, give each client there turn correctly,
logic chat and it detects win a win/lose/draw happens.
 */
package client;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class MultiplayerScene {
    public final Scene scene;
    private String username;
    private boolean isMyTurn;
    private int[][] board = new int[6][7];
    private GridPane gridPane = new GridPane();
    private Label turnLabel = new Label("Waiting...");
    private Circle[][] circleGrid = new Circle[6][7];
    private int myPlayerId;
    private Label player1Label;
    private Label player2Label;

    private VBox chatPanel;
    private VBox chatBox;
    private TextField chatInput;
    private boolean isChatVisible = false;
    private Button chatBtn;

    private StackPane rootPane;

    public MultiplayerScene(String username, int playerId) {
        this.username = username;
        this.myPlayerId = playerId;
        this.isMyTurn = (playerId == 1);

        rootPane = new StackPane();
        rootPane.setPrefSize(950, 700);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPrefSize(950, 700);
        mainLayout.setStyle("-fx-background-color:#DCEEFBFF;");
        mainLayout.setPadding(new Insets(20));

        rootPane.getChildren().add(mainLayout);

        Label title = new Label("Connect-4");
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 48));
        title.setTextFill(Color.valueOf("#3a66b0"));
        BorderPane.setAlignment(title, Pos.CENTER);
        mainLayout.setTop(title);

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);

        HBox arrowRow = new HBox(20);
        arrowRow.setAlignment(Pos.CENTER);
        arrowRow.setPadding(new Insets(10));

        for (int col = 0; col < 7; col++) {
            int finalCol = col;
            Button dropBtn = new Button("▼");
            dropBtn.setPrefSize(60, 40);
            String normalStyle = "-fx-background-color: #3a66b0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 10;";
            String hoverStyle = "-fx-background-color: #2a4d80; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 10;";

            dropBtn.setStyle(normalStyle);
            dropBtn.setOnMouseEntered(e -> dropBtn.setStyle(hoverStyle));
            dropBtn.setOnMouseExited(e -> dropBtn.setStyle(normalStyle));

            dropBtn.setOnAction(e -> {
                if (!isMyTurn) return;

                for (int row = 5; row >= 0; row--) {
                    if (board[row][finalCol] == 0) {
                        animateTokenDrop(row, finalCol, myPlayerId);
                        board[row][finalCol] = myPlayerId;

                        if (checkWin(row, finalCol, myPlayerId)) {
                            ClientConnection.send("WIN");
                            turnLabel.setText("YOU WIN!");
                            SceneManager.setGameResult("You Win!");
                            ClientMain.mainStage.setScene(SceneManager.getGameOverScene());
                            return;
                        }

                        boolean boardIsFull = true;
                        for (int c = 0; c < 7; c++) {
                            if (board[0][c] == 0) {
                                boardIsFull = false;
                                break;
                            }
                        }

                        if (boardIsFull) {
                            ClientConnection.send("DRAW");
                            turnLabel.setText("DRAW");
                            SceneManager.setGameResult("Draw!");
                            ClientMain.mainStage.setScene(SceneManager.getGameOverScene());
                            return;
                        }

                        ClientConnection.send("MOVE:" + finalCol);
                        isMyTurn = false;

                        Platform.runLater(() -> {
                            turnLabel.setText("OPPONENT'S TURN");
                            turnLabel.setStyle("-fx-background-color: #f0e6d3; -fx-text-fill: #cd853f; -fx-border-color: #cd853f; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10;");

                            if ((myPlayerId == 1 && isMyTurn) || (myPlayerId == 2 && !isMyTurn)) {
                                player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                                player1Label.setTextFill(Color.valueOf("#ff0000"));
                                player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                                player2Label.setTextFill(Color.valueOf("#ccaa00"));
                            } else {
                                player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                                player2Label.setTextFill(Color.valueOf("#ffcc00"));
                                player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                                player1Label.setTextFill(Color.valueOf("#cc0000"));
                            }
                        });
                        return;
                    }
                }
            });
            arrowRow.getChildren().add(dropBtn);
        }

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setStyle("-fx-background-color: #dceefb; -fx-border-color: #3a66b0; -fx-border-width: 3px; -fx-padding: 5px; -fx-hgap: 5; -fx-vgap: 5;");

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Circle circle = new Circle(30);
                circle.setFill(Color.WHITE);
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(1.5);
                StackPane cellPane = new StackPane();
                cellPane.setStyle("-fx-background-color: #a0c0ff;");
                cellPane.getChildren().add(circle);
                GridPane.setMargin(cellPane, new Insets(2));
                gridPane.add(cellPane, col, row);
                circleGrid[row][col] = circle;
            }
        }

        turnLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        turnLabel.setTextFill(Color.BLACK);
        turnLabel.setMinWidth(100);
        turnLabel.setStyle("-fx-background-color: white; -fx-border-color: #999999; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 10;");
        VBox.setMargin(turnLabel, new Insets(20, 0, 0, 0));

        centerBox.getChildren().addAll(arrowRow, gridPane, turnLabel);
        mainLayout.setCenter(centerBox);

        VBox leftPanel = new VBox(20);
        leftPanel.setPrefWidth(200);
        leftPanel.setPadding(new Insets(0, 100, -20, 20));
        leftPanel.setAlignment(Pos.CENTER);

        player1Label = new Label("Player 1\n" + SceneManager.getPlayerOneName());
        player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));

        player1Label.setTextFill(Color.valueOf("#cc0000"));
        player1Label.setAlignment(Pos.TOP_LEFT);
        player1Label.setWrapText(true);

        Circle redDot = new Circle(25, Color.RED);
        redDot.setStroke(Color.BLACK);
        redDot.setStrokeWidth(2);

        leftPanel.getChildren().addAll(player1Label, redDot);
        mainLayout.setLeft(leftPanel);
        VBox rightPanel = new VBox(20);
        rightPanel.setPrefWidth(200);

        leftPanel.setPadding(new Insets(0, 100, 20, 20));
        rightPanel.setAlignment(Pos.CENTER);

        player2Label = new Label("Player 2\n" + SceneManager.getPlayerTwoName());
        player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));

        player2Label.setTextFill(Color.valueOf("#ccaa00"));
        player2Label.setAlignment(Pos.TOP_LEFT);
        player2Label.setWrapText(true);

        Circle yellowDot = new Circle(25, Color.GOLD);
        yellowDot.setStroke(Color.BLACK);
        yellowDot.setStrokeWidth(2);

        rightPanel.getChildren().addAll(player2Label, yellowDot);
        mainLayout.setRight(rightPanel);

        chatPanel = new VBox(10);
        chatPanel.setPrefSize(200, 435);
        chatPanel.setMaxSize(200, 435);
        chatPanel.setStyle("-fx-background-color: #cfe2ff; -fx-border-color: #3a66b0; -fx-border-width: 2px; -fx-border-radius: 10; -fx-background-radius: 10;");
        chatPanel.setPadding(new Insets(10));
        chatPanel.setVisible(false);

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label chatTitle = new Label("Chat");
        chatTitle.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
        chatTitle.setTextFill(Color.valueOf("#3a66b0"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("✖");
        closeBtn.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-max-width: 30; -fx-max-height: 30;");

        String closeNormal = "-fx-background-color: #ff6666; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-max-width: 30; -fx-max-height: 30;";
        String closeHover = "-fx-background-color: #cc4444; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30; -fx-max-width: 30; -fx-max-height: 30;";
        closeBtn.setStyle(closeNormal);
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(closeHover));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(closeNormal));

        closeBtn.setOnAction(e -> {
            isChatVisible = !isChatVisible;

            if (isChatVisible) {
                chatBtn.setStyle("-fx-background-color: #004400; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;");
                chatPanel.setVisible(true);
                chatInput.requestFocus();
            } else {
                chatBtn.setStyle("-fx-background-color: #006600; -fx-text-fill: #bdf5c6; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;");
                chatPanel.setVisible(false);
            }
        });

        headerBox.getChildren().addAll(chatTitle, spacer, closeBtn);

        chatBox = new VBox(5);
        chatBox.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px; -fx-border-radius: 5;");

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(200);
        scrollPane.setStyle("-fx-background: white; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        HBox inputBox = new HBox(5);
        inputBox.setAlignment(Pos.CENTER);

        chatInput = new TextField();
        chatInput.setPromptText("Type a message...");
        chatInput.setPrefHeight(30);
        chatInput.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        HBox.setHgrow(chatInput, Priority.ALWAYS);

        Button sendBtn = new Button("➤");
        sendBtn.setPrefHeight(30);
        sendBtn.setStyle("-fx-font-size: 16px;");

        String sendNormal = "-fx-background-color: #3a66b0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 5;";
        String sendHover = "-fx-background-color: #2a4d80; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 5;";
        sendBtn.setStyle(sendNormal);
        sendBtn.setOnMouseEntered(e -> sendBtn.setStyle(sendHover));
        sendBtn.setOnMouseExited(e -> sendBtn.setStyle(sendNormal));

        sendBtn.setOnAction(e -> {
            String message = chatInput.getText().trim();
            if (!message.isEmpty()) {
                ClientConnection.send("CHAT:" + username + ":" + message);


                HBox messageBox = new HBox(5);
                messageBox.setPadding(new Insets(5));

                Circle avatar = new Circle(8);
                if (myPlayerId == 1) {
                    avatar.setFill(Color.RED);
                } else {
                    avatar.setFill(Color.GOLD);
                }

                avatar.setStroke(Color.BLACK);
                avatar.setStrokeWidth(1);

                Label messageLabel = new Label(username + ": " + message);
                messageLabel.setWrapText(true);
                messageLabel.setStyle("-fx-background-color: #e6f2ff; -fx-padding: 5; -fx-background-radius: 5;");
                HBox.setHgrow(messageLabel, Priority.ALWAYS);

                messageBox.getChildren().addAll(avatar, messageLabel);
                messageBox.setAlignment(Pos.CENTER_RIGHT);

                FadeTransition fade = new FadeTransition(Duration.millis(200), messageBox);
                fade.setFromValue(0);
                fade.setToValue(1);

                Platform.runLater(() -> {
                    chatBox.getChildren().add(messageBox);
                    fade.play();

                    chatBox.layout();
                    ScrollPane scrollPane1 = (ScrollPane) chatBox.getParent();
                    scrollPane1.setVvalue(1.0);
                });

                chatInput.clear();
            }
        });

        chatInput.setOnAction(e -> {
            String message = chatInput.getText().trim();
            if (!message.isEmpty()) {
                ClientConnection.send("CHAT:" + username + ":" + message);

                HBox messageBox = new HBox(5);
                messageBox.setPadding(new Insets(5));

                Circle avatar = new Circle(8);
                if (myPlayerId == 1) {
                    avatar.setFill(Color.RED);
                } else {
                    avatar.setFill(Color.GOLD);
                }

                avatar.setStroke(Color.BLACK);
                avatar.setStrokeWidth(1);

                Label messageLabel = new Label(username + ": " + message);
                messageLabel.setWrapText(true);
                messageLabel.setStyle("-fx-background-color: #e6f2ff; -fx-padding: 5; -fx-background-radius: 5;");
                HBox.setHgrow(messageLabel, Priority.ALWAYS);

                messageBox.getChildren().addAll(avatar, messageLabel);
                messageBox.setAlignment(Pos.CENTER_RIGHT);

                FadeTransition fade = new FadeTransition(Duration.millis(200), messageBox);
                fade.setFromValue(0);
                fade.setToValue(1);

                Platform.runLater(() -> {
                    chatBox.getChildren().add(messageBox);
                    fade.play();

                    chatBox.layout();
                    ScrollPane scrollPane1 = (ScrollPane) chatBox.getParent();
                    scrollPane1.setVvalue(1.0);
                });

                chatInput.clear();
            }
        });

        inputBox.getChildren().addAll(chatInput, sendBtn);

        chatPanel.getChildren().addAll(headerBox, scrollPane, inputBox);

        StackPane.setAlignment(chatPanel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(chatPanel, new Insets(10, 10, 128, 10));

        Button quitBtn = new Button("Quit Game");
        quitBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
        quitBtn.setPrefWidth(150);
        quitBtn.setPrefHeight(40);

        String quitNormal = "-fx-background-color: #333333; -fx-text-fill: #ff3333; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        String quitHover = "-fx-background-color: #222222; -fx-text-fill: #ff6666; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        quitBtn.setStyle(quitNormal);
        quitBtn.setOnMouseEntered(e -> quitBtn.setStyle(quitHover));
        quitBtn.setOnMouseExited(e -> quitBtn.setStyle(quitNormal));

        quitBtn.setOnAction(e -> {
            ClientConnection.send("QUIT_GAME");
            ClientMain.mainStage.setScene(SceneManager.getHomeScene(username));
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setPrefWidth(150);
        exitBtn.setPrefHeight(40);

        String exitNormal = "-fx-background-color: #8B0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        String exitHover = "-fx-background-color: #a80000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        exitBtn.setStyle(exitNormal);
        exitBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(exitHover));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(exitNormal));

        exitBtn.setOnAction(e -> System.exit(0));

        chatBtn = new Button("Chat");
        chatBtn.setPrefWidth(150);
        chatBtn.setPrefHeight(40);
        chatBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));

        String chatNormal = "-fx-background-color: #006600; -fx-text-fill: #bdf5c6; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        String chatHover = "-fx-background-color: #004400; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        chatBtn.setStyle(chatNormal);
        chatBtn.setOnMouseEntered(e -> chatBtn.setStyle(chatHover));
        chatBtn.setOnMouseExited(e -> chatBtn.setStyle(chatNormal));

        chatBtn.setOnAction(e -> {
            isChatVisible = !isChatVisible;

            if (isChatVisible) {
                chatBtn.setStyle("-fx-background-color: #004400; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;");
                chatPanel.setVisible(true);
                chatInput.requestFocus();
            } else {
                chatBtn.setStyle("-fx-background-color: #006600; -fx-text-fill: #bdf5c6; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;");
                chatPanel.setVisible(false);
            }
        });

        HBox bottomRow = new HBox();
        bottomRow.setSpacing(20);
        bottomRow.setAlignment(Pos.CENTER);

        VBox leftButtons = new VBox(10, quitBtn, exitBtn);
        leftButtons.setAlignment(Pos.CENTER_LEFT);
        leftButtons.setPadding(new Insets(-55, 0, 0, 0));

        VBox rightButtons = new VBox(chatBtn);
        rightButtons.setAlignment(Pos.BOTTOM_RIGHT);
        rightButtons.setPadding(new Insets(-55, 0, 0, 0));

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        bottomRow.getChildren().addAll(leftButtons, bottomSpacer, rightButtons);
        mainLayout.setBottom(bottomRow);

        Platform.runLater(() -> {
            if (isMyTurn) {
                turnLabel.setText("YOUR TURN");
                turnLabel.setStyle("-fx-background-color: #d9f0d3; -fx-text-fill: #2e8b57; -fx-border-color: #2e8b57; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10;");
            } else {
                turnLabel.setText("OPPONENT'S TURN");
                turnLabel.setStyle("-fx-background-color: #f0e6d3; -fx-text-fill: #cd853f; -fx-border-color: #cd853f; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10;");
            }

            if ((myPlayerId == 1 && isMyTurn) || (myPlayerId == 2 && !isMyTurn)) {
                player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                player1Label.setTextFill(Color.valueOf("#ff0000"));
                player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                player2Label.setTextFill(Color.valueOf("#ccaa00"));
            } else {
                player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                player2Label.setTextFill(Color.valueOf("#ffcc00"));
                player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                player1Label.setTextFill(Color.valueOf("#cc0000"));
            }
        });

        rootPane.getChildren().add(chatPanel);

        scene = new Scene(rootPane, 950, 700);

        new Thread(() -> {
            while (true) {
                String msg = ClientConnection.read();
                if (msg == null) break;

                if (msg.startsWith("CHAT:")) {
                    String[] parts = msg.split(":", 3);
                    if (parts.length == 3) {
                        String sender = parts[1];
                        String text = parts[2];

                        HBox messageBox = new HBox(5);
                        messageBox.setPadding(new Insets(5));

                        Circle avatar = new Circle(8);
                        if (myPlayerId == 1) {
                            avatar.setFill(Color.GOLD);
                        } else {
                            avatar.setFill(Color.RED);
                        }
                        avatar.setStroke(Color.BLACK);
                        avatar.setStrokeWidth(1);

                        Label messageLabel = new Label(sender + ": " + text);
                        messageLabel.setWrapText(true);
                        messageLabel.setStyle("-fx-background-color: #f2f2f2; -fx-padding: 5; -fx-background-radius: 5;");

                        HBox.setHgrow(messageLabel, Priority.ALWAYS);
                        messageBox.getChildren().addAll(avatar, messageLabel);
                        messageBox.setAlignment(Pos.CENTER_LEFT);

                        FadeTransition fade = new FadeTransition(Duration.millis(200), messageBox);
                        fade.setFromValue(0);
                        fade.setToValue(1);

                        Platform.runLater(() -> {
                            chatBox.getChildren().add(messageBox);
                            fade.play();

                            chatBox.layout();
                            ScrollPane scrollPane1 = (ScrollPane) chatBox.getParent();
                            scrollPane1.setVvalue(1.0);

                            if (!isChatVisible) {
                                chatBtn.setStyle("-fx-background-color: #006600; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0.0, 0, 1);");
                            }
                        });
                    }
                    continue;
                }

                if (msg.startsWith("MOVE:")) {
                    int col = Integer.parseInt(msg.split(":")[1]);
                    Platform.runLater(() -> {
                        int row = -1;
                        for (int r = 5; r >= 0; r--) {
                            if (board[r][col] == 0) {
                                int opponentId;
                                if (myPlayerId == 1) {
                                    opponentId = 2;
                                } else {
                                    opponentId = 1;
                                }

                                if (opponentId == 1) {
                                    circleGrid[r][col].setFill(Color.RED);
                                } else {
                                    circleGrid[r][col].setFill(Color.GOLD);
                                }
                                board[r][col] = opponentId;
                                row = r;
                                break;
                            }
                        }

                        int opponentId;
                        if (myPlayerId == 1) {
                            opponentId = 2;
                        } else {
                            opponentId = 1;
                        }

                        if (checkWin(row, col, opponentId)) {
                            turnLabel.setText("YOU LOSE");
                            SceneManager.setGameResult("You Lose!");
                            ClientMain.mainStage.setScene(SceneManager.getGameOverScene());
                        } else {
                            isMyTurn = true;

                            turnLabel.setText("YOUR TURN");
                            turnLabel.setStyle("-fx-background-color: #d9f0d3; -fx-text-fill: #2e8b57; -fx-border-color: #2e8b57; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10;");

                            if ((myPlayerId == 1 && isMyTurn) || (myPlayerId == 2 && !isMyTurn)) {
                                player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                                player1Label.setTextFill(Color.valueOf("#ff0000"));
                                player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                                player2Label.setTextFill(Color.valueOf("#ccaa00"));
                            } else {
                                player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                                player2Label.setTextFill(Color.valueOf("#ffcc00"));
                                player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
                                player1Label.setTextFill(Color.valueOf("#cc0000"));
                            }
                        }
                    });
                } else if (msg.equals("WIN")) {
                    Platform.runLater(() -> {
                        turnLabel.setText("YOU WIN!");
                        SceneManager.setGameResult("You Win!");
                        ClientMain.mainStage.setScene(SceneManager.getGameOverScene());
                    });
                    break;
                } else if (msg.equals("LOSE")) {
                    Platform.runLater(() -> {
                        turnLabel.setText("YOU LOSE");
                        SceneManager.setGameResult("You Lose!");
                        ClientMain.mainStage.setScene(SceneManager.getGameOverScene());
                    });
                    break;
                } else if (msg.equals("DRAW")) {
                    Platform.runLater(() -> {
                        turnLabel.setText("DRAW");
                        SceneManager.setGameResult("Draw!");
                        ClientMain.mainStage.setScene(SceneManager.getGameOverScene());
                    });
                    break;
                } else if (msg.equals("QUIT_GAME")) {
                    Platform.runLater(() -> {
                        ClientMain.mainStage.setScene(SceneManager.getHomeScene(username));
                    });
                    break;
                }
            }
        }).start();
    }


    private boolean checkWin(int row, int col, int playerId) {
        int count = 0;
        for (int c = 0; c < 7; c++) {
            if (board[row][c] == playerId) {
                count++;
                if (count >= 4) return true;
            } else {
                count = 0;
            }
        }

        count = 0;
        for (int r = 0; r < 6; r++) {
            if (board[r][col] == playerId) {
                count++;
                if (count >= 4) return true;
            } else {
                count = 0;
            }
        }
        for (int r = 0; r <= 2; r++) {
            for (int c = 0; c <= 3; c++) {
                if (board[r][c] == playerId &&
                        board[r+1][c+1] == playerId &&
                        board[r+2][c+2] == playerId &&
                        board[r+3][c+3] == playerId) {
                    return true;
                }
            }
        }

        for (int r = 3; r < 6; r++) {
            for (int c = 0; c <= 3; c++) {
                if (board[r][c] == playerId &&
                        board[r-1][c+1] == playerId &&
                        board[r-2][c+2] == playerId &&
                        board[r-3][c+3] == playerId) {
                    return true;
                }
            }
        }

        return false;
    }

    private void animateTokenDrop(int targetRow, int targetCol, int playerId) {
        Circle animatedCircle = new Circle(30);

        if (playerId == 1) {
            animatedCircle.setFill(Color.RED);
        } else {
            animatedCircle.setFill(Color.GOLD);
        }

        animatedCircle.setStroke(Color.BLACK);
        animatedCircle.setStrokeWidth(1.5);

        StackPane tempPane = new StackPane(animatedCircle);
        gridPane.add(tempPane, targetCol, 0);

        new Thread(() -> {
            for (int row = 0; row <= targetRow; row++) {
                final int currentRow = row;

                Platform.runLater(() -> {
                    if (currentRow > 0) {
                        gridPane.getChildren().remove(tempPane);
                    }

                    if (currentRow == targetRow) {
                        if (playerId == 1) {
                            circleGrid[targetRow][targetCol].setFill(Color.RED);
                        } else {
                            circleGrid[targetRow][targetCol].setFill(Color.GOLD);
                        }
                        gridPane.getChildren().remove(tempPane);
                    } else {
                        gridPane.add(tempPane, targetCol, currentRow);
                    }
                });

                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}