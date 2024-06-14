package com.namics.distrelec.b2b.storefront.controllers.pages;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ENVIRONMENT_ISPROD;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Product.PRODUCT_CODE_REGEX;
import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Cookie.SUBSCRIBE_POPUP_DELAY_AFTER;
import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Cookie.SUBSCRIBE_POPUP_DELAY_AFTER_DEFAULT;
import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Cookie.SUBSCRIBE_POPUP_DELAY_BEFORE;
import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Cookie.SUBSCRIBE_POPUP_DELAY_BEFORE_DEFAULT;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.beans.Introspector;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.ariba.DistAribaService;
import com.namics.distrelec.b2b.core.eprocurement.service.cxml.DistCxmlService;
import com.namics.distrelec.b2b.core.eprocurement.service.oci.DistOciService;
import com.namics.distrelec.b2b.core.event.DistSendToFriendEvent;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistWarningComponentModel;
import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.cms.DistCMSNavigationService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.core.version.GitVersionService;
import com.namics.distrelec.b2b.facades.adobe.datalayer.DistDigitalDatalayerFacade;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Page;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.PageCategory;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.PageInfo;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Product;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Profile;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.ProfileInfo;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Registration_;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Segment;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.User;
import com.namics.distrelec.b2b.facades.bomtool.DistBomToolFacade;
import com.namics.distrelec.b2b.facades.caching.DistCachingFacade;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.customer.DistUserDashboardFacade;
import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.EnergyEfficencyData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.seo.DistSeoFacade;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ChannelData;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import com.namics.distrelec.b2b.facades.storesession.data.ShoppingSettingsCookieData;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.util.MetaSanitizerUtil;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.facades.webtrekk.data.CartDataLayer;
import com.namics.distrelec.b2b.facades.webtrekk.data.CustomerDataLayer;
import com.namics.distrelec.b2b.facades.webtrekk.data.PageDataLayer;
import com.namics.distrelec.b2b.facades.webtrekk.data.ShopDataLayer;
import com.namics.distrelec.b2b.facades.wishlist.DistCompareListFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.MiniWishlistData;
import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.controllers.util.ShopSettingsUtil;
import com.namics.distrelec.b2b.storefront.excepions.DistrelecBadRequestException;
import com.namics.distrelec.b2b.storefront.forms.AbstractDistAddressForm;
import com.namics.distrelec.b2b.storefront.forms.LoginForm;
import com.namics.distrelec.b2b.storefront.forms.SendToFriendForm;
import com.namics.distrelec.b2b.storefront.seo.DistLink;
import com.namics.distrelec.b2b.storefront.seo.LinkType;
import com.namics.distrelec.b2b.storefront.support.PageTitleResolver;
import com.namics.distrelec.b2b.storefront.util.CaptchaUtil;
import com.namics.distrelec.b2b.storefront.util.SearchRobotDetector;
import com.namics.distrelec.b2b.storefront.web.view.UiExperienceViewResolver;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;
import com.namics.hybris.ffsearch.util.XSSFilterUtil;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.enums.CmsRobotTag;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

public abstract class AbstractPageController extends AbstractController {

    public static final String URL_PARAMETER_KEY_FOR_NAVIGATION_SOURCE = "dataLinkValue";

    public static final String PAGE_ROOT = "pages/";

    public static final String ROOT = "/";

    public static final String CMS_PAGE_MODEL = "cmsPage";

    public static final String CMS_PAGE_TITLE = "pageTitle";

    public static final String COMPRESSION_CONFIG_KEY_PREFIX = "enable.compression.css.js";

    public static final Logger ERROR_PAGE_LOG = LogManager.getLogger(ControllerConstants.Views.Pages.Error.ErrorPageLog);

    public static final String SHOULD_DISPLAY_STACKTRACES_IN_FRONTEND = "error.displaystacktracesinfrontend";

    public static final String UNKNOWN_YMS_HOSTNAME = "Unknown";

    public static final String CHECHOUT_URL = "checkout";

    protected static final String SUCCESS = "SUCCESS";

    protected static final String ERROR = "ERROR";

    protected static final String ACTIVATION = "ACTIVATION";

    protected static final String B2B = "B2B";

    protected static final String B2C = "B2C";

    protected static final String B2B_LOWER = "b2b";

    protected static final String B2C_LOWER = "b2c";

    protected static final String CHECKOUT = "CHECKOUT";

    protected static final String EXISTING = "EXISTING";

    protected static final String LINK_CANONICAL_RELATIONSHIP = "canonical";

    protected static final String LINK_NEXT = "next";

    protected static final String LINK_PREVIOUS = "prev";

    protected static final String URL_PAGINATION_STRING = "?page=";

    protected static final String OFFERED_QUOTES_STATUS_CODE = "02";

    protected static final String OFFERED_QUOTES_SESSION_ATTR = "offeredQuoteCount";

    protected static final int DEFAULT_SEARCH_MAX_SIZE = 100;

    protected static final String FILTER_CATEGORY_CODE_PATH_ROOT = "filter_categoryCodePathROOT";

    protected static final String REPLACEMENT_PRODUCT_LIST = "replacementProductList";

    protected static final String DEFAULT_EMPTY = "";

    protected static final String VIEW_ICON = "Icon";

    protected static final String VIEW_TECHNICAL = "Technical";

    protected static final String VIEW_STANDARD = "Standard";

    protected static final String CUSTOMER_ACTIVE = "Active";

    protected static final String CUSTOMER_INACTIVE = "Inactive";

    protected static final String REFERER = "Referer";

    protected static final String FORWARD_TO_404 = FORWARD_PREFIX + "/notFound";

    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.DIST_MANUFACTURER,
                                                                               ProductOption.PROMOTION_LABELS);

    private static final Logger LOG = LogManager.getLogger(AbstractPageController.class);

    private static final String LINK_ALTERNATE_RELATIONSHIP = "alternate";

    private static final String URL_QUERY_STRING = "?q=";

    private static final Pattern LANG_PATTERN = Pattern.compile("^/([a-z]{2})(/|$)");

    private static final List<String> TABULAR_VIEW_SORTING_ITEMS = Arrays.asList("Title:asc",
                                                                                 "Title:desc",
                                                                                 "ProductNumberMovex:asc",
                                                                                 "ProductNumberMovex:desc",
                                                                                 "Best match");

    private static final String CUSTOM_FILTER_KEY = "breadcrumb.customFilter";

    private static final String FALL_BACK_STRING = "Custom Filter";

    private static final Map<String, String> WT_PARAM_ID_MAP = new HashMap<>();

    static {
        WT_PARAM_ID_MAP.put("ONSITESEARCHRESULTS", "10");
        WT_PARAM_ID_MAP.put("ERRORS", "9");
        WT_PARAM_ID_MAP.put("CONTENTGROUP", "8");
        WT_PARAM_ID_MAP.put("CHANNEL", "7");
        WT_PARAM_ID_MAP.put("AREA", "6");
        WT_PARAM_ID_MAP.put("LANGUAGE", "5");
        WT_PARAM_ID_MAP.put("PAGEID", "4");
        WT_PARAM_ID_MAP.put("DISCOUNTVALUE", "10");
        WT_PARAM_ID_MAP.put("VOUCHERVALUE", "9");
        WT_PARAM_ID_MAP.put("SHIPPINGCOSTS", "8");
        WT_PARAM_ID_MAP.put("PAYMENTFEE", "7");
        WT_PARAM_ID_MAP.put("VAT", "6");
        WT_PARAM_ID_MAP.put("TOTALVALUE", "5");
        WT_PARAM_ID_MAP.put("NEWPRODUCTCODE", "4");
    }

    @Autowired
    protected DistManufacturerFacade distManufacturerFacade;

    @Autowired
    @Qualifier("userFacade")
    protected DistUserFacade userFacade;

    @Autowired
    @Qualifier("distCmsNavigationService")
    protected DistCMSNavigationService distCmsNavigationService;

    @Autowired
    @Qualifier("englishMessageSourceAccessor")
    protected MessageSourceAccessor englishMessageSourceAccessor;

    @Autowired
    @Qualifier("cmsContentSlotService")
    protected CMSContentSlotService cmsContentSlotService;

    @Autowired
    protected CMSPreviewService cmsPreviewService;

    @Autowired
    protected DistAribaService defaultDistAribaService;

    private String wtErrors;

    @Autowired
    @Qualifier("distSeoFacade")
    private DistSeoFacade distSeoFacade;

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistProductPriceQuotationFacade distProductPriceQuotationFacade;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade b2bCartFacade;

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("distDigitalDatalayerFacade")
    private DistDigitalDatalayerFacade distDigitalDatalayerFacade;

    @Autowired
    private GitVersionService gitVersionService;

    @Autowired
    private CaptchaUtil captchaUtil;

    @Resource(name = "distUserDashboardFacade")
    private DistUserDashboardFacade distUserDashboardFacade;

    @Autowired
    private DistCustomerFacade distCustomerFacade;

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    @Autowired
    private DistCmsPageService distCmsPageService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private SiteConfigService siteConfigService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    private DistAribaService distAribaService;

    @Autowired
    private DistOciService distOciService;

    @Autowired
    private DistCxmlService distCxmlService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private DistCustomerFacade customerFacade;

    @Autowired
    @Qualifier("b2bCustomerFacade")
    private DistCustomerFacade b2bCustomerFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Autowired
    private DistShoppingListFacade distShoppingListFacade;

    @Autowired
    private DistCompareListFacade distCompareListFacade;

    @Autowired
    private PageTitleResolver pageTitleResolver;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private DistCachingFacade distCachingFacade;

    @Autowired
    @Qualifier("b2bOrderFacade")
    private DistB2BOrderFacade orderFacade;

    @Autowired
    private SearchRobotDetector searchRobotDetector;

    @Autowired
    private FactFinderChannelService channelService;

    @Autowired
    private DistCategoryFacade distCategoryFacade;

    @Autowired
    private DistBomToolFacade distBomToolFacade;

    @Autowired
    private DistNewsletterFacade newsletterFacade;

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    private DistCheckoutFacade distCheckoutFacade;

    public static void sortDistLinks(final List<DistLink> headerResult, final Locale locale) {

        Collections.sort(headerResult, (o1, o2) -> {

            // DISTRELEC-14687 : Display all Web-site links lexicographically(Alphabetical Order).
            final Collator umlautCollator = Collator.getInstance(locale);
            if (o1.getCountryName() == null && o2.getCountryName() == null) {
                return 0;
            } else if (o1.getCountryName() == null) {
                return -1;
            } else if (o2.getCountryName() == null) {
                return 1;
            } else {
                return umlautCollator.compare(o1.getCountryName(), o2.getCountryName());
            }
        });
    }

    protected static String getFormClassName(final AbstractDistAddressForm addressForm) {
        return Introspector.decapitalize(ClassUtils.getShortName(addressForm.getClass()));
    }

    public void addGlobalModelAttributes(final Model model, final HttpServletRequest request) {

        model.addAttribute("englishMessageSourceAccessor", getEnglishMessageSourceAccessor());

        checkActive(model, request);

        final long start = System.currentTimeMillis();

        // Adding the common model attributes.
        addCommonModelAttributes(model, request);

        // FIXME it seems to be not used
        model.addAttribute("metaRobotContent", setUpMetaRobotContent());
        model.addAttribute("forceDesktopCookie", getForceDesktopCookie(request));

        addRequestSettings(model, request);

        model.addAttribute("shippingOptionsEditable", Boolean.valueOf(isShippingOptionsEditable()));
        model.addAttribute("paymentOptionsEditable", Boolean.valueOf(isPaymentOptionsEditable()));
        model.addAttribute("isExportShop", Boolean.valueOf(isCurrentShopExport()));

        // DISTRELEC-10246
        model.addAttribute("ffsearchChannel", getChannelService().getCurrentFactFinderChannel());

        // DISTRELEC-7488 adding compare, shopping and favorite lists datas
        addWishListsData(model, request);

        // Caching
        addCachingModelAttributes(model, request);

        // Search
        addSearchModelAttributes(model, request);

        // Adobe Analytics- DTM
        if (isDatalayerEnabled()) {
            populateDatalayer(model, request);
        }

        // Check the login status
        loginSuccessMessage(model, request);

        model.addAttribute("topCategories", distCustomerFacade.getTopCategories());
        model.addAttribute("bomFileCount", distBomToolFacade.getCountOfBomFilesUploadedForCurrentCustomer());

        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding global attributes took: [" + (System.currentTimeMillis() - start) + "ms]");
        }

        model.addAttribute("ymktTrackingEnabled", Boolean.FALSE);

        CMSSiteModel site = cmsSiteService.getCurrentSite();
        model.addAttribute("isReevooActivatedForWebshop", site != null && site.isReevooActivated());
        if (site != null) {
            model.addAttribute("isReevooBrandVisible", site.isReevooBrandVisible());
            Map<String, String> languageTrkRefMapping = site.getReevootrkref();
            if (languageTrkRefMapping.keySet() != null && languageTrkRefMapping.keySet().size() > 0) {
                final String currentLang = getI18nService().getCurrentLocale().getLanguage();
                String trkRef = currentLang != null ? languageTrkRefMapping.get(currentLang)
                                                    : languageTrkRefMapping.get(languageTrkRefMapping.keySet().toArray()[0]);
                model.addAttribute(DistConstants.PropKey.Reevoo.TRKREF_ID, trkRef);
            } else {
                model.addAttribute(DistConstants.PropKey.Reevoo.TRKREF_ID, null);
            }
            addMicrosoftUetTag(model, site);
        }
        model.addAttribute("isGuestCheckout", distCheckoutFacade.isAnonymousCheckout());
    }

    protected void addMicrosoftUetTag(Model model, CMSSiteModel site) {
        model.addAttribute("microsoftUetTagId", configurationService.getConfiguration().getString("microsoft.uet.tagID." + site.getUid(), StringUtils.EMPTY));
    }

    private void checkActive(final Model model, final HttpServletRequest request) {
        if ("false".equals(request.getParameter("active")) && "true".equals(request.getParameter("logout"))) {
            model.addAttribute(GlobalMessages.WARN_MESSAGES_HOLDER, "account.signout.active.error");
        }
    }

    protected DateFormat getCurrentDateFormat() {
        return new SimpleDateFormat(getCurrentDatePatternFormat(), getI18nService().getCurrentLocale());
    }

    protected String getCurrentDatePatternFormat() {
        return getMessageSource().getMessage("text.store.dateformat", null, "MM/dd/yyyy", getI18nService().getCurrentLocale());
    }

    protected String getLocalizedMessage(String key, String... args) {
        return getMessageSource().getMessage(key, args, getI18nService().getCurrentLocale());
    }

    protected void addCommonModelAttributes(final Model model, final HttpServletRequest request) {
        model.addAttribute("languages", getLanguages());
        model.addAttribute("currentSalesOrg", getCurrentSalesOrg());
        LanguageData currentLanguage = getCurrentLanguage();
        model.addAttribute("currentLanguage", currentLanguage);
        model.addAttribute("currentCurrency", getCurrentCurrency());
        model.addAttribute("currentCountry", getCurrentCountry());
        model.addAttribute("currentChannel", getCurrentChannel());
        model.addAttribute("currentBaseStore", getCurrentBaseStore());
        model.addAttribute("heroProducts", distSeoFacade.getActiveHeroProducts());

        model.addAttribute("siteUid", getSiteUid());
        model.addAttribute("siteName", getSiteName());
        model.addAttribute("printFooterContent", getPrintFooterContent());

        model.addAttribute("breadcrumbMaxLength", getBreadcrumbMaxLength());
        model.addAttribute("user", getUser());
        model.addAttribute("cssCompression", isCssCompressionEnabled());
        model.addAttribute("googleAnalyticsTrackingId", getGoogleAnalyticsTrackingId());
        model.addAttribute("googleAdWordsConversionId", getGoogleAdWordsConversionId());
        model.addAttribute("googleAdWordsConversionLabel", getGoogleAdWordsConversionLabel());
        model.addAttribute("googleAdWordsConversionTrackingId", getGoogleAdWordsConversionTrackingId());
        model.addAttribute("intelliAdId", getIntelliAdId());
        model.addAttribute("affilinetConversionTrackingSiteId", getAffilinetConversionTrackingSiteId());
        model.addAttribute("metaLoginForm", getMetaLoginForm());
        model.addAttribute("httpsSite", getHttpsSite(request.getRequestURI()));

        model.addAttribute("shopSettingsCookie", getShopSettingsCookie(request));
        model.addAttribute("pageType", getPageType());
        model.addAttribute("eProcurementCSSClass", getEProcurementCSSClass());
        model.addAttribute("currentKey", getCurrentKey());
        model.addAttribute("currentVersion", getCurrentVersion());

        model.addAttribute("shopSettingsCookie", getShopSettingsCookie(request));
        model.addAttribute("factfid", getFFidCookieValue(request));

        model.addAttribute("pageType", getPageType());
        // model.addAttribute("typekit", getTypeKit());

        model.addAttribute("currentEnv", getCurrentEnvironment());
        model.addAttribute("currentDateTime", getCurrentDateTimeString());
        model.addAttribute("isProd", isProd());

        model.addAttribute("energyEfficencyBgImage", getConfigurationService().getConfiguration().getProperty("energy.efficency.image.url"));

        // For OCI customers
        model.addAttribute("megaFlyOutDisabled", isMegaFlyOutDisabled());
        model.addAttribute("customFooterEnabled", isCustomFooterEnabled());

        model.addAttribute("tabularViewSortingItems", TABULAR_VIEW_SORTING_ITEMS);

        String currentSearchExperience = productSearchFacade.getSearchExperienceFromCurrentBaseStore().getCode().toLowerCase();
        model.addAttribute("searchExperience", currentSearchExperience);
        model.addAttribute("captcha_public_key", getCaptchaUtil().getPublicKey(request));

        if (!distUserDashboardFacade.isOciCustomer() && !distUserDashboardFacade.isAribaCustomer()) {
            model.addAttribute("appReqCount", distUserDashboardFacade.getApprovalRequestsCount());
            model.addAttribute("quoteCount", distUserDashboardFacade.getOfferedQuoteCount());
        }

        // DISTRELEC-22670 Add cookie used to control the newsletter popup
        model.addAttribute("isShowSubscribePopup", isShowSubscribePopup(request));
        model.addAttribute("subscribePopupDelay", configurationService.getConfiguration().getInt(SUBSCRIBE_POPUP_DELAY_BEFORE,
                                                                                                 SUBSCRIBE_POPUP_DELAY_BEFORE_DEFAULT));
        model.addAttribute("popupShownDelay", configurationService.getConfiguration().getInt(SUBSCRIBE_POPUP_DELAY_AFTER,
                                                                                             SUBSCRIBE_POPUP_DELAY_AFTER_DEFAULT));
    }

    private boolean isShowSubscribePopup(HttpServletRequest request) {
        if (isPageIgnored(request)) {
            return false;
        }
        return false;
    }

    private boolean isPageIgnored(HttpServletRequest request) {
        final String regexForIgnoredPages = configurationService.getConfiguration().getString("newsletter.popup.ignored.pages.regex");
        return request.getRequestURI().matches(regexForIgnoredPages);
    }

    protected boolean isShowDoubleOptInPopupForEmail(String email) {
        return false;
    }

    protected boolean isProd() {
        return getConfigurationService().getConfiguration().getBoolean(ENVIRONMENT_ISPROD, Boolean.FALSE);
    }

    public void populateTabularAttributeSortingMap(final Model model, final Map<String, String> sortableAttributeMap) {
        if (sortableAttributeMap != null && !sortableAttributeMap.isEmpty()) {
            model.addAttribute("tabularAttributeSortingMap", sortableAttributeMap);
        }
    }

    public boolean isConsentConfirmationRequired() {
        boolean isConfirmationRequired = false;
        try {
            final String[] baseSites = getConfigurationService().getConfiguration().getString("ymkt.customer.sevice.consent.confirmation.required.shop")
                                                                .split(",");
            final UserModel currentUser = getUserService().getCurrentUser();
            final boolean isAnonymous = getUserService().isAnonymousUser(currentUser);
            if (null != baseSites && baseSites.length > 0) {
                if (!isAnonymous && null != currentUser && currentUser instanceof B2BCustomerModel customer) {
                    final String customerBaseSite = (null != customer.getCustomersBaseSite() && null != customer.getCustomersBaseSite())
                                                                                                                                         ? customer.getCustomersBaseSite()
                                                                                                                                                   .getUid()
                                                                                                                                         : "";
                    isConfirmationRequired = (null != getCurrentBaseSiteUid()) ? Arrays.asList(baseSites).contains(getCurrentBaseSiteUid())
                                                                               : Arrays.asList(baseSites).contains(customerBaseSite);
                } else {
                    isConfirmationRequired = Arrays.asList(baseSites).contains(getCurrentBaseSiteUid());
                }
            }
        } catch (final Exception ex) {
            LOG.info("Exception in checking confirmation flag", ex);
        }
        return isConfirmationRequired;
    }

    private String getCurrentBaseSiteUid() {
        if (getBaseSiteService() != null && getBaseSiteService().getCurrentBaseSite() != null) {
            return getBaseSiteService().getCurrentBaseSite().getUid();
        }
        LOG.warn("no current base site available");
        return null;
    }

    protected void populateEnergyEfficencyData(final Model model, final String productCode) {
        final EnergyEfficencyData energyEfficencyData = productFacade.getEnergyEfficencyData(productCode);
        model.addAttribute("productEnergyEfficency", energyEfficencyData);
    }

    protected void addSearchModelAttributes(final Model model, HttpServletRequest request) {
        model.addAttribute("useTechnicalView", getUseTechnicalView());
        model.addAttribute("useIconView", getUseIconView());
        model.addAttribute("useListView", getUseListView());
        model.addAttribute("autoApplyFilter", getAutoApplyFilter());

        if (request.getParameterMap().containsKey(FILTER_CATEGORY_CODE_PATH_ROOT)) {
            String newFilteredCategoryCode = request.getParameter(FILTER_CATEGORY_CODE_PATH_ROOT);
            sessionService.setAttribute(FILTER_CATEGORY_CODE_PATH_ROOT, newFilteredCategoryCode);
        }

        model.addAttribute(FILTER_CATEGORY_CODE_PATH_ROOT, sessionService.getAttribute(FILTER_CATEGORY_CODE_PATH_ROOT));
    }

    /**
     * Add the request related attributes to the model.
     *
     * @param model
     * @param request
     */
    protected void addRequestSettings(final Model model, final HttpServletRequest request) {
        final URL requestURL = getURLRequest(request);
        model.addAttribute("headerMobileLinksTags", Collections.EMPTY_LIST);
        model.addAttribute("headerCanonicalLinksTags", setCanonicalLink(request));
        model.addAttribute("siteBaseURL", getBaseUrl(requestURL));
        model.addAttribute("isHomePage", Boolean.valueOf(isHomePage(requestURL)));
        model.addAttribute("headerLinksLangTags", setHrefLang(request));
        model.addAttribute("isSecure", Boolean.valueOf(isSecureConnection(request)));
        model.addAttribute("isSearchRobot", Boolean.valueOf(isSearchRobot(request)));
        model.addAttribute("useHttps", Boolean.valueOf(request.isSecure() || getCmsSiteService().getCurrentSite().isHttpsOnly()));
    }

    /**
     * Add Webtrekk related attributes to the model.
     *
     * @param model
     * @param request
     */
    protected void populateDTMCMSBreadcrumbs(final Model model, final HttpServletRequest request) {
        DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        Page page = digitalDatalayer.getPage();
        if (null == page) {
            page = new Page();
        }
        PageInfo pageInfo = page.getPageInfo();
        if (null == pageInfo) {
            pageInfo = new PageInfo();
        }
        List<Breadcrumb> breadCrumbs = new ArrayList<>();
        breadCrumbs = (List<Breadcrumb>) model.asMap().get(WebConstants.BREADCRUMBS_KEY);
        List<String> breadcrumbData = new ArrayList<>();
        if (null != breadCrumbs) {
            for (Breadcrumb breadCrumb : breadCrumbs) {
                breadcrumbData.add(null != breadCrumb.getNameEN() ? breadCrumb.getNameEN().toLowerCase() : "");
            }
            pageInfo.setBreadcrumbs(breadcrumbData);
        }
        pageInfo.setVersion(getCurrentVersion());
        page.setPageInfo(pageInfo);
        digitalDatalayer.setPage(page);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
    }

    /**
     * @param model
     * @param request
     */
    protected void populateDatalayer(final Model model, final HttpServletRequest request) {
        try {
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            if (b2bCartFacade.hasSessionCart()) {
                final CartModel cartData = b2bCartFacade.getSessionCartModel();
                getCartForDTM(digitalDatalayer, cartData, request);
            }
            if (null == digitalDatalayer.getPage()) {
                digitalDatalayer.setPage(new Page());
            }
            final Page page = digitalDatalayer.getPage();
            if (null == digitalDatalayer.getUser()) {
                digitalDatalayer.setUser(new ArrayList<>());
            }
            Map<String, Object> modelMap = model.asMap();
            CountryData currentCountry = (CountryData) modelMap.get("currentCountry");
            CurrencyData currentCurrency = (CurrencyData) modelMap.get("currentCurrency");
            LanguageData currentLanguage = (LanguageData) modelMap.get("currentLanguage");

            CustomerData customerData = (CustomerData) modelMap.get("user");
            List<User> users = digitalDatalayer.getUser();
            User user;
            if (CollectionUtils.isNotEmpty(users)) {
                user = users.get(0);
            } else {
                users = new ArrayList<>();
                user = new User();
                user.setRegistration(new Registration_());
            }

            if (null == user.getProfile()) {
                user.setProfile(new ArrayList<>());
            }
            List<Profile> userProfiles = user.getProfile();

            Profile userProfile = CollectionUtils.isNotEmpty(userProfiles) ? userProfiles.get(0) : new Profile();
            final Segment segment = new Segment();

            ProfileInfo profileInfo = null;
            if (null != userProfile.getProfileInfo()) {
                profileInfo = userProfile.getProfileInfo();
            } else {
                profileInfo = new ProfileInfo();
            }
            if (null != customerData) {
                if (!customerData.getUid().equalsIgnoreCase("anonymous")) {
                    user.setUserID(getWtCustomerId());
                    user.setErpContactID(DistCryptography.toSHA256(customerData.getContactId()));
                    profileInfo.setCustomerID(getWtCustomerId());
                    // 13484: Implemented as MD5 -> SHA256 (not hashed directly to SHA256) at request of Criteo
                    profileInfo.setEmail(DistCryptography.toSHA256(DistCryptography.toMD5(customerData.getEmail())));
                    profileInfo.setLanguage(null != customerData.getLanguage() ? customerData.getLanguage().getNameEN().toLowerCase() : "");
                    profileInfo.setActivationStatus(customerData.isActive() ? CUSTOMER_ACTIVE : CUSTOMER_INACTIVE);
                    profileInfo.setNLRegistered(customerData.isNewsletter() ? "YES" : "NO");
                    // profileInfo.setRegistrationDate(customerData.getCreatedDate());

                    final UserModel b2bUser = getUserService().getCurrentUser();
                    profileInfo.setLastLoginDate(b2bUser.getLastLogin());
                    // profileInfo.setRegistrationDate(customerData);
                    final Collection<DistPaymentModeData> paymentInfoDatas = userFacade.getSupportedPaymentModesForUser();
                    final Collection<DeliveryModeData> deliveryInfoDatas = userFacade.getSupportedDeliveryModesForUser();
                    final AddressData addressData = customerData.getContactAddress();
                    if (null != addressData) {
                        profileInfo.setCity(null != addressData.getTown() ? addressData.getTown().toLowerCase() : "");
                        profileInfo.setPostalCode(addressData.getPostalCode());

                        if (null != paymentInfoDatas && paymentInfoDatas.size() > 0) {
                            DistPaymentModeData defaultPaymentMode = paymentInfoDatas.stream()
                                                                                     .filter(paymentInfoData -> BooleanUtils.isTrue(
                                                                                                                                    paymentInfoData.getDefaultPaymentMode()))
                                                                                     .findFirst().orElse(null);
                            profileInfo.setPayementOption((null != defaultPaymentMode) ? defaultPaymentMode.getCode().toLowerCase() : "");
                        }
                        if (null != deliveryInfoDatas && deliveryInfoDatas.size() > 0) {
                            DeliveryModeData defaultDeliveryMode = deliveryInfoDatas.stream()
                                                                                    .filter(deliveryModeData -> BooleanUtils.isTrue(
                                                                                                                                    deliveryModeData.getDefaultDeliveryMode()))
                                                                                    .findFirst().orElse(null);
                            profileInfo.setDeliveryOption((null != defaultDeliveryMode) ? defaultDeliveryMode.getCode().toLowerCase() : "");
                        }
                        profileInfo.setCountry(null != addressData.getCountry() ? addressData.getCountry().getNameEN() : "");
                    }
                    segment.setJobRole(StringUtils.stripToEmpty(customerData.getFunctionNameEN()).toLowerCase());
                    segment.setAreaofuse(null != addressData ? addressData.getDepartmentEN() : "");
                    segment.setType(customerData.getCustomerType() != null ? customerData.getCustomerType().name().toLowerCase() : "");
                } else {
                    profileInfo.setCustomerID(null != customerData.getUid() ? customerData.getUid().toLowerCase() : "");
                    user.setUserID(null != customerData.getUid() ? customerData.getUid().toLowerCase() : "");
                }

                user.setSegment(segment);
            }
            userProfile.setProfileInfo(profileInfo);
            userProfiles.add(userProfile);
            user.setProfile(userProfiles);

            String registerFrom = request.getParameter("registerFrom");
            if (!StringUtils.isEmpty(registerFrom)) {
                user.setRegistrationMethod(registerFrom);
            }

            String subscribeFrom = request.getParameter("subscribeFrom");
            if (!StringUtils.isEmpty(subscribeFrom)) {
                user.setSubscriptionMethod(subscribeFrom);
            }

            users.add(0, user);

            // Populating DTM attributes from model which is available in AbstractPageController
            if (null == page.getPageInfo()) {
                page.setPageInfo(new PageInfo());
            }
            PageInfo pageInfo = page.getPageInfo();
            if (null == pageInfo) {
                pageInfo = new PageInfo();
            }
            pageInfo.setCountryCode(null != currentCountry.getIsocode() ? currentCountry.getIsocode() : "");
            pageInfo.setCountryName(null != currentCountry.getNameEN() ? currentCountry.getNameEN() : "");
            pageInfo.setShop(getSiteName(Locale.ENGLISH).toLowerCase());
            pageInfo.setLanguage((null != currentLanguage && null != currentLanguage.getNameEN()) ? currentLanguage.getNameEN().toLowerCase() : "");
            pageInfo.setCurrency(currentCurrency != null ? currentCurrency.getIsocode().toLowerCase() : "");
            pageInfo.setContentgroup(getWtContentGroup());
            pageInfo.setPageID(modelMap.get("wtPageId") != null ? modelMap.get("wtPageId").toString().toLowerCase() : "");
            if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.LOGIN_SUCCESS))) {
                pageInfo.setLogIn("success");
                digitalDatalayer.setEventName(DigitalDatalayer.EventName.LOGIN);
            } else if (Boolean.TRUE.equals(request.getSession().getAttribute(WebConstants.CHECKOUT_LOGIN_SUCCESS))) {
                digitalDatalayer.setEventName(DigitalDatalayer.EventName.LOGIN);
                if (!StringUtils.contains(request.getRequestURL().toString(), CHECHOUT_URL)) {
                    request.getSession().removeAttribute(WebConstants.CHECKOUT_LOGIN_SUCCESS);
                }
            }

            if (Boolean.TRUE.equals(request.getSession().getAttribute(WebConstants.LOGOUT_SUCCESS))) {
                pageInfo.setLogOut("success");
                request.getSession().removeAttribute(WebConstants.LOGOUT_SUCCESS);
            }
            if (Boolean.TRUE.equals(request.getSession().getAttribute(WebConstants.CHECKOUT_REGISTER_SUCCESS))) {
                digitalDatalayer.setEventName(DigitalDatalayer.EventName.REGISTER);
                if (!StringUtils.contains(request.getRequestURL().toString(), CHECHOUT_URL)) {
                    request.getSession().removeAttribute(WebConstants.CHECKOUT_REGISTER_SUCCESS);
                }
            }
            if (null == page.getPageCategory()) {
                page.setPageCategory(new PageCategory());
            }
            final PageCategory pageCategory = page.getPageCategory();
            final List<Breadcrumb> breadCrumbs = (List<Breadcrumb>) modelMap.get(WebConstants.BREADCRUMBS_KEY);
            final List<String> breadcrumbData = (breadCrumbs == null) ? Collections.EMPTY_LIST
                                                                      : breadCrumbs.stream().map(breadCrumb -> filterBreadCrumbData(breadCrumb.getNameEN()))
                                                                                   .filter(breadCrumb -> null != filterBreadCrumbData(breadCrumb))
                                                                                   .collect(Collectors.toList());
            if (null != breadcrumbData) {
                pageInfo.setBreadcrumbs(breadcrumbData);
                List<String> filteredBreadcrumbData = breadcrumbData.stream().collect(Collectors.toList());
                filteredBreadcrumbData.removeIf(String::isEmpty);
                if (null != digitalDatalayer.getProduct() && digitalDatalayer.getProduct().size() == 1) {
                    Product product = digitalDatalayer.getProduct().get(0);

                    pageInfo.setPageName(filteredBreadcrumbData.stream().collect(Collectors.joining("|")).toLowerCase() + "|"
                                         + ((null != product && null != product.getProductInfo()) ? product.getProductInfo().getProductID() : ""));
                } else {
                    pageInfo.setPageName(filteredBreadcrumbData.stream().collect(Collectors.joining("|")).toLowerCase());
                }

                pageInfo.setArea(modelMap.get(ThirdPartyConstants.Webtrekk.WT_PAGE_AREA_CODE) == null ? ""
                                                                                                      : (String) modelMap.get(ThirdPartyConstants.Webtrekk.WT_PAGE_AREA_CODE));
                StringBuilder hierarchy = new StringBuilder();
                if (null != getCurrentCurrency()) {
                    hierarchy.append(getCurrentCurrency().getIsocode()
                                                         .toLowerCase())
                             .append("|");
                }
                if (null != currentLanguage) {
                    hierarchy.append(currentLanguage.getIsocode()
                                                    .toLowerCase())
                             .append("|");
                }
                for (int breadcrumbSize = 0; breadcrumbSize < breadcrumbData.size(); breadcrumbSize++) {
                    hierarchy.append(StringUtils.isNotEmpty(breadcrumbData.get(breadcrumbSize)) ? filterBreadCrumbData(breadcrumbData.get(breadcrumbSize))
                                                                                                : DEFAULT_EMPTY);
                    if ((breadcrumbSize + 1) < breadcrumbData.size()) {
                        hierarchy.append("|");
                    }
                }

                populatePageInfoSourceAttributes(pageInfo, request);

                pageCategory.setPrimaryCategory(breadcrumbData.size() >= 1 ? filterBreadCrumbData(breadcrumbData.get(0)) : DEFAULT_EMPTY);
                pageCategory.setSubCategoryL1(breadcrumbData.size() >= 2 ? filterBreadCrumbData(breadcrumbData.get(1)) : DEFAULT_EMPTY);
                pageCategory.setSubCategoryL2(breadcrumbData.size() >= 3 ? filterBreadCrumbData(breadcrumbData.get(2)) : DEFAULT_EMPTY);
                pageCategory.setSubCategoryL3(breadcrumbData.size() >= 4 ? filterBreadCrumbData(breadcrumbData.get(3)) : DEFAULT_EMPTY);
                pageCategory.setSubCategoryL4(breadcrumbData.size() >= 5 ? filterBreadCrumbData(breadcrumbData.get(4)) : DEFAULT_EMPTY);
                pageCategory.setSubCategoryL5(breadcrumbData.size() >= 6 ? filterBreadCrumbData(breadcrumbData.get(5)) : DEFAULT_EMPTY);
                page.setHier1(hierarchy.toString().toLowerCase());

                populatePageInfoSiteMessages(pageInfo, model);
            }

            CategoryData productFamilyData = (CategoryData) model.getAttribute("productFamily");
            if (productFamilyData != null) {
                distDigitalDatalayerFacade.addProductFamilyNameToSubcategory(pageCategory, breadcrumbData, productFamilyData.getName());
            }

            pageInfo.setVersion(getCurrentVersion());
            pageInfo.setView(getUseTechnicalView() ? VIEW_TECHNICAL : (getUseListView() ? VIEW_STANDARD : VIEW_ICON));
            try {
                pageInfo.setPageUrl(request.getRequestURL() != null
                                                                    ? request.getRequestURL().toString() + (null != request.getQueryString() ? ("?"
                                                                                                                                                + XSSFilterUtil.sanitiseQueryString(
                                                                                                                                                                                    request))
                                                                                                                                             : "")
                                                                    : "");
                pageInfo.setDestinationURL(request.getRequestURL() != null ? request.getRequestURL().toString() : "");
                pageInfo.setReferringURL(StringUtils.stripToEmpty(request.getHeader(REFERER)));
            } catch (Exception uriex) {
                LOG.error("Error while populating URI data", uriex);
            }
            pageInfo.setAttributes(new com.namics.distrelec.b2b.facades.adobe.datalayer.data.Attributes());
            page.setPageInfo(pageInfo);
            page.setPageCategory(pageCategory);

            digitalDatalayer.setUser(users);
            digitalDatalayer.setPage(page);

            // Check the request type, whether the request came from email or web-shop.
            final boolean emailRequest = Boolean.getBoolean(request.getParameter("email"));
            if (emailRequest) {
                digitalDatalayer.setRequest(DigitalDatalayerConstants.Request.EMAIL);
            } else {
                digitalDatalayer.setRequest(DigitalDatalayerConstants.Request.WEBSHOP);
            }

            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        } catch (final Exception ex) {
            LOG.error("Error while populating datalayer", ex);
        }
    }

    private String filterBreadCrumbData(String breadCrumbData) {
        final String customFilterName = getMessageSource().getMessage(CUSTOM_FILTER_KEY, null, FALL_BACK_STRING, getI18nService().getCurrentLocale());
        final String customFilterNameEN = getMessageSource().getMessage(CUSTOM_FILTER_KEY, null, FALL_BACK_STRING, Locale.ENGLISH);
        if (StringUtils.isNotBlank(breadCrumbData) && !breadCrumbData.equalsIgnoreCase(customFilterName)
                && !breadCrumbData.equalsIgnoreCase(customFilterNameEN)) {
            return breadCrumbData.toLowerCase();
        }
        return DEFAULT_EMPTY;
    }

    protected void populatePageInfoSourceAttributes(final PageInfo pageInfo, final HttpServletRequest request) {
        final String uriString = request.getRequestURI();
        final String dataLinkValue = request.getParameter(URL_PARAMETER_KEY_FOR_NAVIGATION_SOURCE);

        if (StringUtils.isNotBlank(dataLinkValue)) {
            String sourceComponent;
            String sourceLink;
            String sourceComponentLocation;
            try {
                final CMSNavigationNodeModel clickedNavigationNode = getDistCmsNavigationService().getNavigationNodeForId(dataLinkValue);
                final ArrayList<CMSNavigationNodeModel> nodeHierarchy = getDistCmsNavigationService().getNavigationNodeHierarchy(clickedNavigationNode);
                sourceComponent = "navigation";
                sourceLink = clickedNavigationNode.getUid();
                try {
                    sourceComponentLocation = nodeHierarchy.get(1).getUid();
                } catch (final IndexOutOfBoundsException e) {
                    LOG.warn("Clicked NavigationNode: {} is directly in rootNavigationNode: {}", dataLinkValue, nodeHierarchy.get(0).getUid());
                    sourceComponentLocation = nodeHierarchy.get(0).getUid();
                }
            } catch (final CMSItemNotFoundException e) {
                LOG.debug("No NavigationNode found for URI: {}", uriString);
                sourceComponent = "search box";
                sourceLink = "";
                sourceComponentLocation = "header";
            }
            pageInfo.setSourceComponent(sourceComponent);
            pageInfo.setSourceComponentLocation(sourceComponentLocation);
            pageInfo.setSourceLink(sourceLink);
            LOG.debug("pageInfo: {}", pageInfo);
        } else {
            LOG.debug("Nothing to add for uri: {}", uriString);
        }
    }

    protected List<Product> getProductsDTMDataLayer(final List<de.hybris.platform.commercefacades.product.data.ProductData> results) {
        return getDistDigitalDatalayerFacade().getProductsDTMDataLayer(results);
    }

    protected Product populateProductDTMObjects(final de.hybris.platform.commercefacades.product.data.ProductData productData) {
        return getDistDigitalDatalayerFacade().populateProductDTMObjects(productData);
    }

    protected DigitalDatalayer getDigitalDatalayerFromModel(final Model model) {
        if (!model.containsAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER)) {
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, new DigitalDatalayer());
        }

        return (DigitalDatalayer) model.asMap().get(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER);
    }

    protected void populateDTMSearchData(final DigitalDatalayer digitalDatalayer, final String searchQuery,
                                         final FactFinderProductSearchPageData<SearchStateData, de.hybris.platform.commercefacades.product.data.ProductData> searchPageData) {
        getDistDigitalDatalayerFacade().populateDTMSearchData(searchQuery, searchPageData, digitalDatalayer);
    }

    /**
     * Add the caching attributes to the model. The caching is not used during the checkout process.
     *
     * @param model
     * @param request
     */
    protected void addCachingModelAttributes(final Model model, final HttpServletRequest request) {
        model.addAttribute("cachingKeyFooter", getCachingKeyFooter());
        model.addAttribute("cachingTimeFooter", String.valueOf(distCachingFacade.getCachingTimeFooter()));
        model.addAttribute("cachingKeyHomepage", getCachingKeyHomepage());
        model.addAttribute("cachingTimeHomepage", String.valueOf(distCachingFacade.getCachingTimeHomepage()));
        model.addAttribute("cachingKeyMainnav", getCachingKeyMainnav());
        model.addAttribute("cachingTimeMainnav", String.valueOf(distCachingFacade.getCachingTimeMainnav()));

        // MainNavFlush Param
        final Boolean mainnavflush = getSessionService().getAttribute(DistConstants.Session.MAIN_NAV_FLUSH);
        if (Boolean.TRUE.equals(mainnavflush)) {
            model.addAttribute("mainNavFlush", Boolean.TRUE);
            getSessionService().setAttribute(DistConstants.Session.MAIN_NAV_FLUSH, Boolean.FALSE);
        } else {
            model.addAttribute("mainNavFlush", Boolean.FALSE);
        }
    }

    /**
     * Returns the Caching key for the home page. If the session is newly created, then we calculate the value and store it in the session
     * to avoid evaluating it for each request.
     *
     * @return Caching key for the home page.
     */
    protected String getCachingKeyHomepage() {
        // Checking if the channel has changed
        final String cachingKeyHomepage = getSessionService().getAttribute("cachingKeyHomepage");
        final String channel = ((DistrelecBaseStoreService) getBaseStoreService()).getCurrentChannel(getBaseSiteService().getCurrentBaseSite()).getCode();

        if (cachingKeyHomepage != null && !cachingKeyHomepage.contains(channel)) {
            getSessionService().removeAttribute("cachingKeyHomepage");
        }

        return getSessionService().getOrLoadAttribute("cachingKeyHomepage", () -> distCachingFacade.getCachingKeyHomepage());
    }

    /**
     * Returns the Caching key for the main navigation menu. If the session is newly created, then we calculate the value and store it in
     * the session to avoid evaluating it for each request.
     *
     * @return Caching key for the main navigation menu.
     */
    protected String getCachingKeyMainnav() {
        return getSessionService().getOrLoadAttribute("cachingKeyMainnav", () -> distCachingFacade.getCachingKeyMainnav());
    }

    /**
     * Returns the Caching key for the page footer. If the session is newly created, then we calculate the value and store it in the session
     * to avoid evaluating it for each request.
     *
     * @return Caching key for the page footer.
     */
    protected String getCachingKeyFooter() {
        return getSessionService().getOrLoadAttribute("cachingKeyFooter", () -> distCachingFacade.getCachingKeyFooter());
    }

    private boolean isCustomFooterEnabled() {
        // User session dependent value, store it in the session.
        final Boolean bool = getSessionService().<Boolean> getOrLoadAttribute("customFooterEnabled",
                                                                              () -> Boolean.valueOf(distOciService.isCustomFooterEnabled()
                                                                                      || distAribaService.isCustomFooterEnabled()));
        return bool != null && bool.booleanValue();
    }

    private boolean isMegaFlyOutDisabled() {
        // User session dependent value, store it in the session.
        final Boolean bool = getSessionService().<Boolean> getOrLoadAttribute("megaFlyOutDisabled",
                                                                              () -> getDistOciService().hasMegaFlyOutDisabled());
        return bool != null && bool;
    }

    public boolean isSecureConnection(final HttpServletRequest request) {
        return request.isSecure();
    }

    public boolean isSearchRobot(final HttpServletRequest request) {
        return searchRobotDetector.isSearchRobot(request);
    }

    private Integer getApprovalRequestsCount() {
        return getOrderFacade().getApprovalRequestsCount();
    }

    /**
     * @return the number of the quotes having status "Offered" {@code "02"} if the customer is logged in, zero otherwise.
     */
    private Integer getOfferedQuoteCount() {
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            return 0;
        }

        return getSessionService().getOrLoadAttribute(OFFERED_QUOTES_SESSION_ATTR,
                                                      () -> getDistProductPriceQuotationFacade().getQuotationHistory(OFFERED_QUOTES_STATUS_CODE).size());
    }

    /**
     * If the login success boolean session attribute exists and its value is {@code true} then we remove it from the session and add the
     * {@link GlobalMessages#CONF_MESSAGES_HOLDER} global message to show the login success message.
     */
    private void loginSuccessMessage(final Model model, final HttpServletRequest request) {
        if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.LOGIN_SUCCESS))) {
            getSessionService().removeAttribute(WebConstants.LOGIN_SUCCESS);
            if (request.getServletPath() != null && !ROOT.equalsIgnoreCase(request.getServletPath())) {
                GlobalMessages.addConfMessage(model, "account.confirmation.signin.title");
            }
        }
    }

    private Collection<LanguageData> getLanguages() {
        // Since the available languages does not change, they can be calculated
        // only once and stored in the session.
        return getSessionService().getOrLoadAttribute("languages", () -> getStoreSessionFacade().getAllLanguages());
    }

    protected SalesOrgData getCurrentSalesOrg() {
        // Since the current salesOrg does not change, then it may be stored in
        // the session
        return getSessionService().getOrLoadAttribute("currentSalesOrg", () -> getStoreSessionFacade().getCurrentSalesOrg());
    }

    protected boolean isCurrentShopExport() {
        return getStoreSessionFacade().isCurrentShopExport();
    }

    protected boolean isCurrentShopElfa() { // Excluded DE for the time being
        return getStoreSessionFacade().isCurrentShopElfa();
    }

    protected LanguageData getCurrentLanguage() {
        return getStoreSessionFacade().getCurrentLanguage();
    }

    protected Locale getCurrentLocale() {
        return getStoreSessionFacade().getCurrentLocale();
    }

    protected CurrencyData getCurrentCurrency() {
        return getStoreSessionFacade().getCurrentCurrency();
    }

    protected CountryData getCurrentCountry() {
        return getStoreSessionFacade().getCurrentCountry();
    }

    private ChannelData getCurrentChannel() {
        return getStoreSessionFacade().getCurrentChannel();
    }

    protected BaseStoreData getCurrentBaseStore() {
        return getStoreSessionFacade().getCurrentBaseStore();
    }

    protected ShoppingSettingsCookieData getShopSettingsCookie(final HttpServletRequest request) {
        return ShopSettingsUtil.readShopSettingsCookie(Attributes.SHOP_SETTINGS.getValueFromCookies(request));
    }

    private String getFFidCookieValue(final HttpServletRequest request) {
        String ffid = StringUtils.EMPTY;
        try {
            if (request.getCookies() != null) {
                for (final Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equalsIgnoreCase(Attributes.FF_TRACKING_COOKIE.getName())) {
                        ffid = cookie.getValue();
                    }
                }
            }
            getSessionService().setAttribute(Attributes.FF_TRACKING_COOKIE.getName(), ffid);
        } catch (Exception ex) {
            LOG.error("FFID cookie is missing");
        }
        return ffid;
    }

    private Boolean getUseTechnicalView() {
        return getStoreSessionFacade().isUseTechnicalView();
    }

    private Boolean getUseIconView() {
        return getStoreSessionFacade().isUseIconView();
    }

    private Boolean getUseListView() {
        return getStoreSessionFacade().isUseListView();
    }

    private Boolean getAutoApplyFilter() {
        return getStoreSessionFacade().isAutoApplyFilter();
    }

    private String getSiteUid() {
        // User session dependent value, store it in the session.
        return getSessionService().getOrLoadAttribute("currentSiteUID", () -> {
            final CMSSiteModel site = getCmsSiteService().getCurrentSite();
            return site == null ? StringUtils.EMPTY : site.getUid();
        });
    }

    protected String getSiteName() {
        // User session dependent value, store it in the session.
        return getSessionService().getOrLoadAttribute("currentSiteName", () -> {
            final CMSSiteModel site = getCmsSiteService().getCurrentSite();
            return site == null ? StringUtils.EMPTY : site.getName();
        });
    }

    protected String getSiteName(Locale locale) {
        final CMSSiteModel site = getCmsSiteService().getCurrentSite();
        return null == site ? StringUtils.EMPTY : site.getName(locale);
    }

    private String getPrintFooterContent() {
        // User session dependent value, store it in the session.
        return getSessionService().getOrLoadAttribute("currentSitePrintFooterContent", () -> {
            final CMSSiteModel site = getCmsSiteService().getCurrentSite();
            return site == null ? StringUtils.EMPTY : site.getPrintFooterContent();
        });
    }

    private String getBreadcrumbMaxLength() {
        return getConfigurationService().getConfiguration().getString("breadcrumb.maxlength");
    }

    protected CustomerData getUser() {
        // The user does not change during the session, then we store it in the
        // session. When a user logs in, then a new session is created,
        // which means that the 'currentUser' attribute will be recalculated.
        final Object obj = getSessionService().getAttribute("currentUserData");
        if (obj != null && !((CustomerData) obj).getUid().equals(getUserService().getCurrentUser().getUid())) {
            getSessionService().removeAttribute("currentUserData");
        }

        return getSessionService().getOrLoadAttribute("currentUserData", () -> getCustomerFacade().getCurrentCustomer());
    }

    private Boolean isCssCompressionEnabled() {
        return Boolean.valueOf(getSiteConfigService().getProperty(COMPRESSION_CONFIG_KEY_PREFIX));
    }

    protected void populatePageInfoSiteMessages(final PageInfo pageInfo, final Model model) {
        final ContentSlotModel slotWithMessages = getCmsContentSlotService().getContentSlotForId("DisruptionMessagesSlot");
        final List<DistWarningComponentModel> messageComponents = getWarningsFromList(slotWithMessages);
        pageInfo.setSiteMessages(messageComponents.stream().map(CMSItemModel::getName).collect(Collectors.toList()));
    }

    protected List<DistWarningComponentModel> getWarningsFromList(final ContentSlotModel slot) {
        return slot.getCmsComponents().stream().filter(DistWarningComponentModel.class::isInstance).map(DistWarningComponentModel.class::cast)
                   .filter(component -> BooleanUtils.isTrue(component.getVisible())).collect(Collectors.toList());
    }

    private List<DistLink> setHrefLang(final HttpServletRequest request) {

        final List<DistLink> result = new ArrayList<>();

        // Use this method to add additional <link>
        // DISTRELEC-4590
        setupAlternateHreflangLinks(request, result);
        return result;
    }

    // START: Tracking values

    protected List<DistLink> setPaginationHrefLang(final HttpServletRequest request, final int currentPage, final int totalNumberOfPages) {
        final int addOrSubtract = 1;
        final String urlPath = request.getRequestURL().toString();
        final List<DistLink> result = new ArrayList<>();
        if (currentPage == addOrSubtract && currentPage < totalNumberOfPages) {
            result.add(setupHeaderLink(LINK_NEXT, urlPath + URL_PAGINATION_STRING + (currentPage + addOrSubtract)));
        } else if (currentPage != addOrSubtract && currentPage == totalNumberOfPages) {
            result.add(setupPreviousHeaderLink(urlPath, currentPage - addOrSubtract));
        } else if (currentPage < totalNumberOfPages && currentPage != 0) {
            result.add(setupHeaderLink(LINK_NEXT, urlPath + URL_PAGINATION_STRING + (currentPage + addOrSubtract)));
            result.add(setupPreviousHeaderLink(urlPath, currentPage - addOrSubtract));
        }
        return result;
    }

    // prev to the first page should point to canonical url (without ?page=1)
    protected DistLink setupPreviousHeaderLink(final String url, final int pageNo) {
        if (pageNo == 1) {
            return setupHeaderLink(LINK_PREVIOUS, url);
        }
        return setupHeaderLink(LINK_PREVIOUS, url + URL_PAGINATION_STRING + pageNo);
    }

    protected String getPageType() {
        return ThirdPartyConstants.PageType.OTHER;
    }

    private String getGoogleAnalyticsTrackingId() {
        return getPropertiesValueWithFallback(ThirdPartyConstants.Google.ANALYTICS_TRACKING_ID);
    }

    private String getGoogleAdWordsConversionId() {
        return getPropertiesValueWithFallback(ThirdPartyConstants.Google.ADWORDS_CONVERSION_ID);
    }

    private String getGoogleAdWordsConversionLabel() {
        return getPropertiesValueWithFallback(ThirdPartyConstants.Google.ADWORDS_CONVERSION_LABEL);
    }

    private String getGoogleAdWordsConversionTrackingId() {
        return getPropertiesValueWithFallback(ThirdPartyConstants.Google.ADWORDS_CONVERSION_TRACKING_ID);
    }

    // END: Tracking values

    private String getIntelliAdId() {
        return getPropertiesValueWithFallback(ThirdPartyConstants.IntelliAd.ID);
    }

    private String getAffilinetConversionTrackingSiteId() {
        return getPropertiesValueWithFallback(ThirdPartyConstants.Affilinet.CONVERSION_TRACKING_SITE_ID);
    }

    private LoginForm getMetaLoginForm() {
        return new LoginForm();
    }

    private String getHttpsSite(final String requestURI) {
        return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(getBaseSiteService().getCurrentBaseSite(), true, requestURI);
    }

    /**
     * Retrieve the list of wish lists of the current customer. If the customer is a guest, return empty list
     *
     * @return a list of {@code NamicsMiniWishlistData}
     */
    private List<MiniWishlistData> getMetaLists() {
        return isGuestUser() ? Collections.emptyList() : getDistShoppingListFacade().getMiniWishlists();
    }

    private String getWtCustomerId() {
        final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
        if (customer == null) {
            return StringUtils.EMPTY;
        } else if (getUserService().isAnonymousUser(customer)) {
            return "0";
        } else {
            return getSessionService().getOrLoadAttribute("wtCustomerId", () -> {
                final String customerId = StringUtils.isNotBlank(customer.getCustomerID()) ? customer.getCustomerID() : "unknown";
                return getDistWebtrekkFacade().encodeToUTF8(DistCryptography.encryptString(customerId, DistCryptography.WEBTREKK_KEY));
            });
        }
    }

    private String getWtContentGroup() {
        String builder = getCmsSiteService().getCurrentSite()
                                            .getCountry()
                                            .getIsocode()
                         +
                         // builder.append(
                         // getStoreSessionFacade().getCurrentSalesOrg().getCountryIsocode());
                         // Fix: DISTRELEC-5893
                         "-" +
                         getStoreSessionFacade().getCurrentLanguage()
                                                .getIsocode();
        return getDistWebtrekkFacade().encodeToUTF8(builder.toLowerCase());
    }

    protected String getWtContentId(final HttpServletRequest request) {
        String builder = getCmsSiteService().getCurrentSite()
                                            .getWtDomain()
                         +
                         request.getRequestURI();
        return builder;
    }

    private String getEProcurementCSSClass() {
        // User session dependent value, store it in the session to avoid
        // calculation it for each request.
        return getSessionService().getOrLoadAttribute("eProcurementCSSClass", () -> {
            if (getDistAribaService().isAribaCustomer()) {
                return "eproc eproc-ariba";
            }
            if (getDistOciService().isOciCustomer()) {
                return "eproc eproc-oci";
            }
            if (getDistCxmlService().isCxmlCustomer()) {
                return "eproc eproc-cxml";
            }
            return StringUtils.EMPTY;
        });
    }

    protected String getCurrentEnvironment() {
        return getConfigurationService().getConfiguration().getString(DistConstants.PropKey.Environment.YMS_HOSTNAME, UNKNOWN_YMS_HOSTNAME);
    }

    protected Boolean isCurrentEnvironmentDev() {
        boolean dev = false;
        final String env = getCurrentEnvironment();
        if (env.startsWith(DistConstants.PropKey.Environment.LOCAL_ENV_DEVELOPMENT) || env.startsWith(DistConstants.PropKey.Environment.DEV_ENV_PREFIX)) {
            dev = true;
        }
        return dev ? Boolean.FALSE : Boolean.TRUE;
    }

    protected String setUpMetaRobotContent() {
        return getMessageSource().getMessage("meta.robots.default", new Object[] {}, getI18nService().getCurrentLocale());
    }

    protected String getCurrentVersion() {
        return gitVersionService.getGitVersion();
    }

    private String getCurrentDateTimeString() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss", getI18nService().getCurrentLocale());
        return dateFormat.format(new Date());
    }

    private String getCurrentKey() {
        return gitVersionService.getRevision();
    }

    protected String getForceDesktopCookie(final HttpServletRequest request) {
        return Attributes.FORCE_DESKTOP.getValueFromCookies(request);
    }

    // DISTRELEC-4591
    private List<DistLink> setCanonicalLink(final HttpServletRequest request) {
        return setupCanonicalHreflang(request, new ArrayList<>());
    }

    protected String decodeToUTF8(final String value) {
        String result = "";
        result = UriUtils.decode(value == null ? "" : value, "UTF-8");
        return result;
    }

    protected URL getURLRequest(final HttpServletRequest request) {
        URL url = null;
        try {
            url = new URL(request.getRequestURL().toString());
        } catch (final MalformedURLException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Problems while setting the <link> content. This url is incorrect:{}", url, e);
            }
            return null;
        }
        return url;
    }

    private String getHomeURL(final URL url) {
        return url.getProtocol() + "://" + url.getHost();
    }

    private boolean isHomePage(final URL urlRequest) {
        return 1 == urlRequest.getPath().length();
    }

    protected boolean isShippingOptionsEditable() {
        final Boolean bool = getSessionService().<Boolean> getOrLoadAttribute("isShippingOptionsEditable", () -> {
            final CMSSiteModel site = getCmsSiteService().getCurrentSite();
            return Boolean.valueOf(site != null && site.isShippingOptionsEditable());
        });
        return bool != null && bool.booleanValue();
    }

    protected boolean isPaymentOptionsEditable() {
        final Boolean bool = getSessionService().<Boolean> getOrLoadAttribute("isPaymentOptionsEditable", () -> {
            final CMSSiteModel site = getCmsSiteService().getCurrentSite();
            return Boolean.valueOf(site != null && site.isPaymentOptionsEditable());
        });
        return bool != null && bool.booleanValue();
    }

    protected List<DistLink> setupCanonicalHreflang(final HttpServletRequest request, final List<DistLink> result) {
        final URL url = getURLRequest(request);
        final String urlPath = url.toExternalForm();
        if (urlPath.contains(URL_QUERY_STRING)) {
            result.add(setupHeaderLink(LINK_CANONICAL_RELATIONSHIP, urlPath.substring(0, urlPath.lastIndexOf(URL_QUERY_STRING))));
        } else {
            result.add(setupHeaderLink(LINK_CANONICAL_RELATIONSHIP, urlPath));
        }
        return result;
    }

    private void setupAlternateHreflangLinks(final HttpServletRequest request, final List<DistLink> result) {
        final List<String> paths = getBaseUrlAndPath(request);
        final String nonLocalizedPathValue = getNonLocalizedPath(paths.get(1));
        // add default link
        result.add(setupHeaderLink(LINK_ALTERNATE_RELATIONSHIP, paths.get(0) + nonLocalizedPathValue, "x-default"));
        // add link per each country language from current store
        for (final LanguageModel languageModel : getBaseStoreService().getCurrentBaseStore().getLanguages()) {
            final String nonLocalizedPath = "/".equals(paths.get(1)) ? "/" + languageModel.getIsocode() : nonLocalizedPathValue;
            result.add(setupHeaderLink(LINK_ALTERNATE_RELATIONSHIP, paths.get(0) + "/" + languageModel.getIsocode() + nonLocalizedPath,
                                       languageModel.getIsocode().toLowerCase()));
        }
    }

    // from trunk -merge
    protected <T> void setupAlternateHreflangLinks(final HttpServletRequest request, final List<DistLink> headerResult, final T model,
                                                   final DistUrlResolver<T> urlresolver) {
        // add non country-specific links (language only)
        setupAlternateHreflangLinks(request, headerResult);
        // add link per each country language from current store
        List<CMSSiteModel> cmsSites = null;
        if (model instanceof ProductModel) {
            cmsSites = productFacade.getAvailableCMSSitesForProduct((ProductModel) model);
        } else if (model instanceof DistManufacturerModel) {
            cmsSites = distManufacturerFacade.getAvailableCMSSitesForManufacturer((DistManufacturerModel) model);
        } else if (model instanceof CategoryModel) {
            cmsSites = distCategoryFacade.getAvailableCMSSitesForCategory((CategoryModel) model);
        } else {
            cmsSites = new ArrayList<>(getCmsSiteService().getSites());
        }

        final List<String> paths = getBaseUrlAndPath(request);

        for (final CMSSiteModel cmsSite : cmsSites) {
            // DISTRELEC-8787 TR for the moment.
            if ("distrelec_TR".equals(cmsSite.getUid())) {
                continue;
            }

            // DISTRELEC-20157 Skip creating alternate links for international site which is used only by Smartedit
            if ("distrelec".equals(cmsSite.getUid())) {
                continue;
            }

            // DISTRELEC-11427 : Please make all instances of cross-site linking
            // to be fully encrypted,
            final String domain = getConfigurationService().getConfiguration()
                                                           .getString("website." + cmsSite.getUid()
                                                                      + ((request.isSecure() || cmsSite.isHttpsOnly()) ? ".https" : ".http"));
            if (CollectionUtils.isNotEmpty(cmsSite.getStores())) {
                List<LanguageModel> availableLanguages = cmsSite.getStores().get(0).getLanguages().stream()
                                                                .filter(languageModel -> !languageModel.getIsocode().contains("_"))
                                                                .collect(Collectors.toList());
                for (final LanguageModel languageModel : availableLanguages) {
                    String productOrCategoryUrl = domain
                                                  + (model != null && urlresolver != null ? urlresolver.resolve(model, cmsSite, languageModel.getIsocode()) : //
                                                                                          (LANG_PATTERN.matcher(paths.get(1))
                                                                                                       .matches() ? "/" + languageModel.getIsocode()
                                                                                                                  : getNonLocalizedPath(paths.get(1))));

                    final DistLink altLink = makeLink(LINK_ALTERNATE_RELATIONSHIP, //
                                                      productOrCategoryUrl, //
                                                      cmsSite.getUid().equalsIgnoreCase("distrelec_EX") ? languageModel.getIsocode()
                                                                                                        : languageModel.getIsocode() + "-"
                                                                                                          + cmsSite.getCountry().getIsocode(), //
                                                      cmsSite.getCountry().getName(getCommonI18NService().getLocaleForLanguage(languageModel)));

                    // from trunk - merge
                    if (!languageModel.equals(cmsSite.getDefaultLanguage())) {
                        altLink.setType(LinkType.HEADER);
                    }
                    // if (languageModel.equals(cmsSite.getDefaultLanguage()) &&
                    // footerResult != null) {
                    // footerResult.add(setupFooterLink(domain +
                    // productOrCategoryUrl, cmsSite.getCountry().getName(new
                    // Locale(languageModel.getIsocode()))));
                    // }

                    headerResult.add(altLink);
                }
            }
        }

        // Sorting the links by the country name.
        sortDistLinks(headerResult, getI18nService().getCurrentLocale());
    }

    private String getNonLocalizedPath(final String path) {
        return path.matches("^/[a-z]{2}/.*$") ? path.substring(3) : path;
    }

    private List<String> getBaseUrlAndPath(final HttpServletRequest request) {
        final URL url = getURLRequest(request);
        final String baseUrl = getBaseUrl(url).toString(); // .replace("https",
        // "http"); //
        // removed replace
        // to https
        // DISTRELEC-11262
        final String path = StringUtils.isEmpty(url.getPath()) ? "/" : url.getPath();
        return Arrays.asList(baseUrl, path);
    }

    private StringBuilder getBaseUrl(final URL url) {
        final StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(getHomeURL(url));
        if (url.getPort() != 0 && url.getPort() != -1) {
            baseUrl.append(":").append(url.getPort());
        }
        return baseUrl;
    }

    private DistLink setupHeaderLink(final String rel, final String href, final String hreflang) {
        final DistLink link = makeLink(rel, href, hreflang);
        link.setType(LinkType.HEADER);
        return link;
    }

    private DistLink makeLink(final String rel, final String href) {
        return makeLink(rel, href, null);
    }

    private DistLink makeLink(final String rel, final String href, final String hreflang) {
        return makeLink(rel, href, hreflang, null);
    }

    private DistLink makeLink(final String rel, final String href, final String hreflang, final String countryName) {
        return new DistLink( //
                            StringUtils.isEmpty(rel) ? null : rel, //
                            StringUtils.isEmpty(href) ? null : href, //
                            StringUtils.isEmpty(hreflang) ? null : hreflang, //
                            countryName);
    }

    protected DistLink setupHeaderLink(final String rel, final String href) {
        return makeLink(rel, href);
    }

    public String getWtErrors() {
        return wtErrors;
    }

    public void setWtErrors(final String wtErrors) {
        this.wtErrors = wtErrors;
    }

    @ExceptionHandler(DistrelecBadRequestException.class)
    public String handleBadRequestException(Exception exception, HttpServletRequest request) {
        setAttributesForExceptionHandler(exception, request);
        return FORWARD_PREFIX + "/" + "badRequest";
    }

    @SuppressWarnings("unused")
    @ExceptionHandler(Exception.class)
    public String handleException(final Exception exception, final HttpServletRequest request) {
        setAttributesForExceptionHandler(exception, request);
        return FORWARD_PREFIX + "/" + "unknownError";
    }

    private void setAttributesForExceptionHandler(Exception exception, HttpServletRequest request) {
        String uuidString = java.util.UUID.randomUUID().toString();
        ERROR_PAGE_LOG.error("a technical error occurred [uuid: " + uuidString + " ]", exception);
        request.setAttribute("uuid", uuidString);
        request.setAttribute("exception", exception);
    }

    protected boolean isB2BAdminUser() {
        return getUserService().isMemberOfGroup(getUserService().getCurrentUser(), getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP));
    }

    protected void prepareWebtrekkParams(final Model model, final CartData cartData) {
        getDistWebtrekkFacade().prepareWebtrekkProductParams(model, cartData);
    }

    protected void storeCmsPageInModel(final Model model, final AbstractPageModel cmsPage) {
        getCmsPageInModel(model, cmsPage, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
    }

    protected void storeCmsPageInModel(final Model model, final AbstractPageModel cmsPage, final String searchTrackingId) {
        getCmsPageInModel(model, cmsPage, searchTrackingId);
    }

    private void getCmsPageInModel(final Model model, final AbstractPageModel cmsPage, final String trackingId) {
        if (model != null && cmsPage != null) {
            model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PAGE_ID, cmsPage.getUid());
            model.addAttribute(ThirdPartyConstants.Webtrekk.WT_BASIC_PARAMS, getDistWebtrekkFacade().getWtBasicParameters(cmsPage));
            model.addAttribute(ThirdPartyConstants.Webtrekk.WT_ADVANCED_PARAMS, getDistWebtrekkFacade().getWtAdvancedParameters(cmsPage));
            getDistWebtrekkFacade().addTeaserTrackingId(model, trackingId);
            model.addAttribute(CMS_PAGE_MODEL, cmsPage);

            if (cmsPage instanceof ContentPageModel) {
                storeContentPageTitleInModel(model, getPageTitleResolver().resolveContentPageTitle(cmsPage.getTitle()));
            }
            storeWtPageAreaCodeInModel(model, cmsPage);
            storeWtActivateEventTrackingInModel(model, cmsPage);
            DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            storeDigitalDatalayerElements(digitalDatalayer, cmsPage, trackingId);
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        }
    }

    private void storeDigitalDatalayerElements(final DigitalDatalayer digitalDatalayer, final AbstractPageModel cmsPage, String teaserTrackingId) {
        getDistDigitalDatalayerFacade().storeDigitalDatalayerElements(digitalDatalayer, cmsPage, teaserTrackingId);
    }

    protected String getDataFormatForCurrentCmsSite() {
        final Locale locale = new Locale(getI18nService().getCurrentLocale().getLanguage(), getCmsSiteService().getCurrentSite().getCountry().getIsocode());
        return getMessageSource().getMessage("text.store.dateformat.datepicker.selection", null, "MM/dd/yyyy", locale);
    }

    protected void storeWtPageAreaCodeInModel(final Model model, final AbstractPageModel cmsPage) {
        if (!StringUtils.isBlank(cmsPage.getWtAreaCode())) {
            model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PAGE_AREA_CODE, cmsPage.getWtAreaCode());
        } else {
            model.addAttribute(ThirdPartyConstants.Webtrekk.WT_PAGE_AREA_CODE, getCmsSiteService().getCurrentSite().getWtDefaultAreaCode());
        }
    }

    protected void storeWtActivateEventTrackingInModel(final Model model, final AbstractPageModel cmsPage) {
        if (!StringUtils.isBlank(cmsPage.getWtActivateEventTracking())) {
            model.addAttribute(ThirdPartyConstants.Webtrekk.WT_ACTIVATED_EVENTS, cmsPage.getWtActivateEventTracking());
        } else {
            model.addAttribute(ThirdPartyConstants.Webtrekk.WT_ACTIVATED_EVENTS, getCmsSiteService().getCurrentSite().getWtActivateEventTracking());
        }
    }

    protected void storeContentPageTitleInModel(final Model model, final String title) {
        model.addAttribute(CMS_PAGE_TITLE, MetaSanitizerUtil.sanitize(title));
    }

    protected String getViewForPage(final Model model) {
        if (model.containsAttribute(CMS_PAGE_MODEL)) {
            final AbstractPageModel page = (AbstractPageModel) model.asMap().get(CMS_PAGE_MODEL);
            if (page != null) {
                return getViewForPage(page);
            }
        }
        return null;
    }

    protected String getViewForPage(final AbstractPageModel page) {
        if (page != null) {
            final PageTemplateModel masterTemplate = page.getMasterTemplate();
            if (masterTemplate != null) {
                final String targetPage = getCmsPageService().getFrontendTemplateName(masterTemplate);
                if (targetPage != null && !targetPage.isEmpty()) {
                    final String redirection = PAGE_ROOT + targetPage;
                    return redirection;
                }
            }
        }
        return null;
    }

    protected void addWishListsData(final Model model, final HttpServletRequest request) {
        // Compare list
        final String compareList = Attributes.COMPARE.getValueFromCookies(request);
        final int count = getDistCompareListFacade().getCompareListSize(compareList);
        model.addAttribute("hasCompareProducts", Boolean.valueOf(count > 0));
        model.addAttribute("compareListSize", Integer.valueOf(count));

        // Adding meta lists
        model.addAttribute("metaLists", getMetaLists());
        // Shopping and Favorite lists
        if (!isGuestUser()) {
            model.addAttribute("shoppingListCount", Integer.valueOf(getDistShoppingListFacade().getListsCount()));
        }
    }

    protected boolean isGuestUser() {
        // DISTRELEC-6065 B2E, OCI and ARIBA customers does not have a
        // persistent compare list
        final UserModel currentUser = userService.getCurrentUser();
        return getUserService().isAnonymousUser(currentUser) || isMemberOfGroup(DistConstants.User.EPROCUREMENTGROUP_UID)
                || isMemberOfGroup(DistConstants.User.B2BEESHOPGROUP_UID);
    }

    protected String normalizeProductCode(final String code) {
        if (isNotBlank(code) && !code.contains("it.code")) {
            String cleanedCode = code.replaceAll("-", "");
            if (cleanedCode.matches(PRODUCT_CODE_REGEX)) {
                return cleanedCode;
            }
        }
        return null;
    }

    /**
     * Checks if the current user is member of the specified group
     *
     * @param groupName
     *            the target group name
     * @return {@code true} if the current user is member of the group given by its name, {@code false} otherwise.
     * @see #isMemberOfGroup(UserModel, String)
     */
    protected boolean isMemberOfGroup(final String groupName) {
        return isMemberOfGroup(getUserService().getCurrentUser(), groupName);
    }

    /**
     * Checks whether the user is member of the group given by its name.
     *
     * @param user
     *            the user to check.
     * @param groupName
     *            the target group name.
     * @return {@code true} if the specified user is member of the group given by its name, {@code false} otherwise.
     */
    protected boolean isMemberOfGroup(final UserModel user, final String groupName) {
        return getUserService().isMemberOfGroup(user, getUserService().getUserGroupForUID(groupName));
    }

    /**
     * Checks request URL against properly resolved URL and returns null if url is proper or redirection string if not.
     *
     * @param request
     *            - request that contains current URL
     * @param response
     *            - response to write "301 Moved Permanently" status to if redirected
     * @param resolvedUrl
     *            - properly resolved URL
     * @return null if url is properly resolved or redirection string if not
     * @throws UnsupportedEncodingException
     */
    protected String checkRequestUrl(final HttpServletRequest request, final HttpServletResponse response, final String resolvedUrl)
                                                                                                                                     throws UnsupportedEncodingException {
        try {
            final String requestURI = URIUtil.decode(request.getRequestURI(), StandardCharsets.UTF_8.name());
            final String decoded = URIUtil.decode(resolvedUrl, StandardCharsets.UTF_8.name());

            if (StringUtils.isNotEmpty(decoded) && decoded.startsWith(requestURI)) {
                return null;
            } else {
                request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.MOVED_PERMANENTLY);
                final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(resolvedUrl);
                addQueryString(request, uriComponentsBuilder);
                final RedirectView rv = new RedirectView(uriComponentsBuilder.build().toUriString());
                rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);

                return REDIRECT_PREFIX + uriComponentsBuilder.build().toUriString();
            }
        } catch (final URIException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }
    }

    protected String checkRequestUrl(final HttpServletRequest request, final HttpServletResponse response, final String resolvedUrl,
                                     final boolean isPermanentRedirect) throws UnsupportedEncodingException {
        final String url = checkRequestUrl(request, response, resolvedUrl);

        if (isPermanentRedirect && StringUtils.isNotEmpty(url) && url.startsWith(UrlBasedViewResolver.REDIRECT_URL_PREFIX)) {
            return url.replaceFirst(UrlBasedViewResolver.REDIRECT_URL_PREFIX, UiExperienceViewResolver.REDIRECT_PERMANENT_URL_PREFIX);
        }
        return url;
    }

    protected void addQueryString(final HttpServletRequest request, final UriComponentsBuilder uriComponentsBuilder) {
        if (StringUtils.isNotBlank(request.getQueryString())) {
            uriComponentsBuilder.query(request.getQueryString());
        }
    }

    protected ContentPageModel getContentPageForLabelOrId(final String labelOrId) throws CMSItemNotFoundException {

        ContentPageModel cmsPage = null;
        String key = labelOrId;
        try {
            cmsPage = getCmsPageService().getPageForLabelOrId(labelOrId, cmsPreviewService.getPagePreviewCriteria());

        } catch (IllegalArgumentException iaex) {
            LOG.debug("CatalalogVersion is missing in CMS page {}, Exception: {}", key, iaex);
        } catch (Exception ex) {
            LOG.error("Exception while getting page for label or id {}, Exception: {}", key, ex);
        } finally {
            if (null == cmsPage) {
                cmsPage = getCmsPageService().getHomepage(cmsPreviewService.getPagePreviewCriteria());
                if (cmsPage == null) {
                    // Fallback to site start page label
                    final CMSSiteModel site = getCmsSiteService().getCurrentSite();
                    if (site != null) {
                        key = getCmsSiteService().getStartPageLabelOrId(site);
                    }
                    cmsPage = getCmsPageService().getPageForLabelOrId(key, cmsPreviewService.getPagePreviewCriteria());
                }
            }
        }
        // Actually resolve the label or id - running cms restrictions
        getSessionService().setAttribute(WebConstants.CURRENT_CMS_PAGE, key);
        return cmsPage;
    }

    protected String storeContinueUrl(final HttpServletRequest request) {
        final StringBuilder url = new StringBuilder();
        url.append(XSSFilterUtil.filterUsingRules(request.getServletPath()));
        final String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            url.append('?').append(queryString);
        }
        getSessionService().setAttribute(WebConstants.CONTINUE_URL, url.toString());
        return url.toString();
    }

    protected void setUpMetaData(final Model model, final MetaData metaData) {
        model.addAttribute("metaData", metaData);
    }

    protected void setUpMetaData(final Model model, final String metaDescription) {
        model.addAttribute("metaDescription", MetaSanitizerUtil.sanitize(metaDescription));
    }

    protected void setUpMetaDataForContentPage(final Model model, final ContentPageModel contentPage) {
        setUpMetaData(model, (null != contentPage && StringUtils.isNotEmpty(contentPage.getSeoMetaTitle())) ? contentPage.getSeoMetaTitle()
                                                                                                            : contentPage.getDescription());
        setMetaRobots(model, contentPage);
    }

    protected void setMetaRobots(Model model, ContentPageModel contentPage) {
        CmsRobotTag cmsRobotTag = contentPage.getRobotTag();
        if (null != cmsRobotTag) {
            String robots = null;
            switch (cmsRobotTag) {
                case INDEX_FOLLOW:
                    robots = ThirdPartyConstants.SeoRobots.INDEX_FOLLOW;
                    break;
                case INDEX_NOFOLLOW:
                    robots = ThirdPartyConstants.SeoRobots.INDEX_NOFOLLOW;
                    break;
                case NOINDEX_FOLLOW:
                    robots = ThirdPartyConstants.SeoRobots.NOINDEX_FOLLOW;
                    break;
                case NOINDEX_NOFOLLOW:
                    robots = ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW;
                    break;
            }
            model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, robots);
        }
    }

    protected String getReturnRedirectUrl(final HttpServletRequest request) {
        final String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return REDIRECT_PREFIX + referer;
        }
        return REDIRECT_PREFIX + '/';
    }

    protected void setUpDownloadFile(final HttpServletResponse response, final File downloadFile, final String format) throws IOException {
        // create download only if a file to download exists
        if (downloadFile != null) {
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFile.getName());
            response.setContentType("application/" + format);
            response.setContentLength((int) downloadFile.length());

            FileCopyUtils.copy(new FileInputStream(downloadFile), response.getOutputStream());
        }
    }

    protected DistSendToFriendEvent getSendToFriendEvent(final SendToFriendForm sendToFriendForm) {
        final DistSendToFriendEvent distSendToFriendEvent = new DistSendToFriendEvent();
        distSendToFriendEvent.setYourName(sendToFriendForm.getName());
        distSendToFriendEvent.setYourEmail(sendToFriendForm.getEmail());
        distSendToFriendEvent.setReceiverName(sendToFriendForm.getReceivername());
        distSendToFriendEvent.setReceiverEmail(sendToFriendForm.getEmail2());
        distSendToFriendEvent.setMessage(sendToFriendForm.getMessage());
        distSendToFriendEvent.setLanguage(getCommonI18NService().getCurrentLanguage());
        return distSendToFriendEvent;
    }

    protected String getPropertiesValueWithFallback(final String key) {
        return getSessionService().getOrLoadAttribute(key, () -> {
            final Locale locale = getI18nService().getCurrentLocale();
            final CMSSiteModel cmsSite = getCmsSiteService().getCurrentSite();
            final String cmsSiteKey = key + "." + cmsSite.getUid();
            final String cmsSiteLanguageKey = cmsSiteKey + "." + locale.getLanguage();
            final String value = getConfigurationService().getConfiguration().getString(cmsSiteLanguageKey);
            if (StringUtils.isNotEmpty(value)) {
                return value;
            }

            return getConfigurationService().getConfiguration().getString(cmsSiteKey);
        });
    }

    protected void setCountryOnForm(final AbstractDistAddressForm addressForm) {
        final SalesOrgData salesOrg = getStoreSessionFacade().getCurrentSalesOrg();
        if (salesOrg != null) {
            addressForm.setCountryIso(salesOrg.getCountryIsocode());
        }
    }

    protected boolean isFacetFilterRequest(final HttpServletRequest request) {
        if (request != null) {
            final Enumeration<String> parameterNames = request.getParameterNames();
            if (parameterNames != null) {
                while (parameterNames.hasMoreElements()) {
                    final String paramName = parameterNames.nextElement();

                    // if a parameter filter_ is present, it means that a facet
                    // filter has been activated
                    if (StringUtils.contains(paramName, "filter_")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected ShopDataLayer getShopDataLayer() {
        final ShopDataLayer shopDataLayer = new ShopDataLayer();
        final CMSSiteModel site = getCmsSiteService().getCurrentSite();
        shopDataLayer.setChannel(getCurrentChannel().getType());
        shopDataLayer.setCountry(site.getCountry().getIsocode());
        shopDataLayer.setCountryName(site.getCountry().getName());
        shopDataLayer.setCurrency(getCurrentCurrency().getIsocode());
        shopDataLayer.setLanguage(getCurrentLanguage().getIsocode());
        shopDataLayer.setLanguageName(getCurrentLanguage().getName());
        shopDataLayer.setShop(site.getName(Locale.ENGLISH));
        return shopDataLayer;
    }

    protected PageDataLayer getPageDataLayer(final Model model) {
        final PageDataLayer pageDataLayer = new PageDataLayer();
        pageDataLayer.setOnsiteSearchResults(model.asMap().get("wtTotalSearchResults") != null ? (String) model.asMap().get("wtTotalSearchResults") : "");
        if (model.asMap().get("wtCatAreaCode") != null) {
            pageDataLayer.setArea((String) model.asMap().get("wtCatAreaCode"));
        } else {
            pageDataLayer.setArea((String) model.asMap().get("wtPageAreaCode"));
        }
        pageDataLayer.setContentGroup(getWtContentGroup());
        pageDataLayer.setCustomerID(getWtCustomerId());
        pageDataLayer.setError("");
        pageDataLayer.setPageID(model.asMap().get("wtPageId") != null ? (String) model.asMap().get("wtPageId") : "");
        pageDataLayer.setSearchPhrase(model.asMap().get("searchPhrase") != null ? (String) model.asMap().get("searchPhrase") : "");
        return pageDataLayer;
    }

    protected CustomerDataLayer getCustomerDataLayer() {
        final CustomerDataLayer customerDataLayer = new CustomerDataLayer();
        final UserModel user = getUserService().getCurrentUser();
        customerDataLayer.setUserid(user.getUid());
        if (getUserService().isAnonymousUser(user)) {
            customerDataLayer.setFirstName(user.getName());
        } else {
            customerDataLayer.setNickName(user.getName());
            final AddressModel contactAddress = ((B2BCustomerModel) user).getContactAddress();
            if (contactAddress != null) {
                customerDataLayer.setFirstName(contactAddress.getFirstname());
                customerDataLayer.setLastName(contactAddress.getLastname());
            }
        }
        if (b2bCartFacade.hasSessionCart()) {
            customerDataLayer.setCartid(b2bCartFacade.getSessionCartModel().getCode());
        }

        return customerDataLayer;
    }

    protected CartDataLayer getCartDataLayerFromOrder(final OrderData orderData) {
        try {
            if (null != orderData) {
                final CartDataLayer cartDataLayer = new CartDataLayer();
                cartDataLayer.setDiscountValue(orderData.getDiscounts() == null ? "0.00" : orderData.getDiscounts().toString());
                cartDataLayer.setGTV(orderData.getTotalPrice() == null ? "0.00" : orderData.getTotalPrice().getValue().toString());
                cartDataLayer.setOrderValue(orderData.getSubTotal() == null ? "0.00" : orderData.getSubTotal().getValue().toString());
                cartDataLayer.setOrderId(orderData.getCode());
                cartDataLayer.setPaymentFee(orderData.getPaymentCost() == null ? "0.00" : orderData.getPaymentCost().getValue().toString());
                cartDataLayer.setProducts(getProductsFromOrder(orderData, cartDataLayer));
                cartDataLayer.setShippingCosts(orderData.getDeliveryCost() == null ? "0.00" : orderData.getDeliveryCost().getValue().toString());
                cartDataLayer.setTotalProductDistinct(orderData.getEntries().size());
                cartDataLayer.setVAT(orderData.getTotalTax() == null ? "0.00" : orderData.getTotalTax().getValue().toString());
                if (orderData.getErpVoucherInfoData() != null) {
                    cartDataLayer.setVoucherValue(orderData.getErpVoucherInfoData().getFixedValue().getValue().toString());
                }
                return cartDataLayer;
            }
        } catch (Exception ex) {
            LOG.error("Error while populating cart datalayer object: ", ex);
        }
        return null;
    }

    private List<com.namics.distrelec.b2b.facades.webtrekk.data.ProductData> getProductsFromOrder(final OrderData cartData,
                                                                                                  final CartDataLayer cartDataLayer) {
        final List<String> productCategoryInfo = new ArrayList<>();
        final StringBuilder path = new StringBuilder();
        final List<com.namics.distrelec.b2b.facades.webtrekk.data.ProductData> products = new ArrayList<>();
        int qty = 0;
        for (final OrderEntryData data : cartData.getEntries()) {
            final com.namics.distrelec.b2b.facades.webtrekk.data.ProductData product = new com.namics.distrelec.b2b.facades.webtrekk.data.ProductData();
            final ProductModel productModel = productService.getProductForCode(data.getProduct().getCode());
            if (productModel != null && CollectionUtils.isNotEmpty(productModel.getSupercategories())) {
                for (final CategoryModel category : productModel.getSupercategories()) {
                    product.setProductCategoryPath(getProductCategoryPath(category, productCategoryInfo, path));
                }
            }
            product.setProductcategoryLevel1(CollectionUtils.isEmpty(productCategoryInfo) ? "" : productCategoryInfo.get(0));
            product.setProductID(data.getProduct().getCode());
            if (data.getProduct().getDistManufacturer() != null) {
                product.setProductManufacturer(data.getProduct().getDistManufacturer().getName());
            }
            product.setProductName(data.getProduct().getName());
            product.setProductQuantity(data.getQuantity().toString());
            product.setProductTotalCost(data.getTotalPrice().getValue().toString());
            qty = qty + data.getQuantity().intValue();
            products.add(product);
        }
        cartDataLayer.setTotalProducts(qty);
        return products;
    }

    protected void getCartForDTM(final DigitalDatalayer digitalDatalayer, final AbstractOrderModel cartData, HttpServletRequest request) {
        getDistDigitalDatalayerFacade().getCartForDTM(digitalDatalayer, cartData, getWtCustomerId());
    }

    protected CartDataLayer getCartDataLayer() {
        if (b2bCartFacade.hasSessionCart()) {
            final CartDataLayer cartDataLayer = new CartDataLayer();
            final CartModel cartModel = b2bCartFacade.getSessionCartModel();
            cartDataLayer.setDiscountValue(cartModel.getDiscounts() == null ? "0.00" : cartModel.getDiscounts().toString());
            cartDataLayer.setGTV(cartModel.getTotalPrice() == null ? "0.00" : cartModel.getTotalPrice().toString());
            cartDataLayer.setOrderValue(cartModel.getSubtotal() == null ? "0.00" : cartModel.getSubtotal().toString());
            cartDataLayer.setOrderId(cartModel.getCode());
            cartDataLayer.setPaymentFee(cartModel.getPaymentCost() == null ? "0.00" : cartModel.getPaymentCost().toString());
            cartDataLayer.setProducts(getProductsFromCart(cartModel, cartDataLayer));
            cartDataLayer.setShippingCosts(cartModel.getDeliveryCost() == null ? "0.00" : cartModel.getDeliveryCost().toString());
            cartDataLayer.setTotalProductDistinct(cartModel.getEntries().size());
            cartDataLayer.setVAT(cartModel.getTotalTax() == null ? "0.00" : cartModel.getTotalTax().toString());
            if (cartModel.getErpVoucherInfo() != null) {
                cartDataLayer.setVoucherValue(cartModel.getErpVoucherInfo().getFixedValue().toString());
            }
            return cartDataLayer;
        }
        return null;
    }

    private List<com.namics.distrelec.b2b.facades.webtrekk.data.ProductData> getProductsFromCart(final CartModel cartModel, final CartDataLayer cartDataLayer) {
        final List<String> productCategoryInfo = new ArrayList<>();
        final StringBuilder path = new StringBuilder();
        final List<com.namics.distrelec.b2b.facades.webtrekk.data.ProductData> products = new ArrayList<>();
        int qty = 0;
        for (final AbstractOrderEntryModel entry : cartModel.getEntries()) {
            final com.namics.distrelec.b2b.facades.webtrekk.data.ProductData product = new com.namics.distrelec.b2b.facades.webtrekk.data.ProductData();
            final ProductModel productModel = productService.getProductForCode(entry.getProduct().getCode());
            if (productModel != null && CollectionUtils.isNotEmpty(productModel.getSupercategories())) {
                for (final CategoryModel category : productModel.getSupercategories()) {
                    product.setProductCategoryPath(getProductCategoryPath(category, productCategoryInfo, path));
                }
            }
            product.setProductcategoryLevel1(CollectionUtils.isEmpty(productCategoryInfo) ? "" : productCategoryInfo.get(0));
            product.setProductID(entry.getProduct().getCode());
            if ((entry.getProduct()).getManufacturer() != null) {
                product.setProductManufacturer((entry.getProduct()).getManufacturer().getName());
            }
            product.setProductName(entry.getProduct().getName());
            product.setProductQuantity(entry.getQuantity().toString());
            product.setProductTotalCost(entry.getTotalPrice().toString());
            qty = qty + entry.getQuantity().intValue();
            products.add(product);
        }
        cartDataLayer.setTotalProducts(qty);
        return products;
    }

    private String getProductCategoryPath(final CategoryModel category, final List<String> productCategoryInfo, final StringBuilder path) {
        for (final CategoryModel superCategory : category.getSupercategories()) {
            if (superCategory != null && !(superCategory instanceof ClassificationClassModel)) {
                if (superCategory.getLevel().equals(Integer.valueOf(category.getLevel().intValue() - 1)) && superCategory.getLevel().intValue() > 0) {
                    if (superCategory.getLevel().intValue() == 1) {
                        productCategoryInfo.add(superCategory.getName());
                    }
                    getProductCategoryPath(superCategory, productCategoryInfo, path);
                    continue;
                }
            }
        }
        return path.append("/").append(category.getName()).toString();
    }

    protected void addPaymentModeModelAttributes(final Model model) {
        model.addAttribute("canRequestInvoicePaymentMode", userFacade.canRequestInvoicePaymentMode());
        model.addAttribute("invoicePaymentModeRequested", userFacade.isInvoicePaymentModeRequested());

        boolean invoicePaymentModeJustRequested = getSessionService().getAttribute(WebConstants.INVOICE_PAYMENT_MODE_REQUESTED) != null;
        if (invoicePaymentModeJustRequested) {
            getSessionService().removeAttribute(WebConstants.INVOICE_PAYMENT_MODE_REQUESTED);
        }
        model.addAttribute("invoicePaymentModeJustRequested", invoicePaymentModeJustRequested);
    }

    protected void sendInvoicePaymentModeRequest() {
        userFacade.requestInvoicePaymentMode();
        getSessionService().setAttribute(WebConstants.INVOICE_PAYMENT_MODE_REQUESTED, true);
    }

    public boolean isDatalayerEnabled() {
        return configurationService.getConfiguration().getBoolean(DistConfigConstants.FEATURE_DATALAYER, true);
    }

    public DistrelecCMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final DistrelecCMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public DistCmsPageService getCmsPageService() {
        return distCmsPageService;
    }

    public void setCmsPageService(final DistCmsPageService distCmsPageService) {
        this.distCmsPageService = distCmsPageService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public SiteConfigService getSiteConfigService() {
        return siteConfigService;
    }

    public void setSiteConfigService(final SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public DistSiteBaseUrlResolutionService getDistSiteBaseUrlResolutionService() {
        return distSiteBaseUrlResolutionService;
    }

    public void setDistSiteBaseUrlResolutionService(final DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService) {
        this.distSiteBaseUrlResolutionService = distSiteBaseUrlResolutionService;
    }

    public DistAribaService getDistAribaService() {
        return distAribaService;
    }

    public void setDistAribaService(final DistAribaService distAribaService) {
        this.distAribaService = distAribaService;
    }

    public DistOciService getDistOciService() {
        return distOciService;
    }

    public void setDistOciService(final DistOciService distOciService) {
        this.distOciService = distOciService;
    }

    public DistCxmlService getDistCxmlService() {
        return distCxmlService;
    }

    public void setDistCxmlService(final DistCxmlService distCxmlService) {
        this.distCxmlService = distCxmlService;
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    public DistCustomerFacade getCustomerFacade() {
        return customerFacade;
    }

    public void setCustomerFacade(final DistCustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    public DistCustomerFacade getB2bCustomerFacade() {
        return b2bCustomerFacade;
    }

    public void setB2bCustomerFacade(final DistCustomerFacade b2bCustomerFacade) {
        this.b2bCustomerFacade = b2bCustomerFacade;
    }

    public DistWebtrekkFacade getDistWebtrekkFacade() {
        return distWebtrekkFacade;
    }

    public void setDistWebtrekkFacade(final DistWebtrekkFacade distWebtrekkFacade) {
        this.distWebtrekkFacade = distWebtrekkFacade;
    }

    public DistShoppingListFacade getDistShoppingListFacade() {
        return distShoppingListFacade;
    }

    public void setDistShoppingListFacade(final DistShoppingListFacade distShoppingListFacade) {
        this.distShoppingListFacade = distShoppingListFacade;
    }

    public DistCompareListFacade getDistCompareListFacade() {
        return distCompareListFacade;
    }

    public void setDistCompareListFacade(final DistCompareListFacade distCompareListFacade) {
        this.distCompareListFacade = distCompareListFacade;
    }

    public PageTitleResolver getPageTitleResolver() {
        return pageTitleResolver;
    }

    public void setPageTitleResolver(final PageTitleResolver pageTitleResolver) {
        this.pageTitleResolver = pageTitleResolver;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public DistB2BOrderFacade getOrderFacade() {
        return orderFacade;
    }

    public void setOrderFacade(final DistB2BOrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public DistCachingFacade getDistCachingFacade() {
        return distCachingFacade;
    }

    public void setDistCachingFacade(final DistCachingFacade distCachingFacade) {
        this.distCachingFacade = distCachingFacade;
    }

    public FactFinderChannelService getChannelService() {
        return channelService;
    }

    public void setChannelService(final FactFinderChannelService channelService) {
        this.channelService = channelService;
    }

    public DistProductPriceQuotationFacade getDistProductPriceQuotationFacade() {
        return distProductPriceQuotationFacade;
    }

    public void setDistProductPriceQuotationFacade(final DistProductPriceQuotationFacade distProductPriceQuotationFacade) {
        this.distProductPriceQuotationFacade = distProductPriceQuotationFacade;
    }

    public DistDigitalDatalayerFacade getDistDigitalDatalayerFacade() {
        return distDigitalDatalayerFacade;
    }

    public void setDistDigitalDatalayerFacade(DistDigitalDatalayerFacade distDigitalDatalayerFacade) {
        this.distDigitalDatalayerFacade = distDigitalDatalayerFacade;
    }

    public DistSeoFacade getDistSeoFacade() {
        return distSeoFacade;
    }

    public void setDistSeoFacade(final DistSeoFacade distSeoFacade) {
        this.distSeoFacade = distSeoFacade;
    }

    protected void logError(final Logger logger, final String messagePattern, final Throwable th, final Object... args) {
        DistLogUtils.logError(logger, messagePattern, th, args);
    }

    /**
     * Checks whether the product has a valid replacement.
     *
     * @param productModel
     *            the target product.
     * @return {@code true} is the product has a valid replacement, {@code false} otherwise.
     */
    protected boolean hasValidReplacement(final ProductModel productModel) {
        if (productModel.getBuyableReplacementProduct().booleanValue()) {
            if (productModel.getReplacementReason() == null) {
                return false;
            }
            final List<String> validReasons = Arrays
                                                    .asList(getConfigurationService().getConfiguration().getString("distrelec.eol.replacement.validreasons", "")
                                                                                     .split(","));
            return validReasons.contains(productModel.getReplacementReason()
                                                     .getCode());
        }

        return false;
    }

    protected boolean hasReachedMovAmount() {
        if (!userFacade.isAnonymousUser() || distCheckoutFacade.isAnonymousCheckout()) {
            CartData cartData = getB2bCartFacade().getCurrentCart();
            return BooleanUtils.isTrue(cartData.getSkipMovCheck()) || !(cartData.getMissingMov() != null && cartData.getMissingMov() > 0);
        } else {
            return true;
        }
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public DistB2BCartFacade getB2bCartFacade() {
        return b2bCartFacade;
    }

    public void setB2bCartFacade(final DistB2BCartFacade b2bCartFacade) {
        this.b2bCartFacade = b2bCartFacade;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public DistManufacturerFacade getDistManufacturerFacade() {
        return distManufacturerFacade;
    }

    public void setDistManufacturerFacade(final DistManufacturerFacade distManufacturerFacade) {
        this.distManufacturerFacade = distManufacturerFacade;
    }

    public DistCmsPageService getDistCmsPageService() {
        return distCmsPageService;
    }

    public void setDistCmsPageService(final DistCmsPageService distCmsPageService) {
        this.distCmsPageService = distCmsPageService;
    }

    public SearchRobotDetector getSearchRobotDetector() {
        return searchRobotDetector;
    }

    public void setSearchRobotDetector(final SearchRobotDetector searchRobotDetector) {
        this.searchRobotDetector = searchRobotDetector;
    }

    public DistCategoryFacade getDistCategoryFacade() {
        return distCategoryFacade;
    }

    public void setDistCategoryFacade(final DistCategoryFacade distCategoryFacade) {
        this.distCategoryFacade = distCategoryFacade;
    }

    public DistCMSNavigationService getDistCmsNavigationService() {
        return distCmsNavigationService;
    }

    public void setDistCmsNavigationService(final DistCMSNavigationService distCmsNavigationService) {
        this.distCmsNavigationService = distCmsNavigationService;
    }

    public MessageSourceAccessor getEnglishMessageSourceAccessor() {
        return englishMessageSourceAccessor;
    }

    public void setEnglishMessageSourceAccessor(final MessageSourceAccessor englishMessageSourceAccessor) {
        this.englishMessageSourceAccessor = englishMessageSourceAccessor;
    }

    public CMSContentSlotService getCmsContentSlotService() {
        return cmsContentSlotService;
    }

    public void setCmsContentSlotService(final CMSContentSlotService cmsContentSlotService) {
        this.cmsContentSlotService = cmsContentSlotService;
    }

    protected RequestContextData getRequestContextData(final HttpServletRequest request) {
        return getBean(request, "requestContextData", RequestContextData.class);
    }

    protected boolean checkCustomerHasActiveCart(final B2BCustomerModel customerModel) {
        return customerModel.getCarts().stream() //
                            .filter(cart -> !cart.isGhostOrder()) // Skip the ghost cart
                            .findAny().isPresent();
    }

    protected String encodeToUTF8(final String value) {
        String result = "";
        result = UriUtils.encodeFragment(value == null ? "" : value, "UTF-8");
        return result;
    }

    protected String decodeBase64(String value) {
        return StringUtils.isNotEmpty(value) ? new String(Base64.decodeBase64(decodeToUTF8(value))) : "";
    }

    protected void forwardGlobalMessages(Model model, RedirectAttributes redirectModel) {
        redirectModel.addFlashAttribute(GlobalMessages.INFO_MESSAGES_HOLDER, model.asMap().get(GlobalMessages.INFO_MESSAGES_HOLDER));
        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, model.asMap().get(GlobalMessages.CONF_MESSAGES_HOLDER));
        redirectModel.addFlashAttribute(GlobalMessages.WARN_MESSAGES_HOLDER, model.asMap().get(GlobalMessages.WARN_MESSAGES_HOLDER));
        redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, model.asMap().get(GlobalMessages.ERROR_MESSAGES_HOLDER));
    }

    public DistAribaService getDefaultDistAribaService() {
        return defaultDistAribaService;
    }

    public void setDefaultDistAribaService(final DistAribaService defaultDistAribaService) {
        this.defaultDistAribaService = defaultDistAribaService;
    }

    public DistUserFacade getDistUserFacade() {
        return userFacade;
    }

    public void setDistUserFacade(final DistUserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public void setGitVersionService(final GitVersionService gitVersionService) {
        this.gitVersionService = gitVersionService;
    }

    protected CaptchaUtil getCaptchaUtil() {
        return captchaUtil;
    }

    public void setCaptchaUtil(CaptchaUtil captchaUtil) {
        this.captchaUtil = captchaUtil;
    }

    protected DistNewsletterFacade getNewsletterFacade() {
        return newsletterFacade;
    }

    protected void setNewsletterFacade(DistNewsletterFacade newsletterFacade) {
        this.newsletterFacade = newsletterFacade;
    }

    protected ProductSearchFacade<ProductData> getProductSearchFacade() {
        return productSearchFacade;
    }

    public DistCheckoutFacade getDistCheckoutFacade() {
        return distCheckoutFacade;
    }

    public void setDistCheckoutFacade(DistCheckoutFacade distCheckoutFacade) {
        this.distCheckoutFacade = distCheckoutFacade;
    }

    /**
     * Page Type enumeration.
     *
     * @author rmeier, Namics AG
     */
    public enum PageType {
        ProductSearch("ProductSearch"), //
        Category("Category"), //
        Product("Product"), //
        Cart("Cart"), //
        OrderConfirmation("OrderConfirmation"), //
        ContentPage("ContentPage");

        private final String value;

        PageType(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
