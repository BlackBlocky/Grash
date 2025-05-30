package grash.action;

import grash.action.objects.ActionObject;
import grash.action.objects.NoteObject;
import grash.action.objects.ObstacleObject;
import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.core.GameState;
import grash.core.WindowState;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.action.GrashEvent_ExitActionPhase;
import grash.event.events.action.GrashEvent_NoteHit;
import grash.event.events.action.GrashEvent_PlayerDied;
import grash.event.events.action.GrashEvent_StartActionPhase;
import grash.event.events.core.GrashEvent_Tick;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.event.events.scene.GrashEvent_SwitchScene;
import grash.level.map.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

    private MediaPlayer mapSong;
    private double lastSongCurrentTimeSeconds;
    private MediaPlayer.Status lastSongStatus;

    private Label guiScoreText;
    private Label guiAccuracyText;

    private ProgressBar levelProgressBar;

    public static final double PRE_GENERATED_DISTANCE = 25;
    public static final double DESTROY_DISTANCE = -3;
    public static final double Y_UP = 4;
    public static final double Y_MIDDLE = 6;
    public static final double Y_DOWN = 8;
    public static final double PLAYER_X = 8;

    public static final double Y_NOTE_UP = 2;
    public static final double Y_NOTE_DOWN = 10;

    public static final double PERFECT_NOTE_SECONDS_OFF = 0.1;
    public static final double GOOD_NOTE_SECONDS_OFF = 0.2;
    public static final double OK_NOTE_SECONDS_OFF = 0.3;
    public static final double IGNORE_NOTE_SECONDS_OFF = 1.0;

    public static final double PERFECT_POINTS = 300;
    public static final double GOOD_POINTS = 100;
    public static final double OK_POINTS = 50;
    public static final double FAILED_POINTS = 0;

    public static final double PERFECT_ACCURACY = 1.0;
    public static final double GOOD_ACCURACY = 1.0/3.0;
    public static final double OK_ACCURACY = 1.0/6.0;
    public static final double FAILED_ACCURACY = 0.0;

    public static final Color PERFECT_COLOR = Color.CYAN;
    public static final Color GOOD_COLOR = Color.LIMEGREEN;
    public static final Color OK_COLOR = Color.ORANGE;
    public static final Color FAILED_COLOR = Color.RED;

    public static final HashMap<NoteAccuracy, Double> NOTE_HIT_TO_POINTS_MAP = new HashMap<>(Map.ofEntries(
            Map.entry(NoteAccuracy.Perfect, PERFECT_POINTS),
            Map.entry(NoteAccuracy.Good,    GOOD_POINTS),
            Map.entry(NoteAccuracy.Ok,      OK_POINTS),
            Map.entry(NoteAccuracy.Failed,  FAILED_POINTS)
    ));
    public static final HashMap<NoteAccuracy, Double> NOTE_HIT_TO_ACCURACY_MAP = new HashMap<>(Map.ofEntries(
            Map.entry(NoteAccuracy.Perfect, PERFECT_ACCURACY),
            Map.entry(NoteAccuracy.Good,    GOOD_ACCURACY),
            Map.entry(NoteAccuracy.Ok,      OK_ACCURACY),
            Map.entry(NoteAccuracy.Failed,  FAILED_ACCURACY)
    ));
    public static final HashMap<NoteAccuracy, Color> NOTE_HIT_TO_COLOR_MAP = new HashMap<>(Map.ofEntries(
            Map.entry(NoteAccuracy.Perfect, PERFECT_COLOR),
            Map.entry(NoteAccuracy.Good,    GOOD_COLOR),
            Map.entry(NoteAccuracy.Ok,      OK_COLOR),
            Map.entry(NoteAccuracy.Failed,  FAILED_COLOR)
    ));

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

        this.mapSong = null;

        this.useCustomTime = false;
        this.lastCustomTimeSeconds = 0.0;

        this.useCustomPlayerHeight = false;

        game.getEventBus().registerListener(GrashEvent_Tick.class, this);
        game.getEventBus().registerListener(GrashEvent_StartActionPhase.class, this);
        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
        game.getEventBus().registerListener(GrashEvent_PlayerDied.class, this);
        game.getEventBus().registerListener(GrashEvent_ExitActionPhase.class, this);
        game.getEventBus().registerListener(GrashEvent_NoteHit.class, this);
    }

    public ActionPhaseValues getActionPhaseValues() { return this.actionPhaseValues; }
    public ActionPhaseLogicHandler getActionPhaseLogicHandler() { return this.actionPhaseLogicHandler; }
    public ActionPhaseState getActionPhaseState() { return this.actionPhaseState; }
    public boolean getUseCustomTime() { return this.useCustomTime; }
    public boolean getUseCustomPlayerHeight() { return this.useCustomPlayerHeight; }

    public void setupNewActionPhase(LevelMap actionPhaseMap, double startCountdownTimeSeconds) {
        this.actionPhaseValues = new ActionPhaseValues(actionPhaseMap, startCountdownTimeSeconds, game);
        this.visualEffectValues = new ActionPhaseVisualEffectValues(actionPhaseMap);

        this.lastSongCurrentTimeSeconds = 0.0;

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
            case "NoteHit": {
                onEvent_NoteHit((GrashEvent_NoteHit) event);;
                break;
            }
        }
    }

    public void addToDestroyQueue(ActionObject actionObject) {
        actionPhaseValues.getDestroyQueue().add(actionObject);
    }

    private void processDestroyQueue(HashSet<ActionObject> queue) {
        if(actionPhaseValues == null) return;

        // Solving this with a while loop because otherwise it could happen that during the foreach a new element
        //  is being added to the destroyQueue
        while(!queue.isEmpty()) {
            ActionObject next = queue.iterator().next();
            if(next instanceof ObstacleObject)
                actionPhaseValues.getCurrentObstacleObjects().remove((ObstacleObject) next);
            else
                actionPhaseValues.getCurrentNoteObjects().remove((NoteObject) next);
            queue.remove(next);

            // TODO Make the scoring a bit better pls UwU
            actionPhaseValues.addScore(10);
            updateGUI();
        }
    }

    /**
     * Simply the Update Method for the ActionPhase
     */
    private void onEvent_Tick(GrashEvent_Tick event) {
        if(actionPhaseState == ActionPhaseState.Inactive) return;

        // Waiting until the Song actually starts playing, so the Song and the Map are synced. (about 200 ms delay)
        if(mapSong != null && (lastSongStatus == MediaPlayer.Status.UNKNOWN || lastSongStatus == MediaPlayer.Status.READY)) {
            lastSongStatus = mapSong.getStatus();
            if(lastSongStatus != MediaPlayer.Status.PLAYING) return;
            else actionPhaseValues.setNanoTimeAtStart(System.nanoTime());
        }

        // Make sure that the Audio Player ACTUALLY playing, and not just pretending
        if(mapSong != null && lastSongCurrentTimeSeconds == 0.0) {
            lastSongCurrentTimeSeconds = mapSong.getCurrentTime().toSeconds();
            if(lastSongCurrentTimeSeconds == 0.0) return;
            else actionPhaseValues.setNanoTimeAtStart(System.nanoTime());
        }

        double secondsElapsedSinceStart = calculateTimeSinceStartInSeconds();
        double deltaTime = event.getDeltaTime();
        //System.out.println(1.0 / deltaTime);
//        if(mapSong != null) {
//            secondsElapsedSinceStart = mapSong.getCurrentTime().toMillis() / 1000.0;
//            deltaTime = secondsElapsedSinceStart - lastSongCurrentTimeSeconds;
//            lastSongCurrentTimeSeconds = secondsElapsedSinceStart;
//        }

        //System.out.println(mapSong.getStatus());
        //System.out.println(secondsElapsedSinceStart + " - " + mapSong.getCurrentTime());

        //System.out.println(secondsElapsedSinceStart + " - " + deltaTime + " :: " + mapSong.getCurrentTime().toSeconds());

        //System.out.println(secondsElapsedSinceStart + " - " + mapSong.getCurrentTime().toSeconds());

        // Doing the destroyQueue first, so that all Objects are removed that are supposed to be removed in this frame.
        getActionPhaseLogicHandler().destroyUnneededObjects(
                actionPhaseValues.getCurrentObstacleObjects(),
                actionPhaseValues.getCurrentNoteObjects(),
                actionPhaseValues.getPlayerObject()
        );
        processDestroyQueue(actionPhaseValues.getDestroyQueue());

        // Manipulate the Time when useCustomTime is true. (Going backwards and forwards)
        if(useCustomTime) {
            actionPhaseLogicHandler.updateCustomTime(event.getDeltaTime());
            secondsElapsedSinceStart = actionPhaseValues.getCustomTime();
            deltaTime = actionPhaseValues.getCustomTime() - lastCustomTimeSeconds;

            lastCustomTimeSeconds = actionPhaseValues.getCustomTime();
        }



        // Updating the Player before everything moves, because otherwise something could move into the player.
        actionPhaseLogicHandler.playerLogicHandler(actionPhaseValues.getPlayerObject(), secondsElapsedSinceStart,
                actionPhaseValues.getActionPhaseMap().getSpeed(), event.getDeltaTime(), deltaTime);

        // Checking if the Player collides with something
        if(actionPhaseLogicHandler.checkIfPlayerIsColliding(actionPhaseValues.getPlayerObject(),
                actionPhaseValues.getCurrentObstacleObjects()).getCollisionType() == CollisionType.Deadly) {
            game.getEventBus().triggerEvent(new GrashEvent_PlayerDied());
        }

        /* Doing the Logic first and the Spawning after the Logic, because the Object shouldn't be moved
         when it spawned, because that would mess up the timing, I guess*/
        actionPhaseLogicHandler.moveAllObstacleObjects(actionPhaseValues.getCurrentObstacleObjects(),
                actionPhaseValues.getActionPhaseMap().getSpeed(), deltaTime);
        actionPhaseLogicHandler.moveAllNoteObjects(actionPhaseValues.getCurrentNoteObjects(),
                actionPhaseValues.getActionPhaseMap().getSpeed(), deltaTime);
        actionPhaseObjectHandler.processLevelMapTimeline(secondsElapsedSinceStart);

        // Updating the renderer
        updateCanvas(secondsElapsedSinceStart, deltaTime);

        updateLevelProgressBarAndCheckIfLevelIsOver();
    }

    private void updateCanvas(double secondsElapsedSinceStart, double deltaTime) {
        updateVisualEffectRendererValues(secondsElapsedSinceStart);
        actionPhaseRenderer.updateCanvas(deltaTime, secondsElapsedSinceStart,
                getActionPhaseValues().getCurrentObstacleObjects(),
                getActionPhaseValues().getCurrentNoteObjects(),
                actionPhaseValues.getPlayerObject());
    }

    /**
     * This method setups everything so that the Level can be played.
     * The setupNewActionPhase() method was already called when this is going to be called.
     * This Method is called, after the Scene was switched.
     * So the Window is already loaded.
     */
    private void onEvent_StartActionPhase(GrashEvent_StartActionPhase event) {
        if(game.getGameState() != GameState.GameActionPhase) {
            System.out.println("ERROR: Tried to render something without being in the GameActionPhase!!");
            return;
        }

        setupGUI();

        actionPhaseState = ActionPhaseState.Countdown;
        actionPhaseValues.setNanoTimeAtStart(System.nanoTime()); // TODO This should not be here, but yeah
        actionPhaseState = ActionPhaseState.Active; // TODO This should not be here, but yeah

        if(actionPhaseValues.getActionPhaseMap().getMapMetadata().getSongMetadata() != null) {
            mapSong = new MediaPlayer(actionPhaseValues.getActionPhaseMap().getMapMetadata().getSongMetadata());
            mapSong.play();
            lastSongStatus = MediaPlayer.Status.UNKNOWN;
        }

        /* Generate LevelMapEffects because the Renderer needs and Effect to work with as start Values,
        and not just simple Types like "Color".
        This could be Optimized by generating these things the loader in the first place, but yeah, who cares :P */
        LevelMapEffect startColorEffect = new LevelMapEffect(MapEffectType.Color);
        startColorEffect.setColor(actionPhaseValues.getActionPhaseMap().getStartColor());

        LevelMapEffect startRotationEffect = new LevelMapEffect(MapEffectType.Rotate);
        startRotationEffect.setValueDouble(actionPhaseValues.getActionPhaseMap().getStartRotation());

        LevelMapEffect startFovScaleEffect = new LevelMapEffect(MapEffectType.FOVScale);
        startFovScaleEffect.setValueDouble(actionPhaseValues.getActionPhaseMap().getStartFOVScale());

        // Setup renderer and other necessary stuff
        actionPhaseValues.getActionPhaseMap().getLevelMapTimeline().calculateXStartPosForEveryStack(
                actionPhaseValues.getActionPhaseMap().getSpeed()
        );
        Canvas gameCanvas = (Canvas) game.getPrimaryStage().getScene().lookup("#gameCanvas");
        actionPhaseRenderer.setupRenderer(startColorEffect, startRotationEffect, startFovScaleEffect, gameCanvas);

        /* Only set the next second Color of there even is a second color.
         This needs to be executed AFTER the Renderer was set up,
         because we are using the updateColors() Method */
        LevelMapEffect nextColorAfterStartColor = visualEffectValues.getNextColor();
        if(nextColorAfterStartColor != null)
            actionPhaseRenderer.updateColors(startColorEffect, nextColorAfterStartColor);

        LevelMapEffect nextRotationAfterStartRotation = visualEffectValues.getNextRotation();
        if(nextRotationAfterStartRotation != null)
            actionPhaseRenderer.updateRotations(startRotationEffect, nextRotationAfterStartRotation);

        LevelMapEffect nextFovScaleAfterStartFovScale = visualEffectValues.getNextFovScale();
        if(nextFovScaleAfterStartFovScale != null) {
            actionPhaseRenderer.updateFovScales(startFovScaleEffect, nextFovScaleAfterStartFovScale);
        }

        actionPhaseLogicHandler.resetLogicHandler();
    }

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        if(actionPhaseState == ActionPhaseState.Inactive) return;

        if(event.getKeyCode() == KeyCode.MINUS && !this.useCustomTime) {
            this.actionPhaseValues.setCustomTime(calculateTimeSinceStartInSeconds());
            this.lastCustomTimeSeconds = actionPhaseValues.getCustomTime();
            this.useCustomTime = true;
        }
        if(event.getKeyCode() == KeyCode.NUMBER_SIGN) {
            this.useCustomPlayerHeight = true;
        }
        if(event.getKeyCode() == KeyCode.ESCAPE) {
            game.getEventBus().triggerEvent(new GrashEvent_ExitActionPhase());
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

    private void onEvent_NoteHit(GrashEvent_NoteHit event) {
        System.out.println(event.getNoteAccuracy());
        double scoreAdd = NOTE_HIT_TO_POINTS_MAP.get(event.getNoteAccuracy());
        double accuracyAdd = NOTE_HIT_TO_ACCURACY_MAP.get(event.getNoteAccuracy());

        actionPhaseValues.countNoteHit(event.getNoteAccuracy());
        actionPhaseValues.addScore(scoreAdd);
        actionPhaseValues.addAccuracy(accuracyAdd);

        event.getNoteObject().doTapAnimation(event.getNoteAccuracy());
        event.getNoteObject().destroyObject(200);

        updateGUI();
        System.out.println(actionPhaseValues.getScore() + " - " + actionPhaseValues.calculateCurrentAccuracy() + " - " + actionPhaseValues.getTotalHitNotes());
    }

    private void setupGUI() {
        guiScoreText = (Label) game.getPrimaryStage().getScene().lookup("#scoreText");
        guiAccuracyText = (Label) game.getPrimaryStage().getScene().lookup("#accuracyText");
        levelProgressBar = (ProgressBar) game.getPrimaryStage().getScene().lookup("#levelProgressBar");
    }

    private void updateGUI() {
        double displayedScore = actionPhaseValues.getScore();
        double displayedAccuracy = actionPhaseValues.calculateCurrentAccuracy() * 100;

        guiScoreText.setText(String.format("%09.0f", displayedScore));
        guiAccuracyText.setText(String.format("%03.2f", displayedAccuracy) + "%");
    }

    /**
     * This will close everything related to the ActionPhase.
     * It also will everything clean up for the next run and go back to the menu.
     */
    private void exitActionPhase() {
        actionPhaseState = ActionPhaseState.Inactive;
        actionPhaseValues = null;
        visualEffectValues = null;

        if(mapSong != null) {
            mapSong.dispose();
            mapSong = null;
        }

        // TODO remove the limeline after (ist just for the current dead anim)
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.8), e -> {
            //LockSupport.parkNanos((long)(0.5 * 1_000_000_000.0));
            game.getEventBus().triggerEvent(new GrashEvent_SwitchScene(WindowState.LevelSelectorMenu));
        }));
        timeline.play();
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

        LevelMapEffect currentRotation = visualEffectValues.getCurrentRotation(secondsElapsedSinceStart);
        if(currentRotation != null) {
            LevelMapEffect nextRotation = visualEffectValues.getNextRotation();
            if(nextRotation == null) nextRotation = currentRotation;
            actionPhaseRenderer.updateRotations(currentRotation, nextRotation);
        }

        LevelMapEffect currentFOVScale = visualEffectValues.getCurrentFovScale(secondsElapsedSinceStart);
        if(currentFOVScale != null) {
            LevelMapEffect nextFovScale = visualEffectValues.getNextFovScale();
            if(nextFovScale == null) nextFovScale = currentFOVScale;
            actionPhaseRenderer.updateFovScales(currentFOVScale, nextFovScale);
        }
    }

    private void updateLevelProgressBarAndCheckIfLevelIsOver() {
        if(mapSong == null) return;

        // Update Bar
        double currentProgressSecond = mapSong.getCurrentTime().toSeconds();
        double maxSeconds = mapSong.getTotalDuration().toSeconds();

        levelProgressBar.setProgress(currentProgressSecond / maxSeconds);

        // Check if level is over
        if(currentProgressSecond >= maxSeconds) {
            game.getEventBus().triggerEvent(new GrashEvent_ExitActionPhase());
        }
    }
}
