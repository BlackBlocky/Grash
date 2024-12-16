package grash.event.events.core;

import grash.event.GrashEvent;

/**
 * This Event is Triggered only once, and it loads all necessary assets on init
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 *  {@link grash.assets.ResourceLoader}
 *
 */

public final class GrashEvent_LoadResources extends GrashEvent {
    public GrashEvent_LoadResources() {
        super("LoadResources");
    }
}
