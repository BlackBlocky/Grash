package grash.core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import grash.events.EventBus;
import grash.events.Event_Initialize;
import javafx.stage.StageStyle;

import java.net.URL;

public class Main extends Application {

    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setWidth(1600);
        primaryStage.setHeight(900);
        primaryStage.setResizable(false);
        //primaryStage.setScene(new Scene(new VBox(), 300, 200, Color.WHITE));
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("Grash");
        //primaryStage.show();

        EventBus eventBus = new EventBus();
        GameController controller = new GameController(eventBus, primaryStage);

        eventBus.triggerEvent(new Event_Initialize("Hello World!!!"));

//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(new URL("file:///" + WORKING_DIRECTORY + "\\assets\\fxml\\hello-world.fxml"));
//        VBox vbox = loader.<VBox>load();
    }
}