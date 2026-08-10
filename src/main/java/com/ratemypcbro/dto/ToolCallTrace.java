package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ToolCallTrace(
    int step,
    String toolName,
    String query,
    String searchDepth,
    List<String> includeDomains,
    String summary,
    long durationMs,
    String timestamp
) {}
