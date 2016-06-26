package xyz.openmodloader.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A bus for posting events to and registering event listeners.
 *
 * @see xyz.openmodloader.OpenModLoader#EVENT_BUS
 */
public class EventBus {
    private final ConcurrentHashMap<Class<? extends Event>, List<EventExecutor<?>>> map = new ConcurrentHashMap<>();

    /**
     * Registers a handler for the given event type.
     *
     * @param clazz   The event class.
     * @param handler The event handler.
     * @param <T>     The event type.
     */
    public <T extends Event> void register(Class<T> clazz, EventExecutor<T> handler) {
        if (!map.containsKey(clazz)) {
            map.put(clazz, new ArrayList<>());
        }
        map.get(clazz).add(handler);
    }

    /**
     * Posts an event to the event bus, iterating over the registered listeners
     * until A) the event is canceled or B) all handlers have executed the event.
     *
     * @param event The event to post to the bus
     * @return {@code true} if the event fired successfully or {@code false} if it was canceled
     */
    public boolean post(Event event) {
        Class<? extends Event> clazz = event.getClass();
        if (map.containsKey(clazz)) {
            List<EventExecutor<?>> handlers = map.get(clazz);
            for (EventExecutor handler : handlers) {
                handler.execute(event);
                if (event.isCanceled()) {
                    return false;
                }
            }
        }
        return true;
    }
}
