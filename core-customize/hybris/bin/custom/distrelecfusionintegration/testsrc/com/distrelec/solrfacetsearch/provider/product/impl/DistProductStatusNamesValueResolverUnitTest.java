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
import com.namics.distrelec.b2b.core.enums.ProductStatus;

import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistProductStatusNamesValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String PRODUCT_STATUS = "ProductStatus";

    @InjectMocks
    private DistProductStatusNamesValueResolver distProductStatusNamesValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    private Map<String, Object> indexDocumentContext = new HashMap<>();

    @Mock
    private ProductStatus exclusiveStatus;

    @Mock
    private ProductStatus offerStatus;

    @Mock
    private ProductStatus newStatus;

    @Before
    public void init() {
        super.init();

        indexedProperty.setName("productStatusName");
        indexedProperty.setExportId("productStatusName");
        indexedProperty.setMultiValue(true);
        indexedProperty.setLocalized(true);

        when(distSolrInputDocument.getIndexDocumentContext()).thenReturn(indexDocumentContext);

        when(enumerationService.getEnumerationValue(PRODUCT_STATUS, "EXCLUSIVE")).thenReturn(exclusiveStatus);
        when(enumerationService.getEnumerationName(exclusiveStatus)).thenReturn("Exclusive");

        when(enumerationService.getEnumerationValue(PRODUCT_STATUS, "OFFER")).thenReturn(offerStatus);
        when(enumerationService.getEnumerationName(offerStatus)).thenReturn("Offer");

        when(enumerationService.getEnumerationValue(PRODUCT_STATUS, "NEW")).thenReturn(newStatus);
        when(enumerationService.getEnumerationName(newStatus)).thenReturn("New");
    }

    @Test
    public void testResolveWithProductStatusCodes() throws FieldValueProviderException {
        List<String> productStatusCodes = List.of("EXCLUSIVE", "OFFER", "NEW");
        indexDocumentContext.put(PRODUCT_STATUS_CODES, productStatusCodes);

        distProductStatusNamesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(indexedProperty), product);

        List<String> productStatusNames = List.of("Exclusive", "Offer", "New");
        verify(distSolrInputDocument, times(1)).addField(eq(indexedProperty), eq(productStatusNames), any());
    }

    @Test
    public void testResolveWithProductStatusCodesEmpty() throws FieldValueProviderException {
        List<String> productStatusCodes = List.of();
        indexDocumentContext.put(PRODUCT_STATUS_CODES, productStatusCodes);

        distProductStatusNamesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(indexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

    @Test
    public void testResolveWithProductStatusCodesNull() throws FieldValueProviderException {
        indexDocumentContext.put(PRODUCT_STATUS_CODES, null);

        distProductStatusNamesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(indexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

}
