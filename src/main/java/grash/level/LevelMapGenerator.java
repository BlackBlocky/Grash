package grash.level;

import grash.core.GameController;
import grash.events.GrashEvent;
import grash.events.GrashEventListener;
import grash.events.GrashEvent_LevelLoaded;

/**
 * This class is made for generating the {@link grash.level.LevelMap} Class, or in other words
 * converting the loaded Strings to an actual Level
 */
public class LevelMapGenerator implements GrashEventListener {

    private final GameController game;

    public LevelMapGenerator(GameController gameController) {
        this.game = gameController;

        game.getEventBus().registerListener(GrashEvent_LevelLoaded.class, this);
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "LevelLoaded": {
                onEvent_LevelLoaded((GrashEvent_LevelLoaded) event);
            }
        }
    }

    private void onEvent_LevelLoaded(GrashEvent_LevelLoaded event) {
        System.out.println("LevelLoaded event came to LevelMapGenerator");
    }

}
