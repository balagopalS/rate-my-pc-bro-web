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
public class OllamaAiProvider implements AiProvider {

    private final ChatClient chatClient;
    private final InstructionService instructionService;

    public OllamaAiProvider(@Qualifier("ollamaChatClient") ChatClient chatClient, InstructionService instructionService) {
        this.chatClient = chatClient;
        this.instructionService = instructionService;
    }

    @Override
    public GeneralVerdict getGeneralVerdict(PcSpecs specs, String groundingContext) {
        String systemInstructions = instructionService.getGeneralVerdictSystemInstructions();
        String userPrompt = instructionService.buildGeneralVerdictUserPrompt(specs, groundingContext);

        log.info("🦙 [Ollama Provider] Sending General Verdict prompt to local LLM...");
        log.debug("🦙 [Ollama Prompt]:\n{}", userPrompt);

        GeneralVerdict verdict = chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .call()
                .entity(GeneralVerdict.class);

        log.info("🦙 [Ollama Provider] General Verdict Received: rating=[{}], verdict='{}'", 
                verdict != null ? verdict.getRating() : "null", 
                verdict != null ? verdict.getVerdict() : "null");
        log.debug("🦙 [Ollama Result Payload]: {}", verdict);

        return verdict;
    }

    @Override
    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name, String notes, String groundingContext) {
        String systemInstructions = instructionService.getSoftwareVerdictSystemInstructions();
        String userPrompt = instructionService.buildSoftwareVerdictUserPrompt(specs, type, name, notes, groundingContext);

        log.info("🦙 [Ollama Provider] Sending Software Verdict prompt for [{}: {}] to local LLM...", type, name);
        log.debug("🦙 [Ollama Prompt]:\n{}", userPrompt);

        SoftwareVerdict verdict = chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .call()
                .entity(SoftwareVerdict.class);

        log.info("🦙 [Ollama Provider] Software Verdict Received for [{}]: score=[{}], verdict='{}'", 
                name,
                verdict != null ? verdict.getScore() : "null", 
                verdict != null ? verdict.getVerdict() : "null");
        log.debug("🦙 [Ollama Result Payload]: {}", verdict);

        return verdict;
    }

    @Override
    public String testAi() {
        log.info("🦙 [Ollama Provider] Executing ping health check...");
        return chatClient.prompt("Respond with only a single thumbs up emoji if you can hear me.")
                .call()
                .content();
    }
}
