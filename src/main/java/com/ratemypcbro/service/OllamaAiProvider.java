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

    public OllamaAiProvider(@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public GeneralVerdict getGeneralVerdict(PcSpecs specs, String groundingContext) {
        String systemInstructions = """
            You are a precise PC hardware analyst. 
            Provide honest takes. No sarcastic roasting.
            
            CRITICAL: You MUST return absolute, pure raw JSON matching THIS exact shape. 
            Do NOT create arrays for rating or breakdown. 
            Ensure rating is a single number.
            
            {
              "rating": 8.5,
              "verdict": "Short string here",
              "review": "Detailed review string here",
              "breakdown": {
                "cpuScore": 8,
                "gpuScore": 9,
                "ramScore": 7,
                "estimatedPerformance": "1080p Ultra"
              },
              "recommendations": ["rec1", "rec2"]
            }
            
            DO NOT output anything other than the JSON object above.
            """;

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
    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name, String groundingContext) {
        String systemInstructions = """
            You are a precise software benchmarks estimator.
            Analyze the user's hardware specs and the grounding context to predict real-world performance.
            NOTE ON MULTIPLE GPUS: If multiple GPUs are listed in the profile (e.g., 'NVIDIA GeForce RTX 3070 Ti Laptop GPU, Intel(R) UHD Graphics'), ALWAYS base your performance evaluation on the primary DEDICATED GPU (e.g. NVIDIA GeForce / AMD Radeon), NOT the integrated graphics card.
            CRITICAL: Return ONLY a raw JSON instance object with key-value data fields ("software", "score", "verdict", "performance_notes").
            Do NOT include "$schema", "type", or "properties" wrappers.
            """;

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

        log.info("🦙 [Ollama Provider] Sending Software Verdict prompt for [{}: {}] to local LLM...", type, name);
        log.debug("🦙 [Ollama Prompt]:\n{}", prompt);

        SoftwareVerdict verdict = chatClient.prompt()
                .system(systemInstructions)
                .user(prompt)
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
