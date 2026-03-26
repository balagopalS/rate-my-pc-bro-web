package com.ratemypcbro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class AiHealthChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AiHealthChecker.class);
    
    private final OllamaAiProvider ollamaProvider;
    private final ProxyAiProvider proxyProvider;

    public AiHealthChecker(OllamaAiProvider ollamaProvider, ProxyAiProvider proxyProvider) {
        this.ollamaProvider = ollamaProvider;
        this.proxyProvider = proxyProvider;
    }

    @Override
    public void run(String... args) {
        logger.info("─── AI CONNECTIVITY DIAGNOSTICS ───");
        
        checkProvider("LOCAL (Ollama)", ollamaProvider);
        checkProvider("PROXY (OpenRouter)", proxyProvider);
        
        logger.info("───────────────────────────────────");
    }

    private void checkProvider(String name, AiProvider provider) {
        try {
            String response = provider.testAi();
            if (response != null && !response.isBlank()) {
                logger.info("[{}] READY (Response: {})", name, response.trim());
            } else {
                logger.warn("[{}] UNSTABLE (Empty response)", name);
            }
        } catch (Exception e) {
            logger.error("[{}] OFFLINE (Error: {})", name, e.getMessage());
        }
    }
}
