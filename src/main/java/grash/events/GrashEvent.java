package grash.events;

public abstract class GrashEvent {
    private final String eventKey;

    public GrashEvent(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getEventKey() {
        return eventKey;
    }
}
