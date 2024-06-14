package com.distrelec.solrfacetsearch.solr.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.solrfacetsearch.indexer.strategies.IndexNameStrategy;
import com.distrelec.solrfacetsearch.service.FusionExportService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.solr.Index;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FusionSolrStandaloneSearchProviderUnitTest {

    private final static String INDEX_NAME = "webshop_product";

    private final static String SWEDEN_ISO_CODE = "SE";

    private final static String DELETE_QUERY_FORMAT = "(-(indexOperationId:[%s TO *]) AND country:%s)";

    private final static String DELETE_QUERY_WITHOUT_COUNTRY_FORMAT = "-(indexOperationId:[%s TO *])";

    private final static long INDEX_OPERATION_ID = 110633459257114649L;

    @InjectMocks
    private FusionSolrStandaloneSearchProvider fusionSolrStandaloneSearchProvider;

    @Mock
    private FusionExportService fusionExportService;

    @Mock
    private IndexNameStrategy indexNameStrategy;

    @Mock
    private Index index;

    @Mock
    private IndexedType indexedType;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CMSSiteModel cmsSite;

    @Mock
    private CountryModel country;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(index.getIndexedType()).thenReturn(indexedType);
        when(index.getFacetSearchConfig()).thenReturn(facetSearchConfig);
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexConfig.getCmsSite()).thenReturn(cmsSite);
        when(cmsSite.getCountry()).thenReturn(country);
        when(country.getIsocode()).thenReturn(SWEDEN_ISO_CODE);

        when(indexNameStrategy.createIndexName(facetSearchConfig, indexedType)).thenReturn(INDEX_NAME);
    }

    @Test
    public void testDeleteOldDocuments() {
        fusionSolrStandaloneSearchProvider.deleteOldDocuments(index, INDEX_OPERATION_ID);

        String expectedQuery = String.format(DELETE_QUERY_FORMAT, INDEX_OPERATION_ID, SWEDEN_ISO_CODE);
        verify(fusionExportService, times(1)).deleteDocumentsByQuery(INDEX_NAME, expectedQuery);
    }

    @Test
    public void testDeleteOldDocumentsNoCMSSite() {
        when(indexConfig.getCmsSite()).thenReturn(null);

        fusionSolrStandaloneSearchProvider.deleteOldDocuments(index, INDEX_OPERATION_ID);

        String expectedQuery = String.format(DELETE_QUERY_WITHOUT_COUNTRY_FORMAT, INDEX_OPERATION_ID);
        verify(fusionExportService, times(1)).deleteDocumentsByQuery(INDEX_NAME, expectedQuery);
    }

    @Test
    public void testDeleteOldDocumentsNoCountry() {
        when(cmsSite.getCountry()).thenReturn(null);

        fusionSolrStandaloneSearchProvider.deleteOldDocuments(index, INDEX_OPERATION_ID);

        String expectedQuery = String.format(DELETE_QUERY_WITHOUT_COUNTRY_FORMAT, INDEX_OPERATION_ID);
        verify(fusionExportService, times(1)).deleteDocumentsByQuery(INDEX_NAME, expectedQuery);
    }

}
