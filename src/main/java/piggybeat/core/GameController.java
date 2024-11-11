package piggybeat.core;

import piggybeat.events.*;

public class GameController implements EventListener<Event_GameInit> {

    private final long initTimestampMillis;

    public GameController(EventBus eventBus) {
        initTimestampMillis = System.currentTimeMillis();
        eventBus.registerListener(Event_GameInit.class, this);
    }

    public String getWorkingDirectory() {
        return Main.getWorkingDirectory();
    }
    public long getInitTimestampMillis() {
        return this.initTimestampMillis;
    }

    @Override
    public void onEvent(Event_GameInit event) {
        System.out.println(event.test);
    }
}
