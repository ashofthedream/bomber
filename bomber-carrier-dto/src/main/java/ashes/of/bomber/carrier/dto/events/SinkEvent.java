package ashes.of.bomber.carrier.dto.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SinkEvent {
    private long timestamp;
    private SinkEventType type;
    private long flightId;
    private String carrierId;

    private String stage;

    @Nullable
    private String testSuite;

    @Nullable
    private String testCase;
}
