package com.ratemypcbro.controller;

import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.service.AiVerdictService;
import com.ratemypcbro.service.PcSpecService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ratemypcbro")
public class RateMyPcBroController {

    private final PcSpecService pcSpecService;
    private final AiVerdictService aiVerdictService;

    public RateMyPcBroController(PcSpecService pcSpecService, AiVerdictService aiVerdictService) {
        this.pcSpecService = pcSpecService;
        this.aiVerdictService = aiVerdictService;
    }

    @GetMapping
    public ResponseEntity<String> getGeneralVerdict() {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        String result = aiVerdictService.getGeneralVerdict(specs);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(result);
    }

    @GetMapping("/{type}/{name}")
    public ResponseEntity<String> getSoftwareVerdict(
            @PathVariable String type,
            @PathVariable String name) {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        String result = aiVerdictService.getSoftwareRunScore(specs, type, name);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(result);
    }
}
