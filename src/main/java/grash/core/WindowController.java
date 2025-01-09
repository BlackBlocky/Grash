package grash.core;

import grash.event.*;
import grash.event.events.core.GrashEvent_InitializationDone;
import grash.event.events.core.GrashEvent_Initialize;
import grash.event.events.scene.GrashEvent_SceneSwitched;
import grash.event.events.scene.GrashEvent_SwitchScene;
import grash.ui.LevelActionScreenController;
import grash.ui.ScreenController;
import grash.ui.WelcomeScreenController;
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

    /**
     * Here is every filename of the .fxml files hardcoded, which can accessed via a WindowState Key
     */
    private final HashMap<WindowState, String> fxmlFileNamesByWindowState = new HashMap<WindowState, String>() {{
        put(WindowState.Splashscreen, "initScene.fxml");
        put(WindowState.WelcomeScreen, "welcomeScreen.fxml");
        put(WindowState.LevelAction, "levelAction.fxml");
    }};

    /**
     * This HashMap stores a ScreenController reference for every Screen aka. WindowState
     */
    private final HashMap<WindowState, ScreenController> fxmlControllerByWindowState = new HashMap<>();

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
            case "Initialize": {
                onEvent_Initialize((GrashEvent_Initialize) event);
                break;
            }
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

    /**
     * Just initializes all the ScreenControllers and adding them into the fxmlControllerByWindowState HashMap
     */
    private void onEvent_Initialize(GrashEvent_Initialize event) {
        fxmlControllerByWindowState.put(WindowState.Splashscreen, null);
        fxmlControllerByWindowState.put(WindowState.WelcomeScreen, new WelcomeScreenController(this.game));
        fxmlControllerByWindowState.put(WindowState.LevelAction, new LevelActionScreenController(this.game));
    }

    private void onEvent_InitializeDone(GrashEvent_InitializationDone event) {
        // Changing the Icon here because it isn't loaded before
        game.getPrimaryStage().getIcons().add(game.getResourceLoader().getSprite("GameIcon").getImage());
    }

    /**
     * This Method decides what logic should be called when the Scene is switched
     */
    private void onEvent_SwitchScene(GrashEvent_SwitchScene event) {
        // The switch is for special loading stuff like the SplashScreen, if it is just a normal Scene, then the default option is called
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
        game.getEventBus().triggerEvent(
                new GrashEvent_SceneSwitched(
                    event.getTargetWindowState(),
                    fxmlControllerByWindowState.get(event.getTargetWindowState()
                )
        ));
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
        if(splashscreen != null && splashscreen.isShowing()) splashscreen.hide(); // && stops when the first is false,
                                                                                  // so we have null pointer problems
        // try to Load the Scene
        Pane pane = null;
        FXMLLoader loader = new FXMLLoader();
        try {
            ScreenController screenController = fxmlControllerByWindowState.get(event.getTargetWindowState());
            if(screenController != null) loader.setController(screenController.getClass());
            loader.setLocation(new URL("file:///" + game.getWorkingDirectory() + "/assets/fxml/" + fxmlFileNamesByWindowState.get(event.getTargetWindowState())));
            pane = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(pane == null) return;

        Scene scene = new Scene(pane);
        game.getPrimaryStage().setScene(scene);

        // Get the ScreenController that belongs to the current Scene, and call the init() Function
        // (The ScreenController can be null, because stuff like the SplashScreen do not have a ScreenController)
        if(fxmlControllerByWindowState.get(event.getTargetWindowState()) != null)
            fxmlControllerByWindowState.get(event.getTargetWindowState()).init();
        game.getPrimaryStage().show();
    }
}
