package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProxyAiProviderTest {

    @Autowired
    private ProxyAiProvider proxyAiProvider;

    @Autowired
    private PcSpecService pcSpecService;

    @Test
    void testAiReachable() {
        PcSpecs specs = pcSpecService.getLocalPcSpecs();
        GeneralVerdict response = proxyAiProvider.getGeneralVerdict(specs);
        assertNotNull(response);
        assertNotNull(response.getRating());
        assertNotNull(response.getVerdict());
        assertNotNull(response.getRoast());
        System.out.println("AI Response: " + response);
    }
}
