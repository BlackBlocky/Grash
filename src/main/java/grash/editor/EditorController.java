package grash.editor;

import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.core.GrashEvent_Tick;
import grash.event.events.editor.GrashEvent_ContentModified;
import grash.event.events.editor.GrashEvent_SetupEditor;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.event.events.input.GrashEvent_KeyUp;
import grash.level.map.*;
import javafx.scene.input.KeyCode;

import java.util.Comparator;

public class EditorController implements GrashEventListener {

    private static final double SCROLL_SPEED = 2.0;

    private final GameController game;
    private final EditorRenderingController renderingController;
    private final EditorSelectionController selectionController;
    private final EditorInsertController insertController;
    private final EditorEditController editController;

    private EditorMapData currentEditorMapData;
    private EditorState editorState;

    private double currentPreviewTime;
    private double currentScrollValue;

    private boolean isInShiftMode;
    private boolean isInAltMode;

    public EditorController(GameController gameController) {
        this.game = gameController;
        this.renderingController = new EditorRenderingController(game, this);
        this.selectionController = new EditorSelectionController(game, this);
        this.insertController = new EditorInsertController(game, this);
        this.editController = new EditorEditController(game, this);
        this.editorState = EditorState.inactive;
        this.currentPreviewTime = 0.0;
        this.isInShiftMode = false;
        this.isInAltMode = false;

        game.getEventBus().registerListener(GrashEvent_SetupEditor.class, this);
        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
        game.getEventBus().registerListener(GrashEvent_KeyUp.class, this);
        game.getEventBus().registerListener(GrashEvent_Tick.class, this);
    }

    public EditorState getEditorState() {
        return editorState;
    }

    public EditorSelectionController getSelectionController() {
        return selectionController;
    }

    public double getCurrentPreviewTime() {
        return currentPreviewTime;
    }

    public boolean getAllowKeyInput() {
        return !editController.isEditing();
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "SetupEditor": {
                event_SetupEditor((GrashEvent_SetupEditor) event);
                break;
            }
            case "KeyDown": {
                event_KeyDown((GrashEvent_KeyDown) event);
                break;
            }
            case "KeyUp": {
                event_KeyUp((GrashEvent_KeyUp) event);
                break;
            }
            case "Tick": {
                event_Tick((GrashEvent_Tick) event);
                break;
            }
        }
    }

    private void event_SetupEditor(GrashEvent_SetupEditor event) {
        this.currentEditorMapData = new EditorMapData(event.getEditingLevelMap());
        sortAllArraysByTime(currentEditorMapData);

        this.currentPreviewTime = 0.0;
        this.currentScrollValue = 0.0;
        this.isInShiftMode = false;
        this.isInAltMode = false;

        selectionController.resetAndSetup(currentEditorMapData);

        renderingController.setup(currentEditorMapData);
        insertController.reset(currentEditorMapData);
        editController.setup();
        updateMapPreviewRender(currentPreviewTime);

        editorState = EditorState.active;
    }

    private void sortAllArraysByTime(EditorMapData editorMapData) {
        editorMapData.spikes.sort(Comparator.comparingDouble(LevelMapElement::getTimeStart));
        editorMapData.slides.sort(Comparator.comparingDouble(LevelMapElement::getTimeStart));
        editorMapData.walls.sort(Comparator.comparingDouble(LevelMapElement::getTimeStart));
        editorMapData.doubleJumps.sort(Comparator.comparingDouble(LevelMapElement::getTimeStart));
        editorMapData.ropes.sort(Comparator.comparingDouble(LevelMapElement::getTimeStart));

        editorMapData.tapNotes.sort(Comparator.comparingDouble(LevelMapNote::getTimeStart));
        editorMapData.growNotes.sort(Comparator.comparingDouble(LevelMapNote::getTimeStart));
        editorMapData.slideNotes.sort(Comparator.comparingDouble(LevelMapNote::getTimeStart));

        editorMapData.colors.sort(Comparator.comparingDouble(LevelMapEffect::getTimeStart));
        editorMapData.fovScales.sort(Comparator.comparingDouble(LevelMapEffect::getTimeStart));
        editorMapData.rotates.sort(Comparator.comparingDouble(LevelMapEffect::getTimeStart));

        editorMapData.bImages.sort(Comparator.comparingDouble(LevelMapEffect::getTimeStart));
        editorMapData.lasershows.sort(Comparator.comparingDouble(LevelMapEffect::getTimeStart));
    }

    private void event_KeyDown(GrashEvent_KeyDown event) {
        if(editorState == EditorState.inactive) return;
        if(!getAllowKeyInput()) return;

        if(event.getKeyCode() == KeyCode.D) currentScrollValue = SCROLL_SPEED;
        if(event.getKeyCode() == KeyCode.A) currentScrollValue = -SCROLL_SPEED;
        //DackelElijah ist realllll

        if(event.getKeyCode() == KeyCode.SHIFT) this.isInShiftMode = true;
        if(event.getKeyCode() == KeyCode.ALT) this.isInAltMode = true;

        if(event.getKeyCode() == KeyCode.SPACE)
            elementModifyAction(selectionController.getSelectedLevelMapThing(), EditorModifyAction.SwitchIsUp);
        else if(event.getKeyCode() == KeyCode.LEFT)
            elementModifyAction(selectionController.getSelectedLevelMapThing(), EditorModifyAction.MoveTimeLeft);
        else if(event.getKeyCode() == KeyCode.RIGHT)
            elementModifyAction(selectionController.getSelectedLevelMapThing(), EditorModifyAction.MoveTimeRight);
        else if(event.getKeyCode() == KeyCode.DOWN)
            elementModifyAction(selectionController.getSelectedLevelMapThing(), EditorModifyAction.MoveDown);
        else if(event.getKeyCode() == KeyCode.UP)
            elementModifyAction(selectionController.getSelectedLevelMapThing(), EditorModifyAction.MoveUp);
    }

    private void event_KeyUp(GrashEvent_KeyUp event) {
        if(editorState == EditorState.inactive) return;
        if(!getAllowKeyInput()) return;

        if((event.getKeyCode() == KeyCode.A && currentScrollValue != SCROLL_SPEED) ||
                (event.getKeyCode() == KeyCode.D && currentScrollValue != -SCROLL_SPEED))
            currentScrollValue = 0.0;

        if(event.getKeyCode() == KeyCode.SHIFT) this.isInShiftMode = false;
        if(event.getKeyCode() == KeyCode.ALT) this.isInAltMode = false;
    }

    private void event_Tick(GrashEvent_Tick event) {
        if(editorState == EditorState.inactive) return;

        moveView(event.getDeltaTime());
        updateMapPreviewRender(currentPreviewTime);
    }

    private void moveView(double deltaTime) {
        if(currentScrollValue != 0.0) {
            currentPreviewTime += currentScrollValue * deltaTime;
            updateMapPreviewRender(currentPreviewTime);
        }
    }

    private void elementModifyAction(LevelMapThing thing, EditorModifyAction modifyAction) {
        if(thing == null) return;

        double moveSpeedNow = 0.02;
        if(isInAltMode) moveSpeedNow *= 0.25;

        // Do the Action
        switch (modifyAction) {
            case SwitchIsUp -> {
                if(thing.getMapThingType() != MapThingType.Element) break;
                LevelMapElement element = (LevelMapElement) thing;
                element.setIsUp(!element.getIsUp());
            }
            case MoveTimeLeft -> {
                if(thing.getMapThingType() == MapThingType.Element && isInShiftMode) {
                    LevelMapElement element = (LevelMapElement) thing;
                    element.setTimeEnd(element.getTimeEnd() - moveSpeedNow);
                }
                else thing.setTimeStart(thing.getTimeStart() - moveSpeedNow);
            }
            case MoveTimeRight -> {
                if(thing.getMapThingType() == MapThingType.Element && isInShiftMode) {
                    LevelMapElement element = (LevelMapElement) thing;
                    element.setTimeEnd(element.getTimeEnd() + moveSpeedNow);
                }
                else thing.setTimeStart(thing.getTimeStart() + moveSpeedNow);
            }
            case MoveDown -> {
                if(thing.getMapThingType() != MapThingType.Element) break;
                LevelMapElement element = (LevelMapElement) thing;
                element.setHeightNormalized(element.getHeightNormalized() + 0.05);
            }
            case MoveUp -> {
                if(thing.getMapThingType() != MapThingType.Element) break;
                LevelMapElement element = (LevelMapElement) thing;
                element.setHeightNormalized(element.getHeightNormalized() - 0.05);
            }
        }

        // Sort the connected Lists
        switch (thing.getMapThingType()) {
            case Element -> currentEditorMapData.sortAllElements();
            case Note -> currentEditorMapData.sortAllNotes();
            case Effect -> currentEditorMapData.sortAllEffects();
        }

        selectionController.reFindSelectionIndex();

        // Call the Event
        game.getEventBus().triggerEvent(new GrashEvent_ContentModified(thing));
    }

    private void updateMapPreviewRender(double time) {
        renderingController.newFrame(this.currentEditorMapData, time);
    }
}
