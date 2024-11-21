package grash.events;

/**
 * This Event is Triggered only once, and it loads all necessary assets on init
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 *
 *
 */

public final class GrashEvent_LoadResources extends GrashEvent {
    public GrashEvent_LoadResources() {
        super("LoadResources");
    }
}
