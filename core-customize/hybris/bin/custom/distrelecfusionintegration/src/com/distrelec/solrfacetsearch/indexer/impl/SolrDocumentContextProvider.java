package com.distrelec.solrfacetsearch.indexer.impl;

import java.util.Map;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.provider.ValueResolver;

/***
 * The idea of SolrDocumentContextProviders is to provide a context per document created.
 * This context then can be accessed by individual {@link ValueResolver}
 * This prevents that data used by multiple ValueResolver such as stock-levels, prices, etc. have to be fetched multiple time per document.
 */
public interface SolrDocumentContextProvider<T extends ItemModel> {
    void addDocumentContext(Map<String, Object> context, T item, IndexerBatchContext batchContext);
}
