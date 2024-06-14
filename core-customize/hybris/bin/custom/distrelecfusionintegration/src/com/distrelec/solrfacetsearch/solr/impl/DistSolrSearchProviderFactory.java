package com.distrelec.solrfacetsearch.solr.impl;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.DefaultSolrSearchProviderFactory;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrStandaloneSearchProvider;

public class DistSolrSearchProviderFactory extends DefaultSolrSearchProviderFactory {

    private SolrStandaloneSearchProvider solrDistStandaloneSearchProvider;

    @Override
    public SolrSearchProvider getSearchProvider(FacetSearchConfig facetSearchConfig, IndexedType indexedType) throws SolrServiceException {

        SolrServerMode mode = facetSearchConfig.getSolrConfig().getMode();
        if (SolrServerMode.FUSION == mode) {
            return solrDistStandaloneSearchProvider;
        }
        return super.getSearchProvider(facetSearchConfig, indexedType);
    }

    public void setSolrDistStandaloneSearchProvider(final SolrStandaloneSearchProvider solrDistStandaloneSearchProvider) {
        this.solrDistStandaloneSearchProvider = solrDistStandaloneSearchProvider;
    }
}
