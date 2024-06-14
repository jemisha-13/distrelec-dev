package com.distrelec.solrfacetsearch.indexer.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;

import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultSolrInputDocument;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.RangeNameProvider;

public class DistSolrInputDocument extends DefaultSolrInputDocument {

    private Map<String, Object> indexDocumentContext = new HashMap<>();

    public DistSolrInputDocument(SolrInputDocument delegate,
                                 IndexerBatchContext batchContext,
                                 FieldNameProvider fieldNameProvider,
                                 RangeNameProvider rangeNameProvider) {
        super(delegate, batchContext, fieldNameProvider, rangeNameProvider);
    }

    @Override
    public void startDocument() {
        super.endDocument();
    }

    @Override
    public void endDocument() {
        super.endDocument();
    }

    public Map<String, Object> getIndexDocumentContext() {
        return indexDocumentContext;
    }

}
