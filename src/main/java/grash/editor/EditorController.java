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
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.text.Style;
import java.util.Comparator;

public class EditorController implements GrashEventListener {

    private static final double SCROLL_SPEED = 1.0;

    private final GameController game;
    private final EditorRenderingController renderingController;
    private final EditorSelectionController selectionController;
    private final EditorInsertController insertController;
    private final EditorEditController editController;
    private final EditorSaveController saveController;

    private EditorMapData currentEditorMapData;
    private EditorState editorState;

    private double currentPreviewTime;
    private double currentScrollValue;

    private double setting_scrollSpeed;
    private double setting_moveSpeed;

    private boolean isInShiftMode;
    private boolean isInAltMode;

    private double playModeStartTimeSeconds;
    private long playModeStartTimeSystemTimeMillis;
    private MediaPlayer playModeMusicPlayer;

    public EditorController(GameController gameController) {
        this.game = gameController;
        this.renderingController = new EditorRenderingController(game, this);
        this.selectionController = new EditorSelectionController(game, this);
        this.insertController = new EditorInsertController(game, this);
        this.editController = new EditorEditController(game, this);
        this.saveController = new EditorSaveController(game, this);
        this.editorState = EditorState.inactive;
        this.currentPreviewTime = 0.0;
        this.isInShiftMode = false;
        this.isInAltMode = false;

        this.setting_scrollSpeed = 2.0;
        this.setting_moveSpeed = 0.02;

        this.playModeStartTimeSeconds = -1.0;
        this.playModeStartTimeSystemTimeMillis = -1L;
        this.playModeMusicPlayer = null;

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

    public void setCurrentPreviewTime(double currentPreviewTime) {
        this.currentPreviewTime = currentPreviewTime;
    }

    public void setSetting_scrollSpeed(double setting_scrollSpeed) {
        this.setting_scrollSpeed = setting_scrollSpeed;
    }
    public void setSetting_moveSpeed(double setting_moveSpeed) {
        this.setting_moveSpeed = setting_moveSpeed;
    }

    public double getSetting_scrollSpeed() {
        return setting_scrollSpeed;
    }
    public double getSetting_moveSpeed() {
        return setting_moveSpeed;
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

        this.playModeMusicPlayer = new MediaPlayer(currentEditorMapData.mapMetadata.getSongMetadata());
        saveController.saveEditorMapData(currentEditorMapData);
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

        if(event.getKeyCode() == KeyCode.P) togglePlayMode();
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

        if(editorState == EditorState.active) moveView(event.getDeltaTime());
        else if(editorState == EditorState.play) playModeUpdate();
        updateMapPreviewRender(currentPreviewTime);
    }

    private void moveView(double deltaTime) {
        if(currentScrollValue != 0.0) {
            currentPreviewTime += currentScrollValue * setting_scrollSpeed * deltaTime;
            updateMapPreviewRender(currentPreviewTime);

            editController.refreshDefaultEditFields(currentPreviewTime);
        }
    }

    private void togglePlayMode() {
        if(editorState == EditorState.active) {
            this.currentPreviewTime = Math.max(currentPreviewTime, 0.0);

            this.playModeStartTimeSeconds = currentPreviewTime;
            this.playModeStartTimeSystemTimeMillis = System.currentTimeMillis();

            this.playModeMusicPlayer.setStartTime(Duration.seconds(currentPreviewTime));
            this.playModeMusicPlayer.play();
            this.editorState = EditorState.play;
        }
        else if(editorState == EditorState.play) {
            this.playModeMusicPlayer.stop();
            this.editorState = EditorState.active;
        }
    }

    private void playModeUpdate() {
        // Wait until the Music player is actually playing
        if(playModeMusicPlayer.getStatus() != MediaPlayer.Status.PLAYING ||
                playModeMusicPlayer.getCurrentTime().toSeconds() == playModeStartTimeSeconds) return;

        // Set the Preview time to the current time calculated by the play mode
        double secondsSincePlayStart =
                (double)(System.currentTimeMillis() - playModeStartTimeSystemTimeMillis) / 1000.0;
        currentPreviewTime = playModeStartTimeSeconds + secondsSincePlayStart;

        editController.refreshDefaultEditFields(currentPreviewTime);
    }

    private void elementModifyAction(LevelMapThing thing, EditorModifyAction modifyAction) {
        if(thing == null) return;

        double moveSpeedNow = this.setting_moveSpeed;
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
                element.setHeightNormalized(element.getHeightNormalized() + this.setting_moveSpeed);
            }
            case MoveUp -> {
                if(thing.getMapThingType() != MapThingType.Element) break;
                LevelMapElement element = (LevelMapElement) thing;
                element.setHeightNormalized(element.getHeightNormalized() - this.setting_moveSpeed);
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
