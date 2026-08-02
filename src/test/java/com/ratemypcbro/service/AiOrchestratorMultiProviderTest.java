package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.SoftwareVerdict;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AiOrchestratorMultiProviderTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${spring.ai.openai.api-key:}")
    private String openAiApiKey;

    @BeforeAll
    static void ensureLocalOllamaReady() {
        System.out.println("\n🔍 [IT Readiness Check] Verifying Local Ollama Provider state...");
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        boolean isOllamaUp = checkOllamaHealth(client);

        if (!isOllamaUp) {
            System.out.println("⚠️ Local Ollama server is not running on 11434. Attempting to auto-start 'ollama serve'...");
            try {
                ProcessBuilder pb = new ProcessBuilder("ollama", "serve");
                pb.start();
                
                // Wait for Ollama to initialize (up to 8 seconds)
                for (int i = 0; i < 8; i++) {
                    Thread.sleep(1000);
                    if (checkOllamaHealth(client)) {
                        isOllamaUp = true;
                        System.out.println("✅ Local Ollama server successfully auto-started!");
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to auto-start Ollama process: " + e.getMessage());
            }
        } else {
            System.out.println("✅ Local Ollama server is online and reachable!");
        }

        assertTrue(isOllamaUp, 
            "CRITICAL IT REQUIREMENT FAILED: Local Ollama provider is not running or unstartable on http://localhost:11434.");
    }

    private static boolean checkOllamaHealth(HttpClient client) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/tags"))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    @BeforeEach
    void setUp() {
        // Assert Proxy API Key requirement
        assertNotNull(openAiApiKey, "CRITICAL IT REQUIREMENT FAILED: OpenRouter / Proxy API key property is null.");
        assertFalse(openAiApiKey.isBlank(), "CRITICAL IT REQUIREMENT FAILED: OPENROUTER_API_KEY is missing or empty.");
    }

    @AfterEach
    void resetStateToDefault() {
        System.out.println("🧹 [IT Cleanup] Resetting active AI provider state back to default (LOCAL)...");
        restTemplate.postForEntity("/ratemypcbro/config/provider?type=LOCAL", null, Map.class);
    }

    @Test
    @DisplayName("E2E IT: Test Default LOCAL Mode Workflow (Ollama) via REST Endpoints")
    void testEndToEndLocalModeWorkflow() {
        System.out.println("\n=== [E2E IT] Testing LOCAL Mode HTTP Endpoints (Ollama) ===");

        // 1. GET /ratemypcbro (General Hardware Verdict)
        ResponseEntity<GeneralVerdict> generalResponse = restTemplate.getForEntity("/ratemypcbro", GeneralVerdict.class);
        assertEquals(HttpStatus.OK, generalResponse.getStatusCode());
        GeneralVerdict generalVerdict = generalResponse.getBody();
        assertNotNull(generalVerdict);
        assertNotNull(generalVerdict.getVerdict());
        System.out.println("HTTP GET /ratemypcbro -> Rating: " + generalVerdict.getRating() + "/10, Verdict: " + generalVerdict.getVerdict());

        // 2. GET /ratemypcbro/software (Premiere Pro)
        ResponseEntity<SoftwareVerdict> softwareResponse = restTemplate.getForEntity(
                "/ratemypcbro/software?type=software&name=Premiere Pro", SoftwareVerdict.class);
        assertEquals(HttpStatus.OK, softwareResponse.getStatusCode());
        SoftwareVerdict softwareVerdict = softwareResponse.getBody();
        assertNotNull(softwareVerdict);
        assertNotNull(softwareVerdict.getVerdict());
        System.out.println("HTTP GET /ratemypcbro/software (Premiere Pro) -> Verdict: " + softwareVerdict.getVerdict());

        // 3. GET /ratemypcbro/software (007 First Light)
        ResponseEntity<SoftwareVerdict> gameResponse = restTemplate.getForEntity(
                "/ratemypcbro/software?type=game&name=007 First Light", SoftwareVerdict.class);
        assertEquals(HttpStatus.OK, gameResponse.getStatusCode());
        SoftwareVerdict gameVerdict = gameResponse.getBody();
        assertNotNull(gameVerdict);
        assertNotNull(gameVerdict.getVerdict());
        System.out.println("HTTP GET /ratemypcbro/software (007 First Light) -> Verdict: " + gameVerdict.getVerdict());
        System.out.println("===============================================================\n");
    }

    @Test
    @DisplayName("E2E IT: Test Provider Switch & PROXY Mode Workflow (OpenRouter API) via REST Endpoints")
    void testEndToEndProxyModeWorkflow() {
        System.out.println("\n=== [E2E IT] Testing Provider Switch & PROXY Mode HTTP Endpoints (OpenRouter API) ===");

        // 1. POST /ratemypcbro/config/provider?type=PROXY (Switch to PROXY)
        ResponseEntity<Map> toggleResponse = restTemplate.postForEntity(
                "/ratemypcbro/config/provider?type=PROXY", null, Map.class);
        assertEquals(HttpStatus.OK, toggleResponse.getStatusCode());
        assertEquals("PROXY", toggleResponse.getBody().get("active_provider"));

        // 2. GET /ratemypcbro/config/provider (Verify active provider status)
        ResponseEntity<Map> statusResponse = restTemplate.getForEntity("/ratemypcbro/config/provider", Map.class);
        assertEquals(HttpStatus.OK, statusResponse.getStatusCode());
        assertEquals("PROXY", statusResponse.getBody().get("active_provider"));

        // 3. GET /ratemypcbro (General Hardware Verdict over Proxy API)
        ResponseEntity<GeneralVerdict> generalResponse = restTemplate.getForEntity("/ratemypcbro", GeneralVerdict.class);
        assertEquals(HttpStatus.OK, generalResponse.getStatusCode());
        GeneralVerdict generalVerdict = generalResponse.getBody();
        assertNotNull(generalVerdict);
        assertNotNull(generalVerdict.getVerdict());
        System.out.println("HTTP GET /ratemypcbro (PROXY) -> Rating: " + generalVerdict.getRating() + "/10, Verdict: " + generalVerdict.getVerdict());

        // 4. GET /ratemypcbro/software (007 First Light over Proxy API)
        ResponseEntity<SoftwareVerdict> gameResponse = restTemplate.getForEntity(
                "/ratemypcbro/software?type=game&name=007 First Light", SoftwareVerdict.class);
        assertEquals(HttpStatus.OK, gameResponse.getStatusCode());
        SoftwareVerdict gameVerdict = gameResponse.getBody();
        assertNotNull(gameVerdict);
        assertNotNull(gameVerdict.getVerdict());
        System.out.println("HTTP GET /ratemypcbro/software (PROXY 007 First Light) -> Verdict: " + gameVerdict.getVerdict());
        System.out.println("=========================================================================\n");
    }

    @Test
    @DisplayName("E2E IT: Test Cache Clearing API Endpoint (POST /ratemypcbro/config/cache/clear)")
    void testCacheClearApiWorkflow() {
        System.out.println("\n=== [E2E IT] Testing Cache Clear REST Endpoint ===");
        ResponseEntity<Map> cacheResponse = restTemplate.postForEntity("/ratemypcbro/config/cache/clear", null, Map.class);
        assertEquals(HttpStatus.OK, cacheResponse.getStatusCode());
        assertEquals("success", cacheResponse.getBody().get("status"));
        assertNotNull(cacheResponse.getBody().get("entries_removed"));
        System.out.println("HTTP POST /ratemypcbro/config/cache/clear -> " + cacheResponse.getBody());
        System.out.println("=======================================================\n");
    }
}
