package grash.action.objects;

import grash.core.GameController;
import grash.math.Vec2;

public abstract class ActionObject {
    private Vec2 position;
    protected GameController game;

    public ActionObject(GameController gameController, Vec2 startPos) {
        this.game = gameController;
        this.position = startPos;
    }

    public Vec2 getPosition() {
        return position;
    }

    public void setPosition(Vec2 position) {
        this.position = position;
    }
}
