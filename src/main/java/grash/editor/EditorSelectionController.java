package grash.editor;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
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
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "KeyDown": {
                onEvent_KeyDown((GrashEvent_KeyDown) event);
            }
        }
    }

    private void onEvent_KeyDown(GrashEvent_KeyDown event) {
        if(editorController.getEditorState() == EditorState.inactive) return;

        if(event.getKeyCode() == KeyCode.PERIOD) moveSelectionIndex(1);
        else if(event.getKeyCode() == KeyCode.COMMA) moveSelectionIndex(-1);
    }
    
    private void moveSelectionIndex(int amount) {
        selectedLevelMapThingIndex += amount;
        selectedLevelMapThingIndex = Math.max(0,
                Math.min(currentEditorMapData.allThings.size() - 1, selectedLevelMapThingIndex)
        );

        selectedLevelMapThing = currentEditorMapData.allThings.get(selectedLevelMapThingIndex);
    }

    private void setSelectionToNull() {
        selectedLevelMapThing = null;
        selectedLevelMapThingIndex = -1;
    }

    private void reFindSelectionIndex() {
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
