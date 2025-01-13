package grash.action.objects;

import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapThing;
import grash.math.Vec2;

public final class ObstacleObject extends ActionObject {

    public ObstacleObject(GameController gameController, Vec2 startPos, LevelMapThing levelMapThing) {
        super(gameController, startPos, levelMapThing);
    }

    @Override
    public Sprite setupSprite() {
        LevelMapElement thisLevelMapElement = (LevelMapElement) levelMapThing;
        switch (thisLevelMapElement.getMapElementType()) {
            case Spike:
                return (thisLevelMapElement.getIsUp()) ? game.getResourceLoader().getSprite("SpikeUp") :
                        game.getResourceLoader().getSprite("SpikeDown");
            case Rope:
                return null;
            case Wall:
                return null;
            case Slide:
                return null;
            case DoubleJump:
                return null;
        }
        return null;
    }
}
