package grash.action.objects;

import grash.math.Vec2;

public final class Hitbox {

    private final Vec2 size;
    private final Vec2 offset;

    public Hitbox(Vec2 size, Vec2 offset) {
        this.size = size;
        this.offset = offset;
    }

    public Vec2 getOffset() {
        return offset;
    }

    public Vec2 getSize() {
        return size;
    }

    public static boolean CHECK_COLLISION(Vec2 positionA, Hitbox hitboxA, Vec2 positionB, Hitbox hitboxB) {

        return false;
    }
}
