package grash.event.events.input;

import grash.event.GrashEvent;
import javafx.scene.input.KeyCode;

/**
 * This Event is triggered when a Key is released.
 * <br>Triggered by:
 * {@link grash.input.KeyInputHandler}
 *
 *  <br>Received by:
 *  {@link grash.action.ActionPhaseLogicHandler}
 *
 */
public class GrashEvent_KeyUp extends GrashEvent {

    private final KeyCode keyCode;

    public GrashEvent_KeyUp(KeyCode keyCode) {
        super("KeyUp");
        this.keyCode = keyCode;
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }
}
