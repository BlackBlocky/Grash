package grash.action.renderer;

import grash.level.map.LevelMapEffect;

public final class RendererEffectData {
    private LevelMapEffect currentEffect;
    private LevelMapEffect nextEffect;

    private double timeBetweenBothEffects;

    public RendererEffectData(LevelMapEffect startEffect) {
        this.currentEffect = startEffect;
        this.nextEffect = startEffect;

        if(startEffect != null) recalculate();
    }

    public void recalculate() {
        timeBetweenBothEffects = nextEffect.getTimeStart() - currentEffect.getTimeStart();
    }

    /**
     * Interpolates the time between the current Effect with the next Effect by give current Time.
     * Example, if we are 1 sec between this transition that goes for 5 seconds, the return value would be around 0.2.
     * !Returns 1.0 if the time between both effects is 0.0.
     */
    public double interpolateTime(double secondsElapsedSinceStart) {
        double progressSeconds =  secondsElapsedSinceStart - currentEffect.getTimeStart();
        if(timeBetweenBothEffects == 0.0) return 1.0;
        else return progressSeconds / timeBetweenBothEffects;
    }

    /**
     * Takes the secondsElapsedSinceStart and returns a value between both effects based by the current time process.
     */
    public double getValueBetweenBothEffectsByTime(double secondsElapsedSinceStart) {
        double normalValue = interpolateTime(secondsElapsedSinceStart);
        return valueBetweenMinMax(currentEffect.getValueDouble(), nextEffect.getValueDouble(), normalValue);
    }

    /**
     * Returns a value between min and max based on the normalized Value from v
     */
    public static double valueBetweenMinMax(double min, double max, double v) {
        return (max - min) * v + min;
    }

    public double getTimeBetweenBothEffects() {
        return timeBetweenBothEffects;
    }

    public LevelMapEffect getCurrentEffect() {
        return currentEffect;
    }

    public void setCurrentEffect(LevelMapEffect currentEffect) {
        this.currentEffect = currentEffect;
    }

    public LevelMapEffect getNextEffect() {
        return nextEffect;
    }

    public void setNextEffect(LevelMapEffect nextEffect) {
        this.nextEffect = nextEffect;
    }
}
