package com.distrelec.solrfacetsearch.provider.category.impl;

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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistCategorySuperCategoriesValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String SUPER_CATEGORY_ONE_CODE = "cat-L2-3D_525341";

    private static final String SUPER_CATEGORY_TWO_CODE = "cat-L3D_525297";

    private static final String SUPER_CATEGORY_THREE_CODE = "cat-L2-3D_1914323";

    @InjectMocks
    private DistCategorySuperCategoriesValueResolver distCategorySuperCategoriesValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    private Map<String, Object> indexDocumentContext = new HashMap<>();

    @Mock
    private CategoryModel superCategoryOne;

    @Mock
    private CategoryModel superCategoryTwo;

    @Mock
    private CategoryModel superCategoryThree;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("superCategories");
        cloneableIndexedProperty.setExportId("superCategories");
        cloneableIndexedProperty.setLocalized(false);
        cloneableIndexedProperty.setMultiValue(true);

        when(superCategoryOne.getCode()).thenReturn(SUPER_CATEGORY_ONE_CODE);
        when(superCategoryTwo.getCode()).thenReturn(SUPER_CATEGORY_TWO_CODE);
        when(superCategoryThree.getCode()).thenReturn(SUPER_CATEGORY_THREE_CODE);

        when(category.getSupercategories()).thenReturn(List.of(superCategoryOne, superCategoryTwo, superCategoryThree));
    }

    @Test
    public void testResolveCategoryUrl() throws FieldValueProviderException {
        List<String> expectedCodes = List.of(SUPER_CATEGORY_ONE_CODE, SUPER_CATEGORY_TWO_CODE, SUPER_CATEGORY_THREE_CODE);

        distCategorySuperCategoriesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(1)).addField(eq(cloneableIndexedProperty), eq(expectedCodes), any());
    }

    @Test
    public void testResolveCategoryEmptySuperCategories() throws FieldValueProviderException {
        when(category.getSupercategories()).thenReturn(List.of());

        distCategorySuperCategoriesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }

    @Test
    public void testResolveCategoryNullSuperCategories() throws FieldValueProviderException {
        when(category.getSupercategories()).thenReturn(null);

        distCategorySuperCategoriesValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), category);

        verify(distSolrInputDocument, times(0)).addField(any(), any(), any());
    }
}
