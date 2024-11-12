package grash.core;

import grash.events.*;

import java.util.HashSet;

public class GameController implements EventListener<Event_Initialize> {

    private final long initTimestampMillis;
    private final EventBus eventBus;

    private final HashSet<GrashComponent> grashComponents = new HashSet<>();

    private int idCounter = 0;

    public GameController(EventBus eventBus) {
        this.initTimestampMillis = System.currentTimeMillis();
        this.eventBus = eventBus;

        eventBus.registerListener(Event_Initialize.class, this);

        new WindowController(this);
    }

    public String getWorkingDirectory() {
        return Main.WORKING_DIRECTORY;
    }
    public long getInitTimestampMillis() {
        return this.initTimestampMillis;
    }
    public EventBus getEventBus() {
        return this.eventBus;
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
    public void onEvent(Event_Initialize event) {
        System.out.println(event.test);
    }
}
