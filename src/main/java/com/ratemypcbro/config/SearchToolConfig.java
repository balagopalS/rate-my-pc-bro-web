package com.ratemypcbro.config;

import com.ratemypcbro.dto.SearchRequest;
import com.ratemypcbro.dto.SearchResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * Configuration for AI Tool Calling.
 * Defines the beans that Spring AI can auto-discover and present to the LLM as 'Tools'.
 */
@Configuration
public class SearchToolConfig {

    /**
     * A mock implementation of the web search tool for Phase 1.
     * In Phase 2, this will be replaced with a real API call (e.g., Brave Search).
     */
    @Bean
    @Description("Search the internet for the latest 2026 PC hardware benchmarks, market prices, and software requirements.")
    public Function<SearchRequest, SearchResponse> webSearchTool() {
        return request -> {
            System.out.println("AI is searching the web for: " + request.query());
            // Mocking a response for now
            return new SearchResponse(
                "Mocked search result for '" + request.query() + "': " +
                "Hardware is currently trending in 2026. Market prices are stable. " +
                "Benchmarks show high efficiency for current architectures."
            );
        };
    }
}
