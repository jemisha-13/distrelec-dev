package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.PRODUCT_STATUS_CODES;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;

import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistProductStatusCodesValueResolverUnitTest extends DistAbstractValueResolverTest {

    @InjectMocks
    private DistProductStatusCodesValueResolver distProductStatusCodesValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    private Map<String, Object> indexDocumentContext = new HashMap<>();

    @Before
    public void init() {
        super.init();

        indexedProperty.setName("productStatus");
        indexedProperty.setExportId("productStatus");
        indexedProperty.setMultiValue(true);
        indexedProperty.setLocalized(false);

        when(distSolrInputDocument.getIndexDocumentContext()).thenReturn(indexDocumentContext);
    }

    @Test
    public void testResolveWithProductStatusCodes() throws FieldValueProviderException {
        List<String> productStatusCodes = List.of("AVAILABLEDELIVERY", "AVAILABLEPICKUP", "EXCLUSIVE", "OFFER", "NEW", "CALIBRATION");
        indexDocumentContext.put(PRODUCT_STATUS_CODES, productStatusCodes);

        distProductStatusCodesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(indexedProperty), product);

        verify(distSolrInputDocument, times(1))
                                               .addField(eq(indexedProperty), eq(productStatusCodes), any());
    }

    @Test
    public void testResolveWithProductStatusCodesEmpty() throws FieldValueProviderException {
        List<String> productStatusCodes = List.of();
        indexDocumentContext.put(PRODUCT_STATUS_CODES, productStatusCodes);

        distProductStatusCodesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(indexedProperty), product);

        verify(distSolrInputDocument, times(0))
                                               .addField(any(), any(), any());
    }

    @Test
    public void testResolveWithProductStatusCodesNull() throws FieldValueProviderException {
        indexDocumentContext.put(PRODUCT_STATUS_CODES, null);

        distProductStatusCodesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(indexedProperty), product);

        verify(distSolrInputDocument, times(0))
                                               .addField(any(), any(), any());
    }

}
