package grash.action;

import grash.level.map.LevelMap;

public final class ActionPhaseValues {
    private LevelMap actionPhaseMap;

    private double timeProgress;
    private double score;
    private double countdownLeft;

    /**
     * This is the measured time AFTER the Countdown is done!
     */
    private long nanoTimeAtStart;

    public ActionPhaseValues(LevelMap actionPhaseMap, double countdownLeft) {
        this.actionPhaseMap = actionPhaseMap;
        this.timeProgress = 0.0;
        this.score = 0.0;
        this.countdownLeft = countdownLeft;
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
}
