package com.distrelec.solrfacetsearch.indexer.strategies.impl;

import org.springframework.beans.BeansException;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerStrategy;
import de.hybris.platform.solrfacetsearch.indexer.strategies.impl.DefaultIndexerStrategyFactory;

public class DistIndexerStrategyFactor extends DefaultIndexerStrategyFactory {
    private String distFusionIndexerStrategy;

    @Override
    public IndexerStrategy createIndexerStrategy(FacetSearchConfig facetSearchConfig) throws IndexerException {
        try {
            SolrServerMode mode = facetSearchConfig.getSolrConfig().getMode();
            if (SolrServerMode.FUSION == mode) {
                return this.getApplicationContext().getBean(distFusionIndexerStrategy, IndexerStrategy.class);
            }
            return super.createIndexerStrategy(facetSearchConfig);
        } catch (BeansException e) {
            throw new IndexerException("Cannot create indexer strategy [" + distFusionIndexerStrategy + "]", e);
        }
    }

    public void setDistFusionIndexerStrategy(final String distFusionIndexerStrategy) {
        this.distFusionIndexerStrategy = distFusionIndexerStrategy;
    }
}
