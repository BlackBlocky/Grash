package grash.event.events.level;

import grash.assets.MapData;
import grash.event.GrashEvent;
import grash.level.map.LevelMapGenerator;

/**
 * This Event is triggered after the {@link GrashEvent_LoadLevel} Event. Its doing Stuff like
 * converting the loaded level to a usable format
 * <br>Triggered by:
 * {@link grash.assets.ResourceLoader}
 *
 *  <br>Received by:
 * {@link LevelMapGenerator}
 *
 */

public final class GrashEvent_LevelLoaded extends GrashEvent {

    private MapData mapData;

    public GrashEvent_LevelLoaded(MapData mapData) {
        super("LevelLoaded");
        this.mapData = mapData;
    }

    public MapData getMapData() {
        return mapData;
    }
}
