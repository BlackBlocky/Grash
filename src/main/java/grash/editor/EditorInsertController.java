package grash.editor;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.editor.GrashEvent_EditorCreatedMapThing;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.level.map.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class EditorInsertController implements GrashEventListener {

    private final GameController game;
    private final EditorController editorController;

    private EditorInsertMode currentInsertMode;

    private final Map<EditorInsertMode, Map<Integer, Enum<?>>> insertMap;
    private Map<EditorInsertMode, Map<Enum<?>, List<? extends LevelMapThing>>> mapThingsListMap;
    private Map<EditorInsertMode, Runnable> mapDataSortFunctionByInsertMode;

    private final HashMap<EditorInsertMode, GridPane> infoPanelsHashMap;

    public EditorInsertController(GameController game, EditorController editorController) {
        this.game = game;
        this.editorController = editorController;
        this.currentInsertMode = EditorInsertMode.None;

        insertMap = Map.ofEntries(
                Map.entry(EditorInsertMode.Element, Map.ofEntries(
                        Map.entry(0, MapElementType.Spike),
                        Map.entry(1, MapElementType.Wall),
                        Map.entry(2, MapElementType.DoubleJump),
                        Map.entry(3, MapElementType.Rope),
                        Map.entry(4, MapElementType.Slide)
                )),
                Map.entry(EditorInsertMode.Note, Map.ofEntries(
                        Map.entry(0, MapNoteType.TapNote),
                        Map.entry(1, MapNoteType.GrowNote),
                        Map.entry(2, MapNoteType.SlideNote)
                )),
                Map.entry(EditorInsertMode.Effect, Map.ofEntries(
                        Map.entry(0, MapEffectType.Color),
                        Map.entry(1, MapEffectType.FOVScale),
                        Map.entry(2, MapEffectType.Rotate),
                        Map.entry(3, MapEffectType.LaserShow)
                ))
        );

        this.infoPanelsHashMap = new HashMap<>();

        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
    }

    public void reset(EditorMapData editorMapData) {
        this.currentInsertMode = EditorInsertMode.None;
        regenerateMapsBasedOnMapData(editorMapData);

        infoPanelsHashMap.clear();
        infoPanelsHashMap.put(
                EditorInsertMode.Element, (GridPane) game.getPrimaryStage().getScene().lookup("#elementInfoPanel")
        );
        infoPanelsHashMap.put(
                EditorInsertMode.Note, (GridPane) game.getPrimaryStage().getScene().lookup("#noteInfoPanel")
        );
        infoPanelsHashMap.put(
                EditorInsertMode.Effect, (GridPane) game.getPrimaryStage().getScene().lookup("#effectInfoPanel")
        );
    }

    private void regenerateMapsBasedOnMapData(EditorMapData editorMapData) {
        this.mapThingsListMap = Map.ofEntries(
                Map.entry(EditorInsertMode.Element, Map.ofEntries(
                        Map.entry(MapElementType.Spike, editorMapData.spikes),
                        Map.entry(MapElementType.Wall, editorMapData.walls),
                        Map.entry(MapElementType.DoubleJump, editorMapData.doubleJumps),
                        Map.entry(MapElementType.Rope, editorMapData.ropes),
                        Map.entry(MapElementType.Slide, editorMapData.slides)
                )),
                Map.entry(EditorInsertMode.Note, Map.ofEntries(
                        Map.entry(MapNoteType.TapNote, editorMapData.tapNotes),
                        Map.entry(MapNoteType.GrowNote, editorMapData.growNotes),
                        Map.entry(MapNoteType.SlideNote, editorMapData.slideNotes)
                )),
                Map.entry(EditorInsertMode.Effect, Map.ofEntries(
                        Map.entry(MapEffectType.Color, editorMapData.colors),
                        Map.entry(MapEffectType.FOVScale, editorMapData.fovScales),
                        Map.entry(MapEffectType.Rotate, editorMapData.rotates),
                        Map.entry(MapEffectType.LaserShow, editorMapData.lasershows)
                ))
        );

        this.mapDataSortFunctionByInsertMode = Map.ofEntries(
                Map.entry(EditorInsertMode.Element, editorMapData::sortAllElements),
                Map.entry(EditorInsertMode.Note, editorMapData::sortAllNotes),
                Map.entry(EditorInsertMode.Effect, editorMapData::sortAllEffects)
        );
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

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        switch (event.getKeyCode()) {
            case KeyCode.E -> switchInsertMode(EditorInsertMode.Element);
            case KeyCode.R -> switchInsertMode(EditorInsertMode.Note);
            case KeyCode.T -> switchInsertMode(EditorInsertMode.Effect);

            case KeyCode.DIGIT1 -> startInsertAction(0);
            case KeyCode.DIGIT2 -> startInsertAction(1);
            case KeyCode.DIGIT3 -> startInsertAction(2);
            case KeyCode.DIGIT4 -> startInsertAction(3);
            case KeyCode.DIGIT5 -> startInsertAction(4);
            case KeyCode.DIGIT6 -> startInsertAction(5);
            case KeyCode.DIGIT7 -> startInsertAction(6);
            case KeyCode.DIGIT8 -> startInsertAction(7);
            case KeyCode.DIGIT9 -> startInsertAction(8);
            case KeyCode.DIGIT0 -> startInsertAction(9);
        }
    }

    private void switchInsertMode(EditorInsertMode newMode) {
        if(currentInsertMode != EditorInsertMode.None && currentInsertMode == newMode) {
            currentInsertMode = EditorInsertMode.None;
            toggleInfoPanels(currentInsertMode);
            return;
        }

        currentInsertMode = newMode;
        toggleInfoPanels(currentInsertMode);
    }

    private void toggleInfoPanels(EditorInsertMode insertMode) {
        for(GridPane pane : infoPanelsHashMap.values()) {
            pane.setVisible(false);
        }

        if(insertMode != EditorInsertMode.None) {
            infoPanelsHashMap.get(insertMode).setVisible(true);
        }
    }

    private void startInsertAction(int index) {
        if(currentInsertMode == EditorInsertMode.None) return;

        Map<Integer, Enum<?>> activeInsertMap = insertMap.get(currentInsertMode);
        if(!activeInsertMap.containsKey(index)) return;

        addThingToLevel(currentInsertMode, activeInsertMap.get(index));
    }

    private void addThingToLevel(EditorInsertMode insertMode, Enum<?> type) {
        LevelMapThing newThing = null;

        // Create the Thing
        switch (insertMode) {
            case Element -> newThing = new LevelMapElement((MapElementType) type);
            case Note -> newThing = new LevelMapNote((MapNoteType) type);
            case Effect -> newThing = new LevelMapEffect((MapEffectType) type);
        }

        // Set some default values (values that aren't matching to the element does care anyway)
        double currentEditorTime = editorController.getCurrentPreviewTime();
        assignDefaultValues(newThing, insertMode, currentEditorTime);

        // Get the list of the Type of the Thing and add it
        Map<Enum<?>, List<? extends LevelMapThing>> typesMap = mapThingsListMap.get(insertMode);
        List<LevelMapThing> thingsList = (List<LevelMapThing>) typesMap.get(type);

        thingsList.add(newThing);

        // Sort the Types of the Thing
        mapDataSortFunctionByInsertMode.get(insertMode).run();

        game.getEventBus().triggerEvent(new GrashEvent_EditorCreatedMapThing(newThing));
    }

    /**
     * This Function going to modify the thing directly by the pointer
     * @param thing this will be modified
     */
    private void assignDefaultValues(LevelMapThing thing, EditorInsertMode insertMode, double time) {
        switch (insertMode) {
            case Element -> {
                LevelMapElement element = (LevelMapElement) thing;
                element.setTimeStart(time);
                element.setTimeEnd(time + 1.0);
                element.setHeightNormalized(0.5);
                element.setIsLeft(false); // Idk why that exists lol xD
                element.setIsUp(false);
            }
            case Note -> {
                LevelMapNote note = (LevelMapNote) thing;
                note.setTimeStart(time);
                note.setTimeEnd(time + 1.0);
                note.setIsLeft(false);
                note.setIsLeft(false);
                note.setYType((byte) 1);
            }
            case Effect -> {
                LevelMapEffect effect = (LevelMapEffect) thing;
                effect.setTimeStart(time);
                effect.setColor(Color.GRAY);
                effect.setValueDouble(0.0);
                effect.setValueInteger(0);
            }
        }
    }
 }
