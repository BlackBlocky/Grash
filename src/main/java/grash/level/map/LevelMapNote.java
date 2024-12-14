package grash.level.map;

public class LevelMapNote extends LevelMapThing {
    private boolean isLeft;
    private byte yType;
    private double timeEnd;

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
