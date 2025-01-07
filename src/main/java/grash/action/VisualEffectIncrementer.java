package grash.action;

import grash.level.map.LevelMapEffect;

/**
 * This class is supposed to keep track on which color index should be the next, and when the next element should be played.
 * This class is ONLY used by {@link grash.action.ActionPhaseVisualEffectValues}
 */
public final class VisualEffectIncrementer {
    private LevelMapEffect[] linkedEffectsArray;

    private int currentIndex;
    private int maxIndex;
    private double timeUntilNext;

    private final boolean isValid;

    public VisualEffectIncrementer(LevelMapEffect[] effectsArray) {
        // Making sure the LevelMapEffect[] is not empty
        isValid = !(effectsArray == null || effectsArray.length == 0);
        if(!isValid) return;

        // Setting up the Values
        this.linkedEffectsArray = effectsArray;
        this.currentIndex = -1;
        this.maxIndex = this.linkedEffectsArray.length - 1;
        this.timeUntilNext = this.linkedEffectsArray[0].getTimeStart();
    }

    /**
     * Checks if the time for the next Element in the given array has already come. If that is the case,
     * return the next Element. But if that isn't the case, then return null.
     */
    public LevelMapEffect getNextEffectIfReady(double time) {
        if(!isValid) return null;
        if(time < timeUntilNext) return null;
        if(currentIndex == maxIndex) return null;

        // It is time for the next Element
        currentIndex++;
        if(currentIndex + 1 <= maxIndex) timeUntilNext = linkedEffectsArray[currentIndex + 1].getTimeStart();
        return linkedEffectsArray[currentIndex];
    }

    /**
     * This Method just returns the next Element, id doesn't modify anything at all.
     * If there isn't another next element, the method just returns null.
     */
    public LevelMapEffect justGetNextEffect() {
        if(!isValid) return null;
        if(currentIndex + 1 > maxIndex) return null;

        return linkedEffectsArray[currentIndex + 1];
    }
}
