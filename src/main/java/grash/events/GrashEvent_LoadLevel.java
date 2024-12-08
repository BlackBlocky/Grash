package grash.events;

/**
 * This Event is Triggered when the Game Starts
 * <br>Triggered by:
 * {@link grash.ui.WelcomeScreenController}
 *
 *  <br>Received by:
 * {@link grash.assets.ResourceLoader}
 *
 */

public final class GrashEvent_LoadLevel extends GrashEvent {

    private final String levelKey;

    public GrashEvent_LoadLevel(String levelKey) {
        super("LoadLevel");
        this.levelKey = levelKey;
    }

    public String getLevelKey() {return this.levelKey; }
}
