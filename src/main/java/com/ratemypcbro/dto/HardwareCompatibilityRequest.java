package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record HardwareCompatibilityRequest(
    @JsonPropertyDescription("Processor model (e.g. 'i7-12650H')")
    String cpu,
    @JsonPropertyDescription("Graphics card model (e.g. 'RTX 3070 Ti Laptop')")
    String gpu,
    @JsonPropertyDescription("Target game or software name (e.g. 'Cyberpunk 2077')")
    String softwareName
) {}
