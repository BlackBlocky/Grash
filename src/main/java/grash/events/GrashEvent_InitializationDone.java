package grash.events;

/**
 * This Event is Triggered when the Game is ready to run
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 * {@link grash.core.GameController}
 * {@link grash.core.WindowController}
 *
 */
public class GrashEvent_InitializationDone extends GrashEvent {
    public GrashEvent_InitializationDone() {
        super("InitializationDone");
    }
}
