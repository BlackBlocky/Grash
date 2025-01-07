package grash.action;

import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.core.GameState;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.action.GrashEvent_StartActionPhase;
import grash.event.events.core.GrashEvent_Tick;
import grash.level.map.LevelMap;
import grash.level.map.LevelMapEffect;
import grash.level.map.MapEffectType;

public final class ActionPhaseController implements GrashEventListener {

    private ActionPhaseState actionPhaseState;

    private ActionPhaseValues actionPhaseValues;
    private ActionPhaseVisualEffectValues visualEffectValues;

    private ActionPhaseRenderer actionPhaseRenderer;
    // TODO Start Timestamp machen und dann Current Color im renderer mit den Effect Values Updaten
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
        this.visualEffectValues = new ActionPhaseVisualEffectValues(actionPhaseMap);
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

    /**
     * Simply the Update Method for the ActionPhase
     */
    private void onEvent_Tick(GrashEvent_Tick event) {
        if(actionPhaseState == ActionPhaseState.Inactive) return;

        double secondsElapsedSinceStart = (System.nanoTime() - actionPhaseValues.getNanoTimeAtStart()) / 1_000_000_000.0;

        updateVisualEffectRendererValues(secondsElapsedSinceStart);
        actionPhaseRenderer.updateCanvas(event.getDeltaTime(), secondsElapsedSinceStart);
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
        actionPhaseValues.setNanoTimeAtStart(System.nanoTime()); // TODO This should not be here, but yeah

        /* Generate LevelMapEffects because the Renderer needs and Effect to work with as start Values,
        and not just simple Types like "Color".
        This could be Optimized by generating these things the loader in the first place, but yeah, who cares :P */
        LevelMapEffect startColorEffect = new LevelMapEffect(MapEffectType.Color);
        startColorEffect.setColor(actionPhaseValues.getActionPhaseMap().getStartColor());

        actionPhaseRenderer.setupRenderer(this.game, startColorEffect);

        LevelMapEffect nextColorAfterStartColor = visualEffectValues.getNextColor();
        if(nextColorAfterStartColor != null)
            actionPhaseRenderer.updateColors(startColorEffect, nextColorAfterStartColor);
    }

    private void updateVisualEffectRendererValues(double secondsElapsedSinceStart) {
        /* This works because when the currentColor is not null, it is time for the next color and the
        Incrementer switching on step in the index. */
        LevelMapEffect currentColor = visualEffectValues.getCurrentColor(secondsElapsedSinceStart);
        if(currentColor != null) {
            LevelMapEffect nextColor = visualEffectValues.getNextColor();
            if(nextColor == null) nextColor = currentColor;
            actionPhaseRenderer.updateColors(currentColor, nextColor);
        };
    }
}
