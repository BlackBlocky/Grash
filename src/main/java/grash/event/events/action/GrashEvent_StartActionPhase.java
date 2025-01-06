package grash.event.events.action;

import grash.event.GrashEvent;

/**
 * This Event is Triggered, when the Level is loaded and ready and all that stuff, and now the level actually starts.
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 *  {@link grash.action.ActionPhaseController}
 *
 */

public class GrashEvent_StartActionPhase extends GrashEvent {

    public GrashEvent_StartActionPhase() {
        super("StartActionPhase");
    }
}
