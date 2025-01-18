package grash.action;

import grash.action.objects.ObstacleObject;
import grash.action.objects.PlayerObject;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.input.GrashEvent_KeyDown;
import javafx.scene.input.KeyCode;

import java.util.List;

public final class ActionPhaseLogicHandler implements GrashEventListener {

    private final ActionPhaseController controller;
    private final GameController game;

    public ActionPhaseLogicHandler(ActionPhaseController controller, GameController gameController) {
        this.controller = controller;
        this.game = gameController;

        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
    }

    public void moveAllObstacleObjects(List<ObstacleObject> allObstacleObjects, double speed, double deltaTime) {
        for(ObstacleObject obstacleObject : allObstacleObjects) {
            obstacleObject.getPosition().x -= speed * deltaTime;
        }
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "KeyDown": {
                onEvent_KeyDown((GrashEvent_KeyDown) event);
                break;
            }
        }
    }

    public void playerLogicHandler(PlayerObject player) {
        player.playerTick();
    }

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        System.out.println(controller.getActionPhaseState());
        if(controller.getActionPhaseState() != ActionPhaseState.Active) return;

        if(event.getKeyCode() == KeyCode.W) {
            controller.getActionPhaseValues().getPlayerObject().doSwitchSideOnNextTick();
        }
    }

}
