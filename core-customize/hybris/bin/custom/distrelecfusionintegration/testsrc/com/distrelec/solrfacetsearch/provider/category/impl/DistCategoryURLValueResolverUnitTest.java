package com.distrelec.solrfacetsearch.provider.category.impl;

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
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistCategoryURLValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String CATEGORY_URL = "/optoelectronics/leds/leds-smd/smd-leds-single-colour/c/cat-DNAV_PL_020101";

    @InjectMocks
    private DistCategoryURLValueResolver distCategoryURLValueResolver;

    @Mock
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("categoryURL");
        cloneableIndexedProperty.setExportId("categoryURL");
        cloneableIndexedProperty.setLocalized(true);
    }

    @Test
    public void testResolveCategoryUrl() throws FieldValueProviderException {
        when(categoryModelUrlResolver.resolve(category)).thenReturn(CATEGORY_URL);

        distCategoryURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(CATEGORY_URL), any());
    }

    @Test
    public void testResolveCategoryUrlBlank() throws FieldValueProviderException {
        when(categoryModelUrlResolver.resolve(category)).thenReturn(null);

        distCategoryURLValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }
}
