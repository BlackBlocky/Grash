package grash.editor;

import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.core.GrashEvent_Tick;
import grash.event.events.editor.GrashEvent_SetupEditor;
import grash.event.events.input.GrashEvent_KeyDown;
import grash.event.events.input.GrashEvent_KeyUp;
import javafx.scene.input.KeyCode;

public class EditorController implements GrashEventListener {

    private static final double SCROLL_SPEED = 2.0;

    private final GameController game;
    private final EditorRenderingController renderingController;

    private EditorMapData currentEditorMapData;
    private EditorState editorState;

    private double currentPreviewTime;
    private double currentScrollValue;

    public EditorController(GameController gameController) {
        this.game = gameController;
        this.renderingController = new EditorRenderingController(game);
        this.editorState = EditorState.inactive;
        this.currentPreviewTime = 0.0;

        game.getEventBus().registerListener(GrashEvent_SetupEditor.class, this);
        game.getEventBus().registerListener(GrashEvent_KeyDown.class, this);
        game.getEventBus().registerListener(GrashEvent_KeyUp.class, this);
        game.getEventBus().registerListener(GrashEvent_Tick.class, this);
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

        this.currentPreviewTime = 0.0;
        this.currentScrollValue = 0.0;

        renderingController.setup();
        updateMapPreviewRender(currentPreviewTime);

        editorState = EditorState.active;
    }

    private void event_KeyDown(GrashEvent_KeyDown event) {
        if(event.getKeyCode() == KeyCode.D) currentScrollValue = SCROLL_SPEED;
        if(event.getKeyCode() == KeyCode.A) currentScrollValue = -SCROLL_SPEED;
    }

    private void event_KeyUp(GrashEvent_KeyUp event) {
        if((event.getKeyCode() == KeyCode.A && currentScrollValue != SCROLL_SPEED) ||
                (event.getKeyCode() == KeyCode.D && currentScrollValue != -SCROLL_SPEED))
            currentScrollValue = 0.0;
    }

    private void event_Tick(GrashEvent_Tick event) {
        moveView(event.getDeltaTime());
    }

    private void moveView(double deltaTime) {
        if(currentScrollValue != 0.0) {
            currentPreviewTime += currentScrollValue * deltaTime;
            updateMapPreviewRender(currentPreviewTime);
        }
    }

    private void updateMapPreviewRender(double time) {
        renderingController.newFrame(this.currentEditorMapData, time);
    }
}
