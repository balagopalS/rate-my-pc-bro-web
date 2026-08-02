package com.ratemypcbro.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import java.util.List;

/**
 * Rich Tavily AI Search request object exposed to the AI Inference Engine.
 */
public record SearchRequest(
    @JsonPropertyDescription("The targeted search query string. Keep concise and focused (e.g., 'GTA 6 PS5 equivalent performance RTX 3070 Ti' or 'Cyberpunk 2077 1440p DLSS FPS reddit').")
    String query,

    @JsonPropertyDescription("Search depth strategy: 'basic' (default) for quick surface checks, or 'advanced' for deep technical RAG crawling.")
    String searchDepth,

    @JsonPropertyDescription("Optional high-authority target domains (e.g. ['techpowerup.com', 'notebookcheck.net', 'reddit.com', 'pcgamingwiki.com']).")
    List<String> includeDomains,

    @JsonPropertyDescription("Optional low-quality domains to exclude (e.g. ['userbenchmark.com']).")
    List<String> excludeDomains,

    @JsonPropertyDescription("Search topic category: 'general' (default) for hardware & game benchmarks, or 'news' for recent announcements.")
    String topic,

    @JsonPropertyDescription("Maximum number of top search result snippets to retrieve (default: 3, max: 5).")
    Integer maxResults
) {}
