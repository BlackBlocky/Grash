package grash.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import grash.events.EventBus;
import grash.events.Event_Initialize;

import java.net.URL;

public class Main extends Application {

    private static final String workingDirectory = System.getProperty("user.dir");

    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        EventBus eventBus = new EventBus();
        GameController controller = new GameController(eventBus);

        eventBus.triggerEvent(new Event_Initialize("Hello World!!!"));

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