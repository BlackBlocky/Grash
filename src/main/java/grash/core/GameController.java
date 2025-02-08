package grash.core;

import grash.action.ActionPhaseController;
import grash.assets.ResourceLoader;
import grash.event.*;

import java.util.HashSet;

import grash.event.events.action.GrashEvent_StartActionPhase;
import grash.event.events.core.GrashEvent_InitializationDone;
import grash.event.events.core.GrashEvent_Initialize;
import grash.event.events.core.GrashEvent_LoadResources;
import grash.event.events.core.GrashEvent_Tick;
import grash.event.events.level.GrashEvent_InitLevel;
import grash.event.events.level.GrashEvent_LevelGoingToStart;
import grash.event.events.level.GrashEvent_LevelReadyToInit;
import grash.event.events.level.GrashEvent_LoadLevel;
import grash.event.events.scene.GrashEvent_SceneSwitched;
import grash.event.events.scene.GrashEvent_SwitchScene;
import grash.input.KeyInputHandler;
import grash.level.LevelController;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;

public final class GameController implements GrashEventListener {

    private final GrashEventBus eventBus;
    private final ResourceLoader resourceLoader;
    private final WindowController windowController;
    private final LevelController levelController;
    private final ActionPhaseController actionPhaseController;
    private final KeyInputHandler keyInputHandler;

    private final long initTimestampMillis;
    private final Stage primaryStage;

    private final HashSet<GrashComponent> grashComponents = new HashSet<>();

    private int idCounter = 0;

    private GameState gameState = GameState.UnInit;

    /**
     * The Main Constructor of the Game. By Calling the Constructor, every Component in the Game is going the Initialized.
     * The Game also immediately switched to the Splashscreen, for loading the resources after it.
     */
    public GameController(Stage primaryStage) {
        this.initTimestampMillis = System.currentTimeMillis(); // System time
        this.primaryStage = primaryStage;
        this.eventBus = new GrashEventBus();

        this.resourceLoader = new ResourceLoader(this);
        this.windowController = new WindowController(this);
        this.levelController = new LevelController(this);
        this.actionPhaseController = new ActionPhaseController(this);
        this.keyInputHandler = new KeyInputHandler(this);

        eventBus.registerListener(GrashEvent_InitializationDone.class, this);
        eventBus.registerListener(GrashEvent_SceneSwitched.class, this);
        eventBus.registerListener(GrashEvent_LevelReadyToInit.class, this);
        eventBus.registerListener(GrashEvent_LevelGoingToStart.class, this);

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
    public GameState getGameState() { return this.gameState; }
    public WindowState getWindowState() { return this.windowController.getWindowState(); }
    public ResourceLoader getResourceLoader() { return this.resourceLoader; }
    public KeyInputHandler getKeyInputHandler() { return this.keyInputHandler; }

    /**
     * This Method will ever return a unique ID, it will NEVER return the same (only after 4.294.967.296 calls :P)
     * @return Returns a unique ID
     */
    public int generateUniqueID() {
        idCounter++;
        return idCounter;
    }

    /**
     * This Method adds a grashComponent to the Game, and also calls the start Methode
     */
    public void registerGrashComponent(GrashComponent grashComponent) {
        if(!this.grashComponents.contains(grashComponent)) {
            grashComponents.add(grashComponent);
            grashComponent.start();
        }
    }

    /**
     * This Method calls the Update for every GrashComponent
     */
    private void callUpdate() {
        for(GrashComponent g : grashComponents) {
            g.update();
        }
    }

    AnimationTimer gameLoop = new AnimationTimer() {
        private double lastTime = System.nanoTime();

        @Override
        public void handle(long currentTime) {
            // DeltaTime calculation
            double deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            //System.out.println(1.0 / deltaTime);
            lastTime = currentTime;

            getEventBus().triggerEvent(new GrashEvent_Tick(deltaTime));
        }
    };

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
            case "LevelGoingToStart": {
                onEvent_LevelGoingToStart((GrashEvent_LevelGoingToStart) event);
                break;
            }
        }
    }

    /**
     * Literally doing nothing but yeah
     */
    private void onEvent_InitializeDone(GrashEvent_InitializationDone event) {
        System.out.println("second: " + event.getClass().getName());
        this.gameState = GameState.StartScreen;
    }

    private void onEvent_SceneSwitched(GrashEvent_SceneSwitched event) {
        switch (event.getSwitchedWindowState()) {
            case Splashscreen: {
                // Doing all the Initial Stuff
                getEventBus().triggerEvent(new GrashEvent_LoadResources());
                getEventBus().triggerEvent(new GrashEvent_InitializationDone());

                gameLoop.start();

                getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.WelcomeScreen));
                getEventBus().triggerEvent(new GrashEvent_LoadLevel("Dev::Showcase::Version_1::Easy"));
                break;
            }
            case LevelAction: {
                // The ActionController is set up in the LevelGoingToStartEvent
                getEventBus().triggerEvent(new GrashEvent_StartActionPhase());
                break;
            }
        }
    }

    private void onEvent_LevelReadyToInit(GrashEvent_LevelReadyToInit event) {
        System.out.println("Level " + event.getLevelMap().getMapMetadata().getMapName() + " is ready!");
        //getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.LevelAction));
        getEventBus().triggerEvent(new GrashEvent_InitLevel(event.getLevelMap()));
    }

    private void onEvent_LevelGoingToStart(GrashEvent_LevelGoingToStart event) {
        gameState = GameState.GameActionPhase;
        actionPhaseController.setupNewActionPhase(event.getLevelMap(), 3);
        getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.LevelAction));
    }

}
