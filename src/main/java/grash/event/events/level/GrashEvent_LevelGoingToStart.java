package grash.event.events.level;

import grash.event.GrashEvent;
import grash.level.map.LevelMap;

/**
 * This Event is triggered after when everything is ready in the Map and now the Game just needs
 * To switch into the Level Mode
 * <br>Triggered by:
 * {@link grash.level.LevelController}
 *
 *  <br>Received by:
 * {@link grash.core.GameController}
 *
 */

public class GrashEvent_LevelGoingToStart extends GrashEvent {

    private final LevelMap levelMap;

    public GrashEvent_LevelGoingToStart(LevelMap levelMap) {
        super("LevelGoingToStart");
        this.levelMap = levelMap;
    }

    public LevelMap getLevelMap() {
        return levelMap;
    }
}
