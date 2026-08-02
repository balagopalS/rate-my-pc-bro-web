package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PcSpecServiceTest {

    @Test
    @DisplayName("Verify OSHI hardware spec discovery gathers non-null system fields")
    void testLocalPcSpecDiscovery() {
        PcSpecService pcSpecService = new PcSpecService();
        PcSpecs specs = pcSpecService.getLocalPcSpecs();

        assertNotNull(specs, "PcSpecs object should not be null");
        assertNotNull(specs.getOs(), "Operating system should be detected");
        assertNotNull(specs.getComputerModel(), "Computer model should be detected");
        assertNotNull(specs.getProcessor(), "Processor should be detected");
        assertNotNull(specs.getCpuDetails(), "CPU details should be detected");
        assertNotNull(specs.getGraphicsCard(), "Graphics card should be detected");
        assertNotNull(specs.getVram(), "VRAM should be detected");
        assertNotNull(specs.getTotalMemory(), "Total memory should be detected");
        assertNotNull(specs.getRamDetails(), "RAM details should be detected");
        assertNotNull(specs.getStorage(), "Storage info should be detected");
        assertNotNull(specs.getMotherboard(), "Motherboard should be detected");
        assertNotNull(specs.getPowerSource(), "Power source should be detected");
        assertNotNull(specs.getDisplays(), "Displays info should be detected");

        System.out.println("Discovered System Specs: " + specs);
    }
}
