package ashes.of.bomber.configuration;

import java.time.Duration;


/**
 * Runner settings
 */
public record Settings(Duration duration, int threads, long iterations) {
}
