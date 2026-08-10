package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record HardwareUpgradeRequest(
    @JsonPropertyDescription("System model or chassis name (e.g. 'Acer Nitro AN515-58' or 'Custom ATX Case')")
    String systemModel,
    @JsonPropertyDescription("Processor model (e.g. 'i7-12650H')")
    String cpu,
    @JsonPropertyDescription("Motherboard model or chipset details")
    String motherboard
) {}
