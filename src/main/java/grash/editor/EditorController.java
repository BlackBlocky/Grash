package grash.editor;

import grash.action.renderer.ActionPhaseRenderer;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;

public class EditorController implements GrashEventListener {

    private GameController game;
    private ActionPhaseRenderer editorRenderer;

    public EditorController(GameController gameController) {
        this.game = gameController;
        this.editorRenderer = new ActionPhaseRenderer(game);
    }

    @Override
    public void onEvent(GrashEvent event) {

    }

    private void setupRenderer() {

    }
}
