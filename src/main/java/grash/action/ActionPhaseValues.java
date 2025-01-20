package grash.action;

import grash.action.objects.Hitbox;
import grash.action.objects.ObstacleObject;
import grash.action.objects.PlayerObject;
import grash.core.GameController;
import grash.level.map.LevelMap;
import grash.math.Vec2;

import java.util.ArrayList;
import java.util.List;

public final class ActionPhaseValues {
    private LevelMap actionPhaseMap;

    private double timeProgress;
    private double score;
    private double countdownLeft;
    private int currentTimelineIndex;

    private final List<ObstacleObject> currentObstacleObjects;
    private final PlayerObject playerObject;

    /**
     * This is the measured time AFTER the Countdown is done!
     */
    private long nanoTimeAtStart;

    public ActionPhaseValues(LevelMap actionPhaseMap, double countdownLeft, GameController gameController) {
        this.actionPhaseMap = actionPhaseMap;
        this.timeProgress = 0.0;
        this.score = 0.0;
        this.countdownLeft = countdownLeft;
        this.currentTimelineIndex = -1;

        this.currentObstacleObjects = new ArrayList<>();

        Hitbox playerHitbox = new Hitbox(Vec2.ONE(), Vec2.ZERO());
        this.playerObject = new PlayerObject(gameController,
                new Vec2(ActionPhaseController.PLAYER_X, ActionPhaseController.Y_DOWN), playerHitbox);
    }

    public void addScore(double value) {
        this.score += value;
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

    public PlayerObject getPlayerObject() {
        return playerObject;
    }
}
