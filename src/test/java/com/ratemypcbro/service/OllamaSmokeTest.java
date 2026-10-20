package com.ratemypcbro.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnabledIfSystemProperty(named = "ollama.integration", matches = "true")
class OllamaSmokeTest {

    @Autowired
    private AiOrchestrator aiOrchestrator;

    @MockitoBean
    private AiHealthChecker aiHealthChecker;

    @Test
    void localOllamaRespondsToHealthPrompt() {
        aiOrchestrator.setProviderType(AiProvider.Type.LOCAL);

        String response = aiOrchestrator.testAi();

        assertNotNull(response);
        assertFalse(response.isBlank());
        assertFalse(response.startsWith("AI provider is unavailable."), response);
    }
}
