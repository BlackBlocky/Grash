package grash.level;

import grash.assets.MapData;
import grash.core.GameController;
import grash.events.GrashEvent;
import grash.events.GrashEventListener;
import grash.events.GrashEvent_LevelLoaded;

/**
 * This class is made for generating the {@link grash.level.LevelMap} Class, or in other words
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
     * Generating the {@link grash.level.LevelMap} from the MapData, or in other words, make dumb strings to cool stuff
     */
    private void onEvent_LevelLoaded(GrashEvent_LevelLoaded event) {
        System.out.println("LevelLoaded event came to LevelMapGenerator");

        LevelMap generatedLevelMap = generatedLevelMapFromMapData(event.getMapData());


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
                    null,
                    null,
                    null,

                    null,
                    null,
                    null,

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
     * Converts the "spikes" String to a {@link grash.level.LevelMapElement} Array
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

}
