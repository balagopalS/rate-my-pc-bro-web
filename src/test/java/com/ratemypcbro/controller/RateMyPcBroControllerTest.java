package com.ratemypcbro.controller;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import com.ratemypcbro.service.AiOrchestrator;
import com.ratemypcbro.service.AiProvider;
import com.ratemypcbro.service.PcSpecService;
import com.ratemypcbro.service.WebScraper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RateMyPcBroController.class)
class RateMyPcBroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AiOrchestrator aiOrchestrator;

    @MockitoBean
    private PcSpecService pcSpecService;

    @MockitoBean
    private WebScraper webScraper;

    @MockitoBean
    private com.ratemypcbro.service.AgentToolService agentToolService;

    @Test
    void shouldReturnGeneralVerdict() throws Exception {
        GeneralVerdict mockVerdict = GeneralVerdict.builder()
                .rating(8.5)
                .verdict("Solid build")
                .review("Nice rig bro")
                .build();

        when(pcSpecService.getLocalPcSpecs()).thenReturn(PcSpecs.builder().build());
        when(aiOrchestrator.getGeneralVerdict(any())).thenReturn(mockVerdict);

        mockMvc.perform(get("/ratemypcbro"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rating").value(8.5))
                .andExpect(jsonPath("$.verdict").value("Solid build"))
                .andExpect(jsonPath("$.review").value("Nice rig bro"));
    }

    @Test
    void shouldReturnSoftwareVerdict() throws Exception {
        SoftwareVerdict mockVerdict = new SoftwareVerdict("Cyperpunk 2077", "9/10", "Will run great", "Expect 60fps");
        when(pcSpecService.getLocalPcSpecs()).thenReturn(PcSpecs.builder().build());
        when(aiOrchestrator.getSoftwareRunScore(any(), anyString(), anyString(), any())).thenReturn(mockVerdict);

        mockMvc.perform(get("/ratemypcbro/software")
                .param("type", "game")
                .param("name", "cyberpunk"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.software").value("Cyperpunk 2077"))
                .andExpect(jsonPath("$.score").value("9/10"));
    }

    @Test
    void shouldToggleProvider() throws Exception {
        mockMvc.perform(post("/ratemypcbro/config/provider")
                .param("type", "PROXY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.active_provider").value("PROXY"));
    }

    @Test
    void shouldGetActiveProvider() throws Exception {
        when(aiOrchestrator.getProviderType()).thenReturn(AiProvider.Type.LOCAL);

        mockMvc.perform(get("/ratemypcbro/config/provider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active_provider").value("LOCAL"));
    }

    @Test
    void shouldReturnHealthStatus() throws Exception {
        when(aiOrchestrator.testAi()).thenReturn("?");
        when(aiOrchestrator.getProviderType()).thenReturn(AiProvider.Type.PROXY);

        mockMvc.perform(get("/ratemypcbro/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AI is reachable"))
                .andExpect(jsonPath("$.response").value("?"))
                .andExpect(jsonPath("$.active_provider").value("PROXY"));
    }

    @Test
    void shouldClearCache() throws Exception {
        when(webScraper.clearCache()).thenReturn(5);

        mockMvc.perform(post("/ratemypcbro/config/cache/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.entries_removed").value(5));
    }
}
