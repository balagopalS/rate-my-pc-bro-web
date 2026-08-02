package com.ratemypcbro.controller;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import com.ratemypcbro.service.AiOrchestrator;
import com.ratemypcbro.service.AiProvider;
import com.ratemypcbro.service.PcSpecService;
import com.ratemypcbro.service.WebScraper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ratemypcbro")
public class RateMyPcBroController {

    private final PcSpecService pcSpecService;
    private final AiOrchestrator aiOrchestrator;
    private final WebScraper webScraper;

    public RateMyPcBroController(PcSpecService pcSpecService, AiOrchestrator aiOrchestrator, WebScraper webScraper) {
        this.pcSpecService = pcSpecService;
        this.aiOrchestrator = aiOrchestrator;
        this.webScraper = webScraper;
    }

    @GetMapping
    //this returns a general verdict for the local pc
    public ResponseEntity<GeneralVerdict> getGeneralVerdict() {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        GeneralVerdict result = aiOrchestrator.getGeneralVerdict(specs);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/software")
    //this returns a software verdict for the local pc for a given software and type
    public ResponseEntity<SoftwareVerdict> getSoftwareVerdict(
            @RequestParam String type,
            @RequestParam String name) {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        SoftwareVerdict result = aiOrchestrator.getSoftwareRunScore(specs, type, name);
        return ResponseEntity.ok(result);
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
    //this endpoint clears the in-memory web scraping cache
    public ResponseEntity<Map<String, Object>> clearCache() {
        int clearedCount = webScraper.clearCache();
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Web scraping cache cleared successfully",
            "entries_removed", clearedCount
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
