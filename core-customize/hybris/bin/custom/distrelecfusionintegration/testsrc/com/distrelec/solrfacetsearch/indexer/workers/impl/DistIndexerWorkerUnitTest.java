package com.distrelec.solrfacetsearch.indexer.workers.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerBatchStrategy;
import de.hybris.platform.solrfacetsearch.indexer.strategies.IndexerBatchStrategyFactory;
import de.hybris.platform.solrfacetsearch.indexer.workers.IndexerWorkerParameters;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import ma.glasnost.orika.impl.ConfigurableMapper;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistIndexerWorkerUnitTest {

    private static final long INDEX_OPERATION_ID = 111125495540089144L;

    @InjectMocks
    private DistIndexerWorker distIndexerWorker;

    @Mock
    private IndexerWorkerParameters supTypeWorkerParameters;

    @Mock
    private ConfigurableMapper facetSearchConfigMapper;

    @Mock
    private IndexerBatchStrategyFactory indexerBatchStrategyFactory;

    @Mock
    private FacetSearchConfigService facetSearchConfigService;

    @Mock
    private SolrSearchProviderFactory solrSearchProviderFactory;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private FacetSearchConfig copyFacetSearchConfig;

    @Mock
    private IndexerWorkerParameters indexerWorkerParameters;

    @Mock
    private IndexedType indexedType;

    @Mock
    private IndexedProperty indexedProperty1;

    @Mock
    private IndexedProperty indexedProperty2;

    private List<IndexedProperty> indexedProperties;

    @Mock
    private IndexerBatchStrategy indexerBatchStrategy;

    @Mock
    private SolrSearchProvider solrSearchProvider;

    @Mock
    private Index index;

    private PK pk1;

    private PK pk2;

    private List<PK> pks;

    @Before
    public void init() throws IndexerException, SolrServiceException, FacetConfigServiceException {
        MockitoAnnotations.openMocks(this);

        when(supTypeWorkerParameters.getFacetSearchConfigData()).thenReturn(facetSearchConfig);
        when(facetSearchConfigMapper.map(facetSearchConfig, FacetSearchConfig.class)).thenReturn(copyFacetSearchConfig);

        when(supTypeWorkerParameters.getIndexedType()).thenReturn("Product");
        when(facetSearchConfigService.resolveIndexedType(copyFacetSearchConfig, supTypeWorkerParameters.getIndexedType())).thenReturn(indexedType);

        List<String> indexedPropertyStrings = List.of("prop1", "prop2");
        indexedProperties = List.of(indexedProperty1, indexedProperty2);
        when(supTypeWorkerParameters.getIndexedProperties()).thenReturn(indexedPropertyStrings);
        when(facetSearchConfigService.resolveIndexedProperties(copyFacetSearchConfig, indexedType,indexedPropertyStrings)).thenReturn(indexedProperties);

        when(solrSearchProviderFactory.getSearchProvider(copyFacetSearchConfig, indexedType)).thenReturn(solrSearchProvider);
        when(solrSearchProvider.resolveIndex(copyFacetSearchConfig, indexedType, supTypeWorkerParameters.getIndex()))
          .thenReturn(index);

        when(indexerBatchStrategyFactory.createIndexerBatchStrategy(copyFacetSearchConfig)).thenReturn(indexerBatchStrategy);
        when(supTypeWorkerParameters.getIndexOperationId()).thenReturn(INDEX_OPERATION_ID);
        when(supTypeWorkerParameters.getIndexOperation()).thenReturn(IndexOperation.FULL);
        when(supTypeWorkerParameters.isExternalIndexOperation()).thenReturn(TRUE);
        when(supTypeWorkerParameters.getIndexerHints()).thenReturn(Map.of());
        pk1 = PK.fromLong(26491529586071L);
        pk2 = PK.fromLong(26491529586072L);
        pks = List.of(pk1, pk2);
        when(supTypeWorkerParameters.getPks()).thenReturn(pks);
    }

    @Test
    public void testDoRun() throws FacetConfigServiceException, IndexerException, SolrServiceException, InterruptedException {
        distIndexerWorker.doRun();

        verify(indexerBatchStrategy, times(1)).setIndexOperationId(INDEX_OPERATION_ID);
        verify(indexerBatchStrategy, times(1)).setIndexOperation(IndexOperation.FULL);
        verify(indexerBatchStrategy, times(1)).setExternalIndexOperation(TRUE);
        verify(indexerBatchStrategy, times(1)).setFacetSearchConfig(copyFacetSearchConfig);
        verify(indexerBatchStrategy, times(1)).setIndexedType(indexedType);
        verify(indexerBatchStrategy, times(1)).setIndexedProperties(indexedProperties);
        verify(indexerBatchStrategy, times(1)).setIndex(index);
        verify(indexerBatchStrategy, times(1)).setIndexerHints(Map.of());
        verify(indexerBatchStrategy, times(1)).setPks(pks);
        verify(indexerBatchStrategy, times(1)).execute();
    }

    @Test
    public void testInitialize() {
        distIndexerWorker.initialize(indexerWorkerParameters);

        assertThat(distIndexerWorker.isInitialized(), is(TRUE));
    }
}
