package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class AgentToolService {

    private final WebScraper scraper;

    public AgentToolService(WebScraper scraper) {
        this.scraper = scraper;
    }

    // ==========================================
    // PHASE 2 AGGREGATORS (Deterministic)
    // ==========================================

    /**
     * Executes queries for General PC analysis (benchmarks and reddit).
     */
    public String getGeneralGrounding(PcSpecs specs) {
        log.info("🚀 [AgentToolService] Initiating General Grounding Phase for CPU: [{}] and GPU: [{}]", specs.getProcessor(), specs.getGraphicsCard());
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(searchCpuBenchmark(specs.getProcessor())).append("\n\n");
        sb.append(searchGpuBenchmark(specs.getGraphicsCard())).append("\n\n");
        sb.append(searchHardwareBottleneck(specs.getProcessor(), specs.getGraphicsCard())).append("\n\n");
        
        if (specs.getComputerModel() != null && !specs.getComputerModel().toLowerCase().contains("unknown") && !specs.getComputerModel().toLowerCase().contains("to be filled")) {
            sb.append(searchChassisIssues(specs.getComputerModel())).append("\n\n");
        }

        return sb.toString().trim();
    }

    /**
     * Executes queries for Software Performance prediction.
     */
    public String getSoftwareGrounding(PcSpecs specs, String softwareName) {
        log.info("🎮 [AgentToolService] Initiating Software Grounding for Game: [{}] on GPU: [{}]", softwareName, specs.getGraphicsCard());
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(searchSoftwareRequirements(softwareName)).append("\n\n");
        sb.append(searchSoftwarePerformance(softwareName, specs.getGraphicsCard())).append("\n\n");
        sb.append(searchSoftwarePerformance(softwareName, specs.getProcessor()));

        return sb.toString().trim();
    }

    // ==========================================
    // PHASE 3 ATOMIC TOOLS (Future-Proofed)
    // ==========================================

    public String searchCpuBenchmark(String cpuName) {
        return fetchSection("CPU BENCHMARK", cpuName + " Passmark benchmark score");
    }

    public String searchGpuBenchmark(String gpuName) {
        return fetchSection("GPU BENCHMARK", gpuName + " TechPowerUp review summary");
    }

    public String searchHardwareBottleneck(String cpuName, String gpuName) {
        return fetchSection("BOTTLENECK EXPERIENCES", cpuName + " and " + gpuName + " bottleneck site:reddit.com");
    }

    public String searchChassisIssues(String computerModel) {
        return fetchSection("CHASSIS ISSUES", computerModel + " thermal throttling common issues site:reddit.com");
    }

    public String searchSoftwareRequirements(String softwareName) {
        return fetchSection("OFFICIAL REQUIREMENTS", softwareName + " official recommended PC system requirements");
    }

    public String searchSoftwarePerformance(String softwareName, String hardwareComponent) {
        return fetchSection("REAL-WORLD PERFORMANCE", softwareName + " " + hardwareComponent + " performance FPS site:reddit.com");
    }

    private String fetchSection(String label, String query) {
        String results = scraper.search(query, 3);
        if (results == null || results.isBlank() || results.startsWith("Failed")) {
            return ""; // Fail silently if no data to not poison the prompt
        }
        return "[" + label + " (" + query + ")]\n- " + results;
    }
}
