package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistCountryValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String COUNTRY = "CH";

    private static final String COUNTRY_TWO = "LI";

    @InjectMocks
    private DistCountryValueResolver distCountryValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private FacetSearchConfig facetSearchConfig;

    @Mock
    private IndexConfig indexConfig;

    @Mock
    private CountryModel country;

    @Mock
    private CountryModel countryTwo;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("country");
        cloneableIndexedProperty.setExportId("country");
        cloneableIndexedProperty.setLocalized(false);

        when(country.getIsocode()).thenReturn(COUNTRY);
        when(countryTwo.getIsocode()).thenReturn(COUNTRY_TWO);
        when(indexConfig.getAllCountries()).thenReturn(List.of(country, countryTwo));
        when(facetSearchConfig.getIndexConfig()).thenReturn(indexConfig);
        when(indexerBatchContext.getFacetSearchConfig()).thenReturn(facetSearchConfig);
    }

    @Test
    public void testResolveCountry() throws FieldValueProviderException {
        distCountryValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(List.of(COUNTRY, COUNTRY_TWO)), any());
    }

}
