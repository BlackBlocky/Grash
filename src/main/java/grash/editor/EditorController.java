package grash.editor;

import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.editor.GrashEvent_SetupEditor;

public class EditorController implements GrashEventListener {

    private final GameController game;
    private final EditorRenderingController renderingController;

    private EditorMapData currentEditorMapData;

    public EditorController(GameController gameController) {
        this.game = gameController;
        this.renderingController = new EditorRenderingController(game);

        game.getEventBus().registerListener(GrashEvent_SetupEditor.class, this);
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "SetupEditor": {
                event_SetupEditor((GrashEvent_SetupEditor) event);
                break;
            }
        }
    }

    private void event_SetupEditor(GrashEvent_SetupEditor event) {
        this.currentEditorMapData = new EditorMapData(event.getEditingLevelMap());

        renderingController.setup();
        updateMapPreviewRender(0.0);
    }

    private void updateMapPreviewRender(double time) {
        renderingController.newFrame(this.currentEditorMapData, time);
    }
}
