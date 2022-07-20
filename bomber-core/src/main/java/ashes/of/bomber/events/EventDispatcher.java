package ashes.of.bomber.events;

public interface EventDispatcher {
    <E> EventDispatcher dispatch(E event);
}
