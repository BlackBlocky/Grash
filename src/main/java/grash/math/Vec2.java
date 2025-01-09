package grash.math;

public final class Vec2 {
    public double x;
    public double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vec2 ONE() {
        return new Vec2(1, 1);
    }

    public static Vec2 ZERO() {
        return new Vec2(0, 0);
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(this.x + other.x, this.y + other.y);
    }

    public Vec2 subtract(Vec2 other) {
        return new Vec2(this.x - other.x, this.y - other.y);
    }

    public Vec2 multiply(double v) {
        return new Vec2(this.x * v, this.y * v);
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }
}
