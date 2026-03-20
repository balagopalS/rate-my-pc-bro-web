package com.ratemypcbro.service;

import com.ratemypcbro.dto.PcSpecs;

public interface AiProvider {
    String getGeneralVerdict(PcSpecs specs);
    String getSoftwareRunScore(PcSpecs specs, String type, String name);
    
    enum Type {
        LOCAL, PROXY
    }
}
