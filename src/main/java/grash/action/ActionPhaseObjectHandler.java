package grash.action;

import grash.action.objects.ObstacleObject;
import grash.core.GameController;
import grash.level.LevelMapTimeline;
import grash.level.LevelMapTimelineStack;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapThing;
import grash.math.Vec2;

public final class ActionPhaseObjectHandler {

    private final ActionPhaseController controller;
    private final GameController game;

    public ActionPhaseObjectHandler(ActionPhaseController controller, GameController gameController) {
        this.controller = controller;
        this.game = gameController;
    }

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
                    break;
                }
                case Effect: {
                    break;
                }
            }
        }
    }

    private double calculateObjectXStartPos(double objectTime, double secondsElapsedSinceStart) {
        return (objectTime - secondsElapsedSinceStart)
                * controller.getActionPhaseValues().getActionPhaseMap().getSpeed();
    }

    private void addObstacleObjectToAction(LevelMapElement levelMapElement, double secondsElapsedSinceStart) {
        ActionPhaseValues actionPhaseValues = controller.getActionPhaseValues();

        switch (levelMapElement.getMapElementType()) {
            case Spike: {
                Vec2 spawnPos = Vec2.ZERO();
                spawnPos.x = calculateObjectXStartPos(levelMapElement.getTimeStart(), secondsElapsedSinceStart);
                spawnPos.y = (levelMapElement.getIsUp()) ? ActionPhaseController.Y_UP : ActionPhaseController.Y_DOWN;

                actionPhaseValues.getCurrentObstacleObjects().add(new ObstacleObject(game, spawnPos, levelMapElement));
                break;
            }
        }
    }

}
