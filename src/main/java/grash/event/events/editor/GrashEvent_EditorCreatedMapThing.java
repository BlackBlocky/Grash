package grash.event.events.editor;

import grash.event.GrashEvent;
import grash.level.map.LevelMapThing;

/**
 * This Event is Triggered when a new Thing in the Editor was created
 * <br>Triggered by:
 * {@link grash.editor.EditorInsertController}
 *
 *  <br>Received by:
 * {@link grash.editor.EditorSelectionController}
 *
 */

public class GrashEvent_EditorCreatedMapThing extends GrashEvent {
    private final LevelMapThing createdThing;

    public GrashEvent_EditorCreatedMapThing(LevelMapThing createdThing) {
        super("EditorCreatedMapThing");
        this.createdThing = createdThing;
    }

    public LevelMapThing getCreatedThing() {
        return createdThing;
    }
}
