package grash.level;

import grash.level.map.LevelMap;
import grash.level.map.LevelMapThing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class LevelMapTimeline {
    private LevelMapTimelineStack[] timelineFirstToLast;

    public LevelMapTimeline(LevelMap levelMap) {
        LevelMapThing[] allLevelMapThings = putAllMapThingsToOneArray(levelMap);

        // Sort the Array that contains all the LevelMapThings by the start time from 0 to n
        Arrays.sort(allLevelMapThings, Comparator.comparingDouble(LevelMapThing::getTimeStart));

        timelineFirstToLast = generateTimelineElementsBySortedLevelMapThings(allLevelMapThings);
    }

    public LevelMapTimelineStack getTimelineStack(int index) {
        if(timelineFirstToLast.length - 1 < index) return null;
        return timelineFirstToLast[index];
    }

    public LevelMapTimelineStack getNextTimelineStack(int index) {
        if(timelineFirstToLast.length - 1 < index + 1) return null;
        return timelineFirstToLast[index];
    }

    /**
     * Takes every Parameter from the LevelMap that contains LevelMapThings and shoots all of them into one single array
     */
    private LevelMapThing[] putAllMapThingsToOneArray(LevelMap levelMap) {
        List<LevelMapThing> allLevelMapThings = new ArrayList<>();

        allLevelMapThings.addAll(Arrays.asList(levelMap.getSpikes()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getSlides()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getWalls()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getDoubleJumps()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getRopes()));

        allLevelMapThings.addAll(Arrays.asList(levelMap.getTapNotes()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getGrowNotes()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getSlideNotes()));

        allLevelMapThings.addAll(Arrays.asList(levelMap.getColors()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getFovScales()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getRotates()));

        //TODO allLevelMapThings.addAll(Arrays.asList(levelMap.getbImages()));
        allLevelMapThings.addAll(Arrays.asList(levelMap.getLasershows()));

        return allLevelMapThings.toArray(new LevelMapThing[0]);
    }

    /**
     * Works through the Array from 0 to n. The Function takes the StartTime and checks,
     * if there is another MapThing with the same StartTime next to it:
     * If this is the case, add the Element to the current Stack, and go on.
     * If this isn't the case anymore, go to the next Element.
     */
    private LevelMapTimelineStack[] generateTimelineElementsBySortedLevelMapThings(LevelMapThing[] levelMapThingsSorted) {
        List<LevelMapTimelineStack> stacks = new ArrayList<>();

        double currentTime = Double.MIN_VALUE;
        List<LevelMapThing> currentTimestampCollector = new ArrayList<>();
        for(LevelMapThing thingI : levelMapThingsSorted) {
            if(currentTime < thingI.getTimeStart()) {
                /* Moving the already Collected MapThing from the last Period to a new TimeStamp
                And Making sure with the if statement that the first Iteration doesn't do anything,
                because it doesn't collect anything at this point. */
                if(currentTime != Double.MIN_VALUE) stacks.add(createStackAndClearList(currentTimestampCollector, currentTime));

                // Setting up for the next Iteration
                currentTime = thingI.getTimeStart();
                currentTimestampCollector.clear();
            }

            currentTimestampCollector.add(thingI);
        }
        // Adding the Last Stack to the Stacks List
        stacks.add(createStackAndClearList(currentTimestampCollector, currentTime));

        return stacks.toArray(new LevelMapTimelineStack[0]);
    }

    /**
     * This function just creates a LevelMapTimelineStack Element with the given Parameters and clears the List
     * after it is done (ITS ONLY INTENDED TO BE USED BY generateTimelineElementsBySortedLevelMapThings())
     */
    private LevelMapTimelineStack createStackAndClearList(List<LevelMapThing> stacksElements, double stackTime) {
        return new LevelMapTimelineStack(stacksElements.toArray(new LevelMapThing[0]), stackTime);
    }
}
