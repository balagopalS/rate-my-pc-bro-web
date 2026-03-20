package com.ratemypcbro.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PcSpecs {
    private String os;
    private String processor;
    private String graphicsCard;
    private String totalMemory;
    private String storage;
}
