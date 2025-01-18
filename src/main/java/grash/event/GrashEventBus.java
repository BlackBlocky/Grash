package grash.event;

import grash.event.events.core.GrashEvent_Tick;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public final class GrashEventBus {
    private final HashMap<Class<? extends GrashEvent>, HashSet<GrashEventListener>> listeners = new HashMap<>();

    private final LinkedList<GrashEvent> eventQueue = new LinkedList<>();
    private boolean isAlreadyProcessing = false;

    public <T extends GrashEvent> void registerListener(Class<T> eventType, GrashEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new HashSet<>()).add(listener);
    }

    public <T extends GrashEvent> void triggerEvent(T event) {
        eventQueue.addLast(event);
        if(isAlreadyProcessing) return;

        isAlreadyProcessing = true;
        do {
            GrashEvent activeEvent = eventQueue.getFirst();
            /*if(activeEvent.getClass() != GrashEvent_Tick.class) {
                System.out.println("Doing Event: " + activeEvent.getClass().getName());
            }*/

            HashSet<GrashEventListener> eventListeners = listeners.get(activeEvent.getClass());
            if(eventListeners != null) {
                processEvent(activeEvent, eventListeners);
                eventQueue.remove(activeEvent);
            }
            else {
                eventQueue.remove(activeEvent);
            }

            if(eventQueue.isEmpty()) isAlreadyProcessing = false;

        } while(!eventQueue.isEmpty());
    }

    private <T extends GrashEvent> void processEvent(T event, HashSet<GrashEventListener> eventListeners ) {
        for (GrashEventListener activeListener : eventListeners) {
            activeListener.onEvent(event);
        }
    }

}
