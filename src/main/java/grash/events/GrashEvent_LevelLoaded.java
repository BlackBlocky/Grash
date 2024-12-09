package grash.events;

import grash.assets.MapData;

/**
 * This Event is triggered after the {@link grash.events.GrashEvent_LoadLevel} Event. Its doing Stuff like
 * converting the loaded level to a usable format
 * <br>Triggered by:
 * {@link grash.assets.ResourceLoader}
 *
 *  <br>Received by:
 * {@link grash.level.LevelMapGenerator}
 *
 */

public class GrashEvent_LevelLoaded extends GrashEvent {

    private MapData mapData;

    public GrashEvent_LevelLoaded(MapData mapData) {
        super("LevelLoaded");
        this.mapData = mapData;
    }

    public MapData getMapData() {
        return mapData;
    }
}
