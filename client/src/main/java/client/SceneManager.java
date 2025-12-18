/*
this manages the scene transition and the shared information, helps making going from a
scene to another organized
 */
package client;

import javafx.scene.Scene;

public class SceneManager {
    private static int playerId;
    private static String gameResult = "Game Over";

    private static String playerOneName = "Player 1";
    private static String playerTwoName = "Player 2";

    public static Scene getLoginScene() {
        return new LoginScene().scene;
    }

    public static Scene getHomeScene(String username) {
       return new HomeScene(username).scene;
    }

    public static Scene getWaitingScene(String username) {

        return new WaitingScene(username).scene;

    }
    public static Scene getSinglePlayerScene(String username) {
        return new SinglePlayerScene(username).scene;
    }

    public static Scene getStatsScene(String username) {
        return new StatsScene(username).scene;
    }

    public static Scene getMultiplayerScene(String username) {
        return new MultiplayerScene(username, playerId).scene;
    }

    public static Scene getGameOverScene() {
        return new GameOverScene(gameResult).scene;
    }

    public static void setPlayerId(int id) {
        playerId = id;
    }

    public static int getPlayerId() {
        return playerId;
    }

    public static void setGameResult(String result) {
        gameResult = result;
    }

    public static void setPlayerNames(String p1, String p2) {
        playerOneName = p1;
        playerTwoName = p2;
    }

    public static String getPlayerOneName() {
        return playerOneName;
    }

    public static String getPlayerTwoName() {
        return playerTwoName;
    }

    private static boolean isSinglePlayer = false;

    public static void setSinglePlayer(boolean flag) {
        isSinglePlayer = flag;
    }

    public static boolean isSinglePlayer() {
        return isSinglePlayer;
    }
}

