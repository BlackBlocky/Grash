package grash.level.map;

public abstract class LevelMapThing {
    private double timeStart;
    private final MapThingType mapThingType;

    public LevelMapThing(MapThingType mapThingType) {
        this.mapThingType = mapThingType;
    }

    public double getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(double timeStart) {
        this.timeStart = timeStart;
    }
}
