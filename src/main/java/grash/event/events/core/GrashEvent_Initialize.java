package grash.event.events.core;

import grash.event.GrashEvent;
import grash.input.KeyInputHandler;

/**
 * This Event is Triggered when the Game Starts
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 * {@link grash.core.WindowController}
 * {@link KeyInputHandler}
 *
 */
public final class GrashEvent_Initialize extends GrashEvent {
    public String test = "";
    public GrashEvent_Initialize(String t) {
        super("Initialize");
        this.test = t;
    }
}
