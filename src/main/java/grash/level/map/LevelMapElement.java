package grash.level.map;

public final class LevelMapElement extends LevelMapThing {
    private final MapElementType mapElementType;

    private boolean isUp;
    private boolean isLeft;

    private double heightNormalized;


    private double timeEnd;

    public LevelMapElement(MapElementType mapElementType) {
        super(MapThingType.Element);
        this.mapElementType = mapElementType;
    }

    public void setIsUp(boolean up) {
        isUp = up;
    }

    public void setIsLeft(boolean left) {
        isLeft = left;
    }

    public void setHeightNormalized(double heightNormalized) {
        this.heightNormalized = heightNormalized;
    }

    public void setTimeEnd(double timeEnd) {
        this.timeEnd = timeEnd;
    }

    public boolean getIsUp() {
        return isUp;
    }

    public boolean getIsLeft() {
        return isLeft;
    }

    public double getHeightNormalized() {
        return heightNormalized;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public MapElementType getMapElementType() {
        return mapElementType;
    }
}
