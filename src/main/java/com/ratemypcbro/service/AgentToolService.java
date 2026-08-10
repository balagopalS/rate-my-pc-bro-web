package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentToolService {

    private static final List<String> HARDWARE_DOMAINS = List.of(
            "techpowerup.com", "notebookcheck.net", "reddit.com", "tomshardware.com"
    );

    private static final List<String> SOFTWARE_DOMAINS = List.of(
            "pcgamingwiki.com", "systemrequirementslab.com", "reddit.com", "techpowerup.com"
    );

    private final WebScraper scraper;
    private final Map<String, String> hardwareBaselineCache = new ConcurrentHashMap<>();

    /**
     * Executes single consolidated scoped query for General PC analysis (CPU + GPU + Chassis + Thermals + Bottlenecks).
     * Caches the Hardware Baseline in memory for reuse by Software Verdict queries.
     */
    public String getGeneralGrounding(PcSpecs specs) {
        String key = buildHardwareSignature(specs);
        if (hardwareBaselineCache.containsKey(key)) {
            log.info("📦 [AgentToolService] Hardware Baseline Cache HIT for specs: [{}]", key);
            return hardwareBaselineCache.get(key);
        }

        log.info("🚀 [AgentToolService] Initiating Scoped Hardware Grounding for CPU: [{}] and GPU: [{}]", specs.getProcessor(), specs.getGraphicsCard());

        String query = buildHardwareScopedQuery(specs);
        String result = scraper.search(query, 3, HARDWARE_DOMAINS);
        String grounding = "[HARDWARE BASELINE & THERMALS (" + query + ")]\n" + result;

        hardwareBaselineCache.put(key, grounding);
        return grounding;
    }

    /**
     * Executes Software Performance prediction grounding.
     * Reuses cached Hardware Baseline if available, or fetches Hardware Baseline + Software Grounding in PARALLEL via CompletableFuture!
     */
    public String getSoftwareGrounding(PcSpecs specs, String softwareName) {
        log.info("🎮 [AgentToolService] Initiating Orchestrated Software Grounding for App: [{}] on CPU: [{}] & GPU: [{}]",
                softwareName, specs.getProcessor(), specs.getGraphicsCard());

        String key = buildHardwareSignature(specs);

        // 1. If Hardware Baseline is already cached (e.g. GET /ratemypcbro called first), fetch ONLY software context!
        if (hardwareBaselineCache.containsKey(key)) {
            log.info("📦 [AgentToolService] Reusing Cached Hardware Baseline for App: [{}]", softwareName);
            String cachedHardware = hardwareBaselineCache.get(key);
            String softwareGrounding = fetchScopedSoftwareGrounding(specs, softwareName);
            return cachedHardware + "\n\n" + softwareGrounding;
        }

        // 2. Cold start: Execute Hardware Baseline query AND Software query in PARALLEL using CompletableFuture!
        log.info("⚡ [AgentToolService] Cold Start: Executing Hardware Baseline & Software Grounding in PARALLEL via CompletableFuture...");
        CompletableFuture<String> hwFuture = CompletableFuture.supplyAsync(() -> getGeneralGrounding(specs));
        CompletableFuture<String> swFuture = CompletableFuture.supplyAsync(() -> fetchScopedSoftwareGrounding(specs, softwareName));

        CompletableFuture.allOf(hwFuture, swFuture).join();

        try {
            return hwFuture.get() + "\n\n" + swFuture.get();
        } catch (Exception e) {
            log.error("⚠️ Failed to resolve parallel grounding futures", e);
            return fetchScopedSoftwareGrounding(specs, softwareName);
        }
    }

    private String fetchScopedSoftwareGrounding(PcSpecs specs, String softwareName) {
        String cleanGpu = cleanGpuForQuery(specs.getGraphicsCard());
        String cleanCpu = cleanCpuForQuery(specs.getProcessor());

        String query = String.format("%s official system requirements %s %s performance FPS",
                softwareName, cleanGpu, cleanCpu);

        String result = scraper.search(query, 3, SOFTWARE_DOMAINS);
        return "[SOFTWARE SPECIFIC PERFORMANCE (" + query + ")]\n" + result;
    }

    private String buildHardwareScopedQuery(PcSpecs specs) {
        StringBuilder sb = new StringBuilder();
        String cleanCpu = cleanCpuForQuery(specs.getProcessor());
        String cleanGpu = cleanGpuForQuery(specs.getGraphicsCard());

        if (!cleanCpu.isBlank()) sb.append(cleanCpu).append(" ");
        if (!cleanGpu.isBlank()) sb.append(cleanGpu).append(" ");
        if (specs.getComputerModel() != null && !specs.getComputerModel().toLowerCase().contains("unknown")
                && !specs.getComputerModel().toLowerCase().contains("to be filled")) {
            sb.append(specs.getComputerModel()).append(" ");
        }
        sb.append("gaming performance bottleneck thermal throttling");
        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    private String cleanGpuForQuery(String rawGpu) {
        if (rawGpu == null) return "";
        String[] parts = rawGpu.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.toLowerCase().contains("intel(r) uhd") && !trimmed.toLowerCase().contains("integrated graphics")) {
                return trimmed;
            }
        }
        return parts[0].trim();
    }

    private String cleanCpuForQuery(String rawCpu) {
        if (rawCpu == null) return "";
        return rawCpu.replaceAll("(?i)\\((R|TM|C)\\)", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String buildHardwareSignature(PcSpecs specs) {
        return (specs.getProcessor() + "|" + specs.getGraphicsCard() + "|" + specs.getComputerModel()).trim();
    }

    /**
     * Clears in-memory hardware baseline cache.
     */
    public int clearCache() {
        int count = hardwareBaselineCache.size();
        hardwareBaselineCache.clear();
        return count;
    }
}
