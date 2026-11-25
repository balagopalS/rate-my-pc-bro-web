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
 * Spring AI {@code @Tool} suite for general PC hardware auditing.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeneralHardwareTools {

    private final ToolTraceSupport traceSupport;

    @Tool(name = "searchHardwareThermalsAndChassis",
          description = "Search authority hardware databases for thermal cooling design, TGP/TDP wattage limits, fan noise, and thermal throttling analysis for a specific PC chassis or laptop model.")
    public SearchResponse searchHardwareThermalsAndChassis(
            @ToolParam(description = "Exact PC chassis or laptop model name") String systemModel,
            @ToolParam(description = "Processor model") String cpu,
            @ToolParam(description = "Graphics card model") String gpu,
            ToolContext toolContext) {
        String query = systemModel + " " + cpu + " " + gpu + " thermal throttling TGP wattage fan noise cooling";
        log.info("🌡️ [Tool: searchHardwareThermalsAndChassis] Model: '{}', CPU: '{}', GPU: '{}'", systemModel, cpu, gpu);
        return traceSupport.executeTrackedSearch(
                "searchHardwareThermalsAndChassis",
                query,
                "advanced",
                List.of("notebookcheck.net", "techpowerup.com", "tomshardware.com"),
                "general",
                4,
                toolContext
        );
    }

    @Tool(name = "searchBottleneckAndHierarchy",
          description = "Search benchmark databases for exact CPU vs GPU bottlenecks, PCIe generation limits, RAM speed impacts, and resolution scaling (1080p vs 1440p vs 4K).")
    public SearchResponse searchBottleneckAndHierarchy(
            @ToolParam(description = "Processor model") String cpu,
            @ToolParam(description = "Graphics card model") String gpu,
            @ToolParam(description = "RAM capacity and speed details") String ramDetails,
            ToolContext toolContext) {
        String query = cpu + " " + gpu + " " + ramDetails + " gaming bottleneck benchmark 1080p 1440p";
        log.info("⚖️ [Tool: searchBottleneckAndHierarchy] CPU: '{}', GPU: '{}'", cpu, gpu);
        return traceSupport.executeTrackedSearch(
                "searchBottleneckAndHierarchy",
                query,
                "advanced",
                List.of("techpowerup.com", "gpucheck.com", "gamersnexus.net"),
                "general",
                4,
                toolContext
        );
    }

    @Tool(name = "searchRedditHardwareSentiment",
          description = "Search Reddit specifically for real-world owner experiences, long-term build quality issues, BIOS quirks, thermal paste degradation, and undervolting guides.")
    public SearchResponse searchRedditHardwareSentiment(
            @ToolParam(description = "Focused hardware Reddit search query") String query,
            ToolContext toolContext) {
        String searchQuery = query.toLowerCase().contains("reddit") ? query : query + " reddit";
        log.info("💬 [Tool: searchRedditHardwareSentiment] Query: '{}'", searchQuery);
        return traceSupport.executeTrackedSearch(
                "searchRedditHardwareSentiment",
                searchQuery,
                "advanced",
                List.of("reddit.com"),
                "general",
                4,
                toolContext
        );
    }

    @Tool(name = "searchUpgradePathAndMarketPrices",
          description = "Search PC hardware databases and Reddit for expandability, extra M.2 NVMe SSD slots, RAM upgrade capacity limits, and recommended upgrade paths.")
    public SearchResponse searchUpgradePathAndMarketPrices(
            @ToolParam(description = "Exact PC chassis, motherboard, or laptop model") String systemModel,
            @ToolParam(description = "Processor model") String cpu,
            ToolContext toolContext) {
        String query = systemModel + " " + cpu + " M.2 NVMe slots RAM upgrade capacity limits";
        log.info("🛠️ [Tool: searchUpgradePathAndMarketPrices] Model: '{}', CPU: '{}'", systemModel, cpu);
        return traceSupport.executeTrackedSearch(
                "searchUpgradePathAndMarketPrices",
                query,
                "advanced",
                List.of("reddit.com", "tomshardware.com", "pcpartpicker.com"),
                "general",
                4,
                toolContext
        );
    }
}
