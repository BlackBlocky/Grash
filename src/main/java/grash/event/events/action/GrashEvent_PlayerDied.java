package grash.event.events.action;

import grash.event.GrashEvent;

/**
 * This Event is triggered when the Player died in the ActionPhase. (Like colliding with a spike)
 * <br>Triggered by:
 * {@link grash.action.ActionPhaseController}
 *
 *  <br>Received by:
 *  {@link grash.action.ActionPhaseController}
 *
 */

public final class GrashEvent_PlayerDied extends GrashEvent {
    public GrashEvent_PlayerDied() {
        super("PlayerDied");
    }
}
