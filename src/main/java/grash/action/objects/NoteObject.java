package grash.action.objects;

import grash.assets.Sprite;
import grash.core.GameController;
import grash.level.map.LevelMapNote;
import grash.level.map.LevelMapThing;
import grash.level.map.MapNoteType;
import grash.math.Vec2;

import java.util.List;

public class NoteObject extends ActionObject {

    public NoteObject(GameController game, Vec2 spawnPos, LevelMapNote myLevelMapNote) {
        super(game, spawnPos, Vec2.ONE(), Vec2.ZERO(), myLevelMapNote, null);
    }

    public double getHitTimeSeconds() { return this.levelMapThing.getTimeStart(); }
    public LevelMapNote getLevelMapNote() { return (LevelMapNote) this.levelMapThing; }

    public void doTapAnimation() {

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
