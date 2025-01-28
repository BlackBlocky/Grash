package grash.action;

import grash.action.objects.Hitbox;
import grash.action.objects.ObstacleObject;
import grash.action.objects.PlayerObject;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.event.events.input.GrashEvent_KeyUp;
import grash.math.Vec2;
import javafx.scene.input.KeyCode;

import java.util.List;

public final class ActionPhaseLogicHandler implements GrashEventListener {

    private final ActionPhaseController controller;
    private final GameController game;

    private double customTimeScrollMultiplier; // This is between -1.0 and 1.0

    public ActionPhaseLogicHandler(ActionPhaseController controller, GameController gameController) {
        this.controller = controller;
        this.game = gameController;

        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
        game.getEventBus().registerListener(GrashEvent_KeyUp.class, this);
    }

    /**
     * Clears out everything from the last run and makes everything nice and fresh again
     */
    public void resetLogicHandler() {
        customTimeScrollMultiplier = 0.0;
    }

    public void moveAllObstacleObjects(List<ObstacleObject> allObstacleObjects, double speed, double deltaTime) {
        for(ObstacleObject obstacleObject : allObstacleObjects) {
            obstacleObject.getPosition().x -= speed * deltaTime;

            Vec2[] additionalPositions = obstacleObject.getAdditionalPositions();
            if(additionalPositions == null) continue;
            for(Vec2 additionalPos : obstacleObject.getAdditionalPositions()) {
                additionalPos.x -= speed * deltaTime;
            }
        }
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "KeyDown": {
                onEvent_KeyDown((GrashEvent_KeyDown) event);
                break;
            }
            case "KeyUp": {
                onEvent_KeyUp((GrashEvent_KeyUp) event);
                break;
            }
        }
    }

    public void playerLogicHandler(PlayerObject player, double secondsElapsedSinceStart, double mapSpeed,
                                   double realDeltaTime) {
        player.playerTick(secondsElapsedSinceStart, mapSpeed, realDeltaTime);
    }

    public boolean checkIfPlayerIsColliding(PlayerObject player, List<ObstacleObject> allObstacleObjects) {
        for(ObstacleObject obstacleObject : allObstacleObjects) {
            if(obstacleObject.getHitbox() == null) continue;

            if(Hitbox.CHECK_COLLISION(player.getPosition(), player.getHitbox(),
                    obstacleObject.getPosition(), obstacleObject.getHitbox())) {
                System.out.println("Collision");
                return true;
            }
        }
        return false;
    }

    /**
     * This Method is called every Tick, when CustomTime is used
     */
    public void updateCustomTime(double realDeltaTime) {
        double scrollSpeed = 1.5;
        controller.getActionPhaseValues().modifyCustomTime(
                customTimeScrollMultiplier * realDeltaTime * scrollSpeed
        );
    }

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        if(controller.getActionPhaseState() != ActionPhaseState.Active) return;

        // Custom Time controls (only active when custom time is used)
        if(controller.getUseCustomTime()) {
            if(event.getKeyCode() == KeyCode.PERIOD) { customTimeScrollMultiplier = 1.0; }
            else if(event.getKeyCode() == KeyCode.COMMA) { customTimeScrollMultiplier = -1.0; }
        }

        // Custom player height (only active when custom player height is used)
        if(controller.getUseCustomPlayerHeight()) {
            if(event.getKeyCode() == KeyCode.PAGE_UP)
                { controller.getActionPhaseValues().getPlayerObject().setHeightChangeMultiplier(-1.0); }
            else if(event.getKeyCode() == KeyCode.PAGE_DOWN)
                { controller.getActionPhaseValues().getPlayerObject().setHeightChangeMultiplier(1.0); }
        }

        // Player controls
        if(event.getKeyCode() == KeyCode.W) {
            controller.getActionPhaseValues().getPlayerObject().doSwitchSideOnNextTick();
        }
        else if(event.getKeyCode() == KeyCode.SPACE) {
            controller.getActionPhaseValues().getPlayerObject().doJumpOnNextTick();
        }
    }

    private void onEvent_KeyUp(GrashEvent_KeyUp event) {
        if(controller.getActionPhaseState() != ActionPhaseState.Active) return;

        // Custom Time controls (only active when custom time is used)
        if(controller.getUseCustomTime()) {
            if(event.getKeyCode() == KeyCode.PERIOD && customTimeScrollMultiplier == 1.0) {
                customTimeScrollMultiplier = 0.0;
            }
            else if(event.getKeyCode() == KeyCode.COMMA && customTimeScrollMultiplier == -1.0) {
                customTimeScrollMultiplier = 0.0;
            }
        }

        // Custom player height (only active when custom player height is used)
        if(controller.getUseCustomPlayerHeight()) {
            if(event.getKeyCode() == KeyCode.PAGE_UP || event.getKeyCode() == KeyCode.PAGE_DOWN) {
                controller.getActionPhaseValues().getPlayerObject().setHeightChangeMultiplier(0.0);
            }
        }
    }

}
