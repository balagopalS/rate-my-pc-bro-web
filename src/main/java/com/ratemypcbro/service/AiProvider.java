package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;

public interface AiProvider {
    GeneralVerdict getGeneralVerdict(PcSpecs specs);
    SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name);
    String testAi();
    
    enum Type {
        LOCAL, PROXY
    }
}
