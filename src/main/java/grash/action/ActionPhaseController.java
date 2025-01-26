package grash.action;

import grash.action.objects.ObstacleObject;
import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.core.GameState;
import grash.core.WindowState;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.action.GrashEvent_ExitActionPhase;
import grash.event.events.action.GrashEvent_PlayerDied;
import grash.event.events.action.GrashEvent_StartActionPhase;
import grash.event.events.core.GrashEvent_Tick;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.event.events.scene.GrashEvent_SwitchScene;
import grash.level.LevelMapTimeline;
import grash.level.LevelMapTimelineStack;
import grash.level.map.*;
import javafx.scene.input.KeyCode;

import java.util.concurrent.locks.LockSupport;

public final class ActionPhaseController implements GrashEventListener {

    private ActionPhaseState actionPhaseState;

    private ActionPhaseValues actionPhaseValues;
    private ActionPhaseVisualEffectValues visualEffectValues;

    private final ActionPhaseObjectHandler actionPhaseObjectHandler;
    private final ActionPhaseLogicHandler actionPhaseLogicHandler;

    private final ActionPhaseRenderer actionPhaseRenderer;
    // TODO Start Timestamp machen und dann Current Color im renderer mit den Effect Values Updaten
    private final GameController game;

    public static final double PRE_GENERATED_DISTANCE = 25;
    public static final double Y_UP = 4;
    public static final double Y_MIDDLE = 6;
    public static final double Y_DOWN = 8;
    public static final double PLAYER_X = 8;

    private boolean useCustomTime;
    private double lastCustomTimeSeconds;

    private boolean useCustomPlayerHeight;

    public ActionPhaseController(GameController gameController) {
        this.actionPhaseValues = null;
        this.actionPhaseState = ActionPhaseState.Inactive;

        this.game = gameController;
        this.actionPhaseRenderer = new ActionPhaseRenderer(game);
        this.actionPhaseObjectHandler = new ActionPhaseObjectHandler(this, game);
        this.actionPhaseLogicHandler = new ActionPhaseLogicHandler(this, game);

        this.useCustomTime = false;
        this.lastCustomTimeSeconds = 0.0;

        this.useCustomPlayerHeight = false;

        game.getEventBus().registerListener(GrashEvent_Tick.class, this);
        game.getEventBus().registerListener(GrashEvent_StartActionPhase.class, this);
        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
        game.getEventBus().registerListener(GrashEvent_PlayerDied.class, this);
        game.getEventBus().registerListener(GrashEvent_ExitActionPhase.class, this);
    }

    public ActionPhaseValues getActionPhaseValues() { return this.actionPhaseValues; }
    public ActionPhaseState getActionPhaseState() { return this.actionPhaseState; }
    public boolean getUseCustomTime() { return this.useCustomTime; }
    public boolean getUseCustomPlayerHeight() { return this.useCustomPlayerHeight; }

    public void setupNewActionPhase(LevelMap actionPhaseMap, double startCountdownTimeSeconds) {
        this.actionPhaseValues = new ActionPhaseValues(actionPhaseMap, startCountdownTimeSeconds, game);
        this.visualEffectValues = new ActionPhaseVisualEffectValues(actionPhaseMap);

        this.useCustomTime = false;
        this.lastCustomTimeSeconds = 0.0;
        this.useCustomPlayerHeight = false;
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
            case "KeyDown": {
                onEvent_KeyDown((GrashEvent_KeyDown) event);
                break;
            }
            case "PlayerDied": {
                onEvent_PlayerDied((GrashEvent_PlayerDied) event);
                break;
            }
            case "ExitActionPhase": {
                onEvent_ExitActionPhase((GrashEvent_ExitActionPhase) event);
                break;
            }
        }
    }

    /**
     * Simply the Update Method for the ActionPhase
     */
    private void onEvent_Tick(GrashEvent_Tick event) {
        if(actionPhaseState == ActionPhaseState.Inactive) return;

        double secondsElapsedSinceStart = calculateTimeSinceStartInSeconds();
        double deltaTime = event.getDeltaTime();

        if(useCustomTime) {
            actionPhaseLogicHandler.updateCustomTime(event.getDeltaTime());
            secondsElapsedSinceStart = actionPhaseValues.getCustomTime();
            deltaTime = actionPhaseValues.getCustomTime() - lastCustomTimeSeconds;

            lastCustomTimeSeconds = actionPhaseValues.getCustomTime();
        }

        // Updating the Player before everything moves, because otherwise something could move into the player.
        actionPhaseLogicHandler.playerLogicHandler(actionPhaseValues.getPlayerObject(), secondsElapsedSinceStart,
                actionPhaseValues.getActionPhaseMap().getSpeed(), event.getDeltaTime());

        // Checking if the Player collides with something
        if(actionPhaseLogicHandler.checkIfPlayerIsColliding(actionPhaseValues.getPlayerObject(),
                actionPhaseValues.getCurrentObstacleObjects())) {
            game.getEventBus().triggerEvent(new GrashEvent_PlayerDied());
        }

        /* Doing the Logic first and the Spawning after the Logic, because the Object shouldn't be moved
         when it spawned, because that would mess up the timing, I guess*/
        actionPhaseLogicHandler.moveAllObstacleObjects(actionPhaseValues.getCurrentObstacleObjects(),
                actionPhaseValues.getActionPhaseMap().getSpeed(), deltaTime);
        actionPhaseObjectHandler.processLevelMapTimeline(secondsElapsedSinceStart);

        // Updating the renderer
        updateVisualEffectRendererValues(secondsElapsedSinceStart);
        actionPhaseRenderer.updateCanvas(deltaTime, secondsElapsedSinceStart,
                getActionPhaseValues().getCurrentObstacleObjects(),
                actionPhaseValues.getPlayerObject());
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
        actionPhaseState = ActionPhaseState.Active; // TODO This should not be here, but yeah

        /* Generate LevelMapEffects because the Renderer needs and Effect to work with as start Values,
        and not just simple Types like "Color".
        This could be Optimized by generating these things the loader in the first place, but yeah, who cares :P */
        LevelMapEffect startColorEffect = new LevelMapEffect(MapEffectType.Color);
        startColorEffect.setColor(actionPhaseValues.getActionPhaseMap().getStartColor());

        // Setup renderer and other necessary stuff
        actionPhaseValues.getActionPhaseMap().getLevelMapTimeline().calculateXStartPosForEveryStack(
                actionPhaseValues.getActionPhaseMap().getSpeed()
        );
        actionPhaseRenderer.setupRenderer(startColorEffect);

        /* Only set the next second Color of there even is a second color.
         This needs to be executed AFTER the Renderer was set up,
         because we are using the updateColors() Method */
        LevelMapEffect nextColorAfterStartColor = visualEffectValues.getNextColor();
        if(nextColorAfterStartColor != null)
            actionPhaseRenderer.updateColors(startColorEffect, nextColorAfterStartColor);
    }

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        if(actionPhaseState == ActionPhaseState.Inactive) return;

        if(event.getKeyCode() == KeyCode.MINUS) {
            this.actionPhaseValues.setCustomTime(calculateTimeSinceStartInSeconds());
            this.lastCustomTimeSeconds = actionPhaseValues.getCustomTime();
            this.useCustomTime = true;
        }
        if(event.getKeyCode() == KeyCode.NUMBER_SIGN) {
            this.useCustomPlayerHeight = true;
        }
    }

    private void onEvent_PlayerDied(GrashEvent_PlayerDied event) {
        // TODO this is only for test should me resetting the level instead
        game.getEventBus().triggerEvent(new GrashEvent_ExitActionPhase());
    }

    private void onEvent_ExitActionPhase(GrashEvent_ExitActionPhase event) {
        exitActionPhase();
    }

    private double calculateTimeSinceStartInSeconds() {
        return (System.nanoTime() - actionPhaseValues.getNanoTimeAtStart()) / 1_000_000_000.0;
    }

    /**
     * This will close everything related to the ActionPhase.
     * It also will everything clean up for the next run and go back to the menu.
     */
    private void exitActionPhase() {
        actionPhaseState = ActionPhaseState.Inactive;
        actionPhaseValues = null;
        visualEffectValues = null;

        LockSupport.parkNanos((long)(0.5 * 1_000_000_000.0));

        game.getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.WelcomeScreen));
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
