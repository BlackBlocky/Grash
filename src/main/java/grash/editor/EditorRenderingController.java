package grash.editor;

import grash.action.ActionPhaseController;
import grash.action.ActionPhaseObjectHandler;
import grash.action.objects.ActionObject;
import grash.action.objects.NoteObject;
import grash.action.objects.ObstacleObject;
import grash.action.objects.PlayerObject;
import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.level.map.*;
import grash.math.Vec2;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EditorRenderingController {

    private static final double renderingRangeSeconds = 2.0;

    private final GameController game;
    private final EditorController editorController;
    private final ActionPhaseRenderer editorRenderer;

    private Canvas editorCanvas;

    private PlayerObject dummyPlayer;

    public EditorRenderingController(GameController gameController, EditorController editorController) {
        this.game = gameController;
        this.editorController = editorController;
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

        // Create a dummyPlayer object
        dummyPlayer = new PlayerObject(game, new Vec2(ActionPhaseController.PLAYER_X, ActionPhaseController.Y_DOWN),
                null, null, null);
    }

    private ActionObject currentSelectedActionObject; // set in getRelevantObstacles() or getRelevantNotes()
    public void newFrame(EditorMapData editorMapData, double time) {
        currentSelectedActionObject = null;

        setCurrentEffects(editorMapData, time);
        editorRenderer.updateCanvas(1/1000.0, time,
                getRelevantObstacles(editorMapData, time),
                getRelevantNotes(editorMapData, time),
                dummyPlayer);

        editorRenderer.renderEditorOverdraw(time,
                editorMapData.mapMetadata.getSongBPM(),
                editorMapData.speed, ActionPhaseController.PLAYER_X,
                currentSelectedActionObject);
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

    /**
     * Important Note: The resulting List is UNSORTED by the time (Because it would be unnecessary).
     * This Method also sets the currentSelectedActionObject.
     */
    private List<ObstacleObject> getRelevantObstacles(EditorMapData editorMapData, double time) {
        ArrayList<ObstacleObject> relevantObstacles = new ArrayList<>();

        Stream.of(editorMapData.spikes, editorMapData.slides, editorMapData.walls,
                        editorMapData.ropes, editorMapData.doubleJumps)
                .flatMap(List::stream).forEach(item -> {
            double itemTime = item.getTimeStart();
            if(isInRange(itemTime, renderingRangeSeconds, time)) {
                ObstacleObject newObstacleObject = generateObstacleObject(editorMapData, item, time);
                relevantObstacles.add(newObstacleObject);

                if(editorController.getSelectionController().getSelectedLevelMapThing() == item) {
                    currentSelectedActionObject = newObstacleObject;
                }
            }

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

    /**
     * Important Note: The resulting List is UNSORTED by the time (Because it would be unnecessary).
     * This Method also sets the currentSelectedActionObject.
     */
    private List<NoteObject> getRelevantNotes(EditorMapData editorMapData, double time) {
        ArrayList<NoteObject> relevantNotes = new ArrayList<>();

        Stream.of(editorMapData.tapNotes, editorMapData.growNotes, editorMapData.slideNotes)
            .flatMap(List::stream).forEach(item -> {
            double itemTime = item.getTimeStart();
            if(isInRange(itemTime, renderingRangeSeconds, time)) {
                NoteObject newNoteObject = generateNoteObject(editorMapData, item, time);
                relevantNotes.add(newNoteObject);

                if(editorController.getSelectionController().getSelectedLevelMapThing() == item) {
                    currentSelectedActionObject = newNoteObject;
                }
            }

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
