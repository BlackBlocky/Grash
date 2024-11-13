package grash.events;

public class GrashEvent_Initialize extends GrashEvent {
    public String test = "";
    public GrashEvent_Initialize(String t) {
        super("Initialize");
        this.test = t;
    }
}
