package grash.action;

import grash.level.map.LevelMap;
import grash.level.map.LevelMapEffect;


public final class ActionPhaseVisualEffectValues {

    private final VisualEffectIncrementer colorIncrementer;
    private LevelMapEffect currentColorEffect;

    public ActionPhaseVisualEffectValues(LevelMap levelMap) {
        colorIncrementer = new VisualEffectIncrementer(levelMap.getColors());
    }

    /**
     * Returns the current color. If it is time to switch to next Color, the VisualEffectIncrementer
     * switches automatically to the next Element, and so returns the next Color.
     * If there is no next Color, it just returns the last color in the List.
     */
    public LevelMapEffect getCurrentColor(double time) {
        LevelMapEffect nextColorElement = colorIncrementer.getNextEffectIfReady(time);
        if(nextColorElement != null) {
            currentColorEffect = nextColorElement;
        }

        return this.currentColorEffect;
    }
    /**
     * Returns the next Color, and doesn't modify anything at all.
     * If there is no next Color left, it just returns null.
     */
    public LevelMapEffect getNextColor() {
        return colorIncrementer.justGetNextEffect();
    }


}
