package grash.events;

import grash.level.map.LevelMap;
import grash.level.map.LevelMapGenerator;

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
