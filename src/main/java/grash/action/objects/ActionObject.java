package grash.action.objects;

import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.LevelMapThing;
import grash.math.Vec2;

public abstract class ActionObject {
    private Vec2 position;
    private Vec2 scale;
    private Vec2 drawOffset;
    private Hitbox hitbox;
    protected GameController game;

    protected final LevelMapThing levelMapThing;

    private final Sprite sprite;

    public ActionObject(GameController gameController, Vec2 startPos, Vec2 scale, Vec2 drawOffset,
                        LevelMapThing levelMapThing, Hitbox hitbox) {
        this.game = gameController;
        this.position = startPos;
        this.scale = scale;
        this.drawOffset = drawOffset;
        this.levelMapThing = levelMapThing;
        this.hitbox = hitbox;

        this.sprite = setupSprite();
    }

    protected abstract Sprite setupSprite();

    public Vec2 getPosition() {
        return position;
    }

    public Vec2 getScale() { return this.scale; }

    public Vec2 getDrawOffset() { return this.drawOffset; }

    public void setPosition(Vec2 position) {
        this.position = position;
    }

    public LevelMapThing getLevelMapThing() {
        return levelMapThing;
    }

    public Hitbox getHitbox() { return this.hitbox; }

    public Sprite getSprite() {
        return sprite;
    }
}
