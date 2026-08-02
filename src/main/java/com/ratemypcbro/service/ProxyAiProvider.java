package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProxyAiProvider implements AiProvider {

    private final ChatClient chatClient;
    private final InstructionService instructionService;

    public ProxyAiProvider(@Qualifier("openAiChatClient") ChatClient chatClient, InstructionService instructionService) {
        this.chatClient = chatClient;
        this.instructionService = instructionService;
    }

    @Override
    public GeneralVerdict getGeneralVerdict(PcSpecs specs, String groundingContext) {
        String systemInstructions = instructionService.getGeneralVerdictSystemInstructions();
        String userPrompt = instructionService.buildGeneralVerdictUserPrompt(specs, groundingContext);

        log.info("🌐 [Proxy API Provider] Sending General Verdict prompt to OpenRouter API...");
        log.debug("🌐 [Proxy Prompt]:\n{}", userPrompt);

        GeneralVerdict verdict = chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .call()
                .entity(GeneralVerdict.class);

        log.info("🌐 [Proxy API Provider] General Verdict Received: rating=[{}], verdict='{}'", 
                verdict != null ? verdict.getRating() : "null", 
                verdict != null ? verdict.getVerdict() : "null");
        log.debug("🌐 [Proxy Result Payload]: {}", verdict);

        return verdict;
    }

    @Override
    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name, String groundingContext) {
        String systemInstructions = instructionService.getSoftwareVerdictSystemInstructions();
        String userPrompt = instructionService.buildSoftwareVerdictUserPrompt(specs, type, name, groundingContext);

        log.info("🌐 [Proxy API Provider] Sending Software Verdict prompt for [{}: {}] to OpenRouter API...", type, name);
        log.debug("🌐 [Proxy Prompt]:\n{}", userPrompt);

        SoftwareVerdict verdict = chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .call()
                .entity(SoftwareVerdict.class);

        log.info("🌐 [Proxy API Provider] Software Verdict Received for [{}]: score=[{}], verdict='{}'", 
                name,
                verdict != null ? verdict.getScore() : "null", 
                verdict != null ? verdict.getVerdict() : "null");
        log.debug("🌐 [Proxy Result Payload]: {}", verdict);

        return verdict;
    }

    @Override
    public String testAi() {
        log.info("🌐 [Proxy API Provider] Executing ping health check...");
        return chatClient.prompt("Respond with only a single rocket emoji if you can hear me.")
                .call()
                .content();
    }
}
