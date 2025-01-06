package grash.event.events.core;

import grash.event.GrashEvent;

/**
 * This Event is Triggered every Frame, or in other words, every impulse by the engine
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 *  {@link grash.action.ActionPhaseController}
 *
 */

public final class GrashEvent_Tick extends GrashEvent {

    private final double deltaTime;

    public GrashEvent_Tick(double deltaTime) {
        super("Tick");
        this.deltaTime = deltaTime;
    }
}
