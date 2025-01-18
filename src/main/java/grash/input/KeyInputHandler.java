package grash.input;

import grash.core.GameController;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.event.events.input.GrashEvent_KeyUp;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;

public final class KeyInputHandler {

    private final GameController game;
    private final HashSet<KeyCode> currentlyPressedKeyCode;

    public KeyInputHandler(GameController gameController) {
        this.game = gameController;
        this.currentlyPressedKeyCode = new HashSet<>();
    }

    /**
     * Handles the Input triggered by JavaFX, and calls the corresponding Event for it.
     * @param isKeyPressed if it false, it means it's an onKeyReleased Event.
     */
    public void handle_JavaFXKeyEvent(KeyEvent keyEvent, boolean isKeyPressed) {
        if(!currentlyPressedKeyCode.contains(keyEvent.getCode())) {
            game.getEventBus().triggerEvent(new GrashEvent_KeyDown(keyEvent.getCode()));
            currentlyPressedKeyCode.add(keyEvent.getCode());
            return;
        }

        if(!isKeyPressed) { // The key is Released
            game.getEventBus().triggerEvent(new GrashEvent_KeyUp(keyEvent.getCode()));
            currentlyPressedKeyCode.remove(keyEvent.getCode());
        }
    }
}
