package grash.event.events.scene;

import grash.core.WindowState;
import grash.event.GrashEvent;
import grash.ui.ScreenController;

/**
 * This Event is Triggered only once, and it loads all necessary assets on init
 * <br>Triggered by:
 * {@link grash.core.WindowController}
 *
 * <br>Received by:
 * {@link grash.core.GameController}
 *
 */

public final class GrashEvent_SceneSwitched extends GrashEvent {
    private final WindowState switchedWindowState;
    private final ScreenController screenController;

    public GrashEvent_SceneSwitched(WindowState switchedWindowState, ScreenController screenController) {
        super("SceneSwitched");
        this.switchedWindowState = switchedWindowState;
        this.screenController = screenController;
    }

    public WindowState getSwitchedWindowState() {
        return switchedWindowState;
    }

    public ScreenController getScreenController() {
        return this.screenController;
    }
}
