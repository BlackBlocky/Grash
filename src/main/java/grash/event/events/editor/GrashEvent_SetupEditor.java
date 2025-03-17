package grash.event.events.editor;

import grash.event.GrashEvent;
import grash.level.map.LevelMap;

/**
 * This Event is Triggered when the Map was loaded and all that Stuff, AND we want to edit it
 * <br>Triggered by:
 * {@link grash.core.GameController}
 *
 *  <br>Received by:
 * {@link grash.editor.EditorController}
 *
 */

public class GrashEvent_SetupEditor extends GrashEvent {
    private final LevelMap editingLevelMap;

    public GrashEvent_SetupEditor(LevelMap editingLevelMap) {
        super("SetupEditor");
        this.editingLevelMap = editingLevelMap;
    }

    public LevelMap getEditingLevelMap() {
        return editingLevelMap;
    }
}
