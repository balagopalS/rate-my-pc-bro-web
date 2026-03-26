package com.ratemypcbro.controller;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;
import com.ratemypcbro.service.AiOrchestrator;
import com.ratemypcbro.service.AiProvider;
import com.ratemypcbro.service.PcSpecService;
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

    @Test
    void shouldReturnGeneralVerdict() throws Exception {
        GeneralVerdict mockVerdict = new GeneralVerdict("8/10", "Solid build", "Nice rig bro");
        when(pcSpecService.getLocalPcSpecs()).thenReturn(PcSpecs.builder().build());
        when(aiOrchestrator.getGeneralVerdict(any())).thenReturn(mockVerdict);

        mockMvc.perform(get("/ratemypcbro"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rating").value("8/10"))
                .andExpect(jsonPath("$.verdict").value("Solid build"))
                .andExpect(jsonPath("$.roast").value("Nice rig bro"));
    }

    @Test
    void shouldReturnSoftwareVerdict() throws Exception {
        SoftwareVerdict mockVerdict = new SoftwareVerdict("Cyperpunk 2077", "9/10", "Will run great", "Expect 60fps");
        when(pcSpecService.getLocalPcSpecs()).thenReturn(PcSpecs.builder().build());
        when(aiOrchestrator.getSoftwareRunScore(any(), anyString(), anyString())).thenReturn(mockVerdict);

        mockMvc.perform(get("/ratemypcbro/game/cyberpunk"))
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
}
