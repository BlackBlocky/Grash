package grash.events;

import java.util.HashMap;
import java.util.HashSet;

public class EventBus {
    private final HashMap<Class<? extends Event>, HashSet<EventListener<? extends Event>>> listeners = new HashMap<>();

    public <T extends Event> void registerListener(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new HashSet<>()).add(listener);
    }

    public <T extends Event> void triggerEvent(T event) {
        HashSet<EventListener<? extends Event>> eventListeners = listeners.get(event.getClass());
        if(eventListeners == null) return;

        for (EventListener<? extends Event> activeListener : eventListeners) {
            @SuppressWarnings("unchecked")
            EventListener<T> typedEventListener = (EventListener<T>) activeListener;
            typedEventListener.onEvent(event);
        }
    }

}
