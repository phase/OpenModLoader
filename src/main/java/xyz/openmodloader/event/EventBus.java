package xyz.openmodloader.event;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A bus for posting events to and registering event listeners.
 *
 * @see xyz.openmodloader.OpenModLoader#getEventBus()
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
     * Registers all the methods of the given object that take a single parameter that extends {@link Event} and have {@link EventHandler}
     * @param object
     */
    public void register(Object object) {
        for (Method m : object.getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(EventHandler.class)) {
                Parameter[] params = m.getParameters();
                if (params.length == 1 && Event.class.isAssignableFrom(params[0].getType())) {
                    m.setAccessible(true);
                    register((Class<? extends Event>)params[0].getType(), (e) -> {
                        try {
                            m.invoke(object, e);
                        } catch (ReflectiveOperationException ex) {
                            ex.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    /**
     * Posts an event to the event bus, iterating over the registered listeners
     * until A) the event is canceled or B) all handlers have executed the event.
     *
     * @param event The event to post to the bus
     * @return {@code true} if the event fired successfully or {@code false} if it was canceled
     */
    public <T extends Event> boolean post(T event) {
        Class<? extends Event> clazz = event.getClass();
        if (map.containsKey(clazz)) {
            List<EventExecutor<T>> handlers = (List<EventExecutor<T>>) (List<?>) map.get(clazz);
            for (EventExecutor<T> handler : handlers) {
                handler.execute(event);
                if (event.isCanceled()) {
                    return false;
                }
            }
        }
        return true;
    }
}
