package com.distrelec.solrfacetsearch.indexer.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.solrfacetsearch.indexer.strategies.impl.DistIndexNameStrategy;
import com.distrelec.solrfacetsearch.service.impl.DistFusionExportService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContextFactory;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.ExporterException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistFusionExporterUnitTest {

    private static final String INDEX_NAME = "webshop_product";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private DistFusionExporter distFusionExporter;

    @Mock
    private IndexerBatchContextFactory<IndexerBatchContext> indexerBatchContextFactory;

    @Mock
    private DistFusionExportService distFusionExportService;

    @Mock
    private DistIndexNameStrategy indexNameStrategy;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexedType indexedType;

    @Mock
    private SolrInputDocument solrInputDocument;

    @Mock
    private IndexerBatchContext indexerBatchContext;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(indexerBatchContextFactory.getContext()).thenReturn(indexerBatchContext);
        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
        when(indexNameStrategy.createIndexName(facetSearchConfig, indexedType)).thenReturn(INDEX_NAME);
    }

    @Test
    public void testExportToUpdateIndex() throws ExporterException {
        List<SolrInputDocument> documents = List.of(solrInputDocument);
        distFusionExporter.exportToUpdateIndex(documents, facetSearchConfig, indexedType);

        verify(distFusionExportService, times(1)).pushDocumentsToCollection(documents, INDEX_NAME);
    }

    @Test
    public void testExportToUpdateIndexNoDocuments() throws ExporterException {
        List<SolrInputDocument> documents = List.of();
        distFusionExporter.exportToUpdateIndex(documents, facetSearchConfig, indexedType);

        verify(distFusionExportService, times(0)).pushDocumentsToCollection(any(), any());
    }

    @Test
    public void testExportToDeleteFromIndex() throws ExporterException {
        thrown.expect(UnsupportedOperationException.class);

        distFusionExporter.exportToDeleteFromIndex(List.of(), facetSearchConfig, indexedType);
    }

}
