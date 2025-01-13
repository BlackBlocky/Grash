package grash.level;

import grash.level.map.LevelMapThing;

public class LevelMapTimelineStack {
    private final LevelMapThing[] myLevelMapThings;
    private final double time;
    private double calculatedXStartPos;

    public LevelMapTimelineStack(LevelMapThing[] myLevelMapThings, double time) {
        this.myLevelMapThings = myLevelMapThings;
        this.time = time;
    }

    public LevelMapThing[] getMyLevelMapThings() {
        return myLevelMapThings;
    }

    public double getTime() {
        return time;
    }

    public double getCalculatedXStartPos() {
        return calculatedXStartPos;
    }

    /**
     * This Function will probably be called when the map is already loaded into the ActionPhase
     */
    protected void calculateXStartPos(double speed) {
        this.calculatedXStartPos = time * speed;
    }

}
