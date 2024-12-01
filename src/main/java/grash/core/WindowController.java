package grash.core;

import grash.events.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.HashMap;

public final class WindowController implements GrashEventListener {

    public final GameController game;

    private Stage splashscreen = null;

    private WindowState windowState = WindowState.None;

    private HashMap<WindowState, String> fxmlFileNamesByWindowState = new HashMap<WindowState, String>() {{
        put(WindowState.Splashscreen, "initScene.fxml");
        put(WindowState.WelcomeScreen, "welcomeScreen.fxml");
    }};

    public WindowController(GameController gameController) {
        this.game = gameController;
        game.getEventBus().registerListener(GrashEvent_Initialize.class, this);
        game.getEventBus().registerListener(GrashEvent_InitializationDone.class, this);
        game.getEventBus().registerListener(GrashEvent_SwitchScene.class, this);
    }

    public WindowState getWindowState() {
        return this.windowState;
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "InitializationDone": {
                onEvent_InitializeDone((GrashEvent_InitializationDone) event);
                break;
            }
            case "SwitchScene": {
                onEvent_SwitchScene((GrashEvent_SwitchScene) event);
                break;
            }
        }
    }

    private void onEvent_InitializeDone(GrashEvent_InitializationDone event) {
        System.out.println("second: " + event.getClass().getName());
    }

    private void onEvent_SwitchScene(GrashEvent_SwitchScene event) {

        // noinspection EnumSwitchStatementWhichMissesCases,SwitchStatementWithTooFewBranches
        switch(event.getTargetWindowState()) {
            case Splashscreen: {
                showSplashScreen(event);
                break;
            }
            default: {
                switchToScene(event);
                break;
            }
        }

        this.windowState = event.getTargetWindowState();
        game.getEventBus().triggerEvent(new GrashEvent_SceneSwitched(event.getTargetWindowState()));
    }

    private void showSplashScreen(GrashEvent_SwitchScene event) {
        game.getPrimaryStage().hide();

        this.splashscreen = new Stage();
        this.splashscreen.setTitle("Grash");
        this.splashscreen.setWidth(500);
        this.splashscreen.setHeight(500);

        this.splashscreen.initStyle(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader();
        try{
            loader.setLocation(new URL("file:///" + game.getWorkingDirectory() + "/assets/fxml/initScene.fxml"));
            StackPane vbox = loader.load();
            Scene initScene = new Scene(vbox);
            this.splashscreen.setScene(initScene);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.splashscreen.show();
    }

    private void switchToScene(GrashEvent_SwitchScene event) {
        if(splashscreen != null) splashscreen.hide();

        // try to Load the Scene
        Pane pane = null;
        FXMLLoader loader = new FXMLLoader();
        try {
            loader.setLocation(new URL("file:///" + game.getWorkingDirectory() + "/assets/fxml/" + fxmlFileNamesByWindowState.get(event.getTargetWindowState())));
            pane = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(pane == null) return;

        Scene scene = new Scene(pane);
        game.getPrimaryStage().setScene(scene);
        game.getPrimaryStage().show();
    }
}
