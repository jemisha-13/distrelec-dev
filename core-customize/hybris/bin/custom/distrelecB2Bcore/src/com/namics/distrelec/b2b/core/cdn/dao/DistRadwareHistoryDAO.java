package com.namics.distrelec.b2b.core.cdn.dao;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.model.radware.DistRadwareCacheHistoryModel;

public interface DistRadwareHistoryDAO {

    Date getLastCacheClearDate(String radwareAppCode);
            
    void updateRadwareCacheClearFlag(String radwareAppCode,boolean isUpdateRequired);
    
    List<DistRadwareCacheHistoryModel> getRadwareAppsForClearingCache(Boolean cacheClearingRequired);
        
}
