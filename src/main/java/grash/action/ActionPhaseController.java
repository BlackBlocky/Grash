package grash.action;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.action.GrashEvent_StartActionPhase;
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

        game.getEventBus().registerListener(GrashEvent_Tick.class, this);
        game.getEventBus().registerListener(GrashEvent_StartActionPhase.class, this);
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
            case "StartActionPhase": {
                onEvent_StartActionPhase((GrashEvent_StartActionPhase) event);
                break;
            }
        }
    }

    private void onEvent_Tick(GrashEvent_Tick event) {
        if(actionPhaseState == ActionPhaseState.Inactive) return;

        // Logic to be continued
    }

    private void onEvent_StartActionPhase(GrashEvent_StartActionPhase event) {
        actionPhaseState = ActionPhaseState.Countdown;
        System.out.println("wdeee");
    }
}
