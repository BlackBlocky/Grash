package grash.editor;

import grash.action.objects.NoteObject;
import grash.action.objects.ObstacleObject;
import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.level.map.LevelMapElement;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EditorRenderingController {

    private final GameController game;
    private final ActionPhaseRenderer editorRenderer;

    private Canvas editorCanvas;

    public EditorRenderingController(GameController gameController) {
        this.game = gameController;
        this.editorRenderer = new ActionPhaseRenderer(game);
    }

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

    }

    private List<ObstacleObject> getRelevantObstacles(EditorMapData editorMapData, double time) {
        ArrayList<ObstacleObject> relevantObstacles = new ArrayList<>();

        Stream.of(editorMapData.getSpikes(), editorMapData.getSlides(), editorMapData.getWalls(),
                        editorMapData.getRopes(), editorMapData.getDoubleJumps())
                .flatMap(Arrays::stream).forEach(item -> {
            double itemTime = item.getTimeStart();
            if(isInRange(itemTime, 5.0)) relevantObstacles.add(generateObstacleObject(item));
        });

        return relevantObstacles;
    }
    private ObstacleObject generateObstacleObject(LevelMapElement element) {
        return null;
    }

    private List<NoteObject> getRelevantNotes(EditorMapData editorMapData, double time) {
        return null;
    }

    private boolean isInRange(double value, double range) {
        return false;
    }

}
