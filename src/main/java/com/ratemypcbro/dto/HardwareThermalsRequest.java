package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record HardwareThermalsRequest(
    @JsonPropertyDescription("The system model name (e.g. 'Acer Nitro AN515-58' or 'Custom Desktop')")
    String systemModel,
    @JsonPropertyDescription("Processor model (e.g. 'i7-12650H')")
    String cpu,
    @JsonPropertyDescription("Graphics card model (e.g. 'RTX 3070 Ti Laptop')")
    String gpu
) {}
