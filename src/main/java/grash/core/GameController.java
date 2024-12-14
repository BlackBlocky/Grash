package grash.core;

import grash.assets.ResourceLoader;
import grash.events.*;

import java.util.HashSet;

import grash.level.LevelController;
import javafx.stage.Stage;

public final class GameController implements GrashEventListener {

    private final GrashEventBus eventBus;
    private final ResourceLoader resourceLoader;
    private final WindowController windowController;
    private final LevelController levelController;

    private final long initTimestampMillis;
    private final Stage primaryStage;

    private final HashSet<GrashComponent> grashComponents = new HashSet<>();

    private int idCounter = 0;

    private GameState gameState = GameState.UnInit;

    public GameController(Stage primaryStage) {
        this.initTimestampMillis = System.currentTimeMillis();
        this.primaryStage = primaryStage;
        this.eventBus = new GrashEventBus();
        this.resourceLoader = new ResourceLoader(this);
        this.windowController = new WindowController(this);
        this.levelController = new LevelController(this);

        eventBus.registerListener(GrashEvent_InitializationDone.class, this);
        eventBus.registerListener(GrashEvent_SceneSwitched.class, this);
        eventBus.registerListener(GrashEvent_LevelReadyToInit.class, this);

        gameState = GameState.Init;
        getEventBus().triggerEvent(new GrashEvent_Initialize(""));
        getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.Splashscreen));
    }

    public String getWorkingDirectory() {
        return Main.WORKING_DIRECTORY;
    }
    public long getInitTimestampMillis() {
        return this.initTimestampMillis;
    }
    public GrashEventBus getEventBus() {
        return this.eventBus;
    }
    public Stage getPrimaryStage() {
        return this.primaryStage;
    }
    public GameState getGameState() {
        return this.gameState;
    }

    public int generateUniqueID() {
        idCounter++;
        return idCounter;
    }

    public void registerGrashComponent(GrashComponent grashComponent) {
        if(!this.grashComponents.contains(grashComponent)) {
            grashComponents.add(grashComponent);
            grashComponent.start();
        }
    }

    private void callUpdate() {
        for(GrashComponent g : grashComponents) {
            g.update();
        }
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "InitializationDone": {
                onEvent_InitializeDone((GrashEvent_InitializationDone) event);
                break;
            }
            case "SceneSwitched": {
                onEvent_SceneSwitched((GrashEvent_SceneSwitched) event);
                break;
            }
            case "LevelReadyToInit": {
                onEvent_LevelReadyToInit((GrashEvent_LevelReadyToInit) event);
                break;
            }
        }
    }

    private void onEvent_InitializeDone(GrashEvent_InitializationDone event) {
        System.out.println("second: " + event.getClass().getName());
        this.gameState = GameState.StartScreen;
    }

    private void onEvent_SceneSwitched(GrashEvent_SceneSwitched event) {
        switch (event.getSwitchedWindowState()) {
            case Splashscreen: {
                getEventBus().triggerEvent(new GrashEvent_LoadResources());
                getEventBus().triggerEvent(new GrashEvent_InitializationDone());
                //getEventBus().triggerEvent(new GrashEvent_LoadLevel("BlackBlocky::Test"));
                getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.WelcomeScreen));
                getEventBus().triggerEvent(new GrashEvent_LoadLevel("Some_Person::My_Cool_Map::Version_1"));
            }
        }
    }

    private void onEvent_LevelReadyToInit(GrashEvent_LevelReadyToInit event) {
        System.out.println("Level " + event.getLevelMap().getMapMetadata().getMapName() + " is ready!");
    }

}
