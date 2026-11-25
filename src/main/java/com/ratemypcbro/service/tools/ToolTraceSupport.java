package com.ratemypcbro.service.tools;

import com.ratemypcbro.dto.SearchRequest;
import com.ratemypcbro.dto.SearchResponse;
import com.ratemypcbro.dto.ToolCallTrace;
import com.ratemypcbro.service.WebScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Shared execution and {@link ToolContext}-based trace recording for Spring AI {@code @Tool} methods.
 * Avoids ThreadLocal state so tool calls remain safe across async or virtual-thread execution.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolTraceSupport {

    public static final String TRACE_CONTEXT_KEY = "toolCallTraces";
    private static final int SNIPPET_PREVIEW_LENGTH = 200;

    private final WebScraper webScraper;

    public SearchResponse executeTrackedSearch(
            String toolName,
            String query,
            String searchDepth,
            List<String> includeDomains,
            String topic,
            int maxResults,
            ToolContext toolContext) {
        long startMs = System.currentTimeMillis();
        SearchRequest req = new SearchRequest(query, searchDepth, includeDomains, null, topic, maxResults);
        String resultText = webScraper.search(req);
        long durationMs = System.currentTimeMillis() - startMs;

        recordTrace(toolContext, toolName, query, searchDepth, includeDomains, resultText, durationMs);
        return new SearchResponse(resultText);
    }

    @SuppressWarnings("unchecked")
    private void recordTrace(
            ToolContext toolContext,
            String toolName,
            String query,
            String searchDepth,
            List<String> includeDomains,
            String resultText,
            long durationMs) {
        if (toolContext == null || toolContext.getContext() == null) {
            return;
        }
        Object rawTraces = toolContext.getContext().get(TRACE_CONTEXT_KEY);
        if (!(rawTraces instanceof List<?>)) {
            return;
        }
        List<ToolCallTrace> traces = (List<ToolCallTrace>) rawTraces;
        String preview = (resultText != null && resultText.length() > SNIPPET_PREVIEW_LENGTH)
                ? resultText.substring(0, SNIPPET_PREVIEW_LENGTH) + "..."
                : resultText;

        synchronized (traces) {
            int stepNum = traces.size() + 1;
            traces.add(new ToolCallTrace(
                    stepNum,
                    toolName,
                    query,
                    searchDepth != null ? searchDepth : "basic",
                    includeDomains,
                    preview,
                    durationMs,
                    Instant.now().toString()
            ));
            log.debug("Recorded trace #{} for tool [{}] ({} ms)", stepNum, toolName, durationMs);
        }
    }
}
