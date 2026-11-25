package com.ratemypcbro.service.tools;

import com.ratemypcbro.dto.SearchRequest;
import com.ratemypcbro.dto.SearchResponse;
import com.ratemypcbro.dto.ToolCallTrace;
import com.ratemypcbro.service.WebScraper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ToolContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToolTraceSupportTest {

    @Mock
    private WebScraper webScraper;

    private ToolTraceSupport traceSupport;
    private GeneralHardwareTools generalHardwareTools;

    @BeforeEach
    void setUp() {
        traceSupport = new ToolTraceSupport(webScraper);
        generalHardwareTools = new GeneralHardwareTools(traceSupport);
    }

    @Test
    void shouldRecordTruncatedTraceInToolContext() {
        String longSnippet = "x".repeat(250);
        when(webScraper.search(any(SearchRequest.class))).thenReturn(longSnippet);

        List<ToolCallTrace> traces = new ArrayList<>();
        ToolContext context = new ToolContext(Map.of(ToolTraceSupport.TRACE_CONTEXT_KEY, traces));

        SearchResponse response = traceSupport.executeTrackedSearch(
                "searchHardwareThermalsAndChassis",
                "RTX 4070 thermals",
                "advanced",
                List.of("notebookcheck.net"),
                "general",
                4,
                context
        );

        assertEquals(longSnippet, response.content());
        assertEquals(1, traces.size());
        ToolCallTrace trace = traces.get(0);
        assertEquals(1, trace.step());
        assertEquals("searchHardwareThermalsAndChassis", trace.toolName());
        assertEquals("RTX 4070 thermals", trace.query());
        assertEquals(203, trace.summary().length());
        assertTrue(trace.summary().endsWith("..."));
    }

    @Test
    void shouldAppendRedditAndIncrementStepNumberAcrossToolCalls() {
        when(webScraper.search(any(SearchRequest.class))).thenReturn("ok");

        List<ToolCallTrace> traces = new ArrayList<>();
        ToolContext context = new ToolContext(Map.of(ToolTraceSupport.TRACE_CONTEXT_KEY, traces));

        generalHardwareTools.searchRedditHardwareSentiment("Legion 5 Pro thermals", context);
        generalHardwareTools.searchBottleneckAndHierarchy("Ryzen 7 7800X3D", "RTX 4080", "32GB DDR5", context);

        assertEquals(2, traces.size());
        assertEquals(1, traces.get(0).step());
        assertEquals("Legion 5 Pro thermals reddit", traces.get(0).query());
        assertEquals(2, traces.get(1).step());
        assertEquals("searchBottleneckAndHierarchy", traces.get(1).toolName());
    }
}
