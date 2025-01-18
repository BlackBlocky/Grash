package grash.event.events.input;

import grash.event.GrashEvent;
import javafx.scene.input.KeyCode;

/**
 * This Event is triggered, when a Key is pressed down (only once after the key was up again)
 * <br>Triggered by:
 * {@link grash.input.KeyInputHandler}
 *
 *  <br>Received by:
 *  {@link grash.action.ActionPhaseLogicHandler}
 *
 */
public class GrashEvent_KeyDown extends GrashEvent {

    private final KeyCode keyCode;

    public GrashEvent_KeyDown(KeyCode keyCode) {
        super("KeyDown");
        this.keyCode = keyCode;
    }

    public KeyCode getKeyCode() {
        return keyCode;
    }
}
