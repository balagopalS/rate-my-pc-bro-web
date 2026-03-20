package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.util.stream.Collectors;

@Service
public class PcSpecService {

    public PcSpecs getLocalPcSpecs() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        String gpuInfo = hal.getGraphicsCards().stream()
                .map(GraphicsCard::getName)
                .collect(Collectors.joining(", "));

        long totalMemory = hal.getMemory().getTotal();

        return PcSpecs.builder()
                .os(os.toString())
                .processor(hal.getProcessor().getProcessorIdentifier().getName())
                .graphicsCard(gpuInfo.isEmpty() ? "Integrated/Unknown" : gpuInfo)
                .totalMemory(FormatUtil.formatBytes(totalMemory))
                .storage("Not detected (stateless)")
                .build();
    }
}
