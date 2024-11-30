package grash.events;

import grash.core.WindowState;

/**
 * This Event is Triggered only once, and it loads all necessary assets on init
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 *  {@link grash.core.WindowController}
 *
 */

public final class GrashEvent_SwitchScene extends GrashEvent {
    private final WindowState targetWindowState;

    public GrashEvent_SwitchScene(WindowState targetWindowState) {
        super("SwitchScene");
        this.targetWindowState = targetWindowState;
    }

    public WindowState getTargetWindowState() { return this.targetWindowState; }
}
