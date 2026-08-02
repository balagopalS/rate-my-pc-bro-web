package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OllamaIntegrationTest {

    @Autowired
    private AiOrchestrator aiOrchestrator;

    @Autowired
    private PcSpecService pcSpecService;

    private PcSpecs specs;

    @BeforeEach
    void setUp() {
        // Ensure local Ollama provider is selected
        aiOrchestrator.setProviderType(AiProvider.Type.LOCAL);
        specs = pcSpecService.getLocalPcSpecs();
        assertNotNull(specs, "Local PC Specs should be detected by OSHI");
    }

    @Test
    @DisplayName("Integration Test: Software Performance Prediction for Adobe Premiere Pro")
    void testSoftwarePerformancePrediction_PremierePro() {
        System.out.println("=== Starting Integration Test for Software: Premiere Pro ===");
        
        SoftwareVerdict verdict = aiOrchestrator.getSoftwareRunScore(specs, "software", "Premiere Pro");

        assertNotNull(verdict, "SoftwareVerdict should not be null");
        assertNotNull(verdict.getSoftware(), "Software name in verdict should not be null");
        assertNotNull(verdict.getVerdict(), "Verdict summary should not be null");
        assertNotNull(verdict.getPerformance_notes(), "Performance notes should not be null");

        System.out.println("Software Evaluated: " + verdict.getSoftware());
        System.out.println("Score: " + verdict.getScore());
        System.out.println("Verdict: " + verdict.getVerdict());
        System.out.println("Performance Notes: " + verdict.getPerformance_notes());
        System.out.println("============================================================\n");
    }

    @Test
    @DisplayName("Integration Test: Game Performance Prediction for 007 Last Light")
    void testGamePerformancePrediction_007LastLight() {
        System.out.println("=== Starting Integration Test for Game: 007 Last Light ===");
        
        SoftwareVerdict verdict = aiOrchestrator.getSoftwareRunScore(specs, "game", "007 Last Light");

        assertNotNull(verdict, "SoftwareVerdict should not be null");
        assertNotNull(verdict.getSoftware(), "Game name in verdict should not be null");
        assertNotNull(verdict.getVerdict(), "Verdict summary should not be null");
        assertNotNull(verdict.getPerformance_notes(), "Performance notes should not be null");

        System.out.println("Game Evaluated: " + verdict.getSoftware());
        System.out.println("Score: " + verdict.getScore());
        System.out.println("Verdict: " + verdict.getVerdict());
        System.out.println("Performance Notes: " + verdict.getPerformance_notes());
        System.out.println("=========================================================\n");
    }
}
