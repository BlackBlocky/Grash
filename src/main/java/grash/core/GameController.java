package grash.core;

import grash.events.*;

import java.util.HashSet;
import javafx.stage.Stage;

public class GameController implements GrashEventListener {

    private final long initTimestampMillis;
    private final GrashEventBus eventBus;
    private final Stage primaryStage;

    private final HashSet<GrashComponent> grashComponents = new HashSet<>();

    private int idCounter = 0;

    public GameController(GrashEventBus eventBus, Stage primaryStage) {
        this.initTimestampMillis = System.currentTimeMillis();
        this.eventBus = eventBus;
        this.primaryStage = primaryStage;

        eventBus.registerListener(GrashEvent_InitializationDone.class, this);

        new WindowController(this);
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
        }
    }

    private void onEvent_InitializeDone(GrashEvent_InitializationDone event) {
        System.out.println("second: " + event.getClass().getName());
    }

}
