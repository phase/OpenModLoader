package xyz.openmodloader.event;

@FunctionalInterface
public interface EventExecutor<T extends Event> {
    void execute(T event);
}
