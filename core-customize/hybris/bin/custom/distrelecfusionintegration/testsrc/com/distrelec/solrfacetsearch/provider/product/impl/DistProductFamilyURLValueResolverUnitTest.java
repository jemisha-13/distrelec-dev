package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistProductFamilyURLValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String PRODUCT_FAMILY_CODE = "1982724";

    private static final String PRODUCT_FAMILY_URL = "/smd-leds-0603-1206-0805-rnd/pf/1982724";

    @InjectMocks
    private DistProductFamilyURLValueResolver distProductFamilyURLValueResolver;

    @Mock
    private DistCategoryService categoryService;

    @Mock
    private DistUrlResolver<CategoryModel> distProductFamilyUrlResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private CategoryModel productFamily;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("productFamilyUrl");
        cloneableIndexedProperty.setExportId("productFamilyUrl");
        cloneableIndexedProperty.setLocalized(true);

        when(product.getPimFamilyCategoryCode()).thenReturn(PRODUCT_FAMILY_CODE);
        when(categoryService.findProductFamily(PRODUCT_FAMILY_CODE)).thenReturn(Optional.of(productFamily));
        when(distProductFamilyUrlResolver.resolve(productFamily)).thenReturn(PRODUCT_FAMILY_URL);
    }

    @Test
    public void testResolveProductFamilyUrl() throws FieldValueProviderException {
        distProductFamilyURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1))
                                               .addField(eq(cloneableIndexedProperty), eq(PRODUCT_FAMILY_URL), any());
    }

    @Test
    public void testResolveProductFamilyUrlNoCode() throws FieldValueProviderException {
        when(product.getPimFamilyCategoryCode()).thenReturn(null);

        distProductFamilyURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

    @Test
    public void testResolveProductFamilyUrlNoProductFamily() throws FieldValueProviderException {
        when(categoryService.findProductFamily(PRODUCT_FAMILY_CODE)).thenReturn(Optional.empty());

        distProductFamilyURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

}
