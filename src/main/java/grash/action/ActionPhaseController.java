package grash.action;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.core.GrashEvent_Tick;
import grash.level.map.LevelMap;

public final class ActionPhaseController implements GrashEventListener {

    private ActionPhaseValues actionPhaseValues;
    private ActionPhaseState actionPhaseState;

    private GameController game;

    public ActionPhaseController(GameController gameController) {
        this.actionPhaseValues = null;
        this.actionPhaseState = ActionPhaseState.Inactive;

        this.game = gameController;
    }

    public void setupNewActionPhase(LevelMap actionPhaseMap, double startCountdownTimeSeconds) {
        this.actionPhaseValues = new ActionPhaseValues(actionPhaseMap, startCountdownTimeSeconds);
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "Tick": {
                onEvent_Tick((GrashEvent_Tick) event);
                break;
            }
        }
    }

    private void onEvent_Tick(GrashEvent_Tick event) {

    }
}
