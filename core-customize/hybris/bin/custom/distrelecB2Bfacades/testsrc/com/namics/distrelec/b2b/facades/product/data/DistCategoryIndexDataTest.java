package com.namics.distrelec.b2b.facades.product.data;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

@UnitTest
public class DistCategoryIndexDataTest {

    private static final String AUTOMATION_CATEGORY_URL = "/en/automation/c/cat-L1D_379516";
    private static final String SPECIAL_SHOP_541300 =  "/en/special/c/cat/"+ DistCategoryIndexData.SPECIAL_SHOPS_PIM_CATEGORY_541300;
    private static final String SPECIAL_SHOP_90 = "/en/special/c/cat/"+ DistCategoryIndexData.SPECIAL_SHOPS_PIM_CATEGORY_90;

    @InjectMocks
    private DistCategoryIndexData indexData;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testChildrenArePresent(){
        indexData.setChildren(createAChild());
        assertTrue(indexData.hasChildren());
    }

    @Test
    public void testChildrenAreNotPresent(){
        indexData.setChildren(Collections.EMPTY_LIST);
        assertFalse(indexData.hasChildren());
    }

    @Test
    public void testNullChildren(){
        indexData.setChildren(null);
        assertFalse(indexData.hasChildren());
    }

    @Test
    public void testUrlIsNull(){
        final String nullString = null;
        indexData.setUrl(nullString);
        assertNull(indexData.getUrl());
        assertFalse(indexData.urlIsNotNull());
    }

    @Test
    public void testUrlIsNotNull(){
        indexData.setUrl(AUTOMATION_CATEGORY_URL);
        assertNotNull(indexData.getUrl());
        assertTrue(indexData.urlIsNotNull());
    }

    @Test
    public void testHasASpecialShop541300(){
        indexData.setUrl(SPECIAL_SHOP_541300);
        assertNotNull(indexData.getUrl());
        assertFalse(indexData.isNotASpecialShop());
    }

    @Test
    public void testHasASpecialShop90(){
        indexData.setUrl(SPECIAL_SHOP_90);
        assertNotNull(indexData.getUrl());
        assertFalse(indexData.isNotASpecialShop());
    }

    @Test
    public void testURLHasSpecialShopURLInsideItseflButIsNotThere(){
        final String url = "/en/special/c/cat/cat-dnav_900";
        indexData.setUrl(url);
        assertNotNull(indexData.getUrl());
        assertTrue(indexData.isNotASpecialShop());
    }

    @Test
    public void testHasNotGotASpecialShop(){
        final String url = "/en/normal/c/cat/cat-DNAV_91";
        indexData.setUrl(url);
        assertNotNull(indexData.getUrl());
        final boolean result = indexData.isNotASpecialShop();
        assertTrue(result);
    }

    private List<DistCategoryIndexData> createAChild(){
        final DistCategoryIndexData childOne = new DistCategoryIndexData();
        return Arrays.asList(childOne);
    }
}
