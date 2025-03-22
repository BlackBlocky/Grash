package grash.editor;

import grash.assets.MapData;
import grash.level.map.LevelMap;
import grash.level.map.LevelMapEffect;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapNote;
import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditorMapData {

    protected double speed;
    protected double growspeed;
    protected Color startColor;
    protected double startFOVScale;
    protected double startRotation;

    protected ArrayList<LevelMapElement> spikes;
    protected ArrayList<LevelMapElement> slides;
    protected ArrayList<LevelMapElement> walls;
    protected ArrayList<LevelMapElement> doubleJumps;
    protected ArrayList<LevelMapElement> ropes;

    protected ArrayList<LevelMapNote> tapNotes;
    protected ArrayList<LevelMapNote> growNotes;
    protected ArrayList<LevelMapNote> slideNotes;

    protected ArrayList<LevelMapEffect> colors;
    protected ArrayList<LevelMapEffect> fovScales;
    protected ArrayList<LevelMapEffect> rotates;

    protected ArrayList<LevelMapEffect> bImages;
    protected ArrayList<LevelMapEffect> lasershows;

    public EditorMapData(LevelMap targetClone) {
        this.speed = targetClone.getSpeed();
        this.growspeed = targetClone.getGrowspeed();
        Color targetColor = targetClone.getStartColor();
        this.startColor = new Color(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(), 1.0);
        this.startFOVScale = targetClone.getStartFOVScale();
        this.startRotation = targetClone.getStartRotation();

        // Were gonna modify the Originals, but because all of that Data is deleted anyways, we can just modify it.
        this.spikes = new ArrayList<>(List.of(targetClone.getSpikes()));
        this.slides = new ArrayList<>(List.of(targetClone.getSlides()));
        this.walls = new ArrayList<>(List.of(targetClone.getWalls()));
        this.doubleJumps = new ArrayList<>(List.of(targetClone.getDoubleJumps()));
        this.ropes = new ArrayList<>(List.of(targetClone.getRopes()));

        this.tapNotes = new ArrayList<>(List.of(targetClone.getTapNotes()));
        this.growNotes = new ArrayList<>(List.of(targetClone.getGrowNotes()));
        this.slideNotes = new ArrayList<>(List.of(targetClone.getSlideNotes()));

        this.colors = new ArrayList<>(List.of(targetClone.getColors()));
        this.fovScales = new ArrayList<>(List.of(targetClone.getFovScales()));
        this.rotates = new ArrayList<>(List.of(targetClone.getRotates()));

        this.bImages = new ArrayList<>(List.of(targetClone.getbImages()));
        this.lasershows = new ArrayList<>(List.of(targetClone.getLasershows()));
    }

    private <T> T[] deepCopyArray(T[] original, java.util.function.Function<T, T> copyFunction) {
        if(original == null) return null;

        T[] copyArray = Arrays.copyOf(original, original.length);

        for(int i = 0; i < copyArray.length; i++) {
            if(copyArray[i] == null) continue;
            copyArray[i] = copyFunction.apply(copyArray[i]);
        }

        return copyArray;
    }
}
