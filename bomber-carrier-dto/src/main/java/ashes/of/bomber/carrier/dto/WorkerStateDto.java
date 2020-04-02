package ashes.of.bomber.carrier.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkerStateDto {
    private String name;
    private long iterationsCount;
    private long remainIterationsCount;
    private long errorsCount;
}
