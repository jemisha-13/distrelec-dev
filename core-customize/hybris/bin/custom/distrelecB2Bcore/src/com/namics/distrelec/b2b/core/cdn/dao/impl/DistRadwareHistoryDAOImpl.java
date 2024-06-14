package com.namics.distrelec.b2b.core.cdn.dao.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.cdn.dao.DistRadwareHistoryDAO;
import com.namics.distrelec.b2b.core.deployment.DeploymentTimestampService;
import com.namics.distrelec.b2b.core.model.radware.DistRadwareCacheHistoryModel;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DistRadwareHistoryDAOImpl implements DistRadwareHistoryDAO {

    private static final String RADWARE_CLEAR_CACHE_QUERY = "select distinct {radwarehistory.PK} from {DistRadwareCacheHistory As radwarehistory} where {radwarehistory.radwareId} =?radwareAppCode";

    private static final String RADWARE_APPS_CLEAR_CACHE = "select {radwarehistory.PK} from {DistRadwareCacheHistory As radwarehistory} where {radwarehistory.cacheClearingRequired} =?cacheClearingRequired or {radwarehistory.cacheClearTimestamp} < ?deploymentTimestamp";

    @Autowired
    private DeploymentTimestampService deploymentTimestampService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ModelService modelService;

    @Override
    public Date getLastCacheClearDate(String radwareAppCode) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("radwareAppCode", radwareAppCode);

        SearchResult<DistRadwareCacheHistoryModel> search = getFlexibleSearchService().search(RADWARE_CLEAR_CACHE_QUERY, params);
        if (search.getCount() == 0) {
            return createRadwareSearchHistory(radwareAppCode, Boolean.TRUE);
        } else {
            return search.getResult().get(0).getCacheClearTimestamp();
        }
    }

    /**
     * Clears application if they are flagged to be cleared, or if they are not cleared after a deployment.
     */
    @Override
    public List<DistRadwareCacheHistoryModel> getRadwareAppsForClearingCache(Boolean cacheClearingRequired) {
        Date deploymentTimestamp = deploymentTimestampService.getTimestamp();

        HashMap<String, Object> params = new HashMap<>();
        params.put("cacheClearingRequired", cacheClearingRequired);
        params.put("deploymentTimestamp", deploymentTimestamp);

        SearchResult<DistRadwareCacheHistoryModel> search = getFlexibleSearchService().search(RADWARE_APPS_CLEAR_CACHE, params);
        if (search.getCount() == 0) {
            return Collections.emptyList();
        } else {
            return search.getResult();
        }
    }

    @Override
    public void updateRadwareCacheClearFlag(String radwareAppCode, boolean isUpdateRequired) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("radwareAppCode", radwareAppCode);

        SearchResult<DistRadwareCacheHistoryModel> search = getFlexibleSearchService().search(RADWARE_CLEAR_CACHE_QUERY, params);
        if (search.getCount() > 0) {
            DistRadwareCacheHistoryModel radwareSearchHistory = search.getResult().get(0);
            radwareSearchHistory.setCacheClearingRequired(isUpdateRequired);
            if (!isUpdateRequired) {
                radwareSearchHistory.setCacheClearTimestamp(new Date());
            }
            getModelService().save(radwareSearchHistory);
        } else {
            createRadwareSearchHistory(radwareAppCode, isUpdateRequired);
        }

    }

    private Date createRadwareSearchHistory(String radwareAppCode, Boolean isUpdateRequired) {
        Date creationDate = new Date();
        DistRadwareCacheHistoryModel radwareSearchHistory = getModelService().create(DistRadwareCacheHistoryModel.class);
        radwareSearchHistory.setRadwareId(radwareAppCode);
        radwareSearchHistory.setCacheClearTimestamp(creationDate);
        radwareSearchHistory.setCacheClearingRequired(isUpdateRequired);
        getModelService().save(radwareSearchHistory);
        return creationDate;
    }

    // Getter and Setter

    public void setDeploymentTimestampService(DeploymentTimestampService deploymentTimestampService) {
        this.deploymentTimestampService = deploymentTimestampService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
