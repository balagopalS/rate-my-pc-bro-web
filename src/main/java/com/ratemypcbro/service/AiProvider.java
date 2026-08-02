package com.ratemypcbro.service;

import com.ratemypcbro.dto.GeneralVerdict;
import com.ratemypcbro.dto.PcSpecs;
import com.ratemypcbro.dto.SoftwareVerdict;

//this is the interface that will be used to get the ai verdict
// all ai providers will implement this interface
//possibly needs modification in the future for tools
public interface AiProvider {
    GeneralVerdict getGeneralVerdict(PcSpecs specs, String groundingContext);
    default SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name, String groundingContext) {
        return getSoftwareRunScore(specs, type, name, null, groundingContext);
    }
    SoftwareVerdict getSoftwareRunScore(PcSpecs specs, String type, String name, String notes, String groundingContext);

    String testAi();
    
    enum Type {
        LOCAL, PROXY
    }
}
