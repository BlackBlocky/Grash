package grash.assets;

public final class MapData {

    private final MapMetadata mapMetadata;

    private final String[][] speed;
    private final String[][] growspeed;
    private final String[][] startcolor;
    private final String[][] startfovscale;
    private final String[][] startrotation;

    private final String[][] spike;
    private final String[][] slide;
    private final String[][] wall;
    private final String[][] doublejump;
    private final String[][] rope;

    private final String[][] tapnote;
    private final String[][] grownnote;
    private final String[][] slidenote;

    private final String[][] color;
    private final String[][] fovscale;
    private final String[][] rotate;

    private final String[][] bimage;
    private final String[][] lasershow;

    public MapData(MapMetadata mapMetadata,
                   String[][] speed,
                   String[][] growspeed,
                   String[][] startcolor,
                   String[][] startfovscale,
                   String[][] startrotation,
                   String[][] spike,
                   String[][] slide,
                   String[][] wall,
                   String[][] doublejump,
                   String[][] rope,
                   String[][] tapnote,
                   String[][] grownnote,
                   String[][] slidenote,
                   String[][] color,
                   String[][] fovscale,
                   String[][] rotate,
                   String[][] bimage,
                   String[][] lasershow) {
        this.mapMetadata = mapMetadata;

        this.speed = speed;
        this.growspeed = growspeed;
        this.startcolor = startcolor;
        this.startfovscale = startfovscale;
        this.startrotation = startrotation;

        this.spike = spike;
        this.slide = slide;
        this.wall = wall;
        this.doublejump = doublejump;
        this.rope = rope;

        this.tapnote = tapnote;
        this.grownnote = grownnote;
        this.slidenote = slidenote;

        this.color = color;
        this.fovscale = fovscale;
        this.rotate = rotate;

        this.bimage = bimage;
        this.lasershow = lasershow;
    }

    public MapMetadata getMapMetadata() {
        return mapMetadata;
    }

    public String[][] getSpeed() {
        return speed;
    }

    public String[][] getGrowspeed() {
        return growspeed;
    }

    public String[][] getStartcolor() {
        return startcolor;
    }

    public String[][] getStartfovscale() {
        return startfovscale;
    }

    public String[][] getStartrotation() {
        return startrotation;
    }

    public String[][] getSpike() {
        return spike;
    }

    public String[][] getSlide() {
        return slide;
    }

    public String[][] getWall() {
        return wall;
    }

    public String[][] getDoublejump() {
        return doublejump;
    }

    public String[][] getRope() {
        return rope;
    }

    public String[][] getTapnote() {
        return tapnote;
    }

    public String[][] getGrownnote() {
        return grownnote;
    }

    public String[][] getSlidenote() {
        return slidenote;
    }

    public String[][] getColor() {
        return color;
    }

    public String[][] getFovscale() {
        return fovscale;
    }

    public String[][] getRotate() {
        return rotate;
    }

    public String[][] getBimage() {
        return bimage;
    }

    public String[][] getLasershow() {
        return lasershow;
    }
}
