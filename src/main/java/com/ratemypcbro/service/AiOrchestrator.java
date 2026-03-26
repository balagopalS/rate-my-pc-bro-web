package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class AiOrchestrator {

    private final OllamaAiProvider localProvider;
    private final ProxyAiProvider proxyProvider;
    private final AtomicReference<AiProvider.Type> activeType;

    public AiOrchestrator(OllamaAiProvider localProvider, ProxyAiProvider proxyProvider) {
        this.localProvider = localProvider;
        this.proxyProvider = proxyProvider;
        this.activeType = new AtomicReference<>(AiProvider.Type.LOCAL);
    }

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
            return getActiveProvider().getGeneralVerdict(specs);
        } catch (Exception e) {
            String errorMsg = "Error getting AI verdict: " + e.getMessage();
            return new GeneralVerdict("N/A", errorMsg, "The AI is currently speechless.");
        }
    }

    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name) {
        try {
            return getActiveProvider().getSoftwareRunScore(specs, type, name);
        } catch (Exception e) {
            String errorMsg = "Error getting AI software score: " + e.getMessage();
            return new SoftwareVerdict(name, "N/A", errorMsg, "Check your connection.");
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
