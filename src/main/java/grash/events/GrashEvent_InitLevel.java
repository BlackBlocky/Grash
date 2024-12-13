package grash.events;

import grash.level.map.LevelMapGenerator;

/**
 * This Event is triggered after the {@link grash.events.GrashEvent_LoadLevel} Event, for starting the Level,
 * and doing stuff like scene loading... etc...
 * <br>Triggered by:
 * {@link LevelMapGenerator}
 *
 *  <br>Received by:
 * {@link grash.assets.ResourceLoader}
 *
 */

public class GrashEvent_InitLevel extends GrashEvent {
    public GrashEvent_InitLevel() {
        super("InitLevel");
    }
}
