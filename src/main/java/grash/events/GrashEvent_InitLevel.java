package grash.events;

import grash.level.map.LevelMapGenerator;

/**
 * This Event is triggered after the {@link GrashEvent_LevelReadyToInit} Event, for starting the Level,
 * and doing stuff like scene loading... etc...
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 * {@link grash.level.LevelController}
 *
 */

public final class GrashEvent_InitLevel extends GrashEvent {
    public GrashEvent_InitLevel() {
        super("InitLevel");
    }
}
