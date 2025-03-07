package grash.action;

import grash.action.objects.*;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.action.GrashEvent_NoteHit;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.event.events.input.GrashEvent_KeyUp;
import grash.level.map.MapElementType;
import grash.level.map.MapNoteType;
import grash.math.Vec2;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void moveAllNoteObjects(List<NoteObject> allNoteObjects, double speed, double deltaTime) {
        for(NoteObject noteObject : allNoteObjects) {
            if(noteObject.getLevelMapNote().getMapNoteType() == MapNoteType.GrowNote) continue;

            noteObject.getPosition().x -= speed * deltaTime;
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
                                   double realDeltaTime, double deltaTime) {
        player.playerTick(secondsElapsedSinceStart, mapSpeed, realDeltaTime, deltaTime);
    }

    public CollisionInfo checkIfPlayerIsColliding(PlayerObject player, List<ObstacleObject> allObstacleObjects) {
        for(ObstacleObject obstacleObject : allObstacleObjects) {
            if(obstacleObject.getHitbox() == null) continue;

            if(Hitbox.CHECK_COLLISION(player.getPosition(), player.getHitbox(),
                    obstacleObject.getPosition(), obstacleObject.getHitbox())) {

                if(obstacleObject.getLevelMapElement().getMapElementType() == MapElementType.DoubleJump)
                    return new CollisionInfo(CollisionType.DoubleJump, obstacleObject);
                else
                    return new CollisionInfo(CollisionType.Deadly, obstacleObject);
            }
        }
        return CollisionInfo.noneCollision;
    }

    public CollisionInfo checkIfPlayerIsCollidingWithNote(PlayerObject player, List<NoteObject> allNoteObjects) {
        if(allNoteObjects.isEmpty()) return CollisionInfo.noneCollision;

        /* If the note is not the next note (at index 0), it won't be hittable
        *  So that means that we only need to check the Note at index 0.
        *  (We check somewhere else if the Note was missed)*/

        NoteObject checkedNote = allNoteObjects.get(0);
        double distanceToPlayerSeconds = (checkedNote.getPosition().x - player.getPosition().x) /
                controller.getActionPhaseValues().getActionPhaseMap().getSpeed();
        if(distanceToPlayerSeconds >= ActionPhaseController.IGNORE_NOTE_SECONDS_OFF) return CollisionInfo.noneCollision;

        distanceToPlayerSeconds = Math.abs(distanceToPlayerSeconds);
        if(distanceToPlayerSeconds <= ActionPhaseController.PERFECT_NOTE_SECONDS_OFF)
            return new CollisionInfo(CollisionType.PerfectNote, checkedNote);
        else if(distanceToPlayerSeconds <= ActionPhaseController.GOOD_NOTE_SECONDS_OFF)
            return new CollisionInfo(CollisionType.GoodNote, checkedNote);
        else if(distanceToPlayerSeconds <= ActionPhaseController.OK_NOTE_SECONDS_OFF)
            return new CollisionInfo(CollisionType.OkNote, checkedNote);
        else
            // The Note is in between ok and Ignore
            // It can also happen that the note is just at failed to the left,
            // but its still failed so... yeah.
            return new CollisionInfo(CollisionType.FailedNote, checkedNote);
    }

    /**
     * Checks if a Rope currently at the Y-Level with the Player. So basically, if it is colliding at Y.
     * @return Null if there is no Rope, otherwise the Rope object.
     */
    public ObstacleObject checkIfRopeIsAtPlayer(List<ObstacleObject> obstacleObjectsPool, PlayerObject player) {
        // Get all the ropes
        List<ObstacleObject> allCurrentRopes = new ArrayList<>();
        for(ObstacleObject item : obstacleObjectsPool) {
            if(item.getLevelMapElement().getMapElementType() == MapElementType.Rope) allCurrentRopes.add(item);
        }

        // Check if a Rope is "colliding"
        ObstacleObject collidingRope = null;
        for(ObstacleObject rope : allCurrentRopes) {
            if(player.getPosition().x < rope.getPosition().x) continue; // StartPos
            if(player.getPosition().x > rope.getAdditionalPositions()[0].x) continue; // End Pos

            collidingRope = rope;
            break;
        }

        return collidingRope;
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

    private void hitNextNote() {
        CollisionInfo noteCollisionInfo = checkIfPlayerIsCollidingWithNote(controller.getActionPhaseValues().getPlayerObject(),
                controller.getActionPhaseValues().getCurrentNoteObjects());

        if(noteCollisionInfo == CollisionInfo.noneCollision) return;

        final HashMap<CollisionType, NoteAccuracy> collisionTypeToNoteAccuracy = new HashMap<>(Map.ofEntries(
                Map.entry(CollisionType.PerfectNote, NoteAccuracy.Perfect),
                Map.entry(CollisionType.GoodNote, NoteAccuracy.Good),
                Map.entry(CollisionType.OkNote, NoteAccuracy.Ok),
                Map.entry(CollisionType.FailedNote, NoteAccuracy.Failed)
        ));

        NoteAccuracy noteAccuracy = collisionTypeToNoteAccuracy.get(noteCollisionInfo.getCollisionType());
        game.getEventBus().triggerEvent(
                new GrashEvent_NoteHit(noteAccuracy,(NoteObject) noteCollisionInfo.getActionObject()));
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
        else if(event.getKeyCode() == KeyCode.SHIFT || event.getKeyCode() == KeyCode.CONTROL) {
            controller.getActionPhaseValues().getPlayerObject().doSwitchSneakStateNextTick();
        }

        // Note hits
        if(event.getKeyCode() == KeyCode.UP) {
            hitNextNote();
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

        // Player controls
        if(event.getKeyCode() == KeyCode.SHIFT || event.getKeyCode() == KeyCode.CONTROL) {
            // Making sure that you don't sneak when you're un-sneak
            if(controller.getActionPhaseValues().getPlayerObject().getPlayerState() == PlayerState.Sneaking)
                controller.getActionPhaseValues().getPlayerObject().doSwitchSneakStateNextTick();
        }
    }

}
