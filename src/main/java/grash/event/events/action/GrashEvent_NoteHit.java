package grash.event.events.action;

import grash.action.NoteAccuracy;
import grash.action.objects.NoteObject;
import grash.event.GrashEvent;

/**
 * This Event is triggered when the Player tapped a note.
 * <br>Triggered by:
 * {@link grash.action.ActionPhaseLogicHandler}s
 *
 *  <br>Received by:
 *  {@link grash.action.ActionPhaseController}
 *
 */

public class GrashEvent_NoteHit extends GrashEvent {
    final private NoteAccuracy noteAccuracy;
    final private NoteObject noteObject;

    public GrashEvent_NoteHit(NoteAccuracy noteAccuracy, NoteObject noteObject) {
        super("NoteHit");
        this.noteAccuracy = noteAccuracy;
        this.noteObject = noteObject;
    }

    public NoteAccuracy getNoteAccuracy() {
        return noteAccuracy;
    }

    public NoteObject getNoteObject() {
        return noteObject;
    }
}
