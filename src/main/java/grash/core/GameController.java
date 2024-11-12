package grash.core;

import grash.events.*;

public class GameController implements EventListener<Event_Initialize> {

    private final long initTimestampMillis;
    private final EventBus eventBus;

    public GameController(EventBus eventBus) {
        this.initTimestampMillis = System.currentTimeMillis();
        this.eventBus = eventBus;

        eventBus.registerListener(Event_Initialize.class, this);
    }

    public String getWorkingDirectory() {
        return Main.getWorkingDirectory();
    }
    public long getInitTimestampMillis() {
        return this.initTimestampMillis;
    }
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public void onEvent(Event_Initialize event) {
        System.out.println(event.test);
    }
}
