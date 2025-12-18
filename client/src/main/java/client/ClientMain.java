/*
this is where our connect four game ahs it's entry point for the client.
it  initializes and displays the login scene
 */
package client;

import javafx.application.Application;
import javafx.stage.Stage;

public class ClientMain extends Application {
    public static Stage mainStage;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        stage.setTitle("Connect 4 Client");
        stage.setResizable(false);
        stage.setScene(SceneManager.getLoginScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
