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
        ActionPhaseValues actionPhaseValues = controller.getActionPhaseValues();

        for(LevelMapThing thing : newLevelMapThings) {
            switch (thing.getMapThingType()) {
                case Element: {
                    ObstacleObject newObstacle = createObstacleObject(
                            actionPhaseValues.getActionPhaseMap().getSpeed(),
                            actionPhaseValues.getPlayerObject().getPosition().x,
                            (LevelMapElement) thing, secondsElapsedSinceStart, game);
                    if(newObstacle != null) actionPhaseValues.getCurrentObstacleObjects().add(newObstacle);
                    break;
                }
                case Note: {
                    NoteObject newNote = createNoteObject(
                            actionPhaseValues.getActionPhaseMap().getSpeed(),
                            actionPhaseValues.getPlayerObject().getPosition().x,
                            (LevelMapNote) thing, secondsElapsedSinceStart, game);
                    if(newNote != null) actionPhaseValues.getCurrentNoteObjects().add(newNote);
                    break;
                }
                case Effect: {
                    break;
                }
            }
        }
    }

    private double calculateObjectXStartPos(double objectTime, double secondsElapsedSinceStart) {
        return calculateObjectXStartPos(objectTime, secondsElapsedSinceStart,
                controller.getActionPhaseValues().getActionPhaseMap().getSpeed(),
                controller.getActionPhaseValues().getPlayerObject().getPosition().x);
    }

    public static double calculateObjectXStartPos(double objectTime, double secondsElapsedSinceStart,
                                           double mapSpeed, double playerX) {
        return ((objectTime - secondsElapsedSinceStart) * mapSpeed) + playerX;
    }

    /**
     * Important Note: The Note must be added at the end of the NotesList,
     * because the other notes are in the queue to be hit.
     */
    public static NoteObject createNoteObject(double mapSpeed, double playerX, LevelMapNote levelMapNote,
                                               double secondsElapsedSinceStart, GameController game) {

        switch (levelMapNote.getMapNoteType()) {
            case TapNote: {
               return createNoteObject_TapNote(levelMapNote, secondsElapsedSinceStart, mapSpeed, playerX, game);
            }
        }

        return null;
    }

    private static NoteObject createNoteObject_TapNote(LevelMapNote levelMapNote, double secondsElapsedSinceStart,
                                                double mapSpeed, double playerX, GameController game) {
        NoteTapInput noteTapInput = null;
        Vec2 spawnPos = Vec2.ZERO();

        spawnPos.x = calculateObjectXStartPos(levelMapNote.getTimeStart(), secondsElapsedSinceStart, mapSpeed, playerX);
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

        return new NoteObject(game, spawnPos, levelMapNote, noteTapInput);
    }

    public static ObstacleObject createObstacleObject(double mapSpeed, double playerX,
                                                LevelMapElement levelMapElement, double secondsElapsedSinceStart,
                                                      GameController game) {
        ObstacleObject result = null;

        switch (levelMapElement.getMapElementType()) {
            case Spike: {
                result = createObstacleObject_Spike(mapSpeed, playerX, levelMapElement, secondsElapsedSinceStart,
                        game);
                break;
            }
            case Wall: {
                result = createObstacleObject_Wall(mapSpeed, playerX, levelMapElement, secondsElapsedSinceStart,
                        game);
                break;
            }
            case Rope: {
                result = createObstacleObject_Rope(mapSpeed, playerX, levelMapElement, secondsElapsedSinceStart,
                        game);
                break;
            }
            case DoubleJump: {
                result = createObstacleObject_DoubleJump(mapSpeed, playerX, levelMapElement, secondsElapsedSinceStart,
                        game);
                break;
            }
            case Slide: {
                result = createObstacleObject_Slide(mapSpeed, playerX, levelMapElement, secondsElapsedSinceStart,
                        game);
                break;
            }
        }

        return result;
    }

    private static ObstacleObject createObstacleObject_Spike(double mapSpeed, double playerX,
                                                      LevelMapElement levelMapElement, double secondsElapsedSinceStart,
                                                             GameController game) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart, mapSpeed, playerX);
        spawnPos.y = (levelMapElement.getIsUp()) ? ActionPhaseController.Y_UP : ActionPhaseController.Y_DOWN;

        Hitbox hitbox = new Hitbox(new Vec2(0.4, 0.6),
                new Vec2(0.3, (levelMapElement.getIsUp()) ? 0.0 : 0.4));

        return new ObstacleObject(game, spawnPos,new Vec2(1, 1), Vec2.ZERO(), levelMapElement, hitbox);
    }

    private static ObstacleObject createObstacleObject_Wall(double mapSpeed, double playerX,
                                                     LevelMapElement levelMapElement, double secondsElapsedSinceStart,
                                                            GameController game) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart, mapSpeed, playerX);
        spawnPos.y = (levelMapElement.getIsUp()) ? ActionPhaseController.Y_UP : ActionPhaseController.Y_DOWN;

        Vec2 drawOffset = (levelMapElement.getIsUp()) ? Vec2.ZERO() : new Vec2(0, -2);
        Hitbox hitbox = new Hitbox(new Vec2(1, 2.5),
                (levelMapElement.getIsUp()) ? Vec2.ZERO() : new Vec2(0, -1.5));

        return new ObstacleObject(game, spawnPos, new Vec2(1, 3), drawOffset, levelMapElement, hitbox);
    }

    private static ObstacleObject createObstacleObject_Rope(double mapSpeed, double playerX,
                                                     LevelMapElement levelMapElement, double secondsElapsedSinceStart,
                                                            GameController game) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart, mapSpeed, playerX);
        spawnPos.y = ActionPhaseController.Y_MIDDLE;

        ObstacleObject newRopeObject = new ObstacleObject(game,
                spawnPos, Vec2.ONE(), Vec2.ZERO(), levelMapElement, null);

        Vec2 ropeEndPos = Vec2.ZERO();
        ropeEndPos.x = calculateObjectXStartPos(levelMapElement.getTimeEnd(), secondsElapsedSinceStart, mapSpeed, playerX);
        ropeEndPos.y = ActionPhaseController.Y_MIDDLE;

        Vec2[] ropeEndPosArray = new Vec2[1]; // It's an array because the ObstacleObject works with arrays
        ropeEndPosArray[0] = ropeEndPos;
        newRopeObject.setAdditionalPositions(ropeEndPosArray);

        return newRopeObject;
    }

    private static ObstacleObject createObstacleObject_DoubleJump(double mapSpeed, double playerX,
                                                           LevelMapElement levelMapElement,
                                                           double secondsElapsedSinceStart,
                                                                  GameController game) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart, mapSpeed, playerX);
        spawnPos.y = calculateHeightFromNormalizedValue(levelMapElement.getHeightNormalized());

        Hitbox hitbox = new Hitbox(new Vec2(0.5, 0.5),
                new Vec2(-0.0625, 0.25));

        return new ObstacleObject(game, spawnPos,new Vec2(1, 1), Vec2.ZERO(), levelMapElement, hitbox);
    }

    private static ObstacleObject createObstacleObject_Slide(double mapSped, double playerX,
                                                      LevelMapElement levelMapElement, double secondsElapsedSinceStart,
                                                             GameController game) {
        Vec2 spawnPos = Vec2.ZERO();
        spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart, mapSped, playerX);
        final double slideHeight = 1.0;
        spawnPos.y = (levelMapElement.getIsUp()) ?
                ActionPhaseController.Y_UP + slideHeight - 0.4 : ActionPhaseController.Y_DOWN - slideHeight + 0.4;

        Vec2 endPos = Vec2.ZERO();
        endPos.x = calculateObjectXStartPos(levelMapElement.getTimeEnd(), secondsElapsedSinceStart, mapSped, playerX);
        endPos.y = spawnPos.y;

        double slideDuration = levelMapElement.getTimeEnd() - levelMapElement.getTimeStart();
        Hitbox slideHitbox = new Hitbox(
                new Vec2(
                        slideDuration * mapSped,
                        4.4),
                new Vec2(
                        0,
                        (levelMapElement.getIsUp()) ? 0 : -3.4
                ));

        ObstacleObject newSlideObject = new ObstacleObject(game, spawnPos,
                Vec2.ONE(), Vec2.ZERO(), levelMapElement, slideHitbox);

        Vec2[] ropeEndPosArray = new Vec2[]{endPos}; // It's an array because the ObstacleObject works with arrays
        newSlideObject.setAdditionalPositions(ropeEndPosArray);

        return newSlideObject;
    }

    private static double calculateHeightFromNormalizedValue(double normalizedHeight) {
        // Linear Interpolation
        return ActionPhaseController.Y_UP +
                (ActionPhaseController.Y_DOWN - ActionPhaseController.Y_UP) * normalizedHeight;
    }

}
