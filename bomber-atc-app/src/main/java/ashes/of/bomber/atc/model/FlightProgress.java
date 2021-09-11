package ashes.of.bomber.atc.model;

import ashes.of.bomber.carrier.dto.events.SinkEvent;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class FlightProgress {
    private final Queue<SinkEvent> events = new PriorityQueue<>(Comparator.comparingLong(SinkEvent::getTimestamp));

}
