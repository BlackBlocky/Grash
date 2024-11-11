package piggybeat.events;

import piggybeat.core.GameController;

public interface EventListener<T extends Event> {
    void onEvent(T event);
}
