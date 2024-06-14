package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.math.BigDecimal;
import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.search.pagedata.SearchPageableData;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Product;
import com.namics.distrelec.b2b.facades.adobe.datalayer.impl.DefaultDistDigitalDatalayerFacade;
import com.namics.distrelec.b2b.facades.order.cart.impl.DefaultDistB2BCartFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.paging.FactFinderSortData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * {@code DigitalDatalayerTest}
 *
 *
 * @since Distrelec 7.3
 */

@UnitTest
public class DigitalDatalayerVolumePriceTest {

    public static final String QUERY = "led";

    // @InjectMocks
    public DefaultDistDigitalDatalayerFacade digitalDatalayerFacade;

    @Mock
    public ProductSearchFacade<ProductData> productSearchFacade;

    @Mock
    protected BaseSiteService baseSiteService;

    @Mock
    protected ConfigurationService configurationService;

    @Mock
    protected DefaultDistB2BCartFacade b2bCartFacade;

    @Mock
    protected DistrelecStoreSessionFacade storeSessionFacade;

    @Mock
    protected DistrelecCMSSiteService distrelecCMSSiteService;

    @Mock
    protected MediaService mediaService;

    @Mock
    protected ProductService productService;

    @Mock
    protected SessionService sessionService;

    @Mock
    protected FactFinderProductSearchPageData<SearchStateData, ProductData> factFinderProductSearchPageData;

    @Before
    public void setUp() {
        // So that the @Mock annotations is recognised, you need the following line..
        MockitoAnnotations.initMocks(this);

        // Create the service and wire in the services/daos
        digitalDatalayerFacade = new DefaultDistDigitalDatalayerFacade();

        digitalDatalayerFacade.setBaseSiteService(baseSiteService);
        digitalDatalayerFacade.setCartFacade(b2bCartFacade);
        digitalDatalayerFacade.setCmsSiteService(distrelecCMSSiteService);
        digitalDatalayerFacade.setConfigurationService(configurationService);
        digitalDatalayerFacade.setMediaService(mediaService);
        digitalDatalayerFacade.setProductService(productService);
        digitalDatalayerFacade.setSessionService(sessionService);
        digitalDatalayerFacade.setStoreSessionFacade(storeSessionFacade);

        final PriceData priceData = new PriceData();
        priceData.setCurrencyIso("CHF");
        priceData.setFormattedValue("100.00");
        priceData.setValue(BigDecimal.valueOf(100));
        CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
        catalogVersionModel.setActive(true);
        catalogVersionModel.setVersion("Online");
        CatalogModel catalogModel = new CatalogModel();
        catalogModel.setActiveCatalogVersion(catalogVersionModel);
        catalogModel.setName("ContantCatalog", Locale.ENGLISH);
        catalogModel.setId("ContantCatalog");
        catalogVersionModel.setCatalog(catalogModel);
        PageTemplateModel pageTemplate = new PageTemplateModel();
        pageTemplate.setActive(true);
        pageTemplate.setCatalogVersion(catalogVersionModel);
        pageTemplate.setFrontendTemplateName("search");
        pageTemplate.setName("search");
        pageTemplate.setUid("search");
        AbstractPageModel pageModel = new AbstractPageModel();
        pageModel.setName("Search");
        pageModel.setTitle("Search", Locale.ENGLISH);
        pageModel.setCatalogVersion(catalogVersionModel);
        pageModel.setMasterTemplate(pageTemplate);
        pageModel.setUid("search");
        ContentPageModel searchContentPage = Mockito.mock(ContentPageModel.class);
        searchContentPage.setHomepage(false);
        searchContentPage.setDescription("ContentPage", Locale.ENGLISH);
        searchContentPage.setName("search");
        searchContentPage.setUid("search");
        searchContentPage.setLabel("Search");
        searchContentPage.setTitle("Search", Locale.ENGLISH);
        searchContentPage.setMasterTemplate(pageTemplate);

        LanguageModel languageModel = new LanguageModel();
        languageModel.setActive(true);
        languageModel.setIsocode("EN");
        languageModel.setName("English", Locale.ENGLISH);

        CurrencyModel currencyMOdel = new CurrencyModel();
        currencyMOdel.setActive(true);
        currencyMOdel.setIsocode("CHF");
        currencyMOdel.setName("Switzerland", Locale.ENGLISH);
        currencyMOdel.setSymbol("CHF");
        CountryModel countryMOdel = new CountryModel();
        countryMOdel.setIsocode("CH");
        countryMOdel.setName("Switzerland", Locale.ENGLISH);

        CMSSiteModel baseSite = new CMSSiteModel();
        baseSite.setName("distrelev_CH", Locale.ENGLISH);
        baseSite.setUid("distrelev_CH");
        baseSite.setChannel(SiteChannel.B2B);
        baseSite.setCountry(countryMOdel);
        baseSite.setUid("distrelec_CH");
        baseSite.setActive(true);
        baseSite.setDefaultCurrency(currencyMOdel);
        baseSite.setDefaultLanguage(languageModel);
        baseSite.setUserTaxGroup(UserTaxGroup.valueOf("SalesOrg_UPG_7310_M01"));
        baseSiteService.setCurrentBaseSite(baseSite, true);
        Set<LanguageModel> setLanguageModel = new HashSet<>();
        Set<CurrencyModel> setCurrencyModel = new HashSet<>();
        setCurrencyModel.add(currencyMOdel);
        setLanguageModel.add(languageModel);
        BaseStoreModel baseStoreModel = new BaseStoreModel();
        baseStoreModel.setUid("distrelec_CH");
        baseStoreModel.setName("distrelev_CH", Locale.ENGLISH);
        baseStoreModel.setCurrencies(setCurrencyModel);
        baseStoreModel.setLanguages(setLanguageModel);
        baseStoreModel.setChannel(SiteChannel.B2B);

        List<Breadcrumb> bread = new ArrayList<>();
        Breadcrumb breadcrumb = new Breadcrumb("https://test.distrelec.ch/search?q=led", "LED", "first");
        bread.add(breadcrumb);
        TitleModel titleMOdel = new TitleModel();
        titleMOdel.setCode("MR");
        titleMOdel.setActive(true);
        titleMOdel.setName("Mr.", Locale.ENGLISH);

        final AddressModel addressModel = new AddressModel();
        addressModel.setFirstname("Distrelec");
        addressModel.setLastname("Testing");
        addressModel.setLine1("Nanikon");
        addressModel.setLine2("886");
        addressModel.setStreetname("Hagenholzstrasse");
        addressModel.setStreetnumber("8");
        addressModel.setPostalcode("8606");
        addressModel.setCountry(countryMOdel);
        addressModel.setPhone1("+41997592665");
        addressModel.setTitle(titleMOdel);

        final B2BCustomerModel b2bCustomerMOdel = new B2BCustomerModel();
        b2bCustomerMOdel.setName("Distrelec Testing");
        b2bCustomerMOdel.setCustomerType(CustomerType.B2B);
        b2bCustomerMOdel.setUid("b2bCustomer");
        b2bCustomerMOdel.setActive(true);
        b2bCustomerMOdel.setCustomerID("b2bCustomer");
        b2bCustomerMOdel.setErpContactID("b2bCustomer");
        b2bCustomerMOdel.setEmail("b2bCustomer@datwyler.com");
        final B2BUnitModel b2bUnitMOdel = new B2BUnitModel();
        b2bUnitMOdel.setUid("b2bUnit");
        b2bUnitMOdel.setActive(true);
        b2bUnitMOdel.setErpCustomerID("b2bUnit");
        b2bUnitMOdel.setCountry(countryMOdel);
        b2bUnitMOdel.setCurrency(currencyMOdel);
        b2bUnitMOdel.setOnlinePriceCalculation(false);
        b2bCustomerMOdel.setContactAddress(addressModel);
        b2bCustomerMOdel.setSessionCurrency(currencyMOdel);
        b2bUnitMOdel.setBillingAddress(addressModel);

        SalesOrgData salesOrgData = new SalesOrgData();
        salesOrgData.setCode("7310");
        salesOrgData.setCountryIsocode("CH");
        salesOrgData.setBrand("Distrelec CH");

        LanguageData language = new LanguageData();
        language.setActive(true);
        language.setIsocode("EN");
        language.setName("English");

        CurrencyData currency = new CurrencyData();
        currency.setActive(true);
        currency.setIsocode("CHF");
        currency.setName("Switzerland");
        currency.setSymbol("CHF");
        final B2BUnitData b2bUnit = new B2BUnitData();
        b2bUnit.setUid("b2bUnit");
        b2bUnit.setActive(true);
        b2bUnit.setErpCustomerId("b2bUnit");

        final CustomerData b2bCustomer = new CustomerData();
        b2bCustomer.setUid("b2bCustomer");
        b2bCustomer.setActive(true);
        b2bCustomer.setCurrency(currency);
        b2bCustomer.setEmail("b2bCustomer@datwyler.com");
        b2bCustomer.setCompanyName("Distrelec");
        b2bCustomer.setLanguage(language);
        b2bCustomer.setTitleCode("Mr");
        TitleModel title = new TitleModel();
        title.setCode("MR");
        title.setActive(true);
        title.setName("Mr.", Locale.ENGLISH);
        CountryData country = new CountryData();
        country.setIsocode("CH");
        country.setName("Switzerland");
        final AddressData address = new AddressData();
        address.setFirstName("Distrelec");
        address.setLastName("Testing");
        address.setLine1("Nanikon");
        address.setLine2("886");
        address.setPostalCode("8606");
        address.setCountry(country);
        address.setPhone("+41997592665");
        address.setTitle("Mr");
        b2bUnit.setCountry(country);

        CurrencyModel currencyModel = new CurrencyModel();
        currencyModel.setActive(true);
        currencyModel.setIsocode("CHF");
        currencyModel.setName("Swiss frank", Locale.ENGLISH);

        DeliveryModeModel deliverModeModel = new DeliveryModeModel();
        deliverModeModel.setCode("PICKUP");
        deliverModeModel.setName("Pickup", Locale.ENGLISH);

        PaymentModeModel paymentModeModel = new PaymentModeModel();
        paymentModeModel.setCode("ZN001");
        paymentModeModel.setActive(true);
        paymentModeModel.setName("CreditCard", Locale.ENGLISH);

        final CartModel cartModel = new CartModel();
        cartModel.setUser(b2bCustomerMOdel);
        cartModel.setCurrency(currencyModel);
        cartModel.setDate(new Date());
        cartModel.setTotalPrice(Double.valueOf(42));
        cartModel.setCode("0000001");
        cartModel.setErpOrderCode("0000002");
        cartModel.setDeliveryMode(deliverModeModel);
        cartModel.setPaymentMode(paymentModeModel);
        cartModel.setDeliveryAddress(addressModel);
        cartModel.setPaymentAddress(addressModel);
        cartModel.setTotalPrice(Double.valueOf("110"));
        cartModel.setTotalTax(Double.valueOf("10"));
        cartModel.setCalculated(true);
        cartModel.setSubtotal(Double.valueOf("100"));

        final CartData cartData = new CartData();
        cartData.setUser(b2bCustomer);
        cartData.setB2bCustomerData(b2bCustomer);
        cartData.setTotalPrice(priceData);
        cartData.setCode("0000001");
        cartData.setTotalPrice(priceData);
        cartData.setTotalTax(priceData);
        cartData.setCalculated(true);
        cartData.setSubTotal(priceData);
        b2bCustomer.setCurrency(currency);

        RegionData region = new RegionData();
        region.setIsocode("CH");
        region.setCountryIso("CH");
        region.setName("Switzerland");

        // Mockito.when(factFinderProductSearchPageData.getSorting()).thenReturn(sorting);
        Mockito.when(storeSessionFacade.getCurrentCountry()).thenReturn(country);
        Mockito.when(storeSessionFacade.getCurrentCurrency()).thenReturn(currency);
        /*
         * Mockito.when(abstractPageController.getClass().getResourceAsStream(Mockito.eq("/gitinfo/gitinfo.properties"))) .thenReturn(new
         * StringInputStream("git.revision=v7.2"));
         */
        Mockito.when(storeSessionFacade.getCurrentLanguage()).thenReturn(language);
        Mockito.when(storeSessionFacade.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        Mockito.when(sessionService.getAttribute(DistConstants.Session.CHANNEL)).thenReturn(SiteChannel.B2B);
        Mockito.when(storeSessionFacade.getCurrentRegion()).thenReturn(region);
        Mockito.when(storeSessionFacade.getCurrentSalesOrg()).thenReturn(salesOrgData);
        Mockito.when(sessionService.getOrLoadAttribute(Mockito.eq("currentSalesOrg"), Mockito.anyObject())).thenReturn(salesOrgData);

    }

    @Test
    public void showCartByIdTest() throws Exception {
        final DigitalDatalayer digitalDatalayer = new DigitalDatalayer();
        final SearchStateData searchState = new SearchStateData();
        final SearchQueryData query = new SearchQueryData();
        query.setCode(QUERY);
        query.setFreeTextSearch(QUERY);
        query.setValue(QUERY);
        searchState.setQuery(query);
        final SearchPageableData pageableData = new SearchPageableData();
        pageableData.setCurrentPage(1);
        pageableData.setPageSize(10);
        pageableData.setSort("ASC");
        pageableData.setTechnicalView(true);

        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = new FactFinderProductSearchPageData<>();
        List<FactFinderSortData> sorting = new ArrayList<>();
        FactFinderSortData sortData = new FactFinderSortData();
        sortData.setCode("ASC");
        sortData.setName("Ascending");
        sortData.setSelected(true);
        sorting.add(0, sortData);
        searchPageData.setSorting(sorting);

        final PriceData priceData = new PriceData();
        priceData.setCurrencyIso("CHF");
        priceData.setFormattedValue("100.00");
        priceData.setValue(BigDecimal.valueOf(100));
        final PriceData v1 = new PriceData();
        final PriceData v2 = new PriceData();
        final PriceData v3 = new PriceData();
        v1.setCurrencyIso("CHF");
        v1.setFormattedValue("CHF 22.-");
        v1.setValue(BigDecimal.valueOf(22));
        v1.setMinQuantity(10L);

        v2.setCurrencyIso("CHF");
        v2.setFormattedValue("CHF 32.-");
        v2.setValue(BigDecimal.valueOf(32));
        v2.setMinQuantity(5L);

        v3.setCurrencyIso("CHF");
        v3.setFormattedValue("CHF 42.-");
        v3.setValue(BigDecimal.valueOf(42));
        v3.setMinQuantity(1L);

        Map<Long, Map<String, PriceData>> volumePriceMap = new HashMap<>();
        final Map<String, PriceData> p1 = new HashMap<>();
        final Map<String, PriceData> p2 = new HashMap<>();
        final Map<String, PriceData> p3 = new HashMap<>();
        p1.put("list", v1);
        p2.put("list", v2);
        p3.put("list", v3);
        p1.put("custom", v1);
        p2.put("custom", v1);
        p3.put("custom", v1);
        volumePriceMap.put(Long.valueOf(1), p1);
        volumePriceMap.put(Long.valueOf(5), p2);
        volumePriceMap.put(Long.valueOf(10), p3);
        final ProductData productData = new ProductData();
        productData.setCode("5849020");
        productData.setName("TestProduct");
        productData.setManufacturer("Fluke");
        productData.setDescription("description");
        productData.setAvailableForPickup(true);
        productData.setPrice(priceData);
        productData.setVolumePricesMap(volumePriceMap);
        productData.setUrl("https://test.distrelec.ch/en/multimeter-digital-fluke-175-cal-trms-ac-fluke-fluke-175-cal/p/30032131?queryFromSuggest=true");
        productData.setSummary("Multimeter digital FLUKE 175");

        final ProductData b2bProductData = new ProductData();
        b2bProductData.setCode("5849020");
        b2bProductData.setName("TestProduct");
        b2bProductData.setManufacturer("Fluke");
        b2bProductData.setDescription("description");
        b2bProductData.setAvailableForPickup(true);
        b2bProductData.setPrice(priceData);
        b2bProductData.setVolumePricesMap(volumePriceMap);
        b2bProductData.setUrl("https://test.distrelec.ch/en/multimeter-digital-fluke-175-cal-trms-ac-fluke-fluke-175-cal/p/30032131?queryFromSuggest=true");
        b2bProductData.setSummary("Multimeter digital FLUKE 175");

        final ProductData productData1 = new ProductData();
        productData1.setCode("30032131");
        productData1.setName("Multimeter digital FLUKE 175");
        productData1.setManufacturer("Fluke");
        productData1.setDescription("Multimeter digital FLUKE 175");
        productData1.setAvailableForPickup(true);
        productData1.setPrice(priceData);
        productData1.setUrl("https://test.distrelec.ch/en/multimeter-digital-fluke-175-cal-trms-ac-fluke-fluke-175-cal/p/30032131?queryFromSuggest=true");
        productData1.setSummary("Multimeter digital FLUKE 175");
        final List<ProductData> searchResults = new ArrayList<>();
        searchResults.add(productData);
        searchResults.add(productData1);
        searchPageData.setCode("led");
        searchPageData.setResults(searchResults);
        final FactFinderPaginationData pagination = new FactFinderPaginationData();
        pagination.setCurrentPage(1);
        pagination.setNumberOfPages(2);
        pagination.setPageSize(10);
        pagination.setSort("DESC");
        pagination.setTotalNumberOfResults(19);
        searchPageData.setPagination(pagination);
        searchState.setUrl("https://test.distrelec.ch/search?q=led");
        searchPageData.setCurrentQuery(searchState);
        searchPageData.setResults(searchResults);
        digitalDatalayerFacade.populateDTMSearchData(QUERY, searchPageData, digitalDatalayer);
        Product product = digitalDatalayerFacade.populateProductDTMObjects(b2bProductData);
        Assert.assertNotNull(product.getProductInfo().getUnitPrice());
        Assert.assertNotNull(product.getProductInfo().getUnitPriceThreshold());
    }

}
