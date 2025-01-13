package grash.action.objects;

import grash.core.GameController;
import grash.level.map.MapThingType;
import grash.math.Vec2;

public abstract class ActionObject {
    private Vec2 position;
    protected GameController game;

    private final MapThingType thingType;

    public ActionObject(GameController gameController, Vec2 startPos, MapThingType thingType) {
        this.game = gameController;
        this.position = startPos;
        this.thingType = thingType;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public MapThingType getThingType() {
        return thingType;
    }
}
