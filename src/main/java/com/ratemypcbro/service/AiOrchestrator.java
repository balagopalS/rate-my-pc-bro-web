package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
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
            log.error("Failed to generate general AI verdict", e);
            return GeneralVerdict.builder()
                    .rating(0.0)
                    .verdict("Unable to generate a verdict right now.")
                    .review("Please try again later.")
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
            log.error("Failed to generate software AI verdict for [{}]", name, e);
            return SoftwareVerdict.builder()
                    .software(name)
                    .score("N/A")
                    .verdict("Unable to generate a verdict right now.")
                    .performance_notes("Please try again later.")
                    .build();
        }
    }

    public String testAi() {
        try {
            return getActiveProvider().testAi();
        } catch (Exception e) {
            log.error("AI health check failed", e);
            return "AI provider is unavailable.";
        }
    }
}
