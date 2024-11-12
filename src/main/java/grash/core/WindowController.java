package grash.core;

import grash.events.EventListener;
import grash.events.Event_Initialize;

public class WindowController implements EventListener<Event_Initialize> {

    public final GameController game;

    public WindowController(GameController gameController) {
        this.game = gameController;
        game.getEventBus().registerListener(Event_Initialize.class, this);
    }

    @Override
    public void onEvent(Event_Initialize event) {

    }
}
