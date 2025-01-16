package grash.action;

import grash.action.objects.ObstacleObject;
import grash.level.map.LevelMap;

import java.util.ArrayList;
import java.util.List;

public final class ActionPhaseValues {
    private LevelMap actionPhaseMap;

    private double timeProgress;
    private double score;
    private double countdownLeft;
    private int currentTimelineIndex;

    private final List<ObstacleObject> currentObstacleObjects;

    /**
     * This is the measured time AFTER the Countdown is done!
     */
    private long nanoTimeAtStart;

    public ActionPhaseValues(LevelMap actionPhaseMap, double countdownLeft) {
        this.actionPhaseMap = actionPhaseMap;
        this.timeProgress = 0.0;
        this.score = 0.0;
        this.countdownLeft = countdownLeft;
        this.currentTimelineIndex = -1;

        this.currentObstacleObjects = new ArrayList<>();
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
}
