package grash.level;

import grash.level.map.LevelMapThing;

public class LevelMapTimelineStack {
    private final LevelMapThing[] myLevelMapThings;
    private final double time;

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
}
