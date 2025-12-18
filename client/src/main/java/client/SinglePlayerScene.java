/*
this is the scene for the single player against the AI, it deals with players moves,
the turns, win/lose/draw, animation for the token drop and win someone wins the game
 */
package client;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class SinglePlayerScene {
    public final Scene scene;

    private char[][] board = new char[6][7];
    private final char PLAYER = 'R';
    private final char AI = 'Y';
    private boolean isPlayerTurn = true;

    private GridPane gridPane = new GridPane();
    private StackPane[][] cellRefs = new StackPane[6][7];
    private Circle[][] circleGrid = new Circle[6][7];
    private String username;
    private boolean gameEnded = false;
    private Label turnLabel = new Label();
    private Label player1Label;
    private Label player2Label;

    public SinglePlayerScene(String username) {
        this.username = username;

        SceneManager.setSinglePlayer(true);
        SceneManager.setPlayerNames(username, "AI");
        SceneManager.setPlayerId(1);

        BorderPane root = new BorderPane();
        root.setPrefSize(950, 700);
        root.setStyle("-fx-background-color: #DCEEFBFF;");
        root.setPadding(new Insets(20));

        Label title = new Label("Connect-4");
        title.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 48));
        title.setTextFill(Color.valueOf("#3a66b0"));
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        VBox centerBox = new VBox(15);
        centerBox.setAlignment(Pos.CENTER);

        HBox arrowRow = new HBox(20);
        arrowRow.setAlignment(Pos.CENTER);
        arrowRow.setPadding(new Insets(10));

        for (int col = 0; col < 7; col++) {
            int finalCol = col;
            Button dropBtn = new Button("▼");
            dropBtn.setPrefSize(60, 40);
            String normal = "-fx-background-color: #3a66b0; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 10;";
            String hover = "-fx-background-color: #2a4d80; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 10;";
            dropBtn.setStyle(normal);
            dropBtn.setOnMouseEntered(e -> dropBtn.setStyle(hover));
            dropBtn.setOnMouseExited(e -> dropBtn.setStyle(normal));
            dropBtn.setOnAction(e -> handleMove(finalCol));
            arrowRow.getChildren().add(dropBtn);
        }

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setStyle("-fx-background-color: #dceefb; -fx-border-color: #3a66b0; -fx-border-width: 3px; -fx-padding: 5px; -fx-hgap: 5; -fx-vgap: 5;");

        gridPane.getChildren().clear();
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
                cellRefs[row][col] = cellPane;
                circleGrid[row][col] = circle;
            }
        }


        turnLabel.setText("Your Turn");
        turnLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        turnLabel.setTextFill(Color.BLACK);
        turnLabel.setMinWidth(100);
        turnLabel.setAlignment(Pos.CENTER);
        turnLabel.setStyle(
                "-fx-background-color: #d9f0d3; -fx-text-fill: #2e8b57; -fx-border-color: #2e8b57; " +
                        "-fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10;"
        );

        VBox turnBox = new VBox(turnLabel);
        turnBox.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(turnBox, new Insets(20, 0, 0, 0));
        centerBox.getChildren().addAll(arrowRow, gridPane, turnBox);
        root.setCenter(centerBox);

        VBox leftPanel = new VBox(20);
        leftPanel.setPrefWidth(200);
        leftPanel.setPadding(new Insets(0, 100, -20, 20));
        leftPanel.setAlignment(Pos.CENTER);

        player1Label = new Label("Player 1\n" + username);
        player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        player1Label.setTextFill(Color.valueOf("#cc0000"));
        player1Label.setAlignment(Pos.TOP_LEFT);
        player1Label.setWrapText(true);

        Circle redDot = new Circle(25, Color.RED);
        redDot.setStroke(Color.BLACK);
        redDot.setStrokeWidth(2);

        leftPanel.getChildren().addAll(player1Label, redDot);
        root.setLeft(leftPanel);

        VBox rightPanel = new VBox(20);
        rightPanel.setPrefWidth(200);
        leftPanel.setPadding(new Insets(0, 100, 20, 20));
        rightPanel.setAlignment(Pos.CENTER);

        player2Label = new Label("Player 2\nAI");
        player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        player2Label.setTextFill(Color.valueOf("#ccaa00"));
        player2Label.setAlignment(Pos.TOP_LEFT);
        player2Label.setWrapText(true);

        Circle yellowDot = new Circle(25, Color.GOLD);
        yellowDot.setStroke(Color.BLACK);
        yellowDot.setStrokeWidth(2);

        rightPanel.getChildren().addAll(player2Label, yellowDot);
        root.setRight(rightPanel);


        Button quitBtn = new Button("Quit");
        quitBtn.setPrefWidth(150);
        quitBtn.setPrefHeight(40);
        quitBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
        String normalQuit = "-fx-background-color: #333333; -fx-text-fill: #ff3333; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        String hoverQuit = "-fx-background-color: #222222; -fx-text-fill: #ff6666; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        quitBtn.setStyle(normalQuit);
        quitBtn.setOnMouseEntered(e -> quitBtn.setStyle(hoverQuit));
        quitBtn.setOnMouseExited(e -> quitBtn.setStyle(normalQuit));

        quitBtn.setOnAction(e -> {
            SceneManager.setSinglePlayer(false);
            ClientConnection.send("QUIT_GAME");
            ClientMain.mainStage.setScene(SceneManager.getHomeScene(username));
        });
        Button exitBtn = new Button("Exit");
        exitBtn.setPrefWidth(150);
        exitBtn.setPrefHeight(40);
        exitBtn.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 16));
        String normalExit = "-fx-background-color: #8B0000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        String hoverExit = "-fx-background-color: #a80000; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-border-radius: 20;";
        exitBtn.setStyle(normalExit);
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(hoverExit));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(normalExit));

        exitBtn.setOnAction(e -> Platform.exit());

        HBox bottomRow = new HBox();
        bottomRow.setSpacing(20);
        bottomRow.setPadding(new Insets(-30, 20, 20, 20));
        bottomRow.setAlignment(Pos.BOTTOM_CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottomRow.getChildren().addAll(quitBtn, spacer, exitBtn);
        root.setBottom(bottomRow);

        updateTurnLabel();

        this.scene = new Scene(root, 950, 700);
    }

    private void updateTurnLabel() {
        if (isPlayerTurn) {
            turnLabel.setText("YOUR TURN");
            turnLabel.setStyle("-fx-background-color: #d9f0d3; -fx-text-fill: #2e8b57; -fx-border-color: #2e8b57; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10;");

            player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
            player1Label.setTextFill(Color.valueOf("#ff0000"));
            player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
            player2Label.setTextFill(Color.valueOf("#ccaa00"));
        } else {
            turnLabel.setText("AI'S TURN");
            turnLabel.setStyle("-fx-background-color: #f0e6d3; -fx-text-fill: #cd853f; -fx-border-color: #cd853f; -fx-border-radius: 20; -fx-background-radius: 20; -fx-font-weight: bold; -fx-padding: 10;");

            player2Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
            player2Label.setTextFill(Color.valueOf("#ffcc00"));
            player1Label.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
            player1Label.setTextFill(Color.valueOf("#cc0000"));
        }
    }

    private void handleMove(int col) {
        if (!gameEnded && isPlayerTurn) {
            int row = placeTokenWithAnimation(col, PLAYER);
            if (row != -1) {
                isPlayerTurn = false;
                updateTurnLabel();

                int[] winCoords = checkWinCoordinates(PLAYER);
                if (winCoords != null) {
                    gameEnded = true;
                    highlightWinningTokens(winCoords);
                    SceneManager.setGameResult("You Win!");
                    goToGameOver();
                    return;
                }

                if (isBoardFull()) {
                    gameEnded = true;
                    SceneManager.setGameResult("Draw");
                    goToGameOver();
                    return;
                }

                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(ev -> {
                    if (!gameEnded) {
                        int aiMove = Connect4Logic.getBestMove(board, AI, PLAYER);
                        if (aiMove != -1) {
                            int aiRow = placeTokenWithAnimation(aiMove, AI);
                            if (aiRow != -1) {
                                int[] aiWinCoords = checkWinCoordinates(AI);
                                if (aiWinCoords != null) {
                                    gameEnded = true;
                                    highlightWinningTokens(aiWinCoords);
                                    SceneManager.setGameResult("You Lose!");
                                    goToGameOver();
                                } else if (isBoardFull()) {
                                    gameEnded = true;
                                    SceneManager.setGameResult("Draw");
                                    goToGameOver();
                                } else {
                                    isPlayerTurn = true;
                                    updateTurnLabel();
                                }
                            }
                        }
                    }
                });
                pause.play();
            }
        }
    }

    private int placeTokenWithAnimation(int col, char token) {
        for (int row = 6 - 1; row >= 0; row--) {
            if (board[row][col] == '\0') {
                board[row][col] = token;

                final int targetRow = row;
                final int targetCol = col;
                final boolean isPlayer = (token == PLAYER);

                Circle animatedCircle;
                if (isPlayer) {
                    animatedCircle = new Circle(30, Color.RED);
                } else {
                    animatedCircle = new Circle(30, Color.GOLD);
                }
                animatedCircle.setStroke(Color.BLACK);
                animatedCircle.setStrokeWidth(1.5);


                StackPane tempPane = new StackPane(animatedCircle);
                gridPane.add(tempPane, targetCol, 0);

                new Thread(() -> {
                    for (int r = 0; r <= targetRow; r++) {
                        final int currentRow = r;

                        Platform.runLater(() -> {
                            if (currentRow > 0) {
                                gridPane.getChildren().remove(tempPane);
                            }

                            if (currentRow == targetRow) {
                                if (isPlayer) {
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
                return targetRow;
            }
        }
        return -1;
    }

    private void highlightWinningTokens(int[] winCoords) {
        int startRow = winCoords[0];
        int startCol = winCoords[1];
        int endRow = winCoords[2];
        int endCol = winCoords[3];

        int rowDir = 0;
        int colDir = 0;

        if (startRow != endRow) rowDir = (endRow - startRow) / 3;
        if (startCol != endCol) colDir = (endCol - startCol) / 3;

        for (int i = 0; i < 4; i++) {
            int r = startRow + i * rowDir;
            int c = startCol + i * colDir;

            Circle token = circleGrid[r][c];

            TranslateTransition bounce = new TranslateTransition(Duration.millis(300), token);
            bounce.setByY(-20);
            bounce.setCycleCount(6);
            bounce.setAutoReverse(true);

            ScaleTransition scale = new ScaleTransition(Duration.millis(300), token);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setCycleCount(6);
            scale.setAutoReverse(true);

            ParallelTransition pt = new ParallelTransition(bounce, scale);
            pt.play();
        }
    }

    private int[] checkWinCoordinates(char token) {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c <= 7 - 4; c++) {
                if (board[r][c] == token && board[r][c+1] == token &&
                        board[r][c+2] == token && board[r][c+3] == token) {
                    return new int[] {r, c, r, c+3};
                }
            }
        }

        for (int r = 0; r <= 6 - 4; r++) {
            for (int c = 0; c < 7; c++) {
                if (board[r][c] == token && board[r+1][c] == token &&
                        board[r+2][c] == token && board[r+3][c] == token) {
                    return new int[] {r, c, r+3, c};
                }
            }
        }

        for (int r = 0; r <= 6 - 4; r++) {
            for (int c = 0; c <= 7 - 4; c++) {
                if (board[r][c] == token && board[r+1][c+1] == token &&
                        board[r+2][c+2] == token && board[r+3][c+3] == token) {
                    return new int[] {r, c, r+3, c+3};
                }
            }
        }

        for (int r = 3; r < 6; r++) {
            for (int c = 0; c <= 7 - 4; c++) {
                if (board[r][c] == token && board[r-1][c+1] == token &&
                        board[r-2][c+2] == token && board[r-3][c+3] == token) {
                    return new int[] {r, c, r-3, c+3};
                }
            }
        }

        return null;
    }

    private boolean isBoardFull() {
        for (int col = 0; col < 7; col++) {
            if (board[0][col] == '\0') return false;
        }
        return true;
    }

    private void goToGameOver() {
        PauseTransition pause = new PauseTransition(Duration.seconds(1.75));
        pause.setOnFinished(e -> {
            board = new char[6][7];
            ClientMain.mainStage.setScene(SceneManager.getGameOverScene());
        });
        pause.play();
    }
}
