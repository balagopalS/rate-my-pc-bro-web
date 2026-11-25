package com.ratemypcbro.service.tools;

import com.ratemypcbro.dto.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fallback web search {@code @Tool} used only when specialized hardware/software tools cannot find required details.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSearchFallbackTools {

    private final ToolTraceSupport traceSupport;

    @Tool(name = "webSearchTool",
          description = "LAST RESORT ESCAPE HATCH: Use ONLY if discrete search tools fail to find what you need. Use with extreme caution!")
    public SearchResponse webSearchTool(
            @ToolParam(description = "Search query string") String query,
            @ToolParam(description = "Search depth: 'basic' or 'advanced'", required = false) String searchDepth,
            @ToolParam(description = "Optional list of domains to restrict search to", required = false) List<String> includeDomains,
            @ToolParam(description = "Topic: 'general' or 'news'", required = false) String topic,
            @ToolParam(description = "Maximum results to fetch (1-5)", required = false) Integer maxResults,
            ToolContext toolContext) {
        log.warn("😱 [Tool: webSearchTool ESCAPE HATCH] LLM invoked fallback search (query: '{}')", query);
        String depth = (searchDepth != null && !searchDepth.isBlank()) ? searchDepth : "basic";
        int limit = (maxResults != null && maxResults > 0) ? Math.min(maxResults, 5) : 3;
        return traceSupport.executeTrackedSearch(
                "webSearchTool",
                query,
                depth,
                includeDomains,
                topic != null ? topic : "general",
                limit,
                toolContext
        );
    }
}
