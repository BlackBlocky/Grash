package grash.action;

import grash.core.GameController;
import grash.core.GameState;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.action.GrashEvent_StartActionPhase;
import grash.event.events.core.GrashEvent_Tick;
import grash.level.map.LevelMap;

public final class ActionPhaseController implements GrashEventListener {

    private ActionPhaseValues actionPhaseValues;
    private ActionPhaseState actionPhaseState;

    private ActionPhaseRenderer actionPhaseRenderer;

    private GameController game;

    public ActionPhaseController(GameController gameController) {
        this.actionPhaseValues = null;
        this.actionPhaseState = ActionPhaseState.Inactive;

        this.game = gameController;
        this.actionPhaseRenderer = new ActionPhaseRenderer();

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

        actionPhaseRenderer.updateCanvas(event.getDeltaTime());
        // Logic to be continued
    }

    /**
     * This method setups everything so that the Level can be played.
     * The setupNewActionPhase() method was already called when this is going to be called.
     */
    private void onEvent_StartActionPhase(GrashEvent_StartActionPhase event) {
        if(game.getGameState() != GameState.GameActionPhase) {
            System.out.println("ERROR: Tried to render something without being in the GameActionPhase!!");
            return;
        }

        actionPhaseState = ActionPhaseState.Countdown;
        actionPhaseRenderer.setupRenderer(this.game, actionPhaseValues.getActionPhaseMap().getStartColor());
    }
}
