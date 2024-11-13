package grash.events;

import java.util.HashMap;
import java.util.HashSet;

public class GrashEventBus {
    private final HashMap<Class<? extends GrashEvent>, HashSet<GrashEventListener>> listeners = new HashMap<>();

    public <T extends GrashEvent> void registerListener(Class<T> eventType, GrashEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new HashSet<>()).add(listener);
    }

    public <T extends GrashEvent> void triggerEvent(T event) {
        HashSet<GrashEventListener> eventListeners = listeners.get(event.getClass());
        if(eventListeners == null) return;

        for (GrashEventListener activeListener : eventListeners) {
            activeListener.onEvent(event);
        }
    }

}
