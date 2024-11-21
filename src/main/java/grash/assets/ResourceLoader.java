package grash.assets;

import grash.core.GameController;
import grash.events.GrashEvent;
import grash.events.GrashEventListener;
import grash.events.GrashEvent_LoadResources;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class ResourceLoader implements GrashEventListener {

    private GameController game;

    private HashMap<String, Path> mapMetdatasMap = new HashMap<>();
    private String[] allMapKeys;

    public ResourceLoader (GameController gameController) {
        this.game = gameController;

        game.getEventBus().registerListener(GrashEvent_LoadResources.class, this);
    }

    public Path getMapMetdata(String mapKey) {
        return mapMetdatasMap.get(mapKey);
    }

    public String[] getAllMapKeys() {
        return allMapKeys;
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch(event.getEventKey()) {
            case "LoadResources": {
                onEvent_LoadResources((GrashEvent_LoadResources) event);
                break;
            }
        }
    }

    private void onEvent_LoadResources(GrashEvent_LoadResources event) {
        MapMetadata[] loadedMapMetadatas = loadAllMaps();
        System.out.println();
    }

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

            loadedMapMetadatas.add(game.getMapLoader().loadMapMetadata(mapFolder.toPath(), grashMapFile.getName()));
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
