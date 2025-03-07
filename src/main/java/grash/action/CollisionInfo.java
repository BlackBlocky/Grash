package grash.action;

import grash.action.objects.ActionObject;

public class CollisionInfo {
    public final static CollisionInfo noneCollision = new CollisionInfo(CollisionType.None, null);

    private final CollisionType collisionType;
    private final ActionObject actionObject;

    public CollisionInfo(CollisionType collisionType, ActionObject obstacleObject) {
        this.collisionType = collisionType;
        this.actionObject = obstacleObject;
    }

    public CollisionType getCollisionType() {
        return collisionType;
    }

    public ActionObject getActionObject() {
        return actionObject;
    }
}
