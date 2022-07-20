package ashes.of.bomber.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventMachineTest {
    private static final Logger log = LogManager.getLogger();

    public static record FirstEvent(String payload) {}
    public static record SecondEvent(String payload) {}

    private EventMachine handler;

    @BeforeEach
    void setUp() {
        handler = new EventMachine();
    }

    @Test
    public void dispatchedEventShouldBeHandled() throws InterruptedException {
        var first = new AtomicInteger();
        var second1 = new AtomicInteger();
        var second2 = new AtomicInteger();

        handler.handle(FirstEvent.class, event -> first.incrementAndGet());
        handler.handle(SecondEvent.class, event -> second1.incrementAndGet());
        handler.handle(SecondEvent.class, event -> second2.incrementAndGet());

        handler.dispatch(new FirstEvent("First 1"));
        handler.dispatch(new FirstEvent("First 1"));
        handler.dispatch(new SecondEvent("Second 1"));

        Thread.sleep(100);

        assertEquals(2, first.get());
        assertEquals(1, second1.get());
        assertEquals(1, second2.get());
    }

    @Test
    public void dispatchedEventMayNotBeHandled() throws InterruptedException {
        var first = new AtomicInteger();

        handler.handle(FirstEvent.class, event -> first.incrementAndGet());
        handler.dispatch(new SecondEvent("Second 1"));

        Thread.sleep(100);

        assertEquals(0, first.get());
    }
}