package grash.level;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.core.GrashEvent_Initialize;
import grash.level.map.LevelMapGenerator;

public class LevelController implements GrashEventListener {

    private final GameController game;

    private LevelMapGenerator levelMapGenerator;

    public LevelController(GameController gameController) {
        this.game = gameController;

        game.getEventBus().registerListener(GrashEvent_Initialize.class, this);
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "Initialize": {
                onEvent_Initialize((GrashEvent_Initialize) event);
            }
        }
    }

    private void onEvent_Initialize(GrashEvent_Initialize event) {
        this.levelMapGenerator = new LevelMapGenerator(game);
    }
}
