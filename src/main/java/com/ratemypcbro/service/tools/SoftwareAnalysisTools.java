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
 * Spring AI {@code @Tool} suite for software/game compatibility and FPS estimation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SoftwareAnalysisTools {

    private final ToolTraceSupport traceSupport;

    @Tool(name = "searchSoftwareRequirements",
          description = "Search official and predicted PC system requirements, minimum/recommended specs, and PC release status for a software or game.")
    public SearchResponse searchSoftwareRequirements(
            @ToolParam(description = "Name of the game or software") String softwareName,
            ToolContext toolContext) {
        String query = softwareName + " official PC system requirements release status";
        log.info("🎯 [Tool: searchSoftwareRequirements] App: '{}'", softwareName);
        return traceSupport.executeTrackedSearch(
                "searchSoftwareRequirements",
                query,
                "basic",
                List.of("pcgamingwiki.com", "systemrequirementslab.com"),
                "news",
                4,
                toolContext
        );
    }

    @Tool(name = "searchRedditCommunitySentiment",
          description = "Search Reddit specifically to gather subjective real-world gamer takes, user sentiments, stuttering reports, thermal throttling experiences, and settings tweaks (e.g. 'Cyberpunk 2077 RTX 3070 Ti laptop stuttering reddit').")
    public SearchResponse searchRedditCommunitySentiment(
            @ToolParam(description = "Focused Reddit search query") String query,
            ToolContext toolContext) {
        String searchQuery = query.toLowerCase().contains("reddit") ? query : query + " reddit";
        log.info("💬 [Tool: searchRedditCommunitySentiment] Query: '{}'", searchQuery);
        return traceSupport.executeTrackedSearch(
                "searchRedditCommunitySentiment",
                searchQuery,
                "advanced",
                List.of("reddit.com"),
                "general",
                4,
                toolContext
        );
    }

    @Tool(name = "searchHardwareCompatibility",
          description = "Search hardware authority databases for exact CPU + GPU benchmarks, resolution scaling (1080p/1440p/4K), and thermal bottleneck analysis.")
    public SearchResponse searchHardwareCompatibility(
            @ToolParam(description = "Name of the game or software") String softwareName,
            @ToolParam(description = "Graphics card model") String gpu,
            @ToolParam(description = "Processor model") String cpu,
            ToolContext toolContext) {
        String query = softwareName + " " + gpu + " " + cpu + " FPS benchmark performance";
        log.info("⚙️ [Tool: searchHardwareCompatibility] GPU: '{}', CPU: '{}', App: '{}'", gpu, cpu, softwareName);
        return traceSupport.executeTrackedSearch(
                "searchHardwareCompatibility",
                query,
                "advanced",
                List.of("techpowerup.com", "notebookcheck.net", "gpucheck.com"),
                "general",
                4,
                toolContext
        );
    }
}
