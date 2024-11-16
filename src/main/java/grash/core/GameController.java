package grash.core;

import com.sun.management.internal.GarbageCollectorExtImpl;
import grash.events.*;

import java.util.HashSet;
import javafx.stage.Stage;

public class GameController implements GrashEventListener {

    private final long initTimestampMillis;
    private final GrashEventBus eventBus;
    private final Stage primaryStage;

    private final HashSet<GrashComponent> grashComponents = new HashSet<>();

    private int idCounter = 0;

    private GameState gameState = GameState.UnInit;

    public GameController(GrashEventBus eventBus, Stage primaryStage) {
        this.initTimestampMillis = System.currentTimeMillis();
        this.eventBus = eventBus;
        this.primaryStage = primaryStage;

        eventBus.registerListener(GrashEvent_InitializationDone.class, this);
        eventBus.registerListener(GrashEvent_SplashscreenCreated.class, this);


        new WindowController(this);
        gameState = GameState.Init;
        getEventBus().triggerEvent(new GrashEvent_Initialize(""));
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
            case "SplashscreenCreated": {
                onEvent_SplashscreenCreated((GrashEvent_SplashscreenCreated) event);
                break;
            }
        }
    }

    private void onEvent_InitializeDone(GrashEvent_InitializationDone event) {
        System.out.println("second: " + event.getClass().getName());
        this.gameState = GameState.StartScreen;
    }

    private void onEvent_SplashscreenCreated(GrashEvent_SplashscreenCreated event) {


        getEventBus().triggerEvent(new GrashEvent_LoadResources());
    }

}
