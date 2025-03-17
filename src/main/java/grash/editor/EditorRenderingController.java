package grash.editor;

import grash.action.ActionPhaseController;
import grash.action.ActionPhaseObjectHandler;
import grash.action.objects.NoteObject;
import grash.action.objects.ObstacleObject;
import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.level.map.LevelMapEffect;
import grash.level.map.LevelMapElement;
import grash.level.map.MapEffectType;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EditorRenderingController {

    private static final double renderingRangeSeconds = 5.0;

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
    public void setup() {
        editorCanvas = (Canvas) game.getPrimaryStage().getScene().lookup("#editorCanvas");
        editorRenderer.setupRenderer(null, null, null, editorCanvas);
    }

    public void newFrame(EditorMapData editorMapData, double time) {
        setCurrentEffects(editorMapData, time);
        editorRenderer.updateCanvas(1.0/60.0, time,
                getRelevantObstacles(editorMapData, time),
                getRelevantNotes(editorMapData, time),
                null);
    }

    private void setCurrentEffects(EditorMapData editorMapData, double time) {
        LevelMapEffect colorEffect = new LevelMapEffect(MapEffectType.Color);
        colorEffect.setColor(editorMapData.startColor);
        colorEffect.setTimeStart(0.0);
        editorRenderer.updateColors(colorEffect, colorEffect);

        LevelMapEffect rotationEffect = new LevelMapEffect(MapEffectType.Rotate);
        rotationEffect.setValueDouble(editorMapData.startRotation);
        rotationEffect.setTimeStart(0.0);
        editorRenderer.updateRotations(rotationEffect, rotationEffect);

        LevelMapEffect fovScaleEffect = new LevelMapEffect(MapEffectType.FOVScale);
        fovScaleEffect.setValueDouble(editorMapData.startFOVScale);
        fovScaleEffect.setTimeStart(0.0);
        editorRenderer.updateFovScales(fovScaleEffect, fovScaleEffect);
    }

    private List<ObstacleObject> getRelevantObstacles(EditorMapData editorMapData, double time) {
        ArrayList<ObstacleObject> relevantObstacles = new ArrayList<>();

        Stream.of(editorMapData.getSpikes(), editorMapData.getSlides(), editorMapData.getWalls(),
                        editorMapData.getRopes(), editorMapData.getDoubleJumps())
                .flatMap(Arrays::stream).forEach(item -> {
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
        return new ArrayList<NoteObject>();
    }

    private boolean isInRange(double value, double range, double baseValue) {
        return ((value - range) < baseValue) && ((value + range) > baseValue);
    }

}
