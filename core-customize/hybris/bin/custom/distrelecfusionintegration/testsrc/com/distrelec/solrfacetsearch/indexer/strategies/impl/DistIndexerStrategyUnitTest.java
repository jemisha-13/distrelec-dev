package com.distrelec.solrfacetsearch.indexer.strategies.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.solrfacetsearch.solr.impl.DistSolrSearchProviderFactory;
import com.distrelec.solrfacetsearch.solr.impl.FusionSolrStandaloneSearchProvider;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.workers.IndexerWorker;
import de.hybris.platform.solrfacetsearch.indexer.workers.IndexerWorkerFactory;
import de.hybris.platform.solrfacetsearch.indexer.workers.impl.DefaultIndexerWorker;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrIndexNotFoundException;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.DefaultIndex;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistIndexerStrategyUnitTest {

    private final static String PRODUCT_INDEX_TYPE_IDENTIFIER = "product";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private DistIndexerStrategy distIndexerStrategy;

    @Mock
    private DistSolrSearchProviderFactory distSolrSearchProviderFactory;

    @Mock
    private FusionSolrStandaloneSearchProvider fusionSolrStandaloneSearchProvider;

    @Mock
    private IndexerWorkerFactory indexerWorkerFactory;

    @Mock
    private TenantService tenantService;

    @Mock
    private SessionService sessionService;

    @Mock
    private UserService userService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private IndexerContext indexerContext;

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private IndexedType indexedType;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private Index index;

    private Index defaultIndex = new DefaultIndex();

    private PK pk1;

    private PK pk2;

    private IndexedProperty indexedProperty1;

    private IndexedProperty indexedProperty2;

    @Mock
    private UserModel userModel;

    private IndexerWorker indexerWorker;

    @Before
    public void init() throws SolrServiceException, IndexerException {
        MockitoAnnotations.openMocks(this);

        when(indexerContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
        when(indexerContext.getIndexedType()).thenReturn(indexedType);
        when(indexerContext.getIndex()).thenReturn(index);
        when(indexedType.getIdentifier()).thenReturn(PRODUCT_INDEX_TYPE_IDENTIFIER);
        when(facetSearchConfig.getName()).thenReturn("productConfig");
        when(distSolrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType)).thenReturn(fusionSolrStandaloneSearchProvider);
        when(fusionSolrStandaloneSearchProvider.resolveIndex(facetSearchConfig, indexedType, PRODUCT_INDEX_TYPE_IDENTIFIER)).thenReturn(defaultIndex);

        pk1 = PK.fromLong(26491529586071L);
        pk2 = PK.fromLong(26491529586072L);

        indexedProperty1 = new IndexedProperty();
        indexedProperty2 = new IndexedProperty();
        when(indexerContext.getIndexedProperties()).thenReturn(List.of(indexedProperty1, indexedProperty2));

        when(userService.getCurrentUser()).thenReturn(userModel);
        indexerWorker = new DefaultIndexerWorker();
        when(indexerWorkerFactory.createIndexerWorker(facetSearchConfig))
                                                                         .thenReturn(indexerWorker);
    }

    @Test
    public void testResolveIndex() throws IndexerException {
        Index result = distIndexerStrategy.resolveIndex();

        assertThat(result, is(index));
    }

    @Test
    public void testResolveIndexDefault() throws IndexerException {
        distIndexerStrategy.setIndex(null);

        Index result = distIndexerStrategy.resolveIndex();

        assertThat(result, notNullValue());
        assertThat(result, is(defaultIndex));
    }

    @Test
    public void testResolveIndexSolrIndexNotFoundException() throws SolrServiceException, IndexerException {
        distIndexerStrategy.setIndex(null);
        when(distSolrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType))
                                                                                             .thenThrow(SolrIndexNotFoundException.class);

        Index result = distIndexerStrategy.resolveIndex();

        assertThat(result, nullValue());
    }

    @Test
    public void testResolveIndexSolrServiceException() throws SolrServiceException, IndexerException {
        thrown.expect(IndexerException.class);
        distIndexerStrategy.setIndex(null);
        when(distSolrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType))
                                                                                             .thenThrow(SolrServiceException.class);
        distIndexerStrategy.resolveIndex();
    }

    @Test
    public void testCreateIndexerWorker() throws IndexerException {
        IndexerWorker indexerWorker = distIndexerStrategy.createIndexerWorker(indexerContext, 23L, List.of(pk1, pk2));

        assertThat(indexerWorker, notNullValue());
        assertThat(indexerWorker.isInitialized(), is(true));
    }
}
