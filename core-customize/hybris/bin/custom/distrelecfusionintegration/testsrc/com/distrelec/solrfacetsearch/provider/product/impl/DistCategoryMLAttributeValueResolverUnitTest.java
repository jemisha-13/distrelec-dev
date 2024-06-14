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
import org.springframework.test.util.ReflectionTestUtils;

import com.distrelec.solrfacetsearch.indexer.impl.DistSolrInputDocument;
import com.distrelec.solrfacetsearch.provider.product.DistAbstractValueResolverTest;
import com.google.gson.Gson;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;

public class DistCategoryMLAttributeValueResolverUnitTest extends DistAbstractValueResolverTest {

    private static final String CAT_1_CODE = "cat-L2D_379532";

    private static final String CAT_1_NAME = "Tools & Soldering";

    private static final String CAT_1_URL = "/en/tools-soldering/c/cat-L2D_379532";

    private static final String CAT_2_CODE = "cat-L2-3D_530524";

    private static final String CAT_2_NAME = "Hand Tools";

    private static final String CAT_2_URL = "/en/tools-soldering/hand-tools/c/cat-L2-3D_530524";

    private static final String CAT_3_CODE = "cat-DNAV_150202";

    private static final String CAT_3_NAME = "Connector Tools";

    private static final String CAT_3_URL = "/en/tools-soldering/hand-tools/connector-tools/c/cat-DNAV_150202";

    private static final String CAT_4_CODE = "cat-DNAV_PL_15020202";

    private static final String CAT_4_NAME = "Circular Connector Tools";

    private static final String CAT_4_URL = "/en/tools-soldering/hand-tools/connector-tools/circular-connector-tools/c/cat-DNAV_PL_15020202";

    private static final String CAT_PATH_EXTENSIONS = "[" +
                                                      "{" +
                                                      "\"url\":\"/en/tools-soldering/c/cat-L2D_379532\"," +
                                                      "\"imageUrl\":\"/Web/WebShopImages/landscape_small/ho/ne/CL_368_iPhone.jpg\"" +
                                                      "}," +
                                                      "{" +
                                                      "\"url\":\"/en/tools-soldering/hand-tools/c/cat-L2-3D_530524\"," +
                                                      "\"imageUrl\":\"/Web/WebShopImages/landscape_small/9-/01/WERA_Kraftform-Kompakt-60i+iS_62i_65i_67i_17_30035419-01.jpg\""
                                                      +
                                                      "}," +
                                                      "{" +
                                                      "\"url\":\"/en/tools-soldering/hand-tools/connector-tools/c/cat-DNAV_150202\"," +
                                                      "\"imageUrl\":\"/Web/WebShopImages/landscape_small/65/29/tycoconnectorstools_596529.jpg\"" +
                                                      "}," +
                                                      "{" +
                                                      "\"url\":\"/en/tools-soldering/hand-tools/connector-tools/circular-connector-tools/c/cat-DNAV_PL_15020202\","
                                                      +
                                                      "\"imageUrl\":\"/Web/WebShopImages/landscape_small/_t/if/no-brand-11w150-000.jpg\"" +
                                                      "}" +
                                                      "]";

    @InjectMocks
    private DistCategoryMLAttributeValueResolver distCategoryMLAttributeValueResolver;

    @Mock
    private DistSolrInputDocument distSolrInputDocument;

    @Mock
    private CategoryModel category;

    private Gson gson = new Gson();

    @Before
    public void init() {
        super.init();

        ReflectionTestUtils.setField(distCategoryMLAttributeValueResolver, "gson", gson);

        cloneableIndexedProperty.setName("categoryAttributes");
        cloneableIndexedProperty.setExportId("categoryAttributes");
        cloneableIndexedProperty.setLocalized(false);

        when(product.getPrimarySuperCategory()).thenReturn(category);
        when(category.getLevel()).thenReturn(4);
        when(category.getCat1code()).thenReturn(CAT_1_CODE);
        when(category.getCat1name()).thenReturn(CAT_1_NAME);
        when(category.getCat2code()).thenReturn(CAT_2_CODE);
        when(category.getCat2name()).thenReturn(CAT_2_NAME);
        when(category.getCat3code()).thenReturn(CAT_3_CODE);
        when(category.getCat3name()).thenReturn(CAT_3_NAME);
        when(category.getCat4code()).thenReturn(CAT_4_CODE);
        when(category.getCat4name()).thenReturn(CAT_4_NAME);
        when(category.getCatPathExtensions()).thenReturn(CAT_PATH_EXTENSIONS);
    }

    @Test
    public void testResolveCategoryLocalisedAttributes() throws FieldValueProviderException {
        distCategoryMLAttributeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category1")), eq(CAT_1_CODE + "|" + CAT_1_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category2")), eq(CAT_2_CODE + "|" + CAT_2_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category3")), eq(CAT_3_CODE + "|" + CAT_3_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category4")), eq(CAT_4_CODE + "|" + CAT_4_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category1Name")), eq(CAT_1_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category2Name")), eq(CAT_2_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category3Name")), eq(CAT_3_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category4Name")), eq(CAT_4_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category1Url")), eq(CAT_1_URL), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category2Url")), eq(CAT_2_URL), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category3Url")), eq(CAT_3_URL), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category4Url")), eq(CAT_4_URL), any());
    }

    @Test
    public void testResolveCategoryLocalisedAttributesLevel3() throws FieldValueProviderException {
        when(category.getLevel()).thenReturn(3);

        distCategoryMLAttributeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category1")), eq(CAT_1_CODE + "|" + CAT_1_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category2")), eq(CAT_2_CODE + "|" + CAT_2_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category3")), eq(CAT_3_CODE + "|" + CAT_3_NAME), any());
        verify(distSolrInputDocument, times(0))
                                               .addField(refEq(createNewIndexedProperty("category4")), eq(CAT_4_CODE + "|" + CAT_4_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category1Name")), eq(CAT_1_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category2Name")), eq(CAT_2_NAME), any());
        verify(distSolrInputDocument, times(1))
                                               .addField(refEq(createNewIndexedProperty("category3Name")), eq(CAT_3_NAME), any());
        verify(distSolrInputDocument, times(0))
                                               .addField(refEq(createNewIndexedProperty("category4Name")), eq(CAT_4_NAME), any());
    }

    @Test
    public void testResolveCategoryLocalisedAttributesNull() throws FieldValueProviderException {
        when(product.getPrimarySuperCategory()).thenReturn(null);

        distCategoryMLAttributeValueResolver.resolve(distSolrInputDocument, indexerBatchContext, List.of(cloneableIndexedProperty), product);

        verify(distSolrInputDocument, times(0))
                                               .addField(any(), any(), any());
    }

    private IndexedProperty createNewIndexedProperty(String attributeName) {
        return distCategoryMLAttributeValueResolver.createNewIndexedProperty(cloneableIndexedProperty,
                                                                             attributeName);
    }
}
