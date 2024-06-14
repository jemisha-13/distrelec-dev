package com.distrelec.solrfacetsearch.indexer.impl;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.Collection;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.distrelec.solrfacetsearch.indexer.strategies.impl.DistIndexNameStrategy;
import com.distrelec.solrfacetsearch.service.impl.DistFusionExportService;

import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContextFactory;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.ExporterException;
import de.hybris.platform.solrfacetsearch.indexer.spi.Exporter;

/***
 * Exports data to Fusion. Method exportToDeleteFromIndex is not supported.
 */
public class DistFusionExporter implements Exporter {

    private static final Logger LOG = LoggerFactory.getLogger(DistFusionExporter.class);

    private IndexerBatchContextFactory<IndexerBatchContext> indexerBatchContextFactory;

    private DistFusionExportService distFusionExportService;

    private DistIndexNameStrategy indexNameStrategy;

    public DistFusionExporter(IndexerBatchContextFactory<IndexerBatchContext> indexerBatchContextFactory,
                              DistFusionExportService distFusionExportService,
                              DistIndexNameStrategy indexNameStrategy) {
        this.indexerBatchContextFactory = indexerBatchContextFactory;
        this.distFusionExportService = distFusionExportService;
        this.indexNameStrategy = indexNameStrategy;
    }

    public void exportToUpdateIndex(Collection<SolrInputDocument> solrDocuments, FacetSearchConfig facetSearchConfig,
                                    IndexedType indexedType) throws ExporterException {
        if (isEmpty(solrDocuments)) {
            LOG.warn("solrDocuments should not be empty");
        } else {
            IndexerBatchContext batchContext = this.indexerBatchContextFactory.getContext();
            String indexName = indexNameStrategy.createIndexName(batchContext.getFacetSearchConfig(), indexedType);
            distFusionExportService.pushDocumentsToCollection(solrDocuments, indexName);
        }
    }

    public void exportToDeleteFromIndex(Collection<String> idsToDelete, FacetSearchConfig facetSearchConfig, IndexedType indexedType) throws ExporterException {
        throw new UnsupportedOperationException();
    }

}
