package com.distrelec.solrfacetsearch.solr.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.solr.impl.SolrStandaloneSearchProvider;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistSolrSearchProviderFactoryUnitTest {

    @InjectMocks
    private DistSolrSearchProviderFactory distSolrSearchProviderFactory;

    @Mock
    private SolrStandaloneSearchProvider solrDistStandaloneSearchProvider;

    @Mock
    private SolrStandaloneSearchProvider solrStandaloneSearchProvider;

    @Mock
    private IndexedType indexedType;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private SolrConfig solrConfig;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(facetSearchConfig.getSolrConfig()).thenReturn(solrConfig);
        when(solrConfig.getMode()).thenReturn(SolrServerMode.FUSION);
    }

    @Test
    public void testGetSearchProviderFusion() throws SolrServiceException {
        SolrSearchProvider searchProvider = distSolrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);

        assertThat(searchProvider, is(solrDistStandaloneSearchProvider));
    }

    @Test
    public void testGetSearchProviderOtherStandalone() throws SolrServiceException {
        when(solrConfig.getMode()).thenReturn(SolrServerMode.STANDALONE);

        SolrSearchProvider searchProvider = distSolrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);

        assertThat(searchProvider, is(solrStandaloneSearchProvider));
    }

}
