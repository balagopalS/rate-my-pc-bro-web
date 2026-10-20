package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record SoftwareRequirementsRequest(
    @JsonPropertyDescription("The exact name of the game or software application (e.g. 'GTA 6' or 'Cyberpunk 2077')")
    String softwareName
) {}
