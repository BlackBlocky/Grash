package grash.editor;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.editor.GrashEvent_EditorCreatedMapThing;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.level.map.LevelMapThing;
import javafx.scene.input.KeyCode;

public class EditorSelectionController implements GrashEventListener {

    private final GameController game;
    private final EditorController editorController;

    // Both of these are connected to EditorMapData::allThings
    private LevelMapThing selectedLevelMapThing; // null if nothing is selected
    private int selectedLevelMapThingIndex; // -1 if nothing is selected

    private EditorMapData currentEditorMapData;

    public EditorSelectionController(GameController game, EditorController editorController) {
        this.game = game;
        this.editorController = editorController;

        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
        game.getEventBus().registerListener(GrashEvent_EditorCreatedMapThing.class, this);
    }

    public void resetAndSetup(EditorMapData editorMapData) {
        setSelectionToNull();

        this.currentEditorMapData = editorMapData;
    }

    public LevelMapThing getSelectedLevelMapThing() { return this.selectedLevelMapThing; }

    /**
     * This Method should be called if the allThings-List is already updated to its new state
     * (if a deletion happened, etc.)
     */
    public void deselect(boolean findNewSelection) {
        if(selectedLevelMapThing == null) return;

        if(findNewSelection) {
            moveSelectionIndex(selectedLevelMapThingIndex == 0 ? 1 : -1);
            reFindSelectionIndex();
        }
        else {
            setSelectionToNull();
        }

        editorController.selectionChanged();
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "KeyDown": {
                onEvent_KeyDown((GrashEvent_KeyDown) event);
                break;
            }
            case "EditorCreatedMapThing": {
                onEvent_EditorCreatedMapThing((GrashEvent_EditorCreatedMapThing) event);
                break;
            }
        }
    }

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        if(editorController.getEditorState() == EditorState.inactive) return;

        if(event.getKeyCode() == KeyCode.PERIOD) moveSelectionIndex(1);
        else if(event.getKeyCode() == KeyCode.COMMA) moveSelectionIndex(-1);
        else if(event.getKeyCode() == KeyCode.MINUS) setSelectionToIndex(getIndexOfNearestThing());
    }

    private void onEvent_EditorCreatedMapThing(GrashEvent_EditorCreatedMapThing event) {
        selectedLevelMapThing = event.getCreatedThing();
        reFindSelectionIndex();
    }

    private int getIndexOfNearestThing() {
        if(currentEditorMapData.allThings.isEmpty()) return -1;

        double time = editorController.getCurrentPreviewTime();

        LevelMapThing closest = currentEditorMapData.allThings.stream()
                .min((a, b) -> Double.compare(Math.abs(a.getTimeStart() - time),
                        Math.abs(b.getTimeStart() - time)))
                .orElse(null); // Falls die Liste leer ist, wird null zurückgegeben

        System.out.println(currentEditorMapData.allThings.indexOf(closest));

        return currentEditorMapData.allThings.indexOf(closest);
    }

    private void setSelectionToIndex(int index) {
        selectedLevelMapThingIndex = index;
        selectedLevelMapThing = currentEditorMapData.allThings.get(selectedLevelMapThingIndex);

        editorController.selectionChanged();
    }

    private void moveSelectionIndex(int amount) {
        selectedLevelMapThingIndex += amount;
        selectedLevelMapThingIndex = Math.max(0,
                Math.min(currentEditorMapData.allThings.size() - 1, selectedLevelMapThingIndex)
        );

        selectedLevelMapThing = currentEditorMapData.allThings.get(selectedLevelMapThingIndex);
        editorController.selectionChanged();
    }

    private void setSelectionToNull() {
        selectedLevelMapThing = null;
        selectedLevelMapThingIndex = -1;
    }

    public void reFindSelectionIndex() {
        if(selectedLevelMapThing == null) {
            setSelectionToNull();
            return;
        }
        if(!currentEditorMapData.allThings.contains(selectedLevelMapThing)) {
            setSelectionToNull();
            return;
        }

        selectedLevelMapThingIndex = currentEditorMapData.allThings.indexOf(selectedLevelMapThing);
    }
}
