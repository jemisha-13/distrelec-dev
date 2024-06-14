package com.distrelec.solrfacetsearch.indexer.strategies;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

public interface IndexNameStrategy {
    String createIndexName(FacetSearchConfig facetSearchConfig, IndexedType indexedType);
}
