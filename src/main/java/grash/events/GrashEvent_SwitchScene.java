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
    private final String fxmlFileName;
    private final WindowState targetWindowState;

    public GrashEvent_SwitchScene(String fxmlFileName, WindowState targetWindowState) {
        super("SwitchScene");
        this.fxmlFileName = fxmlFileName;
        this.targetWindowState = targetWindowState;
    }

    public String getFxmlFileName() { return this.fxmlFileName; }
    public WindowState getTargetWindowState() { return this.targetWindowState; }
}
