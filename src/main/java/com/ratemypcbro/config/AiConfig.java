package com.ratemypcbro.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    //added wiretap to see what we are sending to the models and what we are getting back 

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel model) {
        return ChatClient.builder(model)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultFunctions(
                    "searchHardwareThermalsAndChassis",
                    "searchBottleneckAndHierarchy",
                    "searchRedditHardwareSentiment",
                    "searchUpgradePathAndMarketPrices",
                    "searchSoftwareRequirements",
                    "searchRedditCommunitySentiment",
                    "searchHardwareCompatibility",
                    "webSearchTool"
                )
                .build();
    }

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel model) {
        return ChatClient.builder(model)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultFunctions(
                    "searchHardwareThermalsAndChassis",
                    "searchBottleneckAndHierarchy",
                    "searchRedditHardwareSentiment",
                    "searchUpgradePathAndMarketPrices",
                    "searchSoftwareRequirements",
                    "searchRedditCommunitySentiment",
                    "searchHardwareCompatibility",
                    "webSearchTool"
                )
                .build();
    }
}
