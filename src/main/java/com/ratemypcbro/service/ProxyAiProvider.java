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

        String userPrompt = String.format("""
            Analyze this computer deeply:
            Operating System: %s
            System Model: %s
            CPU: %s
            CPU Architecture/Clocks: %s
            Motherboard: %s
            GPU: %s
            VRAM: %s
            Connected Displays: %s
            Total RAM Capacity: %s
            RAM Speed/Gen: %s
            Storage Breakdown: %s
            Battery/Power Status: %s
            
            1. Set numeric 'rating' /10.
            2. Generate 'verdict'.
            3. Provide thorough 'review' including potential bottlenecks or build quality. Base your insights strictly on the GROUNDING CONTEXT provided below.
            4. Score individual hardware in 'breakdown'.
            5. Recommend actionable upgrades based on the specs.
            
            == GROUNDING CONTEXT ==
            %s
            =======================
            """,
            specs.getOs(), specs.getComputerModel(), specs.getProcessor(), specs.getCpuDetails(), specs.getMotherboard(), specs.getGraphicsCard(), 
            specs.getVram(), specs.getDisplays(), specs.getTotalMemory(), specs.getRamDetails(), specs.getStorage(), specs.getPowerSource(),
            groundingContext
        );

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

        String prompt = String.format("""
            Predict real-world performance of this system for the %s: '%s'.
            
            Hardware Profile:
            - System Model: %s
            - Operating System: %s
            - CPU: %s (%s)
            - GPU: %s
            - VRAM: %s
            - Memory: %s (%s)
            - Storage: %s
            - Target Resolution/Displays: %s
            
            == GROUNDING CONTEXT ==
            %s
            =======================
            
            Base your verdict strictly on the real-world experiences and official requirements found in the GROUNDING CONTEXT.
            """,
            type, name,
            specs.getComputerModel(), specs.getOs(), specs.getProcessor(), specs.getCpuDetails(),
            specs.getGraphicsCard(), specs.getVram(), specs.getTotalMemory(), specs.getRamDetails(),
            specs.getStorage(), specs.getDisplays(),
            groundingContext
        );

        log.info("🌐 [Proxy API Provider] Sending Software Verdict prompt for [{}: {}] to OpenRouter API...", type, name);
        log.debug("🌐 [Proxy Prompt]:\n{}", prompt);

        SoftwareVerdict verdict = chatClient.prompt()
                .system(systemInstructions)
                .user(prompt)
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
