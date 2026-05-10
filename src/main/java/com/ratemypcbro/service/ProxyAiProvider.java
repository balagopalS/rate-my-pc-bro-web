package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
// here too we need to implement tools and streaming responses and structured responses and prompt augmentation
public class ProxyAiProvider implements AiProvider {

    private final ChatClient chatClient;

    public ProxyAiProvider(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public GeneralVerdict getGeneralVerdict(PcSpecs specs) {
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
            3. Provide thorough 'review' including potential bottlenecks or build quality.
            4. Score individual hardware in 'breakdown'.
            5. Recommend actionable upgrades based on the specs.
            """,
            specs.getOs(), specs.getComputerModel(), specs.getProcessor(), specs.getCpuDetails(), specs.getMotherboard(), specs.getGraphicsCard(), 
            specs.getVram(), specs.getDisplays(), specs.getTotalMemory(), specs.getRamDetails(), specs.getStorage(), specs.getPowerSource()
        );

        return chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .call()
                .entity(GeneralVerdict.class);
    }

    @Override
    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name) {
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
            """,
            type, name,
            specs.getComputerModel(), specs.getOs(), specs.getProcessor(), specs.getCpuDetails(),
            specs.getGraphicsCard(), specs.getVram(), specs.getTotalMemory(), specs.getRamDetails(),
            specs.getStorage(), specs.getDisplays()
        );

        return chatClient.prompt()
                .system(systemInstructions)
                .user(prompt)
                .call()
                .entity(SoftwareVerdict.class);
    }

    @Override
    public String testAi() {
        return chatClient.prompt("Respond with only a single rocket emoji if you can hear me.")
                .call()
                .content();
    }
}
