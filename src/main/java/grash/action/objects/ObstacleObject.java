package grash.action.objects;

import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapThing;
import grash.math.Vec2;

public final class ObstacleObject extends ActionObject {

    public ObstacleObject(GameController gameController, Vec2 startPos, Vec2 scale, Vec2 drawOffset,
                          LevelMapThing levelMapThing, Hitbox hitbox) {
        super(gameController, startPos, scale, drawOffset, levelMapThing, hitbox);
    }

    public LevelMapElement getLevelMapElement() {
        return (LevelMapElement) this.levelMapThing;
    }

    @Override
    protected Sprite setupSprite() {
        LevelMapElement thisLevelMapElement = (LevelMapElement) levelMapThing;
        switch (thisLevelMapElement.getMapElementType()) {
            case Spike:
                return (thisLevelMapElement.getIsUp()) ? game.getResourceLoader().getSprite("SpikeUp") :
                        game.getResourceLoader().getSprite("SpikeDown");
            case Rope:
                return null;
            case Wall:
                return (thisLevelMapElement.getIsUp()) ? game.getResourceLoader().getSprite("WallUp") :
                        game.getResourceLoader().getSprite("WallDown");
            case Slide:
                return null;
            case DoubleJump:
                return null;
        }
        return null;
    }
}
