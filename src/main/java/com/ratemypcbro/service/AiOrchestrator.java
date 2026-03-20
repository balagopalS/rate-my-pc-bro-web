package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
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

    public String getGeneralVerdict(PcSpecs specs) {
        return getActiveProvider().getGeneralVerdict(specs);
    }

    public String getSoftwareRunScore(PcSpecs specs, String type, String name) {
        return getActiveProvider().getSoftwareRunScore(specs, type, name);
    }
}
