package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
//this is used to toggle between different ai providers can be PROXY or LOCAL, in this future this class will
// be able to handle tools.
public class AiOrchestrator {

    private final OllamaAiProvider localProvider;
    private final ProxyAiProvider proxyProvider;
    private final AgentToolService agentToolService;
    private final AtomicReference<AiProvider.Type> activeType = new AtomicReference<>(AiProvider.Type.LOCAL);

    public void setProviderType(AiProvider.Type type) {
        this.activeType.set(type);
    }

    public AiProvider.Type getProviderType() {
        return this.activeType.get();
    }

    private AiProvider getActiveProvider() {
        return activeType.get() == AiProvider.Type.LOCAL ? localProvider : proxyProvider;
    }

    public GeneralVerdict getGeneralVerdict(PcSpecs specs) {
        try {
            String groundingContext = agentToolService.getGeneralGrounding(specs);
            GeneralVerdict verdict = getActiveProvider().getGeneralVerdict(specs, groundingContext);
            if (verdict != null) {
                verdict.setReflectedSpecs(specs);
            }
            return verdict;
        } catch (Exception e) {
            String errorMsg = "Error getting AI verdict: " + e.getMessage();
            return GeneralVerdict.builder()
                    .rating(0.0)
                    .verdict(errorMsg)
                    .review("The AI is currently speechless.")
                    .build();
        }
    }

    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name) {
        return getSoftwareRunScore(specs, type, name, null);
    }

    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name, String notes) {
        try {
            String groundingContext = agentToolService.getSoftwareGrounding(specs, name);
            return getActiveProvider().getSoftwareRunScore(specs, type, name, notes, groundingContext);
        } catch (Exception e) {
            String errorMsg = "Error getting AI software score: " + e.getMessage();
            return SoftwareVerdict.builder()
                    .software(name)
                    .score("N/A")
                    .verdict(errorMsg)
                    .performance_notes("Check your connection.")
                    .build();
        }
    }

    public String testAi() {
        try {
            return getActiveProvider().testAi();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}
