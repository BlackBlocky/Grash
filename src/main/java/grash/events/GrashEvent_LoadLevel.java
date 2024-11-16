package grash.events;

public class GrashEvent_LoadLevel extends GrashEvent {

    String levelKey;

    public GrashEvent_LoadLevel(String levelKey) {
        super("LoadLevel");
        this.levelKey = levelKey;
    }
}
