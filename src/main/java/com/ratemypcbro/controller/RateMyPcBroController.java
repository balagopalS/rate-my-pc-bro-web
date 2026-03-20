package com.ratemypcbro.controller;

import com.ratemypcbro.dto.PcSpecs;
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
    public ResponseEntity<String> getGeneralVerdict() {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        String result = aiOrchestrator.getGeneralVerdict(specs);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(result);
    }

    @GetMapping("/{type}/{name}")
    public ResponseEntity<String> getSoftwareVerdict(
            @PathVariable String type,
            @PathVariable String name) {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        String result = aiOrchestrator.getSoftwareRunScore(specs, type, name);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(result);
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
}
