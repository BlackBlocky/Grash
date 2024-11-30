package grash.events;

import grash.core.WindowState;

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

    public GrashEvent_SceneSwitched(WindowState switchedWindowState) {
        super("SceneSwitched");
        this.switchedWindowState = switchedWindowState;
    }

    public WindowState getSwitchedWindowState() {
        return switchedWindowState;
    }
}
