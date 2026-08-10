package com.ratemypcbro.controller;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import com.ratemypcbro.dto.ToolCallTrace;
import com.ratemypcbro.service.AgentToolService;
import com.ratemypcbro.service.AiOrchestrator;
import com.ratemypcbro.service.AiProvider;
import com.ratemypcbro.service.PcSpecService;
import com.ratemypcbro.service.WebScraper;
import com.ratemypcbro.context.ToolCallContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ratemypcbro")
@RequiredArgsConstructor
public class RateMyPcBroController {

    private final PcSpecService pcSpecService;
    private final AiOrchestrator aiOrchestrator;
    private final WebScraper webScraper;
    private final AgentToolService agentToolService;

    @GetMapping
    //this returns a general verdict for the local pc
    public ResponseEntity<GeneralVerdict> getGeneralVerdict() {
        ToolCallContext.clear();
        try {
            PcSpecs specs = pcSpecService.getLocalPcSpecs();
            GeneralVerdict result = aiOrchestrator.getGeneralVerdict(specs);
            List<ToolCallTrace> traces = ToolCallContext.getTraces();
            if (result != null && !traces.isEmpty()) {
                result.setToolCallTrace(traces);
            }
            return ResponseEntity.ok(result);
        } finally {
            ToolCallContext.clear();
        }
    }

    @GetMapping("/software")
    //this returns a software verdict for the local pc for a given software and type
    public ResponseEntity<SoftwareVerdict> getSoftwareVerdict(
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false, name = "caller_id") String callerId) {
        String clientIdentity = (callerId != null && !callerId.isBlank()) ? callerId.trim() : "DEFAULT_CLIENT";
        log.info(
            "📱 [Software Verdict Request] App: '{}', Type: '{}', CallerID: '{}', Notes: '{}'",
            name, type, clientIdentity, notes != null ? notes : "None"
        );
        ToolCallContext.clear();
        try {
            PcSpecs specs = pcSpecService.getLocalPcSpecs();
            SoftwareVerdict result = aiOrchestrator.getSoftwareRunScore(specs, type, name, notes);
            List<ToolCallTrace> traces = ToolCallContext.getTraces();
            if (result != null && !traces.isEmpty()) {
                result.setToolCallTrace(traces);
            }
            return ResponseEntity.ok(result);
        } finally {
            ToolCallContext.clear();
        }
    }

    @PostMapping("/config/provider")
    //this is used to toggle between different ai providers can be PROXY or LOCAL
    public ResponseEntity<Map<String, String>> toggleProvider(@RequestParam AiProvider.Type type) {
        aiOrchestrator.setProviderType(type);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "active_provider", type.name()
        ));
    }

    @GetMapping("/config/provider")
    //this is used to get the current ai provider
    public ResponseEntity<Map<String, String>> getProvider() {
        return ResponseEntity.ok(Map.of(
            "active_provider", aiOrchestrator.getProviderType().name()
        ));
    }

    @PostMapping("/config/cache/clear")
    //this endpoint clears the in-memory web scraping & hardware baseline cache
    public ResponseEntity<Map<String, Object>> clearCache() {
        int scraperCleared = webScraper.clearCache();
        int baselineCleared = agentToolService.clearCache();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Web scraping & hardware baseline caches cleared successfully",
            "entries_removed", scraperCleared + baselineCleared
        ));
    }

    @GetMapping("/health")
    //this is used to check the health of the ai 
    public ResponseEntity<Map<String, String>> healthCheck() {
        String result = aiOrchestrator.testAi();
        return ResponseEntity.ok(Map.of(
            "status", "AI is reachable",
            "response", result,
            "active_provider", aiOrchestrator.getProviderType().name()
        ));
    }
}
