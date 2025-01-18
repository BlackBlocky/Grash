package grash.action.objects;

import grash.assets.Sprite;
import grash.core.GameController;
import grash.math.Vec2;

public final class PlayerObject {

    private final Vec2 position;
    private final Sprite sprite;

    private final GameController game;

    public PlayerObject(GameController gameController, Vec2 startPos) {
        this.position = startPos;
        this.game = gameController;
        this.sprite = game.getResourceLoader().getSprite("MagnetSnake");
    }

    public Vec2 getPosition() {
        return this.position;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

}
