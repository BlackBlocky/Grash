package piggybeat.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.net.URL;

public class Main extends Application {

    private static final String workingDirectory = System.getProperty("user.dir");

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL("file:///" + workingDirectory + "\\assets\\fxml\\hello-world.fxml"));
        VBox vbox = loader.<VBox>load();

        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.setWidth(300);
        primaryStage.setHeight(200);
        primaryStage.setTitle("I like Cookies!");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}