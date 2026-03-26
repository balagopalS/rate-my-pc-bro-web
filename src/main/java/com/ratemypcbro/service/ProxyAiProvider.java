package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ProxyAiProvider implements AiProvider {

    private final ChatClient chatClient;

    public ProxyAiProvider(@Qualifier("openAiChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public GeneralVerdict getGeneralVerdict(PcSpecs specs) {
        String prompt = String.format(
            "You are a professional PC hardware expert with a sense of humor. " +
            "Review the following PC specs and provide a 'verdict' including a rating out of 10 and a roast/praise. " +
            "Specs: [OS: %s, CPU: %s, GPU: %s, RAM: %s]. ",
            specs.getOs(), specs.getProcessor(), specs.getGraphicsCard(), specs.getTotalMemory()
        );
        return chatClient.prompt(prompt)
                .call()
                .entity(GeneralVerdict.class);
    }

    @Override
    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name) {
        String prompt = String.format(
            "You are a professional PC hardware expert. " +
            "Predict the performance of the following PC for the %s '%s'. " +
            "Specs: [OS: %s, CPU: %s, GPU: %s, RAM: %s]. " +
            "Consider real-world benchmarks and system requirements.",
            type, name, specs.getOs(), specs.getProcessor(), specs.getGraphicsCard(), specs.getTotalMemory()
        );
        return chatClient.prompt(prompt)
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
