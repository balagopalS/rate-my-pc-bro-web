package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class AgentToolServiceTest {

    @Test
    void hardwareBaselineCacheKeyIncludesRamDetails() {
        WebScraper scraper = mock(WebScraper.class);
        when(scraper.search(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.eq(3), org.mockito.ArgumentMatchers.anyList()))
                .thenReturn("grounding");
        AgentToolService service = new AgentToolService(scraper);
        PcSpecs ddr4 = PcSpecs.builder().processor("CPU").graphicsCard("GPU").totalMemory("16 GB").ramDetails("DDR4 3200").build();
        PcSpecs ddr5 = PcSpecs.builder().processor("CPU").graphicsCard("GPU").totalMemory("16 GB").ramDetails("DDR5 5600").build();

        service.getGeneralGrounding(ddr4);
        service.getGeneralGrounding(ddr4);
        service.getGeneralGrounding(ddr5);

        verify(scraper, times(2)).search(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.eq(3), org.mockito.ArgumentMatchers.anyList());
        assertEquals(2, service.clearCache());
    }
}
