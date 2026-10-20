package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class AiOrchestratorFallbackTest {

    @Mock
    private OllamaAiProvider localProvider;

    @Mock
    private ProxyAiProvider proxyProvider;

    @Mock
    private AgentToolService agentToolService;

    private AiOrchestrator aiOrchestrator;
    private PcSpecs dummySpecs;

    @BeforeEach
    void setUp() {
        aiOrchestrator = new AiOrchestrator(localProvider, proxyProvider, agentToolService);
        dummySpecs = PcSpecs.builder().processor("Intel i7").graphicsCard("RTX 3070").build();
    }

    @Test
    @DisplayName("Verify AiOrchestrator handles General Verdict provider exceptions gracefully")
    void testGeneralVerdictExceptionFallback() {
        when(agentToolService.getGeneralGrounding(any())).thenReturn("grounding");
        when(localProvider.getGeneralVerdict(any(), anyString()))
                .thenThrow(new RuntimeException("Simulated LLM Connection Timeout"));

        aiOrchestrator.setProviderType(AiProvider.Type.LOCAL);
        GeneralVerdict verdict = aiOrchestrator.getGeneralVerdict(dummySpecs);

        assertNotNull(verdict, "Verdict must not be null even when provider fails");
        assertEquals(0.0, verdict.getRating(), "Fallback rating should be 0.0");
        assertEquals("Unable to generate a verdict right now.", verdict.getVerdict());
        assertFalse(verdict.getVerdict().contains("Timeout"));
    }

    @Test
    @DisplayName("Verify AiOrchestrator handles Software Verdict provider exceptions gracefully")
    void testSoftwareVerdictExceptionFallback() {
        when(agentToolService.getSoftwareGrounding(any(), anyString())).thenReturn("grounding");
        doThrow(new RuntimeException("Simulated API 500 Error"))
                .when(proxyProvider).getSoftwareRunScore(any(), anyString(), anyString(), any(), anyString());

        aiOrchestrator.setProviderType(AiProvider.Type.PROXY);
        SoftwareVerdict verdict = aiOrchestrator.getSoftwareRunScore(dummySpecs, "game", "Cyberpunk 2077");

        assertNotNull(verdict, "SoftwareVerdict must not be null even when proxy fails");
        assertEquals("N/A", verdict.getScore(), "Fallback score should be N/A");
        assertEquals("Unable to generate a verdict right now.", verdict.getVerdict());
        assertFalse(verdict.getVerdict().contains("500"));
    }
}
