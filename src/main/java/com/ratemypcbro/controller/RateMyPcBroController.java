package com.ratemypcbro.controller;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import com.ratemypcbro.service.AiOrchestrator;
import com.ratemypcbro.service.AiProvider;
import com.ratemypcbro.service.PcSpecService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ratemypcbro")
public class RateMyPcBroController {

    private final PcSpecService pcSpecService;
    private final AiOrchestrator aiOrchestrator;

    public RateMyPcBroController(PcSpecService pcSpecService, AiOrchestrator aiOrchestrator) {
        this.pcSpecService = pcSpecService;
        this.aiOrchestrator = aiOrchestrator;
    }

    @GetMapping
    public ResponseEntity<GeneralVerdict> getGeneralVerdict() {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        GeneralVerdict result = aiOrchestrator.getGeneralVerdict(specs);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{type}/{name}")
    public ResponseEntity<SoftwareVerdict> getSoftwareVerdict(
            @PathVariable String type,
            @PathVariable String name) {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        SoftwareVerdict result = aiOrchestrator.getSoftwareRunScore(specs, type, name);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/config/provider")
    public ResponseEntity<Map<String, String>> toggleProvider(@RequestParam AiProvider.Type type) {
        aiOrchestrator.setProviderType(type);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "active_provider", type.name()
        ));
    }

    @GetMapping("/config/provider")
    public ResponseEntity<Map<String, String>> getProvider() {
        return ResponseEntity.ok(Map.of(
            "active_provider", aiOrchestrator.getProviderType().name()
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        String result = aiOrchestrator.testAi();
        return ResponseEntity.ok(Map.of(
            "status", "AI is reachable",
            "response", result,
            "active_provider", aiOrchestrator.getProviderType().name()
        ));
    }
}
