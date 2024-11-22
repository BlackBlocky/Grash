package grash.core;

import grash.events.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;

public final class WindowController implements GrashEventListener {

    public final GameController game;

    private Stage splashscreen = null;

    private WindowState windowState;

    public WindowController(GameController gameController) {
        this.game = gameController;
        game.getEventBus().registerListener(GrashEvent_Initialize.class, this);
        game.getEventBus().registerListener(GrashEvent_InitializationDone.class, this);
    }

    public WindowState getWindowState() {
        return this.windowState;
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "Initialize": {
                onEvent_Initialize((GrashEvent_Initialize) event);
                break;
            }
            case "InitializationDone": {
                onEvent_InitializeDone((GrashEvent_InitializationDone) event);
                break;
            }
            case "SplashscreenCreated": {
                break;
            }
        }
    }

    private void onEvent_Initialize(GrashEvent_Initialize event) {
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

        this.windowState = WindowState.Splashscreen;
        game.getEventBus().triggerEvent(new GrashEvent_SplashscreenCreated());
    }

    private void onEvent_InitializeDone(GrashEvent_InitializationDone event) {
        System.out.println("second: " + event.getClass().getName());
    }
}
