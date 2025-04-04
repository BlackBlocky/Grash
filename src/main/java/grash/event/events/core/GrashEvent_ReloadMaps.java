package grash.event.events.core;

import grash.event.GrashEvent;

/**
 * When this event is triggered, all the Map Meta-Data are going to be reloaded
 * <br>Triggered by:
 * {@link grash.ui.EditorSelectorScreenController}
 *
 *  <br>Received by:
 *  {@link grash.assets.ResourceLoader}
 *
 */

public class GrashEvent_ReloadMaps extends GrashEvent {

    public GrashEvent_ReloadMaps() {
        super("ReloadMaps");
    }
}
