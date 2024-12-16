package grash.level.map;

public final class LevelMapNote extends LevelMapThing {
    private final MapNoteType mapNoteType;

    private boolean isLeft;
    private byte yType;
    private double timeEnd;

    public LevelMapNote(MapNoteType mapNoteType) {
        super(MapThingType.Note);
        this.mapNoteType = mapNoteType;
    }

    public boolean getIsLeft() {
        return isLeft;
    }

    public void setIsLeft(boolean left) {
        isLeft = left;
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
