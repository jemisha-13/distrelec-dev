package com.distrelec.solrfacetsearch.provider.manufacturer.impl;

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
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistManufacturerURLValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String MANUFACTURER_URL = "/manufacturer/rnd-components/man_rnp";

    @InjectMocks
    private DistManufacturerURLValueResolver distManufacturerURLValueResolver;

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
    }

    @Test
    public void testResolveManufacturerUrl() throws FieldValueProviderException {
        when(manufacturerUrlResolver.resolve(manufacturer)).thenReturn(MANUFACTURER_URL);

        distManufacturerURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), manufacturer);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(MANUFACTURER_URL), any());
    }

    @Test
    public void testResolveManufacturerUrlBlank() throws FieldValueProviderException {
        when(manufacturerUrlResolver.resolve(manufacturer)).thenReturn(null);

        distManufacturerURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), manufacturer);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

}
