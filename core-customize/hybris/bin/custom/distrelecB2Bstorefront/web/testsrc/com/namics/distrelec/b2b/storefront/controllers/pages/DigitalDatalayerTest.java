package com.namics.distrelec.b2b.storefront.controllers.pages;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.httpclient.HttpURL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurerAdapter;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.cart.dao.DistCartDao;
import com.namics.distrelec.b2b.core.service.order.impl.DefaultDistCartService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.adobe.datalayer.DistDigitalDatalayerFacade;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.bomtool.DistBomToolFacade;
import com.namics.distrelec.b2b.facades.caching.DistCachingFacade;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.category.DistCategoryIndexFacade;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.order.cart.impl.DefaultDistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.seo.DistSeoFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistCompareListFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade;
import com.namics.distrelec.b2b.storefront.support.PageTitleResolver;
import com.namics.distrelec.b2b.storefront.util.SearchRobotDetector;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.product.data.CartEntryData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.security.dynamic.PrincipalAllGroupsAttributeHandler;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;

/**
 * {@code DigitalDatalayerTest}
 *
 * @since Distrelec 7.3
 */

@RunWith(MockitoJUnitRunner.class)
public class DigitalDatalayerTest {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalDatalayerTest.class);

    private static final String CART_CODE = "0984HFNBAG";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:SS";

    private static final String COUNTRY_NAME_CCASE_EN = "Switzerland";

    private static final String COUNTRY_NAME_LCASE_EN = "switzerland";

    private static final String COUNTRY_NAME_CCASE_DE = "Suisse";

    private static final String CURRENCY_CHF_CCASE_EN = "CHF";

    private static final String CURRENCY_CHF_LCASE_EN = "chf";

    @InjectMocks
    HomePageController controller = new HomePageController();

    // @Mock
    // private AbstractPageController abstractPageController;

    @Mock
    HttpURL url;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    protected Configuration configuration;

    @Mock
    protected CartModel cartModel;

    @Mock
    protected DefaultDistB2BCartFacade b2bCartFacade;

    @Mock
    protected DistCartFacade cartFacade;

    @Mock
    protected DistUserFacade distUserFacade;

    @Mock
    protected DefaultDistCartService cartService;

    @Mock
    protected SessionService sessionService;

    @Mock
    protected B2BCustomerModel b2bCModel;

    protected MockMvc mockMvc;

    @Mock
    protected DistProductService productService;

    @Mock
    protected DistSalesStatusModel productSalesStatusModel;

    @Mock
    protected ProductModel productModel;

    @Mock
    protected ProductData productData;

    @Mock
    protected ProductPageModel pageModel;

    @Mock
    protected CMSSiteModel cmsSiteModel;

    @Mock
    protected UserModel currentUser;

    @Mock
    protected SalesOrgData salesOrgData;

    @Mock
    protected CustomerData distB2BCustomerData;

    @Mock
    protected BaseStoreModel baseStoreModel;

    @Mock
    protected DistrelecProductFacade distrelecProductFacade;

    @Mock
    protected MessageSource messageSource;

    @Mock
    protected I18NService i18nService;

    @Mock
    protected DistSeoFacade distSeoFacade;

    @Mock
    protected PageTitleResolver pageTitleResolver;

    @Mock
    protected DistCmsPageService distCmsPageService;

    @Mock
    protected DistWebtrekkFacade distWebtrekkFacade;

    @Mock
    protected DistrelecCMSSiteService cmsSiteService;

    @Mock
    public DistDigitalDatalayerFacade distDigitalDatalayerFacade;

    @Mock
    protected DistrelecStoreSessionFacade storeSessionFacade;

    @Mock
    protected UserService userService;

    @Mock
    protected DistCustomerFacade b2bCustomerFacade;

    @Mock
    protected DistB2BOrderFacade orderFacade;

    @Mock
    protected CommonI18NService commonI18NService;

    @Mock
    protected SiteConfigService siteConfigService;

    @Mock
    protected SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Mock
    protected BaseSiteService baseSiteService;

    @Mock
    protected DistrelecBaseStoreService baseStoreService;

    @Mock
    protected DistOciService distOciService;

    @Mock
    protected DistAribaService distAribaService;

    @Mock
    protected SearchRobotDetector searchRobotDetector;

    @Mock
    protected FactFinderChannelService channelService;

    @Mock
    protected DistShoppingListFacade distShoppingListFacade;

    @Mock
    protected DistCompareListFacade distCompareListFacade;

    @Mock
    protected InvoiceHistoryFacade invoiceHistoryFacade;

    @Mock
    protected OrderHistoryFacade orderHistoryFacade;

    @Mock
    protected DistCachingFacade distCachingFacade;

    @Mock
    protected DistCartDao distCartDao;

    @Mock
    private HttpServletRequest request;

    @Mock
    RedirectAttributes redirectModel;

    @Mock
    private CMSContentSlotService cmsContentSlotService;

    @Mock
    private DistCategoryFacade distCategoryFacade;

    @Mock
    private DistBomToolFacade distBomToolFacade;

    @Mock
    private CMSPreviewService cmsPreviewService;

    @Mock
    private DistCategoryIndexFacade distCategoryIndexFacade;

    @Mock
    private HttpSession session;

    @Mock
    MediaService mediaService;

    @Spy
    private ModelMap modelMap;
    /*
     * @Mock Map<String, Object> modelMap = new HashMap<String, Object>();
     */

    @Mock
    private AbstractPageModel abstractPageModel;

    @Mock
    private ContentPageModel contentPageModel;

    /*
     * @Autowired private WebApplicationContext context;
     */

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest#setUp()
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Configuration mockConfiguration = mock(Configuration.class);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).apply(new MockMvcConfigurerAdapter() {
            @Override
            public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
                super.afterConfigurerAdded(builder);
            }

            @Override
            public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder, WebApplicationContext cxt) {
                ConfigurationService mockConfigurationService = mock(ConfigurationService.class);
                when(mockConfigurationService.getConfiguration())
                                                                 .thenReturn(mockConfiguration);
                ReflectionTestUtils.invokeMethod(cxt.getAutowireCapableBeanFactory(), "addBean", "configurationService", mockConfigurationService);
                ReflectionTestUtils.invokeMethod(cxt.getAutowireCapableBeanFactory(), "addBean", "Principal_allGroupsAttributeHandler",
                                                 new PrincipalAllGroupsAttributeHandler());
                return super.beforeMockMvcCreated(builder, cxt);
            }
        }).build();
        // Model model = new ExtendedModelMap();
        SalesOrgData salesOrgData = new SalesOrgData();
        salesOrgData.setCode("7310");
        salesOrgData.setCountryIsocode("CH");
        salesOrgData.setBrand("Distrelec CH");
        Mockito.when(storeSessionFacade.getCurrentSalesOrg()).thenReturn(salesOrgData);
        Mockito.when(sessionService.getOrLoadAttribute(eq("currentSalesOrg"), anyObject())).thenReturn(salesOrgData);
        LanguageData language = new LanguageData();
        language.setActive(true);
        language.setIsocode("EN");
        language.setName("English");
        language.setNameEN("English");
        CurrencyData currency = new CurrencyData();
        currency.setActive(true);
        currency.setIsocode(CURRENCY_CHF_CCASE_EN);
        currency.setName(COUNTRY_NAME_CCASE_EN);
        currency.setSymbol(CURRENCY_CHF_CCASE_EN);
        final B2BUnitData b2bUnit = new B2BUnitData();
        b2bUnit.setUid("b2bUnit");
        b2bUnit.setActive(true);
        b2bUnit.setErpCustomerId("b2bUnit");
        CountryData country = new CountryData();
        country.setIsocode("CH");
        country.setName("Suisse");
        country.setNameEN(COUNTRY_NAME_LCASE_EN);
        final AddressData address = new AddressData();
        address.setFirstName("Distrelec");
        address.setLastName("Testing");
        address.setLine1("Nanikon");
        address.setLine2("886");
        address.setPostalCode("8606");
        address.setCountry(country);
        address.setPhone("+41997592665");
        address.setTitle("Mr");
        address.setBillingAddress(true);
        address.setShippingAddress(true);
        final CustomerData b2bCustomer = new CustomerData();
        b2bCustomer.setUid("b2bCustomer");
        b2bCustomer.setContactId("test");
        b2bCustomer.setActive(true);
        b2bCustomer.setCurrency(currency);
        b2bCustomer.setEmail("b2bCustomer@datwyler.com");
        b2bCustomer.setCompanyName("Distrelec");
        b2bCustomer.setUnit(b2bUnit);
        b2bCustomer.setLanguage(language);
        b2bCustomer.setTitleCode("Mr");
        b2bCustomer.setContactAddress(address);
        b2bCustomer.setBillingAddress(address);
        TitleModel title = new TitleModel();
        title.setCode("MR");
        title.setActive(true);
        title.setName("Mr.", Locale.ENGLISH);

        b2bUnit.setCountry(country);

        CurrencyModel currencyModel = new CurrencyModel();
        currencyModel.setActive(true);
        currencyModel.setIsocode(CURRENCY_CHF_CCASE_EN);
        currencyModel.setName("Swiss frank", Locale.ENGLISH);

        b2bCustomer.setCurrency(currency);
        DeliveryModeModel deliverMode = new DeliveryModeModel();
        deliverMode.setCode("PICKUP");
        deliverMode.setName("Pickup", Locale.ENGLISH);

        PaymentModeModel paymentMode = new PaymentModeModel();
        paymentMode.setCode("ZN001");
        paymentMode.setActive(true);
        paymentMode.setName("CreditCard", Locale.ENGLISH);
        PriceData priceData = new PriceData();
        priceData.setCurrencyIso(CURRENCY_CHF_CCASE_EN);
        priceData.setFormattedValue("100.00");
        priceData.setValue(BigDecimal.valueOf(100));
        final CartData cartData = new CartData();
        cartData.setUser(b2bCustomer);
        cartData.setB2bCustomerData(b2bCustomer);
        cartData.setTotalPrice(priceData);
        cartData.setCode("0000001");
        cartData.setTotalPrice(priceData);
        cartData.setTotalTax(priceData);
        cartData.setCalculated(true);
        cartData.setSubTotal(priceData);
        ProductData productData = new ProductData();
        productData.setCode("5849020");
        productData.setName("TestProduct");
        productData.setManufacturer("Fluke");
        productData.setDescription("description");
        UnitModel unit = new UnitModel();
        final CartEntryData cartEntryData = new CartEntryData();
        cartEntryData.setEntryNumber(1L);
        unit.setCode("PCS");
        unit.setUnitType("PCS");
        unit.setName("PCS", Locale.ENGLISH);
        cartEntryData.setQuantity(Long.valueOf(1));
        cartEntryData.setSku("PCS");

        sessionService.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, b2bCustomer);
        Mockito.when(configuration.getBoolean("feature.datalayer.enable", true)).thenReturn(true);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configurationService.getConfiguration().getString(eq("breadcrumb.maxlength"))).thenReturn("10");
        // set current cms site

        MediaModel mediaModel = new MediaModel();
        mediaModel.setURL("http://www.distrelec.com/q12");
        mediaModel.setAltText("Logo");
        mediaModel.setCode("media");
        Mockito.when(mediaService.getMedia(anyObject(), anyString())).thenReturn(mediaModel);
        Mockito.when(mediaService.getMedia(anyObject())).thenReturn(mediaModel);
        Mockito.when(modelMap.get("pageTitle")).thenReturn("Homepage");
        Mockito.when(modelMap.get("metaDescription")).thenReturn("metaDescription");
        SearchPageData<DistB2BInvoiceHistoryData> searchPageData = new SearchPageData<>();
        searchPageData.setResults(Collections.EMPTY_LIST);
        Mockito.when(invoiceHistoryFacade.getInvoiceSearchHistory(anyObject())).thenReturn(searchPageData);
        SearchPageData<OrderHistoryData> orderHistoryPageableData = new SearchPageData<>();
        orderHistoryPageableData.setResults(Collections.EMPTY_LIST);
        Mockito.when(orderHistoryFacade.getOrderHistory(Mockito.anyObject(), Mockito.any())).thenReturn(orderHistoryPageableData);

        Mockito.when(pageTitleResolver.resolveHomePageTitle(anyString())).thenReturn("HomePage");
        Mockito.when(messageSource.getMessage(Mockito.anyString(), Mockito.any(), Mockito.anyString(), Mockito.anyObject())).thenReturn("HomePage");

        RegionData region = new RegionData();
        region.setIsocode("CH");
        region.setCountryIso("CH");
        region.setName("Nanikon");
        Mockito.when(storeSessionFacade.getCurrentCountry()).thenReturn(country);
        Mockito.when(storeSessionFacade.getCurrentCurrency()).thenReturn(currency);
        Mockito.when(storeSessionFacade.getCurrentLanguage()).thenReturn(language);
        Mockito.when(storeSessionFacade.getCurrentLocale()).thenReturn(Locale.ENGLISH);
        Mockito.when(storeSessionFacade.getCurrentRegion()).thenReturn(region);

        LanguageModel languageModel = new LanguageModel();
        languageModel.setActive(true);
        languageModel.setIsocode("EN");
        languageModel.setName("English", Locale.ENGLISH);
        languageModel.setName("Englisch", Locale.GERMAN);
        CurrencyModel currencyMOdel = new CurrencyModel();
        currencyMOdel.setActive(true);
        currencyMOdel.setIsocode(CURRENCY_CHF_CCASE_EN);
        currencyMOdel.setName(COUNTRY_NAME_CCASE_EN, Locale.ENGLISH);
        currencyMOdel.setName(COUNTRY_NAME_CCASE_DE, Locale.GERMAN);
        currencyMOdel.setSymbol(CURRENCY_CHF_CCASE_EN);
        final B2BUnitModel b2bUnitMOdel = new B2BUnitModel();
        b2bUnitMOdel.setUid("b2bUnit");
        b2bUnitMOdel.setActive(true);
        b2bUnitMOdel.setErpCustomerID("b2bUnit");

        final B2BCustomerModel b2bCustomerMOdel = new B2BCustomerModel();
        b2bCustomerMOdel.setName("Distrelec Testing");
        b2bCustomerMOdel.setCustomerType(CustomerType.B2B);
        b2bCustomerMOdel.setUid("b2bCustomer");
        b2bCustomerMOdel.setActive(true);
        b2bCustomerMOdel.setCustomerID("b2bCustomer");
        b2bCustomerMOdel.setErpContactID("b2bCustomer");
        b2bCustomerMOdel.setEmail("b2bCustomer@datwyler.com");

        TitleModel titleMOdel = new TitleModel();
        titleMOdel.setCode("MR");
        titleMOdel.setActive(true);
        titleMOdel.setName("Mr.", Locale.ENGLISH);
        CountryModel countryMOdel = new CountryModel();
        countryMOdel.setIsocode("CH");
        countryMOdel.setName(COUNTRY_NAME_LCASE_EN, Locale.ENGLISH);
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
        b2bUnitMOdel.setCountry(countryMOdel);
        b2bUnitMOdel.setCurrency(currencyMOdel);
        b2bCustomerMOdel.setContactAddress(addressModel);
        b2bCustomerMOdel.setSessionCurrency(currencyMOdel);
        b2bUnitMOdel.setBillingAddress(addressModel);

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
        ProductModel productModel = new ProductModel();
        productModel.setCode("5849020");
        productModel.setName("TestProduct", Locale.ENGLISH);
        productModel.setManufacturerName("Fluke");
        productModel.setDescription("description", Locale.ENGLISH);
        UnitModel unitModel = new UnitModel();
        final CartEntryModel cartEntryModel = new CartEntryModel();
        cartEntryModel.setProduct(productModel);
        unitModel.setCode("PCS");
        unitModel.setUnitType("PCS");
        unitModel.setName("PCS", Locale.ENGLISH);
        cartEntryModel.setQuantity(Long.valueOf(1));
        cartEntryModel.setUnit(unitModel);
        cartEntryModel.setBasePrice(Double.valueOf(42));
        cartEntryModel.setTotalPrice(Double.valueOf(42));
        cartEntryModel.setOrder(cartModel);

        final PaymentTransactionModel paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModel.setCode(b2bCustomerMOdel.getUid() + "." + cartModel.getCode() + "." + UUID.randomUUID().toString().replaceAll("-", ""));
        paymentTransactionModel.setInfo(cartModel.getPaymentInfo());
        paymentTransactionModel.setCurrency(cartModel.getCurrency());
        paymentTransactionModel.setOrder(cartModel);

        cartService.setSessionCart(cartModel);
        sessionService.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, b2bCustomerMOdel);

        List<DeliveryModeData> listDeliverModes = new ArrayList<>();
        DeliveryModeData deliveryModeData = new DeliveryModeData();
        deliveryModeData.setCode("Pickup");
        deliveryModeData.setDefaultDeliveryMode(true);
        deliveryModeData.setName("Pickup");
        deliveryModeData.setSelectable(true);
        listDeliverModes.add(deliveryModeData);
        DistPaymentModeData paymentModeData = new DistPaymentModeData();
        paymentModeData.setCode("ZN001");
        paymentModeData.setCreditCardPayment(true);
        paymentModeData.setName("CreditCard");

        List<DistPaymentModeData> listDistPaymentModeData = new ArrayList<>();
        listDistPaymentModeData.add(paymentModeData);
        Mockito.when(distUserFacade.getSupportedDeliveryModesForUser()).thenReturn(listDeliverModes);
        Mockito.when(distUserFacade.getSupportedPaymentModesForUser()).thenReturn(listDistPaymentModeData);
        // set current cms site
        final CMSSiteModel currentCMSSiteMOdel = new CMSSiteModel();
        currentCMSSiteMOdel.setUid("distrelec_CH");
        currentCMSSiteMOdel.setActive(true);
        currentCMSSiteMOdel.setName("Distrelec", Locale.ENGLISH);
        currentCMSSiteMOdel.setChannel(SiteChannel.B2B);
        currentCMSSiteMOdel.setCountry(countryMOdel);
        currentCMSSiteMOdel.setDefaultCurrency(currencyMOdel);
        currentCMSSiteMOdel.setDefaultLanguage(languageModel);
        currentCMSSiteMOdel.setUserTaxGroup(UserTaxGroup.valueOf("SalesOrg_UPG_7310_M01"));
        currentCMSSiteMOdel.setReevootrkref(Collections.emptyMap());

        baseSiteService.setCurrentBaseSite(currentCMSSiteMOdel, true);
        cmsSiteService.setCurrentSite(currentCMSSiteMOdel);
        // Set the user data in session

        sessionService.setAttribute("cart", cartModel);

        Mockito.when((CMSSiteModel) b2bCModel.getCustomersBaseSite()).thenReturn(currentCMSSiteMOdel);
        Mockito.when(cmsSiteService.getCurrentSite()).thenReturn(currentCMSSiteMOdel);
        Mockito.when(sessionService.getAttribute(eq("currentSite"))).thenReturn(currentCMSSiteMOdel);
        Mockito.when(sessionService.getOrLoadAttribute(eq("currentSiteName"), anyObject())).thenReturn(currentCMSSiteMOdel.getName(Locale.ENGLISH));
        Mockito.when(sessionService.getOrLoadAttribute(eq("currentUserData"), anyObject())).thenReturn(b2bCustomer);
        Mockito.when(sessionService.getOrLoadAttribute(eq("user"), anyObject())).thenReturn(b2bCustomerMOdel);
        Mockito.when(sessionService.getOrLoadAttribute(eq("cart"), anyObject())).thenReturn(cartModel);
        Mockito.when(b2bCartFacade.hasSessionCart()).thenReturn(true);
        Mockito.when(b2bCartFacade.getSessionCart()).thenReturn(cartData);
        Mockito.when(b2bCartFacade.getSessionCartModel()).thenReturn(cartModel);
        Mockito.when(b2bCartFacade.getCurrentCart()).thenReturn(cartData);
        Mockito.when(modelMap.get("user")).thenReturn(b2bCustomer);

        // setup userService

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
        Mockito.when(cmsSiteService.getCurrentSite()).thenReturn(currentCMSSiteMOdel);

        Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStoreModel);
        Mockito.when(baseStoreService.getCurrentChannel(currentCMSSiteMOdel)).thenReturn(SiteChannel.B2B);
        Mockito.when(userService.getCurrentUser()).thenReturn(b2bCustomerMOdel);
        Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(currentCMSSiteMOdel);
        Mockito.when(i18nService.getCurrentLocale()).thenReturn(Locale.ENGLISH);
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
        pageTemplate.setFrontendTemplateName("homePage");
        pageTemplate.setName("homePage");
        pageTemplate.setUid("homePage");

        ContentPageModel contentPage = mock(ContentPageModel.class);
        contentPage.setHomepage(true);
        contentPage.setDescription("ContentPage", Locale.ENGLISH);
        contentPage.setName("homepage");
        contentPage.setUid("homepage");
        contentPage.setLabel("Homepage");
        contentPage.setTitle("HomePage", Locale.ENGLISH);
        contentPage.setMasterTemplate(pageTemplate);

        AbstractPageModel pageModel = new AbstractPageModel();
        pageModel.setName("Homapage");
        pageModel.setTitle("Homapage", Locale.ENGLISH);
        pageModel.setCatalogVersion(catalogVersionModel);
        pageModel.setMasterTemplate(pageTemplate);
        pageModel.setUid("Homapage");

        when(distCmsPageService.getPageForLabelOrId(anyString())).thenReturn(contentPage);

        Mockito.when(cmsSiteService.getCurrentCatalogVersion()).thenReturn(catalogVersionModel);
        Mockito.when(modelMap.get(eq("cmsPage"))).thenReturn(pageModel);
        Mockito.when(distCmsPageService.getHomepage()).thenReturn(contentPage);
        Mockito.when(distCmsPageService.getHomepage().getMasterTemplate()).thenReturn(pageTemplate);
        Mockito.when(abstractPageModel.getMasterTemplate()).thenReturn(pageTemplate);
        userService.setCurrentUser(b2bCustomerMOdel);
        // Set the language in session
        final List<LanguageData> languageDataList = new ArrayList<>();
        Mockito.when(storeSessionFacade.getAllLanguages()).thenReturn(languageDataList);
        Mockito.when(request.isSecure()).thenReturn(false);
        Mockito.when(sessionService.getOrLoadAttribute(eq("languages"), anyObject())).thenReturn(languageDataList);

        when(distCmsPageService.getPageForLabelOrId(anyString())).thenReturn(contentPage);
        b2bCustomerMOdel.setSessionLanguage(languageModel);
        distB2BCustomerData.setLanguage(language);
        Mockito.when(userService.getCurrentUser()).thenReturn(b2bCustomerMOdel);
        Mockito.when(b2bCartFacade.allowedToAccessCartWithCode(CART_CODE)).thenReturn(true);
        Mockito.when(cartFacade.allowedToAccessCartWithCode(CART_CODE)).thenReturn(true);
        Mockito.when(distCartDao.getCartForCode(CART_CODE)).thenReturn(Optional.ofNullable(cartModel));
        Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
        Mockito.when(cartFacade.getSessionCart()).thenReturn(cartData);
        Mockito.when(modelMap.getOrDefault("cmaPage", pageModel)).thenReturn(pageModel);
        Mockito.when(distCmsPageService.getFrontendTemplateName(anyObject())).thenReturn("homePage");

        ContentSlotModel mainNavContentSlot = Mockito.mock(ContentSlotModel.class);
        Mockito.when(mainNavContentSlot.getCmsComponents())
               .thenReturn(Collections.emptyList());

        Mockito.when(cmsContentSlotService.getContentSlotForId("MainNavSlot"))
               .thenReturn(mainNavContentSlot);

        ContentSlotModel disruptionMessagesContentSlot = mock(ContentSlotModel.class);
        Mockito.when(cmsContentSlotService.getContentSlotForId("DisruptionMessagesSlot"))
               .thenReturn(disruptionMessagesContentSlot);
        Mockito.when(disruptionMessagesContentSlot.getCmsComponents())
               .thenReturn(Collections.emptyList());

        Mockito.when(distAribaService.isAribaCustomer())
               .thenReturn(false);
        controller.setDistAribaService(distAribaService);

        when(cmsSiteModel.isHttpsOnly())
                                        .thenReturn(true);

        SearchPageData<OrderHistoryData> openOrdersSearchData = mock(SearchPageData.class);
        when(openOrdersSearchData.getResults())
                                               .thenReturn(Collections.emptyList());
        when(orderHistoryFacade.getOpenOrders(any()))
                                                     .thenReturn(openOrdersSearchData);

        when(distCmsPageService.getPageForLabelOrId(anyString(), any()))
                                                                        .thenReturn(contentPage);

        when(distCategoryIndexFacade.getTopCategoryIndexData())
                                                               .thenReturn(Collections.emptyList());
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest#setUp(com.namics.distrelec.b2b.storefront.
     * controllers.pages.AbstractPageController)
     */

    @Test
    public void showCartByIdTest() throws Exception {

        ResultActions result = mockMvc.perform(get("/")) //
                                      .andExpect(status().isOk()); //
        MvcResult mvcResult = result.andReturn();
        DigitalDatalayer digitalData = (DigitalDatalayer) mvcResult.getModelAndView().getModelMap().get("digitaldata");
        Assert.assertNotNull(digitalData.getUser());
        Assert.assertNotNull(digitalData.getUser().get(0));
        Assert.assertNotNull(digitalData.getUser().get(0).getProfile());
        Assert.assertNotNull(digitalData.getUser().get(0).getProfile().get(0));
        Assert.assertNotNull(digitalData.getUser().get(0).getProfile().get(0).getProfileInfo());
        Assert.assertNotNull(digitalData.getPage().getPageInfo().getCurrency());

        // Tests EN language and lower case
        Assert.assertEquals(digitalData.getPage().getPageInfo().getCurrency(), CURRENCY_CHF_LCASE_EN);
        Assert.assertEquals(digitalData.getUser().get(0).getProfile().get(0).getProfileInfo().getCountry(), COUNTRY_NAME_LCASE_EN);

    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageControllerTest#getController()
     */

    protected AbstractPageController getController() {
        return controller;
    }

    /*
     * private DigitalDatalayer createDigitalDatalayerForm() { DigitalDatalayer form = new DigitalDatalayer();
     * return form; }
     */

    public static String asJsonString(final Object obj) {
        String jsoncontent = "";

        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsoncontent = mapper.writeValueAsString(obj);
            LOG.info(jsoncontent);
        } catch (IOException e1) {
            LOG.warn("Exception occurred during conversion of object to JSON", e1);
        }
        return jsoncontent;
    }
}
