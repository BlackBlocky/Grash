package grash.level;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.core.GrashEvent_Initialize;
import grash.event.events.level.GrashEvent_InitLevel;
import grash.level.map.LevelMap;
import grash.level.map.LevelMapGenerator;

public class LevelController implements GrashEventListener {

    private final GameController game;

    private LevelMapGenerator levelMapGenerator;

    public LevelController(GameController gameController) {
        this.game = gameController;

        game.getEventBus().registerListener(GrashEvent_Initialize.class, this);
        game.getEventBus().registerListener(GrashEvent_InitLevel.class, this);
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "Initialize": {
                onEvent_Initialize((GrashEvent_Initialize) event);
                break;
            }
            case "InitLevel": {
                onEvent_InitLevel((GrashEvent_InitLevel) event);
                break;
            }
        }
    }

    private void onEvent_Initialize(GrashEvent_Initialize event) {
        this.levelMapGenerator = new LevelMapGenerator(game);
    }

    private void onEvent_InitLevel(GrashEvent_InitLevel event) {
        LevelMap constructingLevelMap = event.getLevelMap();
        LevelMapTimeline generatedLevelMapTimeLine = new LevelMapTimeline(constructingLevelMap);
        constructingLevelMap.setLevelMapTimeline(generatedLevelMapTimeLine);

    }
}
