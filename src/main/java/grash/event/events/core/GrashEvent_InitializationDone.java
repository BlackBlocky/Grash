package grash.event.events.core;

import grash.event.GrashEvent;

/**
 * This Event is Triggered when the Game is ready to run
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 * {@link grash.core.GameController}
 * {@link grash.core.WindowController}
 *
 */
public final class GrashEvent_InitializationDone extends GrashEvent {
    public GrashEvent_InitializationDone() {
        super("InitializationDone");
    }
}
