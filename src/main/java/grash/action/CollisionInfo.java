package grash.action;

import grash.action.objects.ObstacleObject;

public class CollisionInfo {
    public final static CollisionInfo noneCollision = new CollisionInfo(CollisionType.None, null);

    private final CollisionType collisionType;
    private final ObstacleObject obstacleObject;

    public CollisionInfo(CollisionType collisionType, ObstacleObject obstacleObject) {
        this.collisionType = collisionType;
        this.obstacleObject = obstacleObject;
    }

    public CollisionType getCollisionType() {
        return collisionType;
    }

    public ObstacleObject getObstacleObject() {
        return obstacleObject;
    }
}
