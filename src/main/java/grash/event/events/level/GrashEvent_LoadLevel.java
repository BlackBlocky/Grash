package grash.event.events.level;

import grash.event.GrashEvent;

/**
 * This Event is triggered to start the loading process fro starting the Level
 * <br>Triggered by:
 * {@link grash.ui.WelcomeScreenController}
 *
 *  <br>Received by:
 * {@link grash.assets.ResourceLoader}
 *
 */

public final class GrashEvent_LoadLevel extends GrashEvent {

    private final String levelKey;

    public GrashEvent_LoadLevel(String levelKey) {
        super("LoadLevel");
        this.levelKey = levelKey;
    }

    public String getLevelKey() {return this.levelKey; }
}
