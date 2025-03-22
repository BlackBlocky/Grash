package grash.level.map;

import grash.assets.MapData;
import grash.core.GameController;
import grash.event.GrashEvent;
import grash.event.GrashEventListener;
import grash.event.events.level.GrashEvent_LevelLoaded;
import grash.event.events.level.GrashEvent_LevelReadyToInit;
import javafx.scene.paint.Color;

/**
 * This class is made for generating the {@link LevelMap} Class, or in other words
 * converting the loaded Strings to an actual Level
 */
public final class LevelMapGenerator implements GrashEventListener {

    private final GameController game;

    public LevelMapGenerator(GameController gameController) {
        this.game = gameController;

        game.getEventBus().registerListener(GrashEvent_LevelLoaded.class, this);
    }

    @Override
    public void onEvent(GrashEvent event) {
        switch (event.getEventKey()) {
            case "LevelLoaded": {
                onEvent_LevelLoaded((GrashEvent_LevelLoaded) event);
            }
        }
    }

    /**
     * Generating the {@link LevelMap} from the MapData, or in other words, make dumb strings to cool stuff
     */
    private void onEvent_LevelLoaded(GrashEvent_LevelLoaded event) {
        System.out.println("LevelLoaded event came to LevelMapGenerator");

        LevelMap generatedLevelMap = generatedLevelMapFromMapData(event.getMapData());

        game.getEventBus().triggerEvent(new GrashEvent_LevelReadyToInit(generatedLevelMap));
    }

    private LevelMap generatedLevelMapFromMapData(MapData mapData) {
        try {
             return new LevelMap(
                    mapData.getMapMetadata(),

                    Double.parseDouble(mapData.getSpeed()[0][0]),
                    Double.parseDouble(mapData.getGrowspeed()[0][0]),
                    new Color(Double.parseDouble(mapData.getStartcolor()[0][0]) / 255.0,
                              Double.parseDouble(mapData.getStartcolor()[0][1]) / 255.0,
                              Double.parseDouble(mapData.getStartcolor()[0][2]) / 255.0,
                              1.0),
                    Double.parseDouble(mapData.getStartfovscale()[0][0]),
                    Double.parseDouble(mapData.getStartrotation()[0][0]),

                    convertSpikes(mapData.getSpike()),
                    convertSlides(mapData.getSlide()),
                    convertWalls(mapData.getWall()),
                    convertDoubleJumps(mapData.getDoublejump()),
                    convertRopes(mapData.getRope()),

                    convertTapNotes(mapData.getTapnote()),
                    convertGrowNotes(mapData.getGrownote()),
                    convertSlideNotes(mapData.getSlidenote()),

                    convertColors(mapData.getColor()),
                    convertFOVScale(mapData.getFovscale()),
                    convertRotates(mapData.getRotate()),

                    new LevelMapEffect[0],
                    convertLaserShows(mapData.getLasershow())
            );
        } catch(Exception e) {
            System.out.println("The Map \"" + mapData.getMapMetadata().getMapKey() + "\" contains errors");
        }

        return null;
    }

    /**
     * Converts the "spikes" String to a {@link LevelMapElement} Array
     */
    private LevelMapElement[] convertSpikes(String[][] spikesString) throws NumberFormatException {
        if(spikesString == null) return new LevelMapElement[0];
        LevelMapElement[] convertedSpikes = new LevelMapElement[spikesString.length];

        for(int i = 0; i < spikesString.length; i++) {
            convertedSpikes[i] = new LevelMapElement(MapElementType.Spike);
            convertedSpikes[i].setIsUp(Boolean.parseBoolean(spikesString[i][0]));
            convertedSpikes[i].setTimeStart(Double.parseDouble(spikesString[i][1]));
            convertedSpikes[i].setTimeEnd(Double.parseDouble(spikesString[i][2]));
        }

        return convertedSpikes;
    }

    private LevelMapElement[] convertSlides(String[][] slidesString) throws NumberFormatException {
        if(slidesString == null) return new LevelMapElement[0];
        LevelMapElement[] convertedSlides = new LevelMapElement[slidesString.length];

        for(int i = 0; i < slidesString.length; i++) {
            convertedSlides[i] = new LevelMapElement(MapElementType.Slide);
            convertedSlides[i].setIsUp(Boolean.parseBoolean(slidesString[i][0]));
            convertedSlides[i].setTimeStart(Double.parseDouble(slidesString[i][1]));
            convertedSlides[i].setTimeEnd(Double.parseDouble(slidesString[i][2]));
        }

        return convertedSlides;
    }

    private LevelMapElement[] convertWalls(String[][] wallsString) throws NumberFormatException {
        if(wallsString == null) return new LevelMapElement[0];
        LevelMapElement[] convertedWalls = new LevelMapElement[wallsString.length];

        for(int i = 0; i < wallsString.length; i++) {
            convertedWalls[i] = new LevelMapElement(MapElementType.Wall);
            convertedWalls[i].setIsUp(Boolean.parseBoolean(wallsString[i][0]));
            convertedWalls[i].setTimeStart(Double.parseDouble(wallsString[i][1]));
        }

        return convertedWalls;
    }

    private LevelMapElement[] convertDoubleJumps(String[][] doubleJumpsString) throws NumberFormatException {
        if(doubleJumpsString == null) return new LevelMapElement[0];
        LevelMapElement[] convertedDoubleJumps = new LevelMapElement[doubleJumpsString.length];

        for(int i = 0; i < doubleJumpsString.length; i++) {
            convertedDoubleJumps[i] = new LevelMapElement(MapElementType.DoubleJump);
            convertedDoubleJumps[i].setHeightNormalized(Double.parseDouble(doubleJumpsString[i][0]));
            convertedDoubleJumps[i].setTimeStart(Double.parseDouble(doubleJumpsString[i][1]));
        }

        return convertedDoubleJumps;
    }

    private LevelMapElement[] convertRopes(String[][] ropesString) throws NumberFormatException {
        if(ropesString == null) return new LevelMapElement[0];
        LevelMapElement[] convertedRopes = new LevelMapElement[ropesString.length];

        for(int i = 0; i < ropesString.length; i++) {
            convertedRopes[i] = new LevelMapElement(MapElementType.Rope);
            convertedRopes[i].setHeightNormalized(Double.parseDouble(ropesString[i][0]));
            convertedRopes[i].setTimeStart(Double.parseDouble(ropesString[i][1]));
            convertedRopes[i].setTimeEnd(Double.parseDouble(ropesString[i][2]));
        }

        return convertedRopes;
    }


    private LevelMapNote[] convertTapNotes(String[][] tapNotesString) throws NumberFormatException {
        if(tapNotesString == null) return new LevelMapNote[0];
        LevelMapNote[] convertedTapNotes = new LevelMapNote[tapNotesString.length];

        for (int i = 0; i < tapNotesString.length; i++) {
            convertedTapNotes[i] = new LevelMapNote(MapNoteType.TapNote);
            convertedTapNotes[i].setIsVertical(true);
            convertedTapNotes[i].setYType(Byte.parseByte(tapNotesString[i][0]));
            convertedTapNotes[i].setTimeStart(Double.parseDouble(tapNotesString[i][1]));
        }

        return convertedTapNotes;
    }

    private LevelMapNote[] convertGrowNotes(String[][] growNotesString) throws NumberFormatException {
        if(growNotesString == null) return new LevelMapNote[0];
        LevelMapNote[] convertedGrowNotes = new LevelMapNote[growNotesString.length];

        for (int i = 0; i < growNotesString.length; i++) {
            convertedGrowNotes[i] = new LevelMapNote(MapNoteType.GrowNote);
            convertedGrowNotes[i].setIsVertical(false);
            convertedGrowNotes[i].setIsLeft(Boolean.parseBoolean(growNotesString[i][0]));
            convertedGrowNotes[i].setTimeStart(Double.parseDouble(growNotesString[i][1]));
        }

        return convertedGrowNotes;
    }

    private LevelMapNote[] convertSlideNotes(String[][] slideNotesString) throws NumberFormatException {
        if(slideNotesString == null) return new LevelMapNote[0];
        LevelMapNote[] convertedSlideNotes = new LevelMapNote[slideNotesString.length];

        for (int i = 0; i < slideNotesString.length; i++) {
            convertedSlideNotes[i] = new LevelMapNote(MapNoteType.SlideNote);
            convertedSlideNotes[i].setIsVertical(true);
            convertedSlideNotes[i].setYType(Byte.parseByte(slideNotesString[i][0]));
            convertedSlideNotes[i].setTimeStart(Double.parseDouble(slideNotesString[i][1]));
            convertedSlideNotes[i].setTimeEnd(Double.parseDouble(slideNotesString[i][2]));
        }

        return convertedSlideNotes;
    }


    private LevelMapEffect[] convertColors(String[][] colorsString) throws IllegalArgumentException {
        if(colorsString == null) return new LevelMapEffect[0];
        LevelMapEffect[] convertedColors = new LevelMapEffect[colorsString.length];

        for (int i = 0; i < colorsString.length; i++) {
            convertedColors[i] = new LevelMapEffect(MapEffectType.Color);
            convertedColors[i].setColor(new Color(
                    Double.parseDouble(colorsString[i][0]) / 255.0,
                    Double.parseDouble(colorsString[i][1]) / 255.0,
                    Double.parseDouble(colorsString[i][2]) / 255.0,
                    1.0
            ));
            convertedColors[i].setTimeStart(Double.parseDouble(colorsString[i][3]));
        }

        return convertedColors;
    }

    private LevelMapEffect[] convertFOVScale(String[][] fovScalesString) throws NumberFormatException {
        if(fovScalesString == null) return new LevelMapEffect[0];
        LevelMapEffect[] convertedFOVScales = new LevelMapEffect[fovScalesString.length];

        for (int i = 0; i < fovScalesString.length; i++) {
            convertedFOVScales[i] = new LevelMapEffect(MapEffectType.FOVScale);
            convertedFOVScales[i].setValueDouble(Double.parseDouble(fovScalesString[i][0]));
            convertedFOVScales[i].setTimeStart(Double.parseDouble(fovScalesString[i][1]));
        }

        return convertedFOVScales;
    }

    private LevelMapEffect[] convertRotates(String[][] rotatesString) throws NumberFormatException {
        if(rotatesString == null) return new LevelMapEffect[0];
        LevelMapEffect[] convertedRotates = new LevelMapEffect[rotatesString.length];

        for(int i = 0; i < rotatesString.length; i++) {
            convertedRotates[i] = new LevelMapEffect(MapEffectType.Rotate);
            convertedRotates[i].setValueDouble(Double.parseDouble(rotatesString[i][0]));
            convertedRotates[i].setTimeStart(Double.parseDouble(rotatesString[i][1]));
        }

        return convertedRotates;
    }

    private LevelMapEffect[] convertLaserShows(String[][] laserShowsString) throws NumberFormatException {
        if(laserShowsString == null) return new LevelMapEffect[0];
        LevelMapEffect[] convertedLaserShows = new LevelMapEffect[laserShowsString.length];

        for(int i = 0; i < laserShowsString.length; i++) {
            convertedLaserShows[i] = new LevelMapEffect(MapEffectType.LaserShow);
            convertedLaserShows[i].setValueDouble(Double.parseDouble(laserShowsString[i][0]));
            convertedLaserShows[i].setTimeStart(Double.parseDouble(laserShowsString[i][1]));
        }

        return convertedLaserShows;
    }
}
