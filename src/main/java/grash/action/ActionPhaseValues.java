package grash.action;

import grash.action.objects.*;
import grash.core.GameController;
import grash.level.map.LevelMap;
import grash.math.Vec2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class ActionPhaseValues {
    private LevelMap actionPhaseMap;

    private double timeProgress;
    private double score;
    private double accuracy;
    private double countdownLeft;
    private int currentTimelineIndex;

    private int perfectNoteHits;
    private int goodNoteHits;
    private int okNoteHits;
    private int failedNoteHits;
    private int totalHitNotes;

    private final List<ObstacleObject> currentObstacleObjects;
    private final List<NoteObject> currentNoteObjects;
    private final PlayerObject playerObject;

    private final HashSet<ActionObject> destroyQueue;

    /**
     * This is the measured time AFTER the Countdown is done!
     */
    private long nanoTimeAtStart;

    private double customTime; // The Time this is used as "secondsSinceStart"

    public ActionPhaseValues(LevelMap actionPhaseMap, double countdownLeft, GameController gameController) {
        this.actionPhaseMap = actionPhaseMap;
        this.timeProgress = 0.0;
        this.score = 0.0;
        this.accuracy = 0.0;
        this.countdownLeft = countdownLeft;
        this.currentTimelineIndex = -1;

        this.perfectNoteHits = 0;
        this.goodNoteHits = 0;
        this.okNoteHits = 0;
        this.failedNoteHits = 0;
        this.totalHitNotes = 0;

        this.currentObstacleObjects = new ArrayList<>();
        this.currentNoteObjects = new ArrayList<>();

        // Creating the PlayerObject
        Hitbox playerHitbox = new Hitbox(new Vec2(0.8, 0.7), new Vec2(0.1, 0.15));
        Hitbox playerSneakHitboxDown = new Hitbox(new Vec2(0.8, 0.35), new Vec2(0.1, 0.5));
        Hitbox playerSneakHitboxUp = new Hitbox(new Vec2(0.8, 0.35), new Vec2(0.1, 0.15));
        this.playerObject = new PlayerObject(gameController,
                new Vec2(ActionPhaseController.PLAYER_X, ActionPhaseController.Y_DOWN),
                playerHitbox, playerSneakHitboxDown, playerSneakHitboxUp);

        this.destroyQueue = new HashSet<ActionObject>();

        this.customTime = 0.0;
    }

    public void addScore(double value) {
        this.score += value;
    }

    public void addAccuracy(double value) {
        this.accuracy += value;
    }

    public void addTimeProgress(double value) {
        this.timeProgress += value;
    }
    public void removeCountdown(double value) {
        this.countdownLeft -= value;
    }

    public long getNanoTimeAtStart() {
        return nanoTimeAtStart;
    }

    public void setNanoTimeAtStart(long nanoTimeAtStart) {
        this.nanoTimeAtStart = nanoTimeAtStart;
    }

    public LevelMap getActionPhaseMap() {
        return actionPhaseMap;
    }

    public double getTimeProgress() {
        return timeProgress;
    }

    public double getScore() {
        return score;
    }

    public double getCountdownLeft() {
        return countdownLeft;
    }

    public int getCurrentTimelineIndex() {
        return currentTimelineIndex;
    }

    public void increaseCurrentTimelineIndex() {
        this.currentTimelineIndex++;
    }

    public List<ObstacleObject> getCurrentObstacleObjects() {
        return currentObstacleObjects;
    }

    public List<NoteObject> getCurrentNoteObjects() { return this.currentNoteObjects; }

    public PlayerObject getPlayerObject() {
        return playerObject;
    }

    public HashSet<ActionObject> getDestroyQueue() { return this.destroyQueue; }

    public double getCustomTime() {
        return customTime;
    }

    public void setCustomTime(double customTime) {
        this.customTime = customTime;
    }

    public void modifyCustomTime(double amount) {
        this.customTime += amount;
    }

    public void countNoteHit(NoteAccuracy noteAccuracy) {
        switch (noteAccuracy) {
            case Perfect -> perfectNoteHits++;
            case Good -> goodNoteHits++;
            case Ok -> okNoteHits++;
            case Failed -> failedNoteHits++;
        }

        totalHitNotes++;
    }

    public int getPerfectNoteHits() { return perfectNoteHits; }
    public int getGoodNoteHits() { return goodNoteHits; }
    public int getOkNoteHits() { return okNoteHits; }
    public int getFailedNoteHits() { return failedNoteHits; }

    public int getTotalHitNotes() {
        return totalHitNotes;
    }

    public double calculateCurrentAccuracy() {
        // This is an easier "on the fly" calculation, at the end a more precise calculation is done.
        return accuracy / (double)totalHitNotes;
    }
}
