package grash.event.events.level;

import grash.event.GrashEvent;
import grash.level.map.LevelMap;

/**
 * This Event is triggered, when the Level is loaded and the LevelMapGenerator is done. Know the Map can be finally "load"
 * <br>Triggered by:
 * {@link grash.level.map.LevelMapGenerator}
 *
 *  <br>Received by:
 * {@link grash.core.GameController}
 *
 */

public final class GrashEvent_LevelReadyToInit extends GrashEvent {
    private final LevelMap levelMap;

    public GrashEvent_LevelReadyToInit(LevelMap levelMap) {
        super("LevelReadyToInit");
        this.levelMap = levelMap;
    }

    public LevelMap getLevelMap() {
        return this.levelMap;
    }
}
