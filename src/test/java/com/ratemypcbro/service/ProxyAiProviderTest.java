package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProxyAiProviderTest {

    @Autowired
    private ProxyAiProvider proxyAiProvider;

    @Autowired
    private PcSpecService pcSpecService;

    @Autowired
    private AgentToolService agentToolService;

    @Test
    @Disabled("External API integration tests require valid OpenRouter API key, disabled for build pipelines.")
    void testGeneralVerdictReachable() {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        GeneralVerdict response = proxyAiProvider.getGeneralVerdict(specs, "");
        assertNotNull(response);
        assertNotNull(response.getRating());
        assertNotNull(response.getVerdict());
        assertNotNull(response.getReview());
        System.out.println("\n=== Live General Verdict Response ===");
        System.out.println(response);
    }

    @Test
    @Disabled("External API integration tests require valid OpenRouter API key, disabled for build pipelines.")
    void testSoftwareVerdictReachable() {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        String grounding = agentToolService.getSoftwareGrounding(specs, "Cyberpunk 2077");
        SoftwareVerdict response = proxyAiProvider.getSoftwareRunScore(specs, "game", "Cyberpunk 2077", grounding);
        assertNotNull(response);
        assertNotNull(response.getSoftware());
        assertNotNull(response.getScore());
        assertNotNull(response.getVerdict());
        System.out.println("\n=== Live Software Verdict Response ===");
        System.out.println(response);
    }
}
