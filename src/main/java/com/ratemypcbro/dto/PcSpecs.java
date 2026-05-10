package com.ratemypcbro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
// oshi now is giving more granular results for CPU details so we updated the class accordingly 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcSpecs {
    private String os;
    private String computerModel; // Manufacturer + System Model (e.g. Alienware X17)
    private String processor;
    private String cpuDetails; // Core counts and frequencies
    private String graphicsCard;
    private String vram;       // Dedicated GPU VRAM
    private String displays;   // Connected monitor resolutions
    private String totalMemory;
    private String ramDetails; // DDR4/DDR5 and speeds
    private String storage;    // Physical Disk details
    private String motherboard;
    private String powerSource; // Battery health (if laptop) and plugged-in status
}
