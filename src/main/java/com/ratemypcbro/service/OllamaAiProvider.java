package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
//this is used to get the ai verdict from ollama, form a local AI
// we will need to modify this in the future to handle tools and also for streaming responses 
// as well as structured responses and prompt augmentation 
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

        return chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .call()
                .entity(GeneralVerdict.class);
    }

    @Override
    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name, String groundingContext) {
        String systemInstructions = """
            You are a precise software benchmarks estimator. 
            Return ONLY raw JSON with this exact shape:
            
            {
              "software": "Name of game",
              "score": "85/100",
              "verdict": "Excellent / playable / slow",
              "performance_notes": "Detailed findings here."
            }
            
            DO NOT include formatting tags like ```json or any trailing conversational fluff.
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

        return chatClient.prompt()
                .system(systemInstructions)
                .user(prompt)
                .call()
                .entity(SoftwareVerdict.class);
    }

    @Override
    public String testAi() {
        return chatClient.prompt("Respond with only a single thumbs up emoji if you can hear me.")
                .call()
                .content();
    }
}
