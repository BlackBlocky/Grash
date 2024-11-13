package grash.core;

import grash.events.EventListener;
import grash.events.Event_Initialize;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.MalformedURLException;
import java.net.URL;

import java.net.URL;

public class WindowController implements EventListener<Event_Initialize> {

    public final GameController game;

    private Stage splashscreen = null;

    public WindowController(GameController gameController) {
        this.game = gameController;
        game.getEventBus().registerListener(Event_Initialize.class, this);
    }

    @Override
    public void onEvent(Event_Initialize event) {
        game.getPrimaryStage().hide();

        this.splashscreen = new Stage();
        this.splashscreen.setTitle("Grash");
        this.splashscreen.setWidth(500);
        this.splashscreen.setHeight(500);

        this.splashscreen.initStyle(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader();
        try{
            loader.setLocation(new URL("file:///" + game.getWorkingDirectory() + "/assets/fxml/initScene.fxml"));
            StackPane vbox = loader.<StackPane>load();
            Scene initScene = new Scene(vbox);
            this.splashscreen.setScene(initScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.splashscreen.show();
    }
}
