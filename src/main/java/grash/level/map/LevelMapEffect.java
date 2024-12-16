package grash.level.map;

import javafx.scene.paint.Color;

public final class LevelMapEffect extends LevelMapThing {
    private double valueDouble;
    private int valueInteger;

    private Color color;

    public double getValueDouble() {
        return valueDouble;
    }

    public void setValueDouble(double valueDouble) {
        this.valueDouble = valueDouble;
    }

    public int getValueInteger() {
        return valueInteger;
    }

    public void setValueInteger(int valueInteger) {
        this.valueInteger = valueInteger;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
