package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import com.ratemypcbro.dto.ToolCallTrace;
import com.ratemypcbro.service.tools.GeneralHardwareTools;
import com.ratemypcbro.service.tools.SoftwareAnalysisTools;
import com.ratemypcbro.service.tools.ToolTraceSupport;
import com.ratemypcbro.service.tools.WebSearchFallbackTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shared {@link AiProvider} implementation backed by a Spring AI {@link ChatClient}.
 * Scopes {@code @Tool} suites per verdict type and collects execution traces via {@code ToolContext}.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractChatClientAiProvider implements AiProvider {

    private final ChatClient chatClient;
    private final InstructionService instructionService;
    private final GeneralHardwareTools generalHardwareTools;
    private final SoftwareAnalysisTools softwareAnalysisTools;
    private final WebSearchFallbackTools webSearchFallbackTools;
    private final String providerLabel;
    private final String healthPrompt;

    @Override
    public GeneralVerdict getGeneralVerdict(PcSpecs specs, String groundingContext) {
        String systemInstructions = instructionService.getGeneralVerdictSystemInstructions();
        String userPrompt = instructionService.buildGeneralVerdictUserPrompt(specs, groundingContext);
        List<ToolCallTrace> traces = new ArrayList<>();

        log.info("[{}] Sending General Verdict prompt...", providerLabel);
        log.debug("[{}] General Prompt:\n{}", providerLabel, userPrompt);

        GeneralVerdict verdict = chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .tools(generalHardwareTools, webSearchFallbackTools)
                .toolContext(Map.of(ToolTraceSupport.TRACE_CONTEXT_KEY, traces))
                .call()
                .entity(GeneralVerdict.class);

        if (verdict != null && !traces.isEmpty()) {
            verdict.setToolCallTrace(List.copyOf(traces));
        }

        log.info("[{}] General Verdict Received: rating=[{}], verdict='{}'",
                providerLabel,
                verdict != null ? verdict.getRating() : "null",
                verdict != null ? verdict.getVerdict() : "null");
        return verdict;
    }

    @Override
    public SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name, String notes, String groundingContext) {
        String systemInstructions = instructionService.getSoftwareVerdictSystemInstructions();
        String userPrompt = instructionService.buildSoftwareVerdictUserPrompt(specs, type, name, notes, groundingContext);
        List<ToolCallTrace> traces = new ArrayList<>();

        log.info("[{}] Sending Software Verdict prompt for [{}: {}]...", providerLabel, type, name);
        log.debug("[{}] Software Prompt:\n{}", providerLabel, userPrompt);

        SoftwareVerdict verdict = chatClient.prompt()
                .system(systemInstructions)
                .user(userPrompt)
                .tools(softwareAnalysisTools, webSearchFallbackTools)
                .toolContext(Map.of(ToolTraceSupport.TRACE_CONTEXT_KEY, traces))
                .call()
                .entity(SoftwareVerdict.class);

        if (verdict != null && !traces.isEmpty()) {
            verdict.setToolCallTrace(List.copyOf(traces));
        }

        log.info("[{}] Software Verdict Received for [{}]: score=[{}], verdict='{}'",
                providerLabel,
                name,
                verdict != null ? verdict.getScore() : "null",
                verdict != null ? verdict.getVerdict() : "null");
        return verdict;
    }

    @Override
    public String testAi() {
        log.info("[{}] Executing ping health check...", providerLabel);
        return chatClient.prompt(healthPrompt)
                .call()
                .content();
    }
}
