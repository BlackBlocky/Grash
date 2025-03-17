package grash.editor;

import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.editor.GrashEvent_SetupEditor;

public class EditorController implements GrashEventListener {

    private GameController game;
    private ActionPhaseRenderer editorRenderer;

    private EditorMapData currentEditorMapData;

    public EditorController(GameController gameController) {
        this.game = gameController;
        this.editorRenderer = new ActionPhaseRenderer(game);

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

    }

    private void setupRenderer() {

    }
}
