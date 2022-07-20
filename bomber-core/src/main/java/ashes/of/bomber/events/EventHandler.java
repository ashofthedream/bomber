package ashes.of.bomber.events;

import java.util.function.Consumer;

public interface EventHandler {
    <E> EventHandler handle(Class<E> cls, Consumer<E> handler);
}
