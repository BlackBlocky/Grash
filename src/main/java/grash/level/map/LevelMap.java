package grash.level.map;

import grash.assets.MapMetadata;
import javafx.scene.paint.Color;

public final class LevelMap {
    private final MapMetadata mapMetadata;

    private final double speed;
    private final double growspeed;
    private final Color startColor;
    private final double startFOVScale;
    private final double startRotation;
    //System.out.println(Evergreen X Daniel);

    private final LevelMapElement[] spikes;
    private final LevelMapElement[] slides;
    private final LevelMapElement[] walls;
    private final LevelMapElement[] doubleJumps;
    private final LevelMapElement[] ropes;

    private final LevelMapElement[] tapNotes;
    private final LevelMapElement[] growNotes;
    private final LevelMapElement[] slideNotes;

    private final LevelMapEffect[] colors;
    private final LevelMapEffect[] fovScales;
    private final LevelMapEffect[] rotates;

    private final LevelMapEffect[] bImages;
    private final LevelMapEffect[] lasershows;

    public LevelMap(MapMetadata mapMetadata,
                    double speed,
                    double growspeed,
                    Color startColor,
                    double startFOVScale,
                    double startRotation,

                    LevelMapElement[] spikes,
                    LevelMapElement[] slides,
                    LevelMapElement[] walls,
                    LevelMapElement[] doubleJumps,
                    LevelMapElement[] ropes,

                    LevelMapElement[] tapNotes,
                    LevelMapElement[] growNotes,
                    LevelMapElement[] slideNotes,

                    LevelMapEffect[] colors,
                    LevelMapEffect[] fovScales,
                    LevelMapEffect[] rotates,

                    LevelMapEffect[] bImages,
                    LevelMapEffect[] lasershows) {
        this.mapMetadata = mapMetadata;

        this.speed = speed;
        this.growspeed = growspeed;
        this.startColor = startColor;
        this.startFOVScale = startFOVScale;
        this.startRotation = startRotation;

        this.spikes = spikes;
        this.slides = slides;
        this.walls = walls;
        this.doubleJumps = doubleJumps;
        this.ropes = ropes;

        this.tapNotes = tapNotes;
        this.growNotes = growNotes;
        this.slideNotes = slideNotes;

        this.colors = colors;
        this.fovScales = fovScales;
        this.rotates = rotates;

        this.bImages = bImages;
        this.lasershows = lasershows;
    }

    public MapMetadata getMapMetadata() {
        return mapMetadata;
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

    public LevelMapElement[] getTapNotes() {
        return tapNotes;
    }

    public LevelMapElement[] getGrowNotes() {
        return growNotes;
    }

    public LevelMapElement[] getSlideNotes() {
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