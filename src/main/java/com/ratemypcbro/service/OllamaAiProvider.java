package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class OllamaAiProvider implements AiProvider {

    private final ChatClient chatClient;

    public OllamaAiProvider(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String getGeneralVerdict(PcSpecs specs) {
        String prompt = String.format(
            "You are a professional PC hardware expert with a sense of humor. " +
            "Review the following PC specs and provide a 'verdict' including a rating out of 10 and a roast/praise. " +
            "Specs: [OS: %s, CPU: %s, GPU: %s, RAM: %s]. " +
            "Respond ONLY with a valid JSON object like {\"rating\": \"...\", \"verdict\": \"...\", \"roast\": \"...\"}",
            specs.getOs(), specs.getProcessor(), specs.getGraphicsCard(), specs.getTotalMemory()
        );
        return chatClient.prompt(prompt).call().content();
    }

    @Override
    public String getSoftwareRunScore(PcSpecs specs, String type, String name) {
        String prompt = String.format(
            "You are a professional PC hardware expert. " +
            "Predict the performance of the following PC for the %s '%s'. " +
            "Specs: [OS: %s, CPU: %s, GPU: %s, RAM: %s]. " +
            "Consider real-world benchmarks and system requirements. " +
            "Respond ONLY with a valid JSON object like {\"software\": \"%s\", \"score\": \"...\", \"verdict\": \"...\", \"performance_notes\": \"...\"}",
            type, name, specs.getOs(), specs.getProcessor(), specs.getGraphicsCard(), specs.getTotalMemory(), name
        );
        return chatClient.prompt(prompt).call().content();
    }
}
