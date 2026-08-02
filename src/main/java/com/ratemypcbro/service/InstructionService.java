package com.ratemypcbro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratemypcbro.dto.PcSpecs;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@Getter
public class InstructionService {

    @Value("classpath:instructions.json")
    private Resource instructionsResource;

    private String generalVerdictSystemInstructions;
    private String generalVerdictUserPromptTemplate;

    private String softwareVerdictSystemInstructions;
    private String softwareVerdictUserPromptTemplate;

    private final ObjectMapper objectMapper;

    public InstructionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try (InputStream is = instructionsResource.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            this.generalVerdictSystemInstructions = root.path("generalVerdictSystemInstructions").asText();
            this.generalVerdictUserPromptTemplate = root.path("generalVerdictUserPromptTemplate").asText();

            this.softwareVerdictSystemInstructions = root.path("softwareVerdictSystemInstructions").asText();
            this.softwareVerdictUserPromptTemplate = root.path("softwareVerdictUserPromptTemplate").asText();

            log.info("✅ Successfully loaded all system instructions & prompt templates from instructions.json resource.");
        } catch (Exception e) {
            log.error("⚠️ Failed to load instructions.json, using fallback instructions", e);
            this.generalVerdictSystemInstructions = "You are a precise PC hardware analyst.";
            this.generalVerdictUserPromptTemplate = "Analyze hardware: %s %s %s %s %s %s %s %s %s %s %s %s Grounding: %s";
            this.softwareVerdictSystemInstructions = "You are a versatile software performance analyst.";
            this.softwareVerdictUserPromptTemplate = "Evaluate app %s: %s for specs %s %s %s %s %s %s %s %s %s %s Grounding: %s";
        }
    }

    /**
     * Formats the General Verdict user prompt template with hardware specs & web grounding context.
     */
    public String buildGeneralVerdictUserPrompt(PcSpecs specs, String groundingContext) {
        return String.format(
            generalVerdictUserPromptTemplate,
            specs.getOs(), specs.getComputerModel(), specs.getProcessor(), specs.getCpuDetails(),
            specs.getMotherboard(), specs.getGraphicsCard(), specs.getVram(), specs.getDisplays(),
            specs.getTotalMemory(), specs.getRamDetails(), specs.getStorage(), specs.getPowerSource(),
            groundingContext
        );
    }

    /**
     * Formats the Software Verdict user prompt template with app details, specs & web grounding context.
     */
    public String buildSoftwareVerdictUserPrompt(PcSpecs specs, String type, String name, String groundingContext) {
        return String.format(
            softwareVerdictUserPromptTemplate,
            type, name,
            specs.getComputerModel(), specs.getOs(), specs.getProcessor(), specs.getCpuDetails(),
            specs.getGraphicsCard(), specs.getVram(), specs.getTotalMemory(), specs.getRamDetails(),
            specs.getStorage(), specs.getDisplays(),
            groundingContext
        );
    }
}
