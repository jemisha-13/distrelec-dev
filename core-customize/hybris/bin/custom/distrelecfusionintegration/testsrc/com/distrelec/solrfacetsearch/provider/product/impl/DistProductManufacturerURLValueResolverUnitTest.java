package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
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
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistProductManufacturerURLValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String MANUFACTURER_URL_FIELD = "manufacturerUrl";

    private static final String MANUFACTURER_URL = "/manufacturer/rnd-components/man_rnp";

    @InjectMocks
    private DistProductManufacturerURLValueResolver distProductManufacturerURLValueResolver;

    @Mock
    private UrlResolver<DistManufacturerModel> manufacturerUrlResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("manufacturerUrl");
        cloneableIndexedProperty.setExportId("manufacturerUrl");
        cloneableIndexedProperty.setLocalized(true);

        when(product.getManufacturer()).thenReturn(manufacturer);
        when(manufacturerUrlResolver.resolve(manufacturer)).thenReturn(MANUFACTURER_URL);
    }

    @Test
    public void testResolveURL() throws FieldValueProviderException {
        distProductManufacturerURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty(MANUFACTURER_URL_FIELD)), eq(MANUFACTURER_URL), any());
    }

    @Test
    public void testResolveNoManufacturer() throws FieldValueProviderException {
        when(product.getManufacturer()).thenReturn(null);

        distProductManufacturerURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(refEq(createNewIndexedProperty(MANUFACTURER_URL_FIELD)), any(), any());
    }

    private IndexedProperty createNewIndexedProperty(String attributeName) {
        return distProductManufacturerURLValueResolver.createNewIndexedProperty(cloneableIndexedProperty, attributeName);
    }
}
