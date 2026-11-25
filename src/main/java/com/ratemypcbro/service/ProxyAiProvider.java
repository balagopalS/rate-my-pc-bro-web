package com.ratemypcbro.service;

import com.ratemypcbro.service.tools.GeneralHardwareTools;
import com.ratemypcbro.service.tools.SoftwareAnalysisTools;
import com.ratemypcbro.service.tools.WebSearchFallbackTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProxyAiProvider extends AbstractChatClientAiProvider {

    public ProxyAiProvider(
            @Qualifier("openAiChatClient") ChatClient chatClient,
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
                "Proxy API Provider",
                "Respond with only a single rocket emoji if you can hear me."
        );
    }
}
