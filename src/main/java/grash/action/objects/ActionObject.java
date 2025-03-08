package grash.action.objects;

import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.LevelMapThing;
import grash.math.Vec2;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ActionObject {
    private Vec2 position;
    private Vec2 scale;
    private Vec2 drawOffset;
    private Hitbox hitbox;
    protected GameController game;

    protected final LevelMapThing levelMapThing;

    private Sprite sprite;

    public ActionObject(GameController gameController, Vec2 startPos, Vec2 scale, Vec2 drawOffset,
                        LevelMapThing levelMapThing, Hitbox hitbox) {
        this.game = gameController;
        this.position = startPos;
        this.scale = scale;
        this.drawOffset = drawOffset;
        this.levelMapThing = levelMapThing;
        this.hitbox = hitbox;

        this.sprite = setupSprite();
        setupObject();
    }

    protected abstract Sprite setupSprite();

    public void setSprite(Sprite newSprite) {
        this.sprite = newSprite;
    }

    /**
     * This Method is supposed to do Object Specific stuff, like calculating the end Pos for a Rope Object
     */
    protected abstract void setupObject();

    public void destroyObject(long delayMillis) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            game.getActionPhaseController().destroyObject(this);
        }, delayMillis, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
    }

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
