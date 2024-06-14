package com.distrelec.solrfacetsearch.indexer.listeners;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.solrfacetsearch.indexer.ExtendedIndexerListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

public class DistExecutionTimesListener implements ExtendedIndexerListener {

    private ModelService modelService;

    @Override
    public void afterPrepareContext(IndexerContext indexerContext) throws IndexerException {
        indexerContext.getFacetSearchConfig().getIndexConfig().setStartTime(LocalDateTime.now());
    }

    @Override
    public void beforeIndex(IndexerContext indexerContext) throws IndexerException {
    }

    @Override
    public void afterIndex(IndexerContext indexerContext) throws IndexerException {
        indexerContext.getFacetSearchConfig().getIndexConfig().setEndTime(LocalDateTime.now());
        updateLastExecutionTimes(indexerContext);
    }

    private void updateLastExecutionTimes(IndexerContext indexerContext) {
        CMSSiteModel cmsSite = indexerContext.getFacetSearchConfig().getIndexConfig().getCmsSite();
        if (cmsSite == null) {
            return;
        }
        LocalDateTime startIndexTime = indexerContext.getFacetSearchConfig().getIndexConfig().getStartTime();

        HashMap<String, Date> newFusionIndexUpdates = new HashMap<>(cmsSite.getLastFusionIndexUpdates());

        // since we create an index per cmsSite, we also need to keep track of the last indexTime per cmsSite
        indexerContext.getFacetSearchConfig().getIndexConfig().getIndexedTypes()
                      .values()
                      .stream()
                      .filter(indexedType -> indexedType.getIdentifier().equals(indexerContext.getIndexedType().getIdentifier()))
                      .forEach(value -> newFusionIndexUpdates.put(indexerContext.getIndexOperation().name() + "_" + value.getIdentifier(),
                                                                  Date.from(startIndexTime.atZone(ZoneId.systemDefault()).toInstant())));

        cmsSite.setLastFusionIndexUpdates(newFusionIndexUpdates);

        modelService.save(cmsSite);
    }

    @Override
    public void afterIndexError(IndexerContext indexerContext) throws IndexerException {
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }
}
