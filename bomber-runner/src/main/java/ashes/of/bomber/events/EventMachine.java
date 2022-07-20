package ashes.of.bomber.events;

import ashes.of.bomber.threads.BomberThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EventMachine implements EventDispatcher, EventHandler {
    private static final Logger log = LogManager.getLogger();

    private final Executor executor;
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new ConcurrentHashMap<>();
    private final Consumer<Object> empty = event -> log.debug("No handler found for event: {}", event.getClass());

    public EventMachine() {
        this.executor = Executors.newFixedThreadPool(1, BomberThreadFactory.SINK_FACTORY);
    }

    @Override
    public <E> EventMachine handle(Class<E> cls, Consumer<E> handler) {
        var handlers = this.handlers.computeIfAbsent(cls, key -> new CopyOnWriteArrayList<>());
        handlers.add( (Consumer<Object>) handler);
        return this;
    }

    @Override
    public <E> EventMachine dispatch(E event) {
        var handlers = this.handlers.getOrDefault(event.getClass(), List.of(empty));
        executor.execute(() -> propagate(event, handlers));
        return this;
    }

    private void propagate(Object event, List<Consumer<Object>> handlers) {
        handlers.forEach(handler -> {
            try {
                handler.accept(event);
            } catch (Exception t) {
                log.warn("Can't handle event", t);
            }
        });
    }
}
