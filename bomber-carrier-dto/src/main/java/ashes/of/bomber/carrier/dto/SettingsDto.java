package ashes.of.bomber.carrier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDto {
    private boolean disabled;
    private long duration;
    private int threadsCount;
    private long threadIterationsCount;
    private long totalIterationsCount;
}
