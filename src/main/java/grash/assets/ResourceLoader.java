package grash.assets;

import grash.core.GameController;
import grash.event.*;
import grash.event.events.core.GrashEvent_LoadResources;
import grash.event.events.level.GrashEvent_LevelLoaded;
import grash.event.events.level.GrashEvent_LoadLevel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public final class ResourceLoader implements GrashEventListener {

    private final GameController game;

    private final MapLoader mapLoader;

    private HashMap<String, MapMetadata> mapMetdatasMap = new HashMap<>();
    private String[] allMapKeys = new String[0];

    public ResourceLoader (GameController gameController) {
        this.game = gameController;
        this.mapLoader = new MapLoader();

        game.getEventBus().registerListener(GrashEvent_LoadResources.class, this);
        game.getEventBus().registerListener(GrashEvent_LoadLevel.class, this);
    }

    public MapMetadata getMapMetadata(String mapKey) {
        return mapMetdatasMap.get(mapKey);
    }

    public String[] getAllMapKeys() {
        return allMapKeys;
    }

    /**
     * Uses the {@link MapLoader} to load the Matching Map to MapKey.
     * If a Map with the Map does not exist, it will return null
     */
    public MapData loadMap(String mapKey) {
        if(!mapMetdatasMap.containsKey(mapKey)) return null;
        return mapLoader.loadMap(mapMetdatasMap.get(mapKey));
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch(event.getEventKey()) {
            case "LoadResources": {
                onEvent_LoadResources((GrashEvent_LoadResources) event);
                break;
            }
            case "LoadLevel": {
                onEvent_LoadLevel((GrashEvent_LoadLevel) event);
                break;
            }
        }
    }

    /**
     * Loading all the MapMetadata (Not the Actual Map) and adding everything loaded to the Resource-Data
     */
    private void onEvent_LoadResources(GrashEvent_LoadResources event) {
        MapMetadata[] loadedMapMetadatas = loadAllMaps();
        ArrayList<String> allLoadedMapKeys = new ArrayList<>();

        if(loadedMapMetadatas == null) return;

        for(MapMetadata mapMD : loadedMapMetadatas) {
            allLoadedMapKeys.add(mapMD.getMapKey());
            mapMetdatasMap.put(mapMD.getMapKey(), mapMD);
        }

        allMapKeys = allLoadedMapKeys.toArray(new String[0]);
    }

    /**
     * Starts the process of loading a Level by loading the MapData as Strings
     */
    private void onEvent_LoadLevel(GrashEvent_LoadLevel event) {
        MapData loadedTargetMap = loadMap(event.getLevelKey());

        if(loadedTargetMap == null) {
            System.out.println("Level " + event.getLevelKey() + " not found!");
            return;
        }

        game.getEventBus().triggerEvent(new GrashEvent_LevelLoaded(loadedTargetMap));
    }

    /**
     *  Loops thought the "assets/maps" folder,
     *  and creates a MapMetadata for every Map that is in the Folder (or subfolders)
     */
    private MapMetadata[] loadAllMaps() {
        // Load the Folder which contains all Maps
        File mapsFolder = new File(game.getWorkingDirectory() + "\\assets\\maps");
        if(!mapsFolder.isDirectory()) return null;

        // Get every Folder in the Maps Folder
        ArrayList<MapMetadata> loadedMapMetadatas = new ArrayList<>();
        File[] allMapFolders = mapsFolder.listFiles();

        if(allMapFolders == null) return null;

        // Loop through all MapFolders, check if they are valid and then load the MetaData
        for(File mapFolder : allMapFolders) {
            File grashMapFile = getDotGrashMapFileInMapFolder(mapFolder);
            if(grashMapFile == null) continue;

            loadedMapMetadatas.add(mapLoader.loadMapMetadata(mapFolder.toPath(), grashMapFile.getName()));
        }

        System.out.println();
        return loadedMapMetadatas.toArray(new MapMetadata[0]);
    }

    /**
     * Checks if there actually is a .grashMap file: If that is the case, return it.
     */
    private File getDotGrashMapFileInMapFolder(File mapFolder) {
        if(!mapFolder.isDirectory()) return null;

        File[] filesInMapFolder = mapFolder.listFiles();
        if(filesInMapFolder == null) return null;

        for(File file : filesInMapFolder) {
            if(file.getName().contains(".grashMap")) return file;
        }

        return null;
    }
}
