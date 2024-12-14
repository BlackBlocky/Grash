package grash.level.map;

import grash.assets.MapData;
import grash.core.GameController;
import grash.events.GrashEvent;
import grash.events.GrashEventListener;
import grash.events.GrashEvent_LevelLoaded;
import grash.events.GrashEvent_LevelReadyToInit;

/**
 * This class is made for generating the {@link LevelMap} Class, or in other words
 * converting the loaded Strings to an actual Level
 */
public class LevelMapGenerator implements GrashEventListener {

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
                    null,
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

                    null,
                    null,
                    null,

                    null,
                    null
            );
        } catch(NumberFormatException numberFormatException) {
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
            convertedSpikes[i] = new LevelMapElement();
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
            convertedSlides[i] = new LevelMapElement();
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
            convertedWalls[i] = new LevelMapElement();
            convertedWalls[i].setIsUp(Boolean.parseBoolean(wallsString[i][0]));
            convertedWalls[i].setTimeStart(Double.parseDouble(wallsString[i][1]));
        }

        return convertedWalls;
    }

    private LevelMapElement[] convertDoubleJumps(String[][] doubleJumpsString) throws NumberFormatException {
        if(doubleJumpsString == null) return new LevelMapElement[0];
        LevelMapElement[] convertedDoubleJumps = new LevelMapElement[doubleJumpsString.length];

        for(int i = 0; i < doubleJumpsString.length; i++) {
            convertedDoubleJumps[i] = new LevelMapElement();
            convertedDoubleJumps[i].setHeightNormalized(Double.parseDouble(doubleJumpsString[i][0]));
            convertedDoubleJumps[i].setTimeStart(Double.parseDouble(doubleJumpsString[i][1]));
        }

        return convertedDoubleJumps;
    }

    private LevelMapElement[] convertRopes(String[][] ropesString) throws NumberFormatException {
        if(ropesString == null) return new LevelMapElement[0];
        LevelMapElement[] convertedRopes = new LevelMapElement[ropesString.length];

        for(int i = 0; i < ropesString.length; i++) {
            convertedRopes[i] = new LevelMapElement();
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
            convertedTapNotes[i] = new LevelMapNote();
            convertedTapNotes[i].setYType(Byte.parseByte(tapNotesString[i][0]));
            convertedTapNotes[i].setTimeStart(Double.parseDouble(tapNotesString[i][1]));
        }

        return convertedTapNotes;
    }

    private LevelMapNote[] convertGrowNotes(String[][] growNotesString) throws NumberFormatException {
        if(growNotesString == null) return new LevelMapNote[0];
        LevelMapNote[] convertedGrowNotes = new LevelMapNote[growNotesString.length];

        for (int i = 0; i < growNotesString.length; i++) {
            convertedGrowNotes[i] = new LevelMapNote();
            convertedGrowNotes[i].setIsLeft(Boolean.parseBoolean(growNotesString[i][0]));
            convertedGrowNotes[i].setTimeStart(Double.parseDouble(growNotesString[i][1]));
        }

        return convertedGrowNotes;
    }

    private LevelMapNote[] convertSlideNotes(String[][] slideNotesString) throws NumberFormatException {
        if(slideNotesString == null) return new LevelMapNote[0];
        LevelMapNote[] convertedSlideNotes = new LevelMapNote[slideNotesString.length];

        for (int i = 0; i < slideNotesString.length; i++) {
            convertedSlideNotes[i] = new LevelMapNote();
            convertedSlideNotes[i].setYType(Byte.parseByte(slideNotesString[i][0]));
            convertedSlideNotes[i].setTimeStart(Double.parseDouble(slideNotesString[i][1]));
            convertedSlideNotes[i].setTimeEnd(Double.parseDouble(slideNotesString[i][2]));
        }

        return convertedSlideNotes;
    }

}
