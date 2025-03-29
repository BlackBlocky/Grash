package grash.event.events.editor;

import grash.event.GrashEvent;
import grash.level.map.LevelMapThing;

/**
 * This Event is Triggered when another Thing in the Editor was selected
 * <br>Triggered by:
 * {@link grash.editor.EditorSelectionController}
 *
 *  <br>Received by:
 * {@link grash.editor.EditorEditController}
 *
 */

public class GrashEvent_SelectionChanged extends GrashEvent {
    private final LevelMapThing thing;

    public GrashEvent_SelectionChanged(LevelMapThing thing) {
        super("SelectionChanged");
        this.thing = thing;
    }

    public LevelMapThing getThing() {
        return thing;
    }
}
