package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.HWDiskStore;
import oshi.hardware.PhysicalMemory;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.util.stream.Collectors;

@Slf4j
@Service
public class PcSpecService {
// lots of new things added to be fetched form oshi and added to our response
// cpu details, storage details, etc
    public PcSpecs getLocalPcSpecs() {
        log.debug("🔍 [OSHI] Initiating hardware spec discovery...");
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        ComputerSystem computerSystem = hal.getComputerSystem();

        // 1. GPU Details
        String gpuInfo = hal.getGraphicsCards().stream()
                .map(GraphicsCard::getName)
                .collect(Collectors.joining(", "));
        
        String vramInfo = hal.getGraphicsCards().stream()
                .map(gpu -> FormatUtil.formatBytes(gpu.getVRam()))
                .collect(Collectors.joining(", "));

        // 2. Granular RAM Details (DDR Type, Speed)
        String ramDetails = hal.getMemory().getPhysicalMemory().stream()
                .map(pm -> pm.getMemoryType() + " @ " + (pm.getClockSpeed() / 1_000_000) + " MHz")
                .distinct()
                .collect(Collectors.joining(", "));
        
        if (ramDetails.isBlank()) {
            ramDetails = "Unknown Speed/Type";
        }

        // 3. Physical Storage Aggregation
        String storageInfo = hal.getDiskStores().stream()
                .map(disk -> disk.getModel().trim() + " (" + FormatUtil.formatBytes(disk.getSize()) + ")")
                .collect(Collectors.joining(", "));
        
        if (storageInfo.isBlank()) {
            storageInfo = "No physical disks detected";
        }

        // 4. Motherboard Baseboard tracking
        String mobo = computerSystem.getBaseboard().getManufacturer() + " " + computerSystem.getBaseboard().getModel();

        // 4b. System Model (Laptop / Prebuilt identifier)
        String systemModel = computerSystem.getManufacturer() + " " + computerSystem.getModel();

        // 5. CPU Details
        String cpuDetails = String.format("%d Physical, %d Logical Cores @ %.2f GHz",
                hal.getProcessor().getPhysicalProcessorCount(),
                hal.getProcessor().getLogicalProcessorCount(),
                hal.getProcessor().getMaxFreq() / 1_000_000_000.0);

        // 6. Displays
        String displays = hal.getDisplays().stream()
                .map(d -> "Display: " + d.toString().trim())
                .collect(Collectors.joining(", "));
        if (displays.isBlank()) {
            displays = "No external displays detected";
        }

        // 7. Battery / Power
        String powerSource = hal.getPowerSources().stream()
                .map(p -> String.format("Current Capacity: %s/%s (Plugged in: %s)",
                        p.getCurrentCapacity(), p.getMaxCapacity(), p.isPowerOnLine()))
                .collect(Collectors.joining(", "));
        if (powerSource.isBlank()) {
            powerSource = "Desktop Mains (No Battery)";
        }

        long totalMemory = hal.getMemory().getTotal();

        PcSpecs specs = PcSpecs.builder()
                .os(os.toString())
                .computerModel(systemModel)
                .processor(hal.getProcessor().getProcessorIdentifier().getName().trim())
                .cpuDetails(cpuDetails)
                .graphicsCard(gpuInfo.isEmpty() ? "Integrated/Unknown" : gpuInfo)
                .vram(vramInfo.isEmpty() ? "Unknown" : vramInfo)
                .displays(displays)
                .totalMemory(FormatUtil.formatBytes(totalMemory))
                .ramDetails(ramDetails)
                .storage(storageInfo)
                .motherboard(mobo)
                .powerSource(powerSource)
                .build();

        log.info("🖥️ [OSHI] Hardware Discovered: CPU=[{}], GPU=[{}], RAM=[{}], OS=[{}]", 
                specs.getProcessor(), specs.getGraphicsCard(), specs.getTotalMemory(), specs.getOs());
        log.debug("🖥️ [OSHI Full Specs]: {}", specs);

        return specs;
    }
}
