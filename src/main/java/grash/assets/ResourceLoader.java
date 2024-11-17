package grash.assets;

import grash.core.GameController;
import grash.events.GrashEvent;
import grash.events.GrashEventListener;
import grash.events.GrashEvent_LoadResources;

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
        loadAllMaps();
    }

    private void loadAllMaps() {
        ArrayList<Path> foundGrashMaps = new ArrayList<>();



        MapMetadata mapMetadata = game.getMapLoader().loadMapMetadata(Paths.get(game.getWorkingDirectory() + "\\assets\\maps\\TestMap"), "TestMap.grashMap");

        System.out.println();

    }
}
