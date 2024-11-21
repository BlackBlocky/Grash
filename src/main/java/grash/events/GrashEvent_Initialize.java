package grash.events;

/**
 * This Event is Triggered when the Game Starts
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 * {@link grash.core.WindowController}
 *
 */
public final class GrashEvent_Initialize extends GrashEvent {
    public String test = "";
    public GrashEvent_Initialize(String t) {
        super("Initialize");
        this.test = t;
    }
}
