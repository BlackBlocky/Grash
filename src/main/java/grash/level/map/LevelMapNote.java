package grash.level.map;

public final class LevelMapNote extends LevelMapThing {
    private final MapNoteType mapNoteType;

    private boolean isLeft;
    private boolean isVertical;
    private byte yType; // y-type = 0=down;1=middle;2=up, time
    private double timeEnd;

    public LevelMapNote(MapNoteType mapNoteType) {
        super(MapThingType.Note);
        this.mapNoteType = mapNoteType;
    }

    public MapNoteType getMapNoteType() {
        return this.mapNoteType;
    }

    public boolean getIsLeft() {
        return isLeft;
    }

    public void setIsLeft(boolean left) {
        isLeft = left;
    }

    public void setIsVertical(boolean vertical) {
        this.isVertical = vertical;
    }

    public boolean getIsVertical() {
        return this.isVertical;
    }

    public byte getYType() {
        return yType;
    }

    public void setYType(byte yType) {
        this.yType = yType;
    }

    public double getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(double timeEnd) {
        this.timeEnd = timeEnd;
    }
}
