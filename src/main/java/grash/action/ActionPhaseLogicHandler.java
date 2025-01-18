package grash.action;

import grash.action.objects.ObstacleObject;
import grash.action.objects.PlayerObject;
import grash.core.GameController;

import java.util.List;

public final class ActionPhaseLogicHandler {

    private final ActionPhaseController controller;
    private final GameController game;

    public ActionPhaseLogicHandler(ActionPhaseController controller, GameController gameController) {
        this.controller = controller;
        this.game = gameController;
    }

    public void moveAllObstacleObjects(List<ObstacleObject> allObstacleObjects, double speed, double deltaTime) {
        for(ObstacleObject obstacleObject : allObstacleObjects) {
            obstacleObject.getPosition().x -= speed * deltaTime;
        }
    }

    public void playerLogicHandler(PlayerObject player) {

    }

}
