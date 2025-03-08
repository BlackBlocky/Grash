package grash.action.objects;

import grash.action.ActionPhaseController;
import grash.action.NoteAccuracy;
import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.LevelMapNote;
import grash.math.Vec2;

public class NoteObject extends ActionObject {

    private String scoreText; // Only contains a value if it needs to display a value
    private NoteAccuracy tappedNoteHitAccuracy; // Its null until the note is tapped

    public NoteObject(GameController game, Vec2 spawnPos, LevelMapNote myLevelMapNote) {
        super(game, spawnPos, Vec2.ONE(), Vec2.ZERO(), myLevelMapNote, null);
        scoreText = "";
        tappedNoteHitAccuracy = null;
    }

    public double getHitTimeSeconds() { return this.levelMapThing.getTimeStart(); }
    public LevelMapNote getLevelMapNote() { return (LevelMapNote) this.levelMapThing; }
    public String getScoreText() { return this.scoreText; }
    public NoteAccuracy getTappedNoteHitAccuracy() { return tappedNoteHitAccuracy; }

    public void doTapAnimation(NoteAccuracy tappedAccuracy) {
        this.tappedNoteHitAccuracy = tappedAccuracy;
        this.scoreText = String.valueOf(ActionPhaseController.NOTE_HIT_TO_POINTS_MAP.get(tappedAccuracy).intValue());
        setSprite(game.getResourceLoader().getSprite("NoteHitSuccess"));
    }

    @Override
    protected Sprite setupSprite() {
        LevelMapNote levelMapNote = (LevelMapNote) this.levelMapThing;

        switch (levelMapNote.getMapNoteType()) {
            case TapNote: {
                switch (levelMapNote.getYType()) {
                    case 0: return game.getResourceLoader().getSprite("TapNoteDown"); // Down
                    case 1: return game.getResourceLoader().getSprite("TapNoteMiddle"); // Middle
                    case 2: return game.getResourceLoader().getSprite("TapNoteUp"); // Up
                }
                break;
            }
            case GrowNote: {
                return null;
            }
            case SlideNote: {
                return null;
            }
        }

        return null;
    }

    @Override
    protected void setupObject() {

    }
}
