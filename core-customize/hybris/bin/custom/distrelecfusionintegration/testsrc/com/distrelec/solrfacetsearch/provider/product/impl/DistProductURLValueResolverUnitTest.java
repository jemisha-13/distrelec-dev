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

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistProductURLValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String PRODUCT_URL_FIELD = "productUrl";

    private static final String PRODUCT_URL = "/smd-led-blue-468nm-1206-150mcd-25ma-rnd-components-rnd-135-00186/p/30132277";

    @InjectMocks
    private DistProductURLValueResolver distProductURLValueResolver;

    @Mock
    private UrlResolver<ProductModel> productUrlResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("urlResolver");
        cloneableIndexedProperty.setExportId("urlResolver");
        cloneableIndexedProperty.setLocalized(true);

        when(productUrlResolver.resolve(product)).thenReturn(PRODUCT_URL);
    }

    @Test
    public void testResolveImages() throws FieldValueProviderException {
        distProductURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty(PRODUCT_URL_FIELD)), eq(PRODUCT_URL), any());
    }

    @Test
    public void testResolveImagesNoManufacturer() throws FieldValueProviderException {
        distProductURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1)).addField(refEq(createNewIndexedProperty(PRODUCT_URL_FIELD)), eq(PRODUCT_URL), any());
    }

    private IndexedProperty createNewIndexedProperty(String attributeName) {
        return distProductURLValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                    attributeName);
    }
}
