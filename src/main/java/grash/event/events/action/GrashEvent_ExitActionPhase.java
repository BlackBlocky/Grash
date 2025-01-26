package grash.event.events.action;

import grash.event.GrashEvent;

/**
 * This Event is triggered when the ActionPhase is over/abandoned
 * <br>Triggered by:
 * {@link grash.action.ActionPhaseController}
 *
 *  <br>Received by:
 *  {@link grash.action.ActionPhaseController}
 *
 */

public final class GrashEvent_ExitActionPhase extends GrashEvent {
    public GrashEvent_ExitActionPhase() {
        super("ExitActionPhase");
    }
}
