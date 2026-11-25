package com.ratemypcbro.service;

import com.ratemypcbro.service.tools.GeneralHardwareTools;
import com.ratemypcbro.service.tools.SoftwareAnalysisTools;
import com.ratemypcbro.service.tools.WebSearchFallbackTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OllamaAiProvider extends AbstractChatClientAiProvider {

    public OllamaAiProvider(
            @Qualifier("ollamaChatClient") ChatClient chatClient,
            InstructionService instructionService,
            GeneralHardwareTools generalHardwareTools,
            SoftwareAnalysisTools softwareAnalysisTools,
            WebSearchFallbackTools webSearchFallbackTools) {
        super(
                chatClient,
                instructionService,
                generalHardwareTools,
                softwareAnalysisTools,
                webSearchFallbackTools,
                "Ollama Provider",
                "Respond with only a single thumbs up emoji if you can hear me."
        );
    }
}
