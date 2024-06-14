package com.distrelec.solrfacetsearch.indexer.workers.impl;

import java.util.List;

import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerBatchStrategy;
import de.hybris.platform.solrfacetsearch.indexer.workers.IndexerWorkerParameters;
import de.hybris.platform.solrfacetsearch.indexer.workers.impl.DefaultIndexerWorker;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import ma.glasnost.orika.impl.ConfigurableMapper;

public class DistIndexerWorker extends DefaultIndexerWorker {

    private IndexerWorkerParameters supTypeWorkerParameters;

    private ConfigurableMapper facetSearchConfigMapper;

    public DistIndexerWorker(final ConfigurableMapper facetSearchConfigMapper) {
        this.facetSearchConfigMapper = facetSearchConfigMapper;
    }

    @Override
    protected void doRun() throws IndexerException, FacetConfigServiceException, SolrServiceException, InterruptedException {
        FacetSearchConfig copyFacetSearchConfig = copyOfFacetSearchConfig();

        IndexedType indexedType = this.getFacetSearchConfigService().resolveIndexedType(copyFacetSearchConfig, this.supTypeWorkerParameters.getIndexedType());

        List<IndexedProperty> indexedProperties = this.getFacetSearchConfigService()
                                                      .resolveIndexedProperties(copyFacetSearchConfig, indexedType,
                                                                                this.supTypeWorkerParameters.getIndexedProperties());
        SolrSearchProvider solrSearchProvider = this.getSolrSearchProviderFactory().getSearchProvider(copyFacetSearchConfig, indexedType);
        Index index = solrSearchProvider.resolveIndex(copyFacetSearchConfig, indexedType, this.supTypeWorkerParameters.getIndex());
        IndexerBatchStrategy indexerBatchStrategy = this.getIndexerBatchStrategyFactory().createIndexerBatchStrategy(copyFacetSearchConfig);
        indexerBatchStrategy.setIndexOperationId(this.supTypeWorkerParameters.getIndexOperationId());
        indexerBatchStrategy.setIndexOperation(this.supTypeWorkerParameters.getIndexOperation());
        indexerBatchStrategy.setExternalIndexOperation(this.supTypeWorkerParameters.isExternalIndexOperation());
        indexerBatchStrategy.setFacetSearchConfig(copyFacetSearchConfig);
        indexerBatchStrategy.setIndexedType(indexedType);
        indexerBatchStrategy.setIndexedProperties(indexedProperties);
        indexerBatchStrategy.setIndex(index);
        indexerBatchStrategy.setIndexerHints(this.supTypeWorkerParameters.getIndexerHints());
        indexerBatchStrategy.setPks(this.supTypeWorkerParameters.getPks());
        indexerBatchStrategy.execute();
    }

    /**
     * @return defensive copy to prevent concurrency issues between all worker threads
     */
    private FacetSearchConfig copyOfFacetSearchConfig() {
        FacetSearchConfig originalFacetSearchConfig = this.supTypeWorkerParameters.getFacetSearchConfigData();
        return this.facetSearchConfigMapper.map(originalFacetSearchConfig, FacetSearchConfig.class);
    }

    @Override
    public void initialize(IndexerWorkerParameters workerParameters) {
        ServicesUtil.validateParameterNotNull(workerParameters, "workerParameters must not be null");
        super.initialize(workerParameters);
        this.supTypeWorkerParameters = workerParameters;
    }
}
