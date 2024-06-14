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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistCategoryAttributeValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String CAT_1_CODE = "cat-L2D_379532";

    private static final String CAT_2_CODE = "cat-L2-3D_530524";

    private static final String CAT_3_CODE = "cat-DNAV_150202";

    private static final String CAT_4_CODE = "cat-DNAV_PL_15020202";

    private static final String CAT_PATH_SELECT_NODE = CAT_1_CODE + "/"
                                                       + CAT_2_CODE + "/"
                                                       + CAT_3_CODE + "/"
                                                       + CAT_4_CODE;

    @InjectMocks
    private DistCategoryAttributeValueResolver distCategoryAttributeValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private CategoryModel category;

    @Before
    public void init() {
        super.init();

        cloneableIndexedProperty.setName("categoryAttributes");
        cloneableIndexedProperty.setExportId("categoryAttributes");
        cloneableIndexedProperty.setLocalized(false);

        when(product.getPrimarySuperCategory()).thenReturn(category);
        when(category.getLevel()).thenReturn(4);
        when(category.getCat1code()).thenReturn(CAT_1_CODE);
        when(category.getCat2code()).thenReturn(CAT_2_CODE);
        when(category.getCat3code()).thenReturn(CAT_3_CODE);
        when(category.getCat4code()).thenReturn(CAT_4_CODE);
        when(category.getCatPathSelectCode()).thenReturn(CAT_PATH_SELECT_NODE);
    }

    @Test
    public void testResolveCategoryAttributes() throws FieldValueProviderException {
        distCategoryAttributeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category1Code")), eq(CAT_1_CODE), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category2Code")), eq(CAT_2_CODE), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category3Code")), eq(CAT_3_CODE), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category4Code")), eq(CAT_4_CODE), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("categoryCodePath")), eq(CAT_PATH_SELECT_NODE), any());
    }

    @Test
    public void testResolveCategoryAttributesLevel3() throws FieldValueProviderException {
        when(category.getLevel()).thenReturn(3);

        distCategoryAttributeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category1Code")), eq(CAT_1_CODE), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category2Code")), eq(CAT_2_CODE), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category3Code")), eq(CAT_3_CODE), any());
        verify(distSolrInputDocument, times(0))
                                               .addField(refEq(createNewIndexedProperty("category4Code")), eq(CAT_4_CODE), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("categoryCodePath")), eq(CAT_PATH_SELECT_NODE), any());
    }

    @Test
    public void testResolveCategoryAttributesNull() throws FieldValueProviderException {
        when(product.getPrimarySuperCategory()).thenReturn(null);

        distCategoryAttributeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0))
                                               .addField(any(), any(), any());
    }

    private IndexedProperty createNewIndexedProperty(String attributeName) {
        return distCategoryAttributeValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                           attributeName);
    }

}
