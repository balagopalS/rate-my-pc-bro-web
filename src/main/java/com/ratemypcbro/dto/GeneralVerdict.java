package com.ratemypcbro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
// we modified the verdict classes to match the new outputs we want to have 
// more granular detail in the response.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralVerdict {
    private Double rating;
    private String verdict;
    private String review;
    
    private HardwareBreakdown breakdown;
    private PcSpecs reflectedSpecs;
    private List<String> recommendations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HardwareBreakdown {
        private Integer cpuScore;
        private Integer gpuScore;
        private Integer ramScore;
        private String estimatedPerformance; 
    }
}
