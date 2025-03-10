package grash.action;

import grash.action.objects.Hitbox;
import grash.action.objects.NoteObject;
import grash.action.objects.ObstacleObject;
import grash.core.GameController;
import grash.level.LevelMapTimeline;
import grash.level.LevelMapTimelineStack;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapNote;
import grash.level.map.LevelMapThing;
import grash.level.map.MapNoteType;
import grash.math.Vec2;

public final class ActionPhaseObjectHandler {

    private final ActionPhaseController controller;
    private final GameController game;

    public ActionPhaseObjectHandler(ActionPhaseController controller, GameController gameController) {
        this.controller = controller;
        this.game = gameController;
    }

    /**
     * Spawning LevelMapThings into the Action, when they are on order with their start time.
     */
    public void processLevelMapTimeline(double secondsElapsedSinceStart) {
        ActionPhaseValues actionPhaseValues = controller.getActionPhaseValues();
        LevelMapTimeline timeline = actionPhaseValues.getActionPhaseMap().getLevelMapTimeline();
        LevelMapTimelineStack nextStack = timeline.getNextTimelineStack(actionPhaseValues.getCurrentTimelineIndex());
        if(nextStack == null) return;

         /* If the x-distance is larger than the x-distance until the Stack is on order,
         place all elements from that stack. */
        double nextStackXValue =
                (nextStack.getTime() - secondsElapsedSinceStart) * actionPhaseValues.getActionPhaseMap().getSpeed();
        if(nextStackXValue <= ActionPhaseController.PRE_GENERATED_DISTANCE) {
            actionPhaseValues.increaseCurrentTimelineIndex();
            addLevelMapThingsToAction(nextStack.getMyLevelMapThings(), secondsElapsedSinceStart);
        }
    }

    private void addLevelMapThingsToAction(LevelMapThing[] newLevelMapThings, double secondsElapsedSinceStart) {
        for(LevelMapThing thing : newLevelMapThings) {
            switch (thing.getMapThingType()) {
                case Element: {
                    addObstacleObjectToAction((LevelMapElement) thing, secondsElapsedSinceStart);
                    break;
                }
                case Note: {
                    addNoteObjectToAction((LevelMapNote) thing, secondsElapsedSinceStart);
                    break;
                }
                case Effect: {
                    break;
                }
            }
        }
    }

    private double calculateObjectXStartPos(double objectTime, double secondsElapsedSinceStart) {
        return ((objectTime - secondsElapsedSinceStart)
                * controller.getActionPhaseValues().getActionPhaseMap().getSpeed())
                + controller.getActionPhaseValues().getPlayerObject().getPosition().x;
    }

    /**
     * Important Note: The Note will be added at the end of the NotesList,
     * because the other notes are in the queue to be hit.
     */
    private void addNoteObjectToAction(LevelMapNote levelMapNote, double secondsElapsedSinceStart) {
        ActionPhaseValues actionPhaseValues = controller.getActionPhaseValues();

        switch (levelMapNote.getMapNoteType()) {
            case TapNote: {
                addNoteObjectToAction_TapNote(actionPhaseValues, levelMapNote, secondsElapsedSinceStart);
                break;
            }
        }
    }

    private void addNoteObjectToAction_TapNote(ActionPhaseValues actionPhaseValues,
                                               LevelMapNote levelMapNote, double secondsElapsedSinceStart) {
        NoteTapInput noteTapInput = null;
        Vec2 spawnPos = Vec2.ZERO();

        spawnPos.x = calculateObjectXStartPos(levelMapNote.getTimeStart(), secondsElapsedSinceStart);
        switch (levelMapNote.getYType()) {
            case 0 -> {
                spawnPos.y = ActionPhaseController.Y_NOTE_DOWN;
                noteTapInput = NoteTapInput.Down;
            }
            case 1 -> {
                spawnPos.y = ActionPhaseController.Y_MIDDLE;
                noteTapInput = NoteTapInput.UpAndDown;
            }
            case 2 -> {
                spawnPos.y = ActionPhaseController.Y_NOTE_UP;
                noteTapInput = NoteTapInput.Up;
            }
        }

        actionPhaseValues.getCurrentNoteObjects().add(new NoteObject(game, spawnPos, levelMapNote, noteTapInput));
    }

    private void addObstacleObjectToAction(LevelMapElement levelMapElement, double secondsElapsedSinceStart) {
        ActionPhaseValues actionPhaseValues = controller.getActionPhaseValues();

        switch (levelMapElement.getMapElementType()) {
            case Spike: {
                addObstacleObjectToAction_Spike(actionPhaseValues, levelMapElement, secondsElapsedSinceStart);
                break;
            }
            case Wall: {
                addObstacleObjectToAction_Wall(actionPhaseValues, levelMapElement, secondsElapsedSinceStart);
                break;
            }
            case Rope: {
                addObstacleObjectToAction_Rope(actionPhaseValues, levelMapElement, secondsElapsedSinceStart);
                break;
            }
            case DoubleJump: {
                addObstacleObjectToAction_DoubleJump(actionPhaseValues, levelMapElement, secondsElapsedSinceStart);
                break;
            }
            case Slide: {
                addObstacleObjectToAction_Slide(actionPhaseValues, levelMapElement, secondsElapsedSinceStart);
                break;
            }
        }
    }

    private void addObstacleObjectToAction_Spike(ActionPhaseValues actionPhaseValues,
                                                 LevelMapElement levelMapElement, double secondsElapsedSinceStart) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart);
        spawnPos.y = (levelMapElement.getIsUp()) ? ActionPhaseController.Y_UP : ActionPhaseController.Y_DOWN;

        Hitbox hitbox = new Hitbox(new Vec2(0.4, 0.6),
                new Vec2(0.3, (levelMapElement.getIsUp()) ? 0.0 : 0.4));

        actionPhaseValues.getCurrentObstacleObjects().add(new ObstacleObject(game,
                spawnPos,new Vec2(1, 1), Vec2.ZERO(), levelMapElement, hitbox));
    }

    private void addObstacleObjectToAction_Wall(ActionPhaseValues actionPhaseValues,
                                                 LevelMapElement levelMapElement, double secondsElapsedSinceStart) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart);
        spawnPos.y = (levelMapElement.getIsUp()) ? ActionPhaseController.Y_UP : ActionPhaseController.Y_DOWN;

        Vec2 drawOffset = (levelMapElement.getIsUp()) ? Vec2.ZERO() : new Vec2(0, -2);
        Hitbox hitbox = new Hitbox(new Vec2(1, 2.5),
                (levelMapElement.getIsUp()) ? Vec2.ZERO() : new Vec2(0, -1.5));

        actionPhaseValues.getCurrentObstacleObjects().add(new ObstacleObject(game,
                spawnPos, new Vec2(1, 3), drawOffset, levelMapElement, hitbox));
    }

    private void addObstacleObjectToAction_Rope(ActionPhaseValues actionPhaseValues,
                                                LevelMapElement levelMapElement, double secondsElapsedSinceStart) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart);
        spawnPos.y = ActionPhaseController.Y_MIDDLE;

        ObstacleObject newRopeObject = new ObstacleObject(game,
                spawnPos, Vec2.ONE(), Vec2.ZERO(), levelMapElement, null);

        Vec2 ropeEndPos = Vec2.ZERO();
        ropeEndPos.x = calculateObjectXStartPos(levelMapElement.getTimeEnd(), secondsElapsedSinceStart);
        ropeEndPos.y = ActionPhaseController.Y_MIDDLE;

        Vec2[] ropeEndPosArray = new Vec2[1]; // It's an array because the ObstacleObject works with arrays
        ropeEndPosArray[0] = ropeEndPos;
        newRopeObject.setAdditionalPositions(ropeEndPosArray);

        actionPhaseValues.getCurrentObstacleObjects().add(newRopeObject);
    }

    private void addObstacleObjectToAction_DoubleJump(ActionPhaseValues actionPhaseValues,
                                                      LevelMapElement levelMapElement,
                                                      double secondsElapsedSinceStart) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart);
        spawnPos.y = calculateHeightFromNormalizedValue(levelMapElement.getHeightNormalized());

        Hitbox hitbox = new Hitbox(new Vec2(0.36, 0.36),
                new Vec2(0, 0.32));

        actionPhaseValues.getCurrentObstacleObjects().add(new ObstacleObject(game,
                spawnPos,new Vec2(1, 1), Vec2.ZERO(), levelMapElement, hitbox));
    }

    private void addObstacleObjectToAction_Slide(ActionPhaseValues actionPhaseValues,
                                                 LevelMapElement levelMapElement, double secondsElapsedSinceStart) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart);
        final double slideHeight = 1.0;
        spawnPos.y = (levelMapElement.getIsUp()) ?
                ActionPhaseController.Y_UP + slideHeight - 0.4 : ActionPhaseController.Y_DOWN - slideHeight + 0.4;

        Vec2 endPos = Vec2.ZERO();
        endPos.x = calculateObjectXStartPos(levelMapElement.getTimeEnd(), secondsElapsedSinceStart);
        endPos.y = spawnPos.y;

        double slideDuration = levelMapElement.getTimeEnd() - levelMapElement.getTimeStart();
        Hitbox slideHitbox = new Hitbox(
                new Vec2(
                        slideDuration * actionPhaseValues.getActionPhaseMap().getSpeed(),
                        4.4),
                new Vec2(
                        0,
                        (levelMapElement.getIsUp()) ? 0 : -3.4
                ));

        ObstacleObject newSlideObject = new ObstacleObject(game, spawnPos,
                Vec2.ONE(), Vec2.ZERO(), levelMapElement, slideHitbox);

        Vec2[] ropeEndPosArray = new Vec2[]{endPos}; // It's an array because the ObstacleObject works with arrays
        newSlideObject.setAdditionalPositions(ropeEndPosArray);

        actionPhaseValues.getCurrentObstacleObjects().add(newSlideObject);
    }

    private double calculateHeightFromNormalizedValue(double normalizedHeight) {
        // Linear Interpolation
        return ActionPhaseController.Y_UP +
                (ActionPhaseController.Y_DOWN - ActionPhaseController.Y_UP) * normalizedHeight;
    }

}
