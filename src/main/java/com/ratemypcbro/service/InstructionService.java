package com.ratemypcbro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private String softwareVerdictSystemInstructions;

    private final ObjectMapper objectMapper;

    public InstructionService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        try (InputStream is = instructionsResource.getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            this.generalVerdictSystemInstructions = root.path("generalVerdictSystemInstructions").asText();
            this.softwareVerdictSystemInstructions = root.path("softwareVerdictSystemInstructions").asText();
            log.info("✅ Loaded system instructions from instructions.json resource.");
        } catch (Exception e) {
            log.error("⚠️ Failed to load instructions.json, using fallback instructions", e);
            this.generalVerdictSystemInstructions = "You are a precise PC hardware analyst.";
            this.softwareVerdictSystemInstructions = "You are a precise software benchmarks estimator.";
        }
    }
}
