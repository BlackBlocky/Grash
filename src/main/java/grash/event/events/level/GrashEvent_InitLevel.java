package grash.event.events.level;

import grash.event.GrashEvent;
import grash.level.map.LevelMap;

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
    private final LevelMap levelMap;

    public GrashEvent_InitLevel(LevelMap levelMap) {
        super("InitLevel");
        this.levelMap = levelMap;
    }

    public LevelMap getLevelMap() {
        return this.levelMap;
    }
}
