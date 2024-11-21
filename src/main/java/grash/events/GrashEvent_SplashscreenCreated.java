package grash.events;

import grash.events.GrashEvent;

/**
 * This Event is Triggered when the Splashscreen shows up
 * <br>Triggered by:
 * {@link grash.core.WindowController}
 *
 *  <br>Received by:
 * {@link grash.core.GameController}
 *
 */
public final class GrashEvent_SplashscreenCreated extends GrashEvent {

    public GrashEvent_SplashscreenCreated() {
        super("SplashscreenCreated");
    }
}
