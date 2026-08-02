package com.ratemypcbro.config;

import com.ratemypcbro.dto.SearchRequest;
import com.ratemypcbro.dto.SearchResponse;
import com.ratemypcbro.service.WebScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * Configuration for AI Tool Calling.
 * Defines the beans that Spring AI can auto-discover and present to the LLM as 'Tools'.
 */
@Slf4j
@Configuration
public class SearchToolConfig {

    private final WebScraper webScraper;

    public SearchToolConfig(WebScraper webScraper) {
        this.webScraper = webScraper;
    }

    /**
     * General PC Audit Tool 1: Thermal Cooling & Chassis Design.
     */
    @Bean
    @Description("Search authority hardware databases for thermal cooling design, TGP/TDP wattage limits, fan noise, and thermal throttling analysis for a specific PC chassis or laptop model.")
    public Function<com.ratemypcbro.dto.HardwareThermalsRequest, SearchResponse> searchHardwareThermalsAndChassis() {
        return request -> {
            long startMs = System.currentTimeMillis();
            int stepNum = com.ratemypcbro.context.ToolCallContext.getNextStepNumber();
            String query = request.systemModel() + " " + request.cpu() + " " + request.gpu() + " thermal throttling TGP wattage fan noise cooling";
            log.info("🌡️ [AI Tool Call #{}: searchHardwareThermalsAndChassis] Model: '{}', CPU: '{}', GPU: '{}'", stepNum, request.systemModel(), request.cpu(), request.gpu());

            SearchRequest req = new SearchRequest(
                query, "advanced", java.util.List.of("notebookcheck.net", "techpowerup.com", "tomshardware.com"), null, "general", 4
            );
            String resultText = webScraper.search(req);
            long durationMs = System.currentTimeMillis() - startMs;

            com.ratemypcbro.dto.ToolCallTrace trace = new com.ratemypcbro.dto.ToolCallTrace(
                stepNum, "searchHardwareThermalsAndChassis", query, "advanced", req.includeDomains(),
                resultText != null && resultText.length() > 200 ? resultText.substring(0, 200) + "..." : resultText,
                durationMs, java.time.Instant.now().toString()
            );
            com.ratemypcbro.context.ToolCallContext.addTrace(trace);

            return new SearchResponse(resultText);
        };
    }

    /**
     * General PC Audit Tool 2: CPU vs GPU Bottleneck & Hierarchy Analysis.
     */
    @Bean
    @Description("Search benchmark databases for exact CPU vs GPU bottlenecks, PCIe generation limits, RAM speed impacts, and resolution scaling (1080p vs 1440p vs 4K).")
    public Function<com.ratemypcbro.dto.HardwareBottleneckRequest, SearchResponse> searchBottleneckAndHierarchy() {
        return request -> {
            long startMs = System.currentTimeMillis();
            int stepNum = com.ratemypcbro.context.ToolCallContext.getNextStepNumber();
            String query = request.cpu() + " " + request.gpu() + " " + request.ramDetails() + " gaming bottleneck benchmark 1080p 1440p";
            log.info("⚖️ [AI Tool Call #{}: searchBottleneckAndHierarchy] CPU: '{}', GPU: '{}'", stepNum, request.cpu(), request.gpu());

            SearchRequest req = new SearchRequest(
                query, "advanced", java.util.List.of("techpowerup.com", "gpucheck.com", "gamersnexus.net"), null, "general", 4
            );
            String resultText = webScraper.search(req);
            long durationMs = System.currentTimeMillis() - startMs;

            com.ratemypcbro.dto.ToolCallTrace trace = new com.ratemypcbro.dto.ToolCallTrace(
                stepNum, "searchBottleneckAndHierarchy", query, "advanced", req.includeDomains(),
                resultText != null && resultText.length() > 200 ? resultText.substring(0, 200) + "..." : resultText,
                durationMs, java.time.Instant.now().toString()
            );
            com.ratemypcbro.context.ToolCallContext.addTrace(trace);

            return new SearchResponse(resultText);
        };
    }

    /**
     * General PC Audit Tool 3: Reddit Hardware Community Sentiment & Owner Experiences.
     */
    @Bean
    @Description("Search Reddit specifically for real-world owner experiences, long-term build quality issues, BIOS quirks, thermal paste degradation, and undervolting guides.")
    public Function<com.ratemypcbro.dto.RedditSentimentRequest, SearchResponse> searchRedditHardwareSentiment() {
        return request -> {
            long startMs = System.currentTimeMillis();
            int stepNum = com.ratemypcbro.context.ToolCallContext.getNextStepNumber();
            String searchQuery = request.query();
            if (!searchQuery.toLowerCase().contains("reddit")) {
                searchQuery += " reddit";
            }
            log.info("💬 [AI Tool Call #{}: searchRedditHardwareSentiment] Query: '{}'", stepNum, searchQuery);

            SearchRequest req = new SearchRequest(
                searchQuery, "advanced", java.util.List.of("reddit.com"), null, "general", 4
            );
            String resultText = webScraper.search(req);
            long durationMs = System.currentTimeMillis() - startMs;

            com.ratemypcbro.dto.ToolCallTrace trace = new com.ratemypcbro.dto.ToolCallTrace(
                stepNum, "searchRedditHardwareSentiment", searchQuery, "advanced", req.includeDomains(),
                resultText != null && resultText.length() > 200 ? resultText.substring(0, 200) + "..." : resultText,
                durationMs, java.time.Instant.now().toString()
            );
            com.ratemypcbro.context.ToolCallContext.addTrace(trace);

            return new SearchResponse(resultText);
        };
    }

    /**
     * General PC Audit Tool 4: Expandability & Upgrade Path Discovery.
     */
    @Bean
    @Description("Search PC hardware databases and Reddit for expandability, extra M.2 NVMe SSD slots, RAM upgrade capacity limits, and recommended upgrade paths.")
    public Function<com.ratemypcbro.dto.HardwareUpgradeRequest, SearchResponse> searchUpgradePathAndMarketPrices() {
        return request -> {
            long startMs = System.currentTimeMillis();
            int stepNum = com.ratemypcbro.context.ToolCallContext.getNextStepNumber();
            String query = request.systemModel() + " " + request.cpu() + " M.2 NVMe slots RAM upgrade capacity limits";
            log.info("🛠️ [AI Tool Call #{}: searchUpgradePathAndMarketPrices] Model: '{}', CPU: '{}'", stepNum, request.systemModel(), request.cpu());

            SearchRequest req = new SearchRequest(
                query, "advanced", java.util.List.of("reddit.com", "tomshardware.com", "pcpartpicker.com"), null, "general", 4
            );
            String resultText = webScraper.search(req);
            long durationMs = System.currentTimeMillis() - startMs;

            com.ratemypcbro.dto.ToolCallTrace trace = new com.ratemypcbro.dto.ToolCallTrace(
                stepNum, "searchUpgradePathAndMarketPrices", query, "advanced", req.includeDomains(),
                resultText != null && resultText.length() > 200 ? resultText.substring(0, 200) + "..." : resultText,
                durationMs, java.time.Instant.now().toString()
            );
            com.ratemypcbro.context.ToolCallContext.addTrace(trace);

            return new SearchResponse(resultText);
        };
    }

    /**
     * Dedicated Tool 1: Software Requirements & PC Release Status Search.
     */
    @Bean
    @Description("Search official and predicted PC system requirements, minimum/recommended specs, and PC release status for a software or game.")
    public Function<com.ratemypcbro.dto.SoftwareRequirementsRequest, SearchResponse> searchSoftwareRequirements() {
        return request -> {
            long startMs = System.currentTimeMillis();
            int stepNum = com.ratemypcbro.context.ToolCallContext.getNextStepNumber();
            String query = request.softwareName() + " official PC system requirements release status";
            log.info("🎯 [AI Tool Call #{}: searchSoftwareRequirements] App: '{}'", stepNum, request.softwareName());

            SearchRequest req = new SearchRequest(
                query, "basic", java.util.List.of("pcgamingwiki.com", "systemrequirementslab.com"), null, "news", 4
            );
            String resultText = webScraper.search(req);
            long durationMs = System.currentTimeMillis() - startMs;

            com.ratemypcbro.dto.ToolCallTrace trace = new com.ratemypcbro.dto.ToolCallTrace(
                stepNum, "searchSoftwareRequirements", query, "basic", req.includeDomains(),
                resultText != null && resultText.length() > 200 ? resultText.substring(0, 200) + "..." : resultText,
                durationMs, java.time.Instant.now().toString()
            );
            com.ratemypcbro.context.ToolCallContext.addTrace(trace);

            return new SearchResponse(resultText);
        };
    }

    /**
     * Dedicated Tool 2: Reddit Community Sentiment & Real-World User Reports.
     */
    @Bean
    @Description("Search Reddit specifically to gather subjective real-world gamer takes, user sentiments, stuttering reports, thermal throttling experiences, and settings tweaks (e.g. 'Cyberpunk 2077 RTX 3070 Ti laptop stuttering reddit').")
    public Function<com.ratemypcbro.dto.RedditSentimentRequest, SearchResponse> searchRedditCommunitySentiment() {
        return request -> {
            long startMs = System.currentTimeMillis();
            int stepNum = com.ratemypcbro.context.ToolCallContext.getNextStepNumber();
            String searchQuery = request.query();
            if (!searchQuery.toLowerCase().contains("reddit")) {
                searchQuery += " reddit";
            }
            log.info("💬 [AI Tool Call #{}: searchRedditCommunitySentiment] Query: '{}'", stepNum, searchQuery);

            SearchRequest req = new SearchRequest(
                searchQuery, "advanced", java.util.List.of("reddit.com"), null, "general", 4
            );
            String resultText = webScraper.search(req);
            long durationMs = System.currentTimeMillis() - startMs;

            com.ratemypcbro.dto.ToolCallTrace trace = new com.ratemypcbro.dto.ToolCallTrace(
                stepNum, "searchRedditCommunitySentiment", searchQuery, "advanced", req.includeDomains(),
                resultText != null && resultText.length() > 200 ? resultText.substring(0, 200) + "..." : resultText,
                durationMs, java.time.Instant.now().toString()
            );
            com.ratemypcbro.context.ToolCallContext.addTrace(trace);

            return new SearchResponse(resultText);
        };
    }

    /**
     * Dedicated Tool 3: Hardware Compatibility & FPS Benchmarks.
     */
    @Bean
    @Description("Search hardware authority databases for exact CPU + GPU benchmarks, resolution scaling (1080p/1440p/4K), and thermal bottleneck analysis.")
    public Function<com.ratemypcbro.dto.HardwareCompatibilityRequest, SearchResponse> searchHardwareCompatibility() {
        return request -> {
            long startMs = System.currentTimeMillis();
            int stepNum = com.ratemypcbro.context.ToolCallContext.getNextStepNumber();
            String query = request.softwareName() + " " + request.gpu() + " " + request.cpu() + " FPS benchmark performance";
            log.info("⚙️ [AI Tool Call #{}: searchHardwareCompatibility] GPU: '{}', CPU: '{}', App: '{}'", stepNum, request.gpu(), request.cpu(), request.softwareName());

            SearchRequest req = new SearchRequest(
                query, "advanced", java.util.List.of("techpowerup.com", "notebookcheck.net", "gpucheck.com"), null, "general", 4
            );
            String resultText = webScraper.search(req);
            long durationMs = System.currentTimeMillis() - startMs;

            com.ratemypcbro.dto.ToolCallTrace trace = new com.ratemypcbro.dto.ToolCallTrace(
                stepNum, "searchHardwareCompatibility", query, "advanced", req.includeDomains(),
                resultText != null && resultText.length() > 200 ? resultText.substring(0, 200) + "..." : resultText,
                durationMs, java.time.Instant.now().toString()
            );
            com.ratemypcbro.context.ToolCallContext.addTrace(trace);

            return new SearchResponse(resultText);
        };
    }

    /**
     * Universal Fallback Tool (Scary / Expensive Escape Hatch).
     */
    @Bean
    @Description("⚠️ LAST RESORT ESCAPE HATCH: Use ONLY if discrete search tools fail to find what you need. Running this tool makes the developer cry because it is very scary and expensive. Use with extreme caution!")
    public Function<SearchRequest, SearchResponse> webSearchTool() {
        return request -> {
            long startMs = System.currentTimeMillis();
            int stepNum = com.ratemypcbro.context.ToolCallContext.getNextStepNumber();
            log.warn("😱 [AI Tool Call #{}: webSearchTool ESCAPE HATCH] LLM invoked the scary expensive tool! (query: '{}')", stepNum, request.query());
            String resultText = webScraper.search(request);
            long durationMs = System.currentTimeMillis() - startMs;

            com.ratemypcbro.dto.ToolCallTrace trace = new com.ratemypcbro.dto.ToolCallTrace(
                stepNum,
                "webSearchTool",
                request.query(),
                request.searchDepth() != null ? request.searchDepth() : "basic",
                request.includeDomains(),
                resultText != null && resultText.length() > 200 ? resultText.substring(0, 200) + "..." : resultText,
                durationMs,
                java.time.Instant.now().toString()
            );
            com.ratemypcbro.context.ToolCallContext.addTrace(trace);

            return new SearchResponse(resultText);
        };
    }
}
