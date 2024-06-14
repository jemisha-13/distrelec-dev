package com.namics.distrelec.b2b.facades.search.converter.populator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.converter.SearchResultProductConverter;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.site.BaseSiteService;

/**
 * This Unit Test class tests the functionality of {@link SearchResultProductConverter}
 * 
 * @Author N Clarke, 2018
 */
@UnitTest
public class SearchResultProductConverterTest {

    private static final String PRODUCT_NUMBER = "30037314";

    private static final String TITLE_WITH_SYMBOL = "Raspberry Pi model B+, 512 MB";

    private static final String MANUFACTURER = "Raspberry Pi";

    private static final String DESCRIPTION = "It is a credit-card sized computer board that plugs into a TV and a keyboard";

    private static final String TYPENAME = "TZT 241 AAA-01";

    private static final String SALESUNIT = "1 PC";

    private static final String BUYABLE = "1";

    private static final String MOVEX_NUMBER = "227162";

    private static final String ELFA_NUMBER = "30037314";

    private static final String NUMBER_NAVISION = "12345678";

    private static final String PLUS_SYMBOL = "+";

    private ProductData b2BProductData;

    @InjectMocks
    private SearchResultProductConverter searchResultProductConverter;

    @Mock
    private DistProductService distProductService;

    @Mock
    private I18NService i18NService;

    @Mock
    private SearchResultUrlPopulator searchResultUrlPopulator;

    @Mock
    private DistrelecProductFacade distrelecProductFacade;

    @Mock
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Mock
    private BaseSiteService baseSiteService;

    /**
     * Sets up data before each test
     */
    @Before
    public void setUp() {
        this.searchResultProductConverter = new SearchResultProductConverter();
        this.b2BProductData = new ProductData();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Verifies if the url is in the format of the PDP for the PLP.
     * {@link SearchResultProductConverter#populateProduct(SearchResultValueData, ProductData)}}
     * 
     * @see <a href="https://jira.distrelec.com/browse/DISTRELEC-13579">DISTRELEC-13579</a> for details.
     * @note The expected url comes from the PDP on P, which is correct.
     */
    @Test
    public void testPLPContainsUrlAsPDP() {
        final String expectedProductFamilyName = "leds through hole";
        final String expectedProductFamilyCode = "cat-DC-52618";
        final String expectedUrl = "/en/leds-through-hole/pf/cat-DC-52618";
        final String expectedCategoryExtensions = "[{ \"url\" : \"/en/optoelectronics/c/cat-L2-3D_525341\", \"imageUrl\" : \"/Web/WebShopImages/landscape_small/ho/ne/CL_84_iPhone.jpg\"},"
                                                  +
                                                  " { \"url\" : \"/en/optoelectronics/leds/c/cat-L3D_525297\", \"imageUrl\" : \"/Web/WebShopImages/landscape_small/ho/ne/CL_86_iPhone.jpg\"},"
                                                  +
                                                  " { \"url\" : \"/en/optoelectronics/leds/leds-through-hole/c/cat-DNAV_PL_020103\", \"imageUrl\" : \"/Web/WebShopImages/landscape_small/30/47/lysdioder_dc-53047.jpg\"}, ]";

        SearchResultValueData valueData = createSearchResultValueData();
        valueData.getValues().put(DistFactFinderExportColumns.PRODUCT_FAMILY_NAME.getValue(), expectedProductFamilyName);
        valueData.getValues().put(DistFactFinderExportColumns.PRODUCT_FAMILY_CODE.getValue(), expectedProductFamilyCode);
        valueData.getValues().put(DistFactFinderExportColumns.CATEGORY_EXTENSIONS.getValue(), expectedCategoryExtensions);

        searchResultProductConverter.populate(valueData, b2BProductData);

        Assert.assertNotNull(b2BProductData.getProductFamilyUrl());
        Assert.assertEquals(expectedUrl, b2BProductData.getProductFamilyUrl());
    }

    /**
     * Tests the basic assignments of
     * {@link SearchResultProductConverter#populateProduct(SearchResultValueData, ProductData)}
     */
    @Test
    public void testBasicAssignments() {
        SearchResultValueData valueData = createSearchResultValueData();

        searchResultProductConverter.setSearchType(DistSearchType.TEXT.name());
        searchResultProductConverter.populate(valueData, b2BProductData);

        Assert.assertEquals(TITLE_WITH_SYMBOL, b2BProductData.getName());
        Assert.assertEquals(PRODUCT_NUMBER, b2BProductData.getCode());
        Assert.assertEquals(MANUFACTURER, b2BProductData.getManufacturer());
        Assert.assertEquals(DESCRIPTION, b2BProductData.getDescription());
        Assert.assertEquals(TYPENAME, b2BProductData.getTypeName());
        Assert.assertEquals(SALESUNIT, b2BProductData.getSalesUnit());
        Assert.assertEquals(Boolean.TRUE, b2BProductData.isBuyable());
        Assert.assertEquals(MOVEX_NUMBER, b2BProductData.getMovexArticleNumber());
        Assert.assertEquals(ELFA_NUMBER, b2BProductData.getElfaArticleNumber());
        Assert.assertEquals(NUMBER_NAVISION, b2BProductData.getNavisionArticleNumber());
    }

    /**
     * Verifies if the plus symbol is not decoded as a space character in
     * {@link SearchResultProductConverter#populateProduct(SearchResultValueData, ProductData)}
     * 
     * @see <a href="https://jira.distrelec.com/browse/DISTRELEC-13827">DISTRELEC-13827</a> for details.
     */
    @Test
    public void testPlusSymbolIsNotConsumedByUrlDecoder() {
        SearchResultValueData valueData = createSearchResultValueData();
        searchResultProductConverter.populate(valueData, b2BProductData);

        Assert.assertTrue(b2BProductData.getName().contains(PLUS_SYMBOL));
        Assert.assertEquals(TITLE_WITH_SYMBOL, b2BProductData.getName());
    }

    /**
     * Creates a new instance of {@link SearchResultValueData} containing prepopulated values to test
     * {@link SearchResultProductConverter#populateProduct(SearchResultValueData, ProductData)}}
     * 
     * @return A new instance of SearchResultValueData containing dummy data from FF.
     */
    private SearchResultValueData createSearchResultValueData() {
        SearchResultValueData valueData = new SearchResultValueData();
        Map<String, Object> values = setupBasicSearchValueData();
        values.put("catalogPlusRecord", "FALSE");
        valueData.setValues(values);
        return valueData;
    }

    /**
     * Populates a Map containing dummy data impersonating FF.
     * 
     * @return A Map containing fields from FF.
     */
    private Map<String, Object> setupBasicSearchValueData() {
        final Map<String, Object> values = new HashMap<>();
        values.put(DistFactFinderExportColumns.PRODUCT_NUMBER.getValue(), PRODUCT_NUMBER);
        values.put(DistFactFinderExportColumns.TITLE.getValue(), TITLE_WITH_SYMBOL);
        values.put(DistFactFinderExportColumns.MANUFACTURER.getValue(), MANUFACTURER);
        values.put(DistFactFinderExportColumns.DESCRIPTION.getValue(), DESCRIPTION);
        values.put(DistFactFinderExportColumns.TYPENAME.getValue(), TYPENAME);
        values.put(DistFactFinderExportColumns.SALESUNIT.getValue(), SALESUNIT);
        values.put(DistFactFinderExportColumns.BUYABLE.getValue(), BUYABLE);
        values.put(DistFactFinderExportColumns.PRODUCT_NUMBER_MOVEX.getValue(), MOVEX_NUMBER);
        values.put(DistFactFinderExportColumns.PRODUCT_NUMBER_ELFA.getValue(), ELFA_NUMBER);
        values.put(DistFactFinderExportColumns.PRODUCT_NUMBER_NAVISION.getValue(), NUMBER_NAVISION);

        return values;
    }
}
