package com.namics.distrelec.b2b.core.cdn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.namics.distrelec.b2b.core.radware.exception.DistRadwareAPIException;

public interface DistrelecCDNService {
    
    void clearCDNCache() throws JsonProcessingException, DistRadwareAPIException;
        
    void registerCDNCacheClearRequest(String websiteUid) throws JsonProcessingException, DistRadwareAPIException ;
    
}
