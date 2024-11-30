package grash.events;

public final class GrashEvent_LoadLevel extends GrashEvent {

    private final String levelKey;

    public GrashEvent_LoadLevel(String levelKey) {
        super("LoadLevel");
        this.levelKey = levelKey;
    }

    public String getLevelKey() {return this.levelKey; }
}
