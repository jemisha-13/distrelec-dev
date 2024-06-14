package com.distrelec.solrfacetsearch.indexer.listeners;

import com.distrelec.solrfacetsearch.service.impl.DistFusionExportService;

import de.hybris.platform.solrfacetsearch.indexer.ExtendedIndexerListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

public class DistMigrationStatusListener implements ExtendedIndexerListener {

    private DistFusionExportService distFusionExportService;

    @Override
    public void afterPrepareContext(IndexerContext indexerContext) throws IndexerException {
    }

    @Override
    public void beforeIndex(IndexerContext indexerContext) throws IndexerException {
    }

    @Override
    public void afterIndex(IndexerContext indexerContext) throws IndexerException {
        distFusionExportService.sendMigrationStatus(indexerContext);
    }

    @Override
    public void afterIndexError(IndexerContext indexerContext) throws IndexerException {
        distFusionExportService.sendMigrationStatus(indexerContext);
    }

    public void setDistFusionExportService(DistFusionExportService distFusionExportService) {
        this.distFusionExportService = distFusionExportService;
    }

}
