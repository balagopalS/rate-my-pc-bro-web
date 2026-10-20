package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record HardwareBottleneckRequest(
    @JsonPropertyDescription("Processor model (e.g. 'i7-12650H')")
    String cpu,
    @JsonPropertyDescription("Graphics card model (e.g. 'RTX 3070 Ti Laptop')")
    String gpu,
    @JsonPropertyDescription("RAM capacity and speed details (e.g. '32GB DDR4 3200MHz')")
    String ramDetails
) {}
