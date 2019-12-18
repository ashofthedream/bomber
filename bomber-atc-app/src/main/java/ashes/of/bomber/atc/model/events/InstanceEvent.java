package ashes.of.bomber.atc.model.events;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.time.LocalDateTime;

public class InstanceEvent {


    @Deprecated
    private final String raw;

    public final LocalDateTime anus = LocalDateTime.now();
    private final Instant time = Instant.now();

    public InstanceEvent(String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    public Instant getTime() {
        return time;
    }
}
