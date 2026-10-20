package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record RedditSentimentRequest(
    @JsonPropertyDescription("The targeted search query for Reddit to discover subjective user takes, stuttering reports, thermal experiences, or settings tweaks (e.g. 'Valorant RTX 3070 Ti laptop FPS stuttering reddit')")
    String query
) {}
