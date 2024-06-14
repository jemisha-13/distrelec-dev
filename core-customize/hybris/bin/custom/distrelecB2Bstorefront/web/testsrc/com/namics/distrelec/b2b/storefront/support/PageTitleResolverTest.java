/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.support;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

@UnitTest
@RunWith(DataProviderRunner.class)
public class PageTitleResolverTest {

    private static final Locale TEST_LOCALE = Locale.ENGLISH;

    @InjectMocks
    @Spy // Spy so I can mock the constants
    private PageTitleResolver pageTitleResolver;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CommerceCategoryService commerceCategoryService;

    @Mock
    private I18NService i18nService;

    @Mock
    private CMSSiteService cmsSiteService;

    @Mock
    private CMSSiteModel cmsSiteModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        BDDMockito.given(cmsSiteService.getCurrentSite()).willReturn(cmsSiteModel);
        BDDMockito.given(i18nService.getCurrentLocale()).willReturn(TEST_LOCALE);
        BDDMockito.given(cmsSiteModel.getName()).willReturn("Distrelec Austria");
        BDDMockito.given(messageSource.getMessage(Mockito.eq(PageTitleResolver.PRODUCT_PAGE_TITLE_BUY_PROPERTY_KEY), Mockito.any(), Mockito.eq(TEST_LOCALE)))
                .willAnswer(i -> "Buy " + ((Object[]) i.getArguments()[1])[0]);
        BDDMockito.given(messageSource.getMessage(Mockito.eq(PageTitleResolver.PRODUCT_PAGE_TITLE_BUY_PROPERTY_KEY), Mockito.any(), Mockito.any(),
                Mockito.eq(TEST_LOCALE))).willAnswer(i -> "Buy " + ((Object[]) i.getArguments()[1])[0]);
        BDDMockito.given(messageSource.getMessage(Mockito.eq(PageTitleResolver.PAGE_TITLE_ONLINE_SHOP_PROPERTY_KEY), Mockito.any(), Mockito.any(),
                Mockito.eq(TEST_LOCALE))).willReturn("Online Shop");

        // Mock spied constants, so the test does not rely on them staying the same.
        BDDMockito.given(pageTitleResolver.getMaxTitleLength()).willReturn(60);
        BDDMockito.given(pageTitleResolver.getLongSeparator()).willReturn(" | ");
        BDDMockito.given(pageTitleResolver.getShortSeparator()).willReturn(" ");
        BDDMockito.given(pageTitleResolver.getRegexToStripCharacters()).willReturn("[-._]");

    }
    
    @DataProvider
    public static Object[][] testResolveProductPageTitleData() {
        // @formatter:off
        return new String[][] {
                //123456789012345678901234567890123456789012345678901234567890  For reference, these are 60 chars
                {"shortPMpn | Buy shortPName | shortMName | Distrelec Austria", "shortPName", "shortPMpn", "shortMName"},   //Base case
                {"shortPMpn | Buy longProductName | shortMName | Distrelec", "longProductName", "shortPMpn", "shortMName"}, //country name does not fit
                {"longProductMPN | Buy longProductName | shortMName", "longProductName", "longProductMPN", "shortMName"},   //shop name does not fit
                {"longProductMPN long-Product-Name longManufacturerName", "long-Product-Name", "longProductMPN", "longManufacturerName"},   // Buy and pipes do not fit
                {"longProductManufacturerPartNumber longProductName longManufacturerName", "long-Product-Name", "long.Product.Manufacturer.Part.Number", "long-Manufacturer.Name"}, //signs do not fit
                {"Buy shortPName | shortMName | Distrelec Austria", "shortPName", null, "shortMName"},  //Missing MPN
                {"shortPMpn | Buy shortPName | Distrelec Austria", "shortPName", "shortPMpn", null},    //Missing Manufacturer
        };
        // @formatter:on
    }
    
    @Test
    @UseDataProvider("testResolveProductPageTitleData")
    public void testResolveProductPageTitle(final String expectedTitle, final String productName, final String mpn, final String manufacturerName) {
        final ProductModel product = Mockito.mock(ProductModel.class);
        BDDMockito.given(product.getName()).willReturn(productName);
        BDDMockito.given(product.getCode()).willReturn("shortPCode");
        BDDMockito.given(product.getTypeName()).willReturn(mpn);
        final DistManufacturerModel manufacturer = Mockito.mock(DistManufacturerModel.class);
        BDDMockito.given(manufacturer.getName()).willReturn(manufacturerName);
        BDDMockito.given(product.getManufacturer()).willReturn(manufacturer);

        final String actualTitle = pageTitleResolver.resolveProductPageTitle(product);

        assertEquals(expectedTitle, actualTitle);
    }

    @DataProvider
    public static Object[][] testStripShopNameSuffixData() {
        // @formatter:off
        return new String[][] {
            {"aaaaaa | Distrelec Austria", "", "aaaaaa | Distrelec Austria"}, //empty shopName
            {"aaaaaa", "Distrelec Austria", "aaaaaa | Distrelec Austria"}, // shopName with country
            {"aaaaaa", "Distrelec", "aaaaaa | Distrelec"}, // shopName without country
            {"Distrelec Switzerland | Best Online Shop for Electronics", "Distrelec Switzerland", "Distrelec Switzerland | Best Online Shop for Electronics"} // shopName preserved if not at the end
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("testStripShopNameSuffixData")
    public void testStripShopNameSuffix(final String expectedStrippedTitle, final String cmsSiteName, final String inputTitle) {
        BDDMockito.given(cmsSiteModel.getName()).willReturn(cmsSiteName);
        final String actualStrippedTitle = pageTitleResolver.stripShopNameSuffix(inputTitle);

        assertEquals(expectedStrippedTitle, actualStrippedTitle);
    }

    @DataProvider
    public static Object[][] testResolveManufacturerPageTitleData() {
        // @formatter:off
        return new String[][] {
                //123456789012345678901234567890123456789012345678901234567890  For reference, these are 60 chars
                {"Foo Online Shop | Distrelec Austria", "Foo"}, //Base case
                {"Foo Bar Online Shop | Distrelec Austria", "Foo Bar"}, //Name has 2 words
                {"Long Manufacturer Name: No Country Online Shop | Distrelec", "Long Manufacturer Name: No Country"}, //country name does not fit
                {"Long Manufacturer Name: No Country Nor Shop Name Online Shop", "Long Manufacturer Name: No Country Nor Shop Name"},   // shop name does not fit
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("testResolveManufacturerPageTitleData")
    public void testResolveManufacturerPageTitle(final String expectedTitle, final String manufacturerName) {
        final DistManufacturerModel manufacturer = Mockito.mock(DistManufacturerModel.class);
        Mockito.when(manufacturer.getName()).thenReturn(manufacturerName);
        final String actualTitle = pageTitleResolver.resolveManufacturerPageTitle(manufacturer);
        assertEquals(expectedTitle, actualTitle);
    }

    @DataProvider
    public static Object[][] testResolveCategoryPageTitleData() {
        // @formatter:off
        return new Object[][] {
                //123456789012345678901234567890123456789012345678901234567890  For reference, these are 60 chars
                {"Connectors Online Shop | Distrelec Austria", "Connectors", Integer.valueOf(1)}, //Base case root
                {"Buy Adapters | Distrelec Austria", "Adapters", Integer.valueOf(2)}, //Base case non root
                {"Test &amp; Measurement Online Shop | Distrelec Austria", "Test & Measurement", Integer.valueOf(1)}, //Name has more than one word root
                {"Buy SCART Adapters | Distrelec Austria", "SCART Adapters", Integer.valueOf(3)}, //Name has more than one word non root
                {"Office, Computing &amp; Network Products Online Shop | Distrelec", "Office, Computing & Network Products", Integer.valueOf(1)}, //country name does not fit root
                {"Buy Signalling Horns &amp; Sirens for Wall Mounting | Distrelec", "Signalling Horns & Sirens for Wall Mounting", Integer.valueOf(2)}, //country name does not fit non root
                {"Long Manufacturer Name: No Country Nor Shop Name Online Shop", "Long Manufacturer Name: No Country Nor Shop Name", Integer.valueOf(1)},   // shop name does not fit root
                {"Buy Push-on Blades / Blade Receptacles Insulated &amp; Fully Insulated", "Push-on Blades / Blade Receptacles Insulated & Fully Insulated", Integer.valueOf(3)},   // shop name does not fit non root
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider("testResolveCategoryPageTitleData")
    public void testResolveCategoryPageTitle(final String expectedTitle, final String categoryName, final Integer level) {
        final CategoryModel category = Mockito.mock(CategoryModel.class);
        BDDMockito.given(commerceCategoryService.getCategoryForCode(Mockito.anyString())).willReturn(category);
        BDDMockito.given(category.getName()).willReturn(categoryName);
        BDDMockito.given(category.getLevel()).willReturn(level);

        final String actualTitle = pageTitleResolver.resolveCategoryPageTitle("categoryCode", Collections.emptyList());

        assertEquals(expectedTitle, actualTitle);
    }

}
