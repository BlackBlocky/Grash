package grash.action.objects;

import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapThing;
import grash.math.Vec2;

public final class ObstacleObject extends ActionObject {

    private Vec2[] additionalPositions;
    private Sprite[] additionalSprites;

    public ObstacleObject(GameController gameController, Vec2 startPos, Vec2 scale, Vec2 drawOffset,
                          LevelMapThing levelMapThing, Hitbox hitbox) {
        super(gameController, startPos, scale, drawOffset, levelMapThing, hitbox);
    }

    public LevelMapElement getLevelMapElement() {
        return (LevelMapElement) this.levelMapThing;
    }
    public Vec2[] getAdditionalPositions() { return additionalPositions; }
    public Sprite[] getAdditionalSprites() { return additionalSprites; }

    public void setAdditionalPositions(Vec2[] additionalPositions) {
        this.additionalPositions = additionalPositions;
    }

    public void setAdditionalSprites(Sprite[] additionalSprites) {
        this.additionalSprites = additionalSprites;
    }

    @Override
    protected Sprite setupSprite() {
        LevelMapElement thisLevelMapElement = (LevelMapElement) levelMapThing;
        switch (thisLevelMapElement.getMapElementType()) {
            case Spike:
                return (thisLevelMapElement.getIsUp()) ? game.getResourceLoader().getSprite("SpikeUp") :
                        game.getResourceLoader().getSprite("SpikeDown");
            case Rope:
                additionalSprites = new Sprite[]{game.getResourceLoader().getSprite("RopeEnd")};
                return game.getResourceLoader().getSprite("RopeStart");
            case Wall:
                return (thisLevelMapElement.getIsUp()) ? game.getResourceLoader().getSprite("WallUp") :
                        game.getResourceLoader().getSprite("WallDown");
            case Slide:
                additionalSprites = new Sprite[]{game.getResourceLoader().getSprite("SlideRight")};
                return game.getResourceLoader().getSprite("SlideLeft");
            case DoubleJump:
                return game.getResourceLoader().getSprite("DoubleJump");
        }
        return null;
    }

    @Override
    protected void setupObject() {
        // No use for now :P

        LevelMapElement thisLevelMapElement = (LevelMapElement) levelMapThing;
        switch (thisLevelMapElement.getMapElementType()) {

        }
    }
}
