package com.distrelec.solrfacetsearch.indexer.impl;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexer;
import de.hybris.platform.solrfacetsearch.provider.IdentityProvider;

public class DistIndexer extends DefaultIndexer {

    private static final Logger LOG = LogManager.getLogger(DistIndexer.class);

    public DistIndexer(final Map<String, SolrDocumentContextProvider> contextProvidersByType) {
        this.contextProvidersByType = contextProvidersByType;
    }

    private Map<String, SolrDocumentContextProvider> contextProvidersByType;

    /**
     * Resolves a document context for a SolrInputDocument and saves it on the document. This context is then available to all value-resolver
     */
    @Override
    protected SolrInputDocument createInputDocument(ItemModel model, IndexConfig indexConfig, IndexedType indexedType) throws FieldValueProviderException {
        long t1 = System.currentTimeMillis();

        this.validateCommonRequiredParameters(model, indexConfig, indexedType);
        IndexerBatchContext batchContext = this.getIndexerBatchContextFactory().getContext();
        SolrInputDocument doc = new SolrInputDocument(new String[0]);
        DistSolrInputDocument wrappedDoc = this.createWrappedDocument(batchContext, doc);

        // we are adding context to the document, so that multiple value-resolver can make use of data fetched with a single call.
        SolrDocumentContextProvider contextProvider = contextProvidersByType.get(batchContext.getIndexedType().getCode());
        if (contextProvider != null) {
            contextProvider.addDocumentContext(wrappedDoc.getIndexDocumentContext(), model, batchContext);
        }

        doc.addField("indexOperationId", batchContext.getIndexOperationId());
        this.addCommonFields(doc, batchContext, model);
        this.addIndexedPropertyFields(wrappedDoc, batchContext, model);
        this.addIndexedTypeFields(wrappedDoc, batchContext, model);

        batchContext.getInputDocuments().add(wrappedDoc);

        LOG.info("Time taken to build SolrInputDocument[{}] for {} is: {} ms", doc.get("id"), model.getItemtype(), System.currentTimeMillis() - t1);
        return doc;
    }

    /**
     * Overrides the method in order not to add common fields like PK, catalogVersion, etc.
     */
    @Override
    protected void addCommonFields(SolrInputDocument document, IndexerBatchContext batchContext, ItemModel model) {
        FacetSearchConfig facetSearchConfig = batchContext.getFacetSearchConfig();
        IndexedType indexedType = batchContext.getIndexedType();
        IdentityProvider<ItemModel> identityProvider = this.getIdentityProvider(indexedType);

        String id = identityProvider.getIdentifier(facetSearchConfig.getIndexConfig(), model);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Using SolrInputDocument id [{}]", id);
        }

        document.addField("id", id);

    }

    @Override
    protected DistSolrInputDocument createWrappedDocument(IndexerBatchContext batchContext, SolrInputDocument delegate) {
        return new DistSolrInputDocument(delegate, batchContext, this.getFieldNameProvider(), this.getRangeNameProvider());
    }

}
