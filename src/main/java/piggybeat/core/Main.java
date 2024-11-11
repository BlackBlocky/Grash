package piggybeat.core;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        Scene mainScene = new Scene(root, 300, 200);

        primaryStage.setTitle("Some Testing");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}