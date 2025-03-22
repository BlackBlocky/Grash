package grash.editor;

import grash.action.ActionPhaseController;
import grash.action.ActionPhaseObjectHandler;
import grash.action.objects.NoteObject;
import grash.action.objects.ObstacleObject;
import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.level.map.LevelMapEffect;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapNote;
import grash.level.map.MapEffectType;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EditorRenderingController {

    private static final double renderingRangeSeconds = 2.0;

    private final GameController game;
    private final ActionPhaseRenderer editorRenderer;

    private Canvas editorCanvas;

    public EditorRenderingController(GameController gameController) {
        this.game = gameController;
        this.editorRenderer = new ActionPhaseRenderer(game);
    }

    /**
     * This is called every time the Editor is booted up
     */
    public void setup(EditorMapData editorMapData) {
        editorCanvas = (Canvas) game.getPrimaryStage().getScene().lookup("#editorCanvas");

        // Create the init Effects for the Renderer
        LevelMapEffect startColor = new LevelMapEffect(MapEffectType.Color);
        startColor.setTimeStart(0.0);
        startColor.setColor(editorMapData.startColor);

        LevelMapEffect startRotation = new LevelMapEffect(MapEffectType.Rotate);
        startRotation.setTimeStart(0.0);
        startRotation.setValueDouble(editorMapData.startRotation);

        LevelMapEffect startFOVScale = new LevelMapEffect(MapEffectType.FOVScale);
        startFOVScale.setTimeStart(0.0);
        startFOVScale.setValueDouble(editorMapData.startFOVScale);

        editorRenderer.setupRenderer(startColor, startRotation, startFOVScale, editorCanvas);
    }

    public void newFrame(EditorMapData editorMapData, double time) {
        setCurrentEffects(editorMapData, time);
        editorRenderer.updateCanvas(1/1000.0, time,
                getRelevantObstacles(editorMapData, time),
                getRelevantNotes(editorMapData, time),
                null);
    }

    private void setCurrentEffects(EditorMapData editorMapData, double time) {
        int nextColorEffectIndex = getIndexOfNextEffectAtTime(time, editorMapData.colors);
        if(nextColorEffectIndex > 0) { // Cancels out -1 and 0
            editorRenderer.updateColors(
                    editorMapData.colors.get(nextColorEffectIndex - 1),
                    editorMapData.colors.get(nextColorEffectIndex));
        }

        int nextRotationEffectIndex = getIndexOfNextEffectAtTime(time, editorMapData.rotates);
        if(nextRotationEffectIndex > 0) {
            editorRenderer.updateRotations(
                    editorMapData.rotates.get(nextRotationEffectIndex -1),
                    editorMapData.rotates.get(nextRotationEffectIndex));
        }

        int nextFovScaleEffectIndex = getIndexOfNextEffectAtTime(time, editorMapData.fovScales);
        if(nextFovScaleEffectIndex > 0) {
            editorRenderer.updateFovScales(
                    editorMapData.fovScales.get(nextFovScaleEffectIndex -1),
                    editorMapData.fovScales.get(nextFovScaleEffectIndex)
            );
        }
    }

    private int getIndexOfNextEffectAtTime(double time, ArrayList<LevelMapEffect> searchingArray) {
        int indexOfNext = -1;

        for(int i = 0; i < searchingArray.size(); i++) {
            if(searchingArray.get(i).getTimeStart() > time) {
                indexOfNext = i;
                break;
            }
        }

        return indexOfNext;
    }

    private List<ObstacleObject> getRelevantObstacles(EditorMapData editorMapData, double time) {
        ArrayList<ObstacleObject> relevantObstacles = new ArrayList<>();

        Stream.of(editorMapData.spikes, editorMapData.slides, editorMapData.walls,
                        editorMapData.ropes, editorMapData.doubleJumps)
                .flatMap(List::stream).forEach(item -> {
            double itemTime = item.getTimeStart();
            if(isInRange(itemTime, renderingRangeSeconds, time))
                relevantObstacles.add(generateObstacleObject(editorMapData, item, time));
        });

        return relevantObstacles;
    }
    private ObstacleObject generateObstacleObject(EditorMapData editorMapData, LevelMapElement element, double time) {
        return ActionPhaseObjectHandler.createObstacleObject(
                editorMapData.speed,
                ActionPhaseController.PLAYER_X,
                element,
                time,
                this.game
        );
    }

    private List<NoteObject> getRelevantNotes(EditorMapData editorMapData, double time) {
        ArrayList<NoteObject> relevantNotes = new ArrayList<>();

        Stream.of(editorMapData.tapNotes, editorMapData.growNotes, editorMapData.slideNotes)
            .flatMap(List::stream).forEach(item -> {
            double itemTime = item.getTimeStart();
            if(isInRange(itemTime, renderingRangeSeconds, time))
                relevantNotes.add(generateNoteObject(editorMapData, item, time));
        });


        return relevantNotes;
    }
    private NoteObject generateNoteObject(EditorMapData editorMapData, LevelMapNote levelMapNote, double time) {
        return ActionPhaseObjectHandler.createNoteObject(
                editorMapData.speed,
                ActionPhaseController.PLAYER_X,
                levelMapNote,
                time,
                this.game
        );
    }

    private boolean isInRange(double value, double range, double baseValue) {
        return ((value - range) < baseValue) && ((value + range) > baseValue);
    }

}
