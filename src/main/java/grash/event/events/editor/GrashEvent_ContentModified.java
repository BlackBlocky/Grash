package grash.event.events.editor;

import grash.event.GrashEvent;
import grash.level.map.LevelMapThing;

/**
 * This Event is Triggered when a new Thing in the Editor was created
 * <br>Triggered by:
 * {@link grash.editor.EditorController}
 *
 *  <br>Received by:
 * {@link grash.editor.EditorEditController}
 *
 */

public class GrashEvent_ContentModified extends GrashEvent {
    private final LevelMapThing modifiedThing;

    public GrashEvent_ContentModified(LevelMapThing modifiedThing) {
        super("ContentModified");
        this.modifiedThing = modifiedThing;
    }

    public LevelMapThing getModifiedThing() {
        return modifiedThing;
    }
}
