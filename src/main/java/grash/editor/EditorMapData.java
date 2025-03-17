package grash.editor;

import grash.assets.MapData;
import grash.level.map.LevelMap;
import grash.level.map.LevelMapEffect;
import grash.level.map.LevelMapElement;
import grash.level.map.LevelMapNote;
import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.Arrays;

public class EditorMapData {

    protected double speed;
    protected double growspeed;
    protected Color startColor;
    protected double startFOVScale;
    protected double startRotation;

    protected LevelMapElement[] spikes;
    protected LevelMapElement[] slides;
    protected LevelMapElement[] walls;
    protected LevelMapElement[] doubleJumps;
    protected LevelMapElement[] ropes;

    protected LevelMapNote[] tapNotes;
    protected LevelMapNote[] growNotes;
    protected LevelMapNote[] slideNotes;

    protected LevelMapEffect[] colors;
    protected LevelMapEffect[] fovScales;
    protected LevelMapEffect[] rotates;

    protected LevelMapEffect[] bImages;
    protected LevelMapEffect[] lasershows;

    public EditorMapData(LevelMap targetClone) {
        this.speed = targetClone.getSpeed();
        this.growspeed = targetClone.getGrowspeed();
        Color targetColor = targetClone.getStartColor();
        this.startColor = new Color(targetColor.getRed(), targetColor.getGreen(), targetColor.getBlue(), 1.0);
        this.startFOVScale = targetClone.getStartFOVScale();
        this.startRotation = targetClone.getStartRotation();

        // Were gonna modify the Originals, but because all of that Data is deleted anyways, we can just modify it.
        this.spikes = targetClone.getSpikes();
        this.slides = targetClone.getSlides();
        this.walls = targetClone.getWalls();
        this.doubleJumps = targetClone.getDoubleJumps();
        this.ropes = targetClone.getRopes();

        this.tapNotes = targetClone.getTapNotes();
        this.growNotes = targetClone.getGrowNotes();
        this.slideNotes = targetClone.getSlideNotes();

        this.colors = targetClone.getColors();
        this.fovScales = targetClone.getFovScales();
        this.rotates = targetClone.getRotates();

        this.bImages = targetClone.getbImages();
        this.lasershows = targetClone.getLasershows();
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

    public double getSpeed() {
        return speed;
    }

    public double getGrowspeed() {
        return growspeed;
    }

    public Color getStartColor() {
        return startColor;
    }

    public double getStartFOVScale() {
        return startFOVScale;
    }

    public double getStartRotation() {
        return startRotation;
    }

    public LevelMapElement[] getSpikes() {
        return spikes;
    }

    public LevelMapElement[] getSlides() {
        return slides;
    }

    public LevelMapElement[] getWalls() {
        return walls;
    }

    public LevelMapElement[] getDoubleJumps() {
        return doubleJumps;
    }

    public LevelMapElement[] getRopes() {
        return ropes;
    }

    public LevelMapNote[] getTapNotes() {
        return tapNotes;
    }

    public LevelMapNote[] getGrowNotes() {
        return growNotes;
    }

    public LevelMapNote[] getSlideNotes() {
        return slideNotes;
    }

    public LevelMapEffect[] getColors() {
        return colors;
    }

    public LevelMapEffect[] getFovScales() {
        return fovScales;
    }

    public LevelMapEffect[] getRotates() {
        return rotates;
    }

    public LevelMapEffect[] getbImages() {
        return bImages;
    }

    public LevelMapEffect[] getLasershows() {
        return lasershows;
    }
}
