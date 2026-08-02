package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SoftwareVerdict {
    private String software;
    private String score;
    private String verdict;
    private String performance_notes;
    
    private FpsEstimates fpsEstimates;
    private String recommendedSettings;
    private List<String> potentialBottlenecks;
    private String detailedAnalysis;

    // Convenience 4-arg constructor for simple fallback initialization & legacy test mocks
    // TODO: Standardize all mock instantiations across unit tests to use Lombok @Builder pattern
    public SoftwareVerdict(String software, String score, String verdict, String performance_notes) {
        this.software = software;
        this.score = score;
        this.verdict = verdict;
        this.performance_notes = performance_notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FpsEstimates {
        private String fhd_1080p;
        private String qhd_1440p;
        private String uhd_4k;
    }
}
