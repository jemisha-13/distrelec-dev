/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.constants;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;

/**
 * All constants for the project.
 *
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 */
public interface DistConstants {

    enum ErrorLogCode {
        ZERO_PRICE_ERROR("ZERO_PRICE_ERROR"),
        SHIPPING_METHOD_ERROR("SHIPPING_METHOD_ERROR"),
        DELIVERY_MODE_ERROR("DELIVERY_MODE_ERROR"),
        CONVERSION_FORMAT_ERROR("CONVERSION_FORMAT_ERROR"),
        SPRING_SECURITY_ERROR("SPRING_SECURITY_ERROR"),
        SESSION_COOKIE_ERROR("SESSION_COOKIE_ERROR"),
        INACTIVE_ORG_ERROR("INACTIVE_ORG_ERROR"),
        INACTIVE_USER_ERROR("INACTIVE_USER_ERROR"),
        INVALID_USER_ERROR("INVALID_USER_ERROR"),
        INVALID_USER_GROUP_ERROR("INVALID_USER_GROUP_ERROR"),
        ADDRESS_ERROR("ADDRESS_ERROR"),
        UPDATE_USER_ERROR("UPDATE_USER_ERROR"),
        REGISTRATION_ERROR("REGISTRATION_ERROR"),
        LOGIN_ERROR("LOGIN_ERROR"),
        ERP_IMPORT_ERROR("ERP_IMPORT_ERROR"),
        ERP_EXPORT_ERROR("ERP_EXPORT_ERROR"),
        SEARCH_ERROR("SEARCH_ERROR"),
        PAYMENT_ERROR("PAYMENT_ERROR"),
        PIM_IMPORT_ERROR("PIM_IMPORT_ERROR"),
        PLACEORDER_ERROR("PLACEORDER_ERROR"),
        CART_CALCULATION_ERROR("CART_CALCULATION_ERROR"),
        CHECKOUT_ERROR("CHECKOUT_ERROR"),
        DATALAYER_ERROR("DATALAYER_ERROR"),
        ORDER_RELATED_ERROR("ORDER_RELATED_ERROR"),
        EMAIL_ERROR("EMAIL_ERROR");

        private final String code;

        private final String uuid;

        /**
         * Create a new instance of {@code ErrorLogCode}
         *
         * @param logType
         */
        ErrorLogCode(final String logType) {
            uuid = UUID.randomUUID().toString();
            code = "[" + logType + "] [" + uuid + "] ";

        }

        public String getCode() {
            return code;
        }

        public String getUuid() {
            return uuid;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return code;
        }
    }

    enum ErrorSource {
        WEBSERVICE_EXCEPTION("WEBSERVICE_EXCEPTION"),
        SAP_FAULT("SAP_FAULT_ERROR_SOURCE"),
        FACTFINDER("FACTFINDER_COM_ERROR_SOURCE"),
        HYBRIS_BINDING_ERROR("HYBRIS_BINDING_ERROR"),
        HYBRIS("HYBRIS_ERROR_SOURCE"),
        MODEL_SAVING_EXCEPTION_HYBRIS("MODEL_SAVING_EXCEPTION_HYBRIS"),
        CXML("CXML_ERROR_SOURCE"),
        ARIBA("ARIBA_ERROR_SOURCE"),
        DB("DB_ERROR_SOURCE");

        private final String code;

        /**
         * Create a new instance of {@code ErrorSource}
         *
         * @param logType
         */
        ErrorSource(final String logType) {
            code = "[" + logType + "] ";
        }

        public String getCode() {
            return code + UUID.randomUUID();
        }

        public String getCode(final String errorCode) {
            return code + "[" + UUID.randomUUID() + "][" + errorCode + "]";
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return code;
        }
    }

    interface Numbers {
        Integer ZERO = 0;

        Integer ONE = 1;
    }

    interface Punctuation {
        String COMMA = ",";

        String EMPTY = StringUtils.EMPTY;

        String EQUALS = "=";

        String DOT = ".";

        String DASH = "-";

        String UNDERSCORE = "_";

        String QUESTION_MARK = "?";

        String FORWARD_SLASH = "/";

        String NEW_LINE = "\n";

        String DOUBLE_QUOTES = "\"";

        String AMPERSAND = "&";

        String PIPE = "|";

        String SPACE = " ";
    }

    interface Checkout {
        String FAST_CHECKOUT = "fastCheckout";

        String MESSAGE_PARAMETER = "accConfMsgs";

        String INVALID_ADDRESS = "invalidAddress";

    }

    interface CheckoutPage {
        String PAGE_CHECKOUT = "/checkout";

        String PAGE_CHECKOUT_DELIVERY = "checkoutDeliveryPage";

        String PAGE_CHECKOUT_REVIEW_AND_PAY = "checkoutReviewAndPayPage";

        String PAGE_BACKORDER_DETAILS = "checkoutBackOrderDetailsPage";
    }

    interface CheckoutPath {
        String CHECKOUT_DELIVERY = "/checkout/delivery";

        String CHECKOUT_REVIEW_AND_PAY = "/checkout/review-and-pay";

        String CHECKOUT_PAYMENT = "/checkout/payment";

        String CHECKOUT_BACK_ORDER_DETAILS = "/checkout/backorderDetails";
    }

    interface PaymentMethods {

        String NEW_CREDIT_CARD = "NewCreditCard";

        String CREDIT_CARD = "CreditCard";

        String PAY_PAL = "PayPal";

        String INVOICE = "Invoice";
    }

    interface FactFinder {
        String WEBSERVICE_SEARCH_URL = "distrelecfactfindersearch.factfinder.webservice.address.search";

        String FF_TRACKING = "queryFromSuggest";

        String FF_MPN_MESSAGE_KEY = "search.mpn.message";

        String FF_MPN_ALTERNATIVE_MATCH_KEY = "Records_Contain_Alternative_MPN_Match";

        String ENCODE_URL_SUFFIX = "_encodeurl";

        String ENCODE_FF_SUFFIX = "_encodeff";

        String FACTFINDER_UNIT_PREFIX = "~~";

        String MIN_MAX_FILTERS_REGEX = ".*[0-9].*";

        String CATEGORY = "CATEGORY";
    }

    interface Fusion {
        String ALLOWED_CHARACTERS_FIELDNAME = "[^a-zA-Z0-9]";

        String FUSION_SEARCH_URL = "distrelecfusionintegration.fusion.search.url";

        String FUSION_DIRECT_SEARCH_URL = "distrelecfusionintegration.fusion.search.url.direct";

        String FUSION_SEARCH_API_KEY = "distrelecfusionintegration.fusion.api.key.search";

        String FUSION_PROFILE_SUFFIX = "distrelecfusionintegration.fusion.collectionSuffix";
    }

    interface User {
        String B2BADMINGROUP_UID = "b2badmingroup";

        String B2BCUSTOMERGROUP_UID = "b2bcustomergroup";

        String B2CCUSTOMERGROUP_UID = "customergroup";

        String B2BAPPROVERGROUP_UID = "b2bapprovergroup";

        String B2BEESHOPGROUP_UID = "b2beeshopgroup";

        String B2BMANAGERGROUP_UID = "b2bmanagergroup";

        String B2BGROUP_UID = "b2bgroup";

        String EPROCUREMENTGROUP_UID = "eProcurementGroup";

        String OCICUSTOMERGROUP_UID = "ociCustomerGroup";

        String ARIBACUSTOMERGROUP_UID = "aribaCustomerGroup";

        String CXMLCUSTOMERGROUP_UID = "cxmlCustomerGroup";

        String DISTCALLCENTERGROUP_UID = "distCallcenterUserGroup";

        String GUEST = "guest";
    }

    interface Login {
        String Z100 = "Z100";

        String COMPANY = "COMPANY";

        String Z200 = "Z200";

        String PRIVATE = "PRIVATE";
    }

    interface Session {
        String CHANNEL = "channel";

        String JSESSIONID = "JSESSIONID";

        String ASGUID = "acceleratorSecureGUID";

        String SID = "sid";

        String AID = "aid";

        String LN = "ln";

        String ALN = "aln";

        String CHECKOUT_USER = "checkoutUser";

        String ORDER_APROVAL_CONFIRMATION = "orderApprovalConfirmation";

        String PAYMENT_DESCRIPTION = "paymentDescription";

        String ORDER_CODE_CONFIRMATION = "orderCodeForConfirmation";

        String PRODUCT_HIERARCHY_LOADED = "productHierarchyLoaded";

        String MAIN_NAV_FLUSH = "mainNavFlush";

        String DOUBLE_OPT_IN = "doubleOptIn";

        String LOG_OUT_PRESS = "logoutEvent";

        String LOG_IN_PRESS = "logInEvent";

        String DELIVERY_MODES = "deliveryModesDatas";

        String PAYMENT_MODES = "paymentModesDatas";

        String B2E_ADDRESS_FORM = "b2eAddressForm";

        String DOUBLE_OPT_IN_POPUP = "showDoubleOptInPopup";

        String IS_NEW_REGISTRATION = "isNewRegistration";

        String IS_STOREFRONT_REQUEST = "isStorefrontRequest";
    }

    interface Payment {
        String METHOD_PAYPAL = "PayPal";

        String METHOD_MAESTRO = "Maestro";

        String METHOD_CLICKANDBUY = "ClickAndBuy";

        String METHOD_EPS = "Eps";

        String METHOD_GIROPAY = "GiroPay";
    }

    interface Shipping {
        String METHOD_PICKUP = "SAP_A1";

        String METHOD_STANDARD = "SAP_N1";

        String METHOD_NORMAL_PICKUP = "SAP_N2";

        String METHOD_ECONOMY = "SAP_E1";

        String METHOD_PICKUP_MOVEX = "Movex_PickUp";
    }

    interface MediaFormat {
        String AUDIO = "audio";

        String LANDSCAPE_SMALL = "landscape_small";

        String LANDSCAPE_MEDIUM = "landscape_medium";

        String LANDSCAPE_LARGE = "landscape_large";

        String PORTRAIT_SMALL = "portrait_small";

        String PORTRAIT_MEDIUM = "portrait_medium";

        String LANDSCAPE_SMALL_WEBP = "landscape_small_webp";

        String LANDSCAPE_MEDIUM_WEBP = "landscape_medium_webp";

        String LANDSCAPE_LARGE_WEBP = "landscape_large_webp";

        String PORTRAIT_SMALL_WEBP = "portrait_small_webp";

        String PORTRAIT_MEDIUM_WEBP = "portrait_medium_webp";

        String IMAGE360_SMALL = "360_small";

        String IMAGE360_MEDIUM = "360_medium";

        String IMAGE360_LARGE = "360_large";

        String IMAGE360_SMALL_WEBP = "360_small_webp";

        String IMAGE360_MEDIUM_WEBP = "360_medium_webp";

        String IMAGE360_LARGE_WEBP = "360_large_webp";

        String BRAND_LOGO = "brand_logo";

        String BRAND_LOGO_WEBP = "brand_logo_webp";

        String PDF = "PDF";

        String VIDEO = "video";
    }

    interface AzureMediaFolder {
        String EXPORTS = "exports";

        String PIMMEDIAS = "pimmedias";
    }

    interface Export {
        String FORMAT_CSV = "csv";

        String FORMAT_XLS = "xls";

        String FORMAT_ZIP = "zip";
    }

    interface Cookie {
        String COMPARE_NAME = "compare";

        int COMPARE_MAX_AGE = 604800;

        String LAST_VIEWED_NAME = "lastViewed";

        int LAST_VIEWED_MAX_AGE = 604800;

        String SHOP_SETTINGS_NAME = "shopSettings";

        int SHOP_SETTINGS_MAX_AGE = 604800;

        String FORCE_DESKTOP_NAME = "forcedesktop";

    }

    interface Company {
        String USER_CONFIRMATION_PENDING = "confirmation_pending";

        String ACTIVE = "active";

        String DEACTIVATED = "deactivated";
    }

    interface CacheName {
        String CACHE_MANAGER_NAME = "sap-pi-ehcache";

        // these cache names must be identical to the ones in sap-pi-ehcache-config.xml
        String AVAILABILITY = "sapAvailabilityCache";

        String CUSTOMER_PRICE = "sapCustomerPriceCache";

        String INVOICE = "sapInvoiceServiceCache";

        String ORDER_SEARCH = "sapOrderSearchServiceCache";

        String ORDER_CALCULATION = "sapOrderCalculationServiceCache";

        String QUOTATION_SEARCH = "sapQuotationServiceCache";

        String PAYMENT_AND_SHIPPING_OPTION = "sapPaymentAndShippingServiceCache";

        String RETURNS_ONLINE = "sapReturnsOnlineServiceCache";
    }

    interface PropKey {

        interface Cookie {
            String LAST_VIEWED_LIST_SIZE = "distrelec.lastViewed.cookie.listSize";

            String SUBSCRIBE_POPUP_DELAY_BEFORE = "distrelec.subscribePopup.delay.before.displayed.seconds";

            int SUBSCRIBE_POPUP_DELAY_BEFORE_DEFAULT = 120;

            String SUBSCRIBE_POPUP_DELAY_AFTER = "distrelec.subscribePopup.delay.after.displayed.seconds";

            int SUBSCRIBE_POPUP_DELAY_AFTER_DEFAULT = 2592000;
        }

        interface Email {
            String DIST_INTERNAL_EMAIL_TO = "distinternalemail.to";

            String FEEDBACK_EMAIL_DEFAULT = "feedback.email.default";

            String FEEDBACK_EMAIL_PREFIX = "feedback.email.";

            String SUPPORT_EMAIL_DEFAULT = "support.email.default";

            String SUPPORT_EMAIL_PREFIX = "support.email.";

            String FEEDBACK_SEARCH_EMAIL_DEFAULT = "feedback.search.email.default";

            String FEEDBACK_SEARCH_EMAIL_PREFIX = "feedback.search.email.";

            String CALL_ME_BACK_EMAIL_DEFAULT = "callmeback.email.default";

            String CALL_ME_BACK_EMAIL_PREFIX = "callmeback.email.";

            String QUOTE_PRODUCT_PRICE_EMAIL_DEFAULT = "quote.productPrice.email.default";

            String QUOTE_PRODUCT_PRICE_EMAIL_DEFAULT_DISPLAYNAME = "quote.productPrice.email.default.displayname";

            String QUOTE_PRODUCT_PRICE_EMAIL_FROM_DEFAULT = "quote.productPrice.email.default.from";

            String QUOTE_PRODUCT_PRICE_EMAIL_PREFIX = "quote.productPrice.email.";

            String INFO_EMAIL_DEFAULT = "info.email.default";

            String INFO_EMAIL_PREFIX = "info.email.";

            String REQUEST_CATALOG_PLUS_PRODUCT_PRICE_EMAIL_PREFIX = "request.catalogPlusProductPrice.email.";

            String REQUEST_CATALOG_PLUS_PRODUCT_PRICE_EMAIL_DEFAULT = "request.catalogPlusProductPrice.email.default";

            String SEMINAR_REGISTRATION_REQUEST_EMAIL_PREFIX = "request.seminarRegistration.email.";

            String SEMINAR_REGISTRATION_REQUEST_EMAIL_DEFAULT = "request.seminarRegistration.email.default";

            String EDUCATION_REGISTRATION_REQUEST_EMAIL_PREFIX = "request.educationRegistration.email.";

            String EDUCATION_REGISTRATION_REQUEST_EMAIL_DEFAULT = "request.educationRegistration.email.default";

            String NEW_PRODUCT_NEWS_LETTER_EMAIL = "new.product.news.letter.email";

            String NEW_PRODUCT_NEWS_LETTER_EMAIL_DEFAULT = NEW_PRODUCT_NEWS_LETTER_EMAIL + ".default";

            String CUSTOMER_ERROR_FEEDBACK_EMAIL = "customer.error.feedback.email";

            String PAYMENT_NOTIFY_ORDER_EMAIL = "distrelec.payment.notify.order.email";

            String PAYMENT_NOTIFY_ORDER_DISPLAY_NAME = "distrelec.payment.notify.order.name";

            String OBSOLESCENCE_NOTIFICATION_EMAIL = "distrelec.obsolescence.notify.email";

            String OBSOLESCENCE_NOTIFICATION_EMAIL_NAME = "distrelec.obsolescence.notify.email.name";

            String RMA_REQUEST_EMAIL_DEFAULT_TO = "rma.email.default.to";

            String RMA_REQUEST_EMAIL_DEFAULT_FROM = "rma.email.default.from";

            String RMA_REQUEST_EMAIL_DEFAULT_DISPLAYNAME = "rma.email.default.displayname";

            String RMA_REQUEST_EMAIL_TO_PREFIX = "rma.email.to.";

            String RMA_REQUEST_EMAIL_FROM_PREFIX = "rma.email.from.";

            String RMA_REQUEST_EMAIL_DISPLAYNAME_PREFIX = "rma.email.displayname.";

            String MISSING_IMAGE_MEDIA_FILE_NOTIFICATION_EMAIL = "missingImageMediaFileCronJob.notification.emailaddress";

            String NPS_EMAIL_TO = "nps.to.mail.id";

            String NPS_EMAIL_CC = "nps.to.mail.cc";
        }

        interface Shop {
            String SHOW_NEW_LABEL_DURATION = "distrelec.showNewLabelDuration";

            String COMPARE_METAHD_LIST_SIZE = "distrelec.compare.metahd.listSize";

            String COUNTRIES_WITHOUT_CANCELLATION_POLICY = "distrelec.checkout.review.countriesWithoutCancellationPolicy";

            String CAMPAIGNS_ON_PRODUCT_DETAIL_PAGE = "switchoff.productdetail.campaigns";
        }

        interface Newsletter {
            String OPTIVO_API_USER = "optivo.api.user";

            String OPTIVO_API_PASSWORD = "optivo.api.password";

            String OPTIVO_CLIENT_ID_PREFIX = "optivo.client.id.";

            String OPTIVO_RECIPIENT_LIST_ID_PREFIX = "optivo.recipientList.id.";

            String OPTIVO_OPTIN_PROCESS_ID_PREFIX = "optivo.optinProcess.id.";

            String OPTIVO_COUNTRY_ID_PREFIX = "optivo.country.code.";

            String OPTIVO_OPTIN_SOURCE_ID_PREFIX = "optivo.optin.id.";

            String OPTIVO_DEFAULT_SUFFIX = "default";
        }

        interface Marketing {
            String COMMUNICATION_CATEGORY_ID = "ymkt.category.id.";

            String NPS_COMMUNICATION_CATEGORY_ID = "ymkt.nps.category.id.";

            String DEFAULT_SUFFIX = "default";

            String COMMUNICATION_CATEGORY_NEWSLETTER = "newsletter";

            String KNOW_HOW_CATEGORY_NAME = "ymkt.category.knowhow.id";

            String PROFILING_CATEGORY_NAME = "ymkt.category.profiling.id";

            String PERSONALISATION_CATEGORY_NAME = "ymkt.category.personalisation.id";

            String SALES_CLEARANCE_CATEGORY_NAME = "ymkt.category.salesandclearance.id";

            String CUSTOMER_SURVEY_CATEGORY_NAME = "ymkt.category.customersurvey.id";

            String PERSONALISE_RECOMMENDATION_CATEGORY_NAME = "ymkt.category.personaliserecommendation.id";

            String NEWSLETTER_DEFAULT_CATEGORY = "ymkt.category.id.newsletter.default";

            String SMS_CATEGORY_NAME = "ymkt.category.sms.name";

            String EMAIL_CATEGORY_NAME = "ymkt.category.email.name";

            String POST_CATEGORY_NAME = "ymkt.category.post.name";

            String PHONE_CATEGORY_NAME = "ymkt.category.phone.name";
        }

        interface Import {
            String FILE_NAME_LANGUAGE_PATTERN = "import.pim.fileName.languagePattern";

            String MASTER_IMPORT_LANGUAGE = "import.pim.masterImport.language";

            String IGNORE_ROOT_ATTRIBUTES = "import.pim.ignoreRootAttributes";

            String PRODUCT_CATALOG_ID = "import.pim.productCatalog.id";

            String PRODUCT_CATALOG_VERSION = "import.pim.productCatalogVersion.version";

            String PRODUCT_CATALOG_PLUS_ID = "import.catplus.productCatalogPlus.id";

            String PRODUCT_CATALOG_PLUS_VERSION = "import.catplus.productCatalogPlusVersion.version";

            String CLASSIFICATION_SYSTEM_ID = "import.pim.classificationSystem.id";

            String CLASSIFICATION_SYSTEM_VERSION = "import.pim.classificationSystemVersion.version";

            String CATEGORY_CODE_PREFIX = "import.pim.category.code.prefix";

            String CATEGORY_CODE_SUFFIX = "import.pim.category.code.suffix";

            String CLASSIFICATION_CLASS_CODE_PREFIX = "import.pim.classificationClass.code.prefix";

            String ROOT_CLASSIFICATION_CLASS_CODE = "import.pim.root.classificationClass.code";

            String ROOT_CATEGORY_PARENT_IDS = "import.pim.root.category.parentIds";

            String CATEGORY_ALLOWED_USER_GROUP = "import.pim.category.allowed.userGroup";

            String IMPORT_PRODUCTS_OF_WHITELISTED_CATEGORIES_ONLY = "import.pim.importProductsOfWhitelistedCategoriesOnly";

            String CANCELLED = "import.pim.cancelled";

            String USE_DYNAMIC_ROOT_ATTRIBUTES = "import.pim.useDynamicRootAttributes";

            String DELETE_EMPTY_CATEGORIES = "import.pim.deleteEmptyCategories";

            String IMPORT_PIM_NOTIFICATION_EMAIL = "import.pim.notification.emailaddress";

            String IMPORT_PIM_TAXONOMY_CATEGORY_SORTING_INDEX_INC = "pim.taxonomy.category.sorting.increment";
        }

        interface FactFinder {
            String CREATE_EXECUTION_PLAN = "distrelecfactfindersearch.create.execution.plan";

            String EXPORT_UPLOAD_DIRECTORY = "distrelecfactfindersearch.export.upload.directory";

            String EXPORT_DIRECTORY_PREFIX = "distrelecfactfindersearch.export.";

            String EXPORT_DIRECTORY_SUFFIX = ".folder";

            String WEBSERVICE_USER = "distrelecfactfindersearch.factfinder.webservice.user";

            String WEBSERVICE_PASSWORD = "distrelecfactfindersearch.factfinder.webservice.password";

            String IMPORTER_USER = "distrelecfactfindersearch.factfinder.importer.user";

            String IMPORTER_PASSWORD = "distrelecfactfindersearch.factfinder.importer.password";

            String TRIGGER_IMPORT = "distrelecfactfindersearch.factfinder.import.triggerImport";

            String EXPORT_UPLOAD_VIA_SCP = "distrelecfactfindersearch.export.upload.scp";

            String EXPORT_UPLOAD_SCP_USER = "distrelecfactfindersearch.export.upload.scp.user";

            String EXPORT_UPLOAD_SCP_HOST = "distrelecfactfindersearch.export.upload.scp.host";

            String EXPORT_UPLOAD_SCP_PORT = "distrelecfactfindersearch.export.upload.scp.port";

            String EXPORT_UPLOAD_SCP_FOLDER = "distrelecfactfindersearch.export.upload.scp.folder";

            String EXPORT_UPLOAD_SCP_PRIVATE_KEY = "distrelecfactfindersearch.export.upload.scp.privatekey";

            String EXPORT_UPLOAD_SCP_KEYPASSWORD = "distrelecfactfindersearch.export.upload.scp.keypassword";

            String BATCH_SIZE = "distrelecfactfindersearch.export.batchSize";

            String QUERY_TIMEOUT = "distrelecfactfindersearch.export.queryTimeout";

            String MAX_TASKS_NUM = "distrelecfactfindersearch.export.maxTasksNum";

            String THREAD_POOL_SIZE = "distrelecfactfindersearch.export.threadPoolSize";
        }

        interface Environment {
            String ENVIRONMENT_KEY = "environment.current";

            String LOCAL_ENV_DEVELOPMENT = "env-development";

            String DEV_ENV_PREFIX = "env-hp-d";

            String LIVE_ENV_PREFIX = "env-hp-p";

            String TEST_ENV_PREFIX = "env-hp-q";

            String YMS_HOSTNAME = "cluster.broadcast.method.jgroups.tcp.bind_addr";

            String INTERNAL_IPS = "internal.ips";

            String INTERNAL_USER = "internal.user";

            String TEST_MEDIA_DOMAIN_HTTPS = "https://test.media.distrelec.com";

            String BUILD_DATE = "build.builddate";

            String AZURE_HOT_FOLDER_BLOB_CONNECTION_STRING_CONFIG = "azure.hotfolder.storage.account.connection-string";
        }

        interface Reevoo {
            String EXPORT_UPLOAD_SCP_USER = "reevoo.export.upload.scp.user";

            String EXPORT_UPLOAD_SCP_HOST = "reevoo.export.upload.scp.host";

            String EXPORT_UPLOAD_SCP_PORT = "reevoo.export.upload.scp.port";

            String EXPORT_UPLOAD_SCP_PRIVATEKEY = "reevoo.export.upload.scp.privatekey";

            String EXPORT_UPLOAD_SCP_KEYPASSWORD = "reevoo.export.upload.scp.keypassword";

            String TRKREF_ID = "trkrefid";

            String EXPORT_UPLOAD_PATH_PREFIX = "reevoo.export.path.prefix";
        }

        interface Sap {
            String EXPORT_UPLOAD_SCP_USER = "sap.export.upload.scp.user";

            String EXPORT_UPLOAD_SCP_HOST = "sap.export.upload.scp.host";

            String EXPORT_UPLOAD_SCP_PORT = "sap.export.upload.scp.port";

            String EXPORT_UPLOAD_SCP_PRIVATEKEY = "sap.export.upload.scp.privatekey";

            String EXPORT_UPLOAD_SCP_KEYPASSWORD = "sap.export.upload.scp.keypassword";

            String EXPORT_UPLOAD_SCP_FOLDER = "sap.export.upload.scp.directory";

            String PRICE_EXPORT_MEDIA_PREFIX = "sap.price.export.upload.scp.mediaPrefix";

            String PARCEL_LAB_EXPORT_UPLOAD_SCP_FOLDER = "sap.parcel.lab.export.upload.scp.directory";

            String PARCEL_LAB_PRODUCT_EXPORT_MEDIA_PREFIX = "sap.parcel.lab.product.export.upload.scp.mediaPrefix";
        }

        interface Breadcrumb {
            String CATALOG_PLUS_LINK = "breadcrumb.catalogPlus.link.";
        }

        interface Punchout {
            String PUNCHOUTLOGIC = "switchoff.punchout.logic";
        }

        interface User {
            String SKIP_PRICE = "price.usd.display.skip.logic";
        }

        interface Category {
            String DEACTIVATIONLOGIC = "switchoff.categorydeactivating.logic";
        }

        interface NetPromoterScore {
            String EXPORT_DIRECTORY_PROPERTY = "erp.nps.export.directory";
        }

        interface ContentSecurityPolicy {
            String EXPORT_DIRECTORY_PROPERTY = "csp.report.export.directory";
        }

    }

    interface Processes {
        String DIST_PLACE_ORDER = "distPlaceOrder";
    }

    interface Ariba {
        String URL_PARAM_KEY_USERNAME = "username";

        String URL_PARAM_KEY_PASSWORD = "password";

        String URL_PARAM_KEY_TOKEN = "customerToken";

        String URL_PARAM_KEY_PAYLOADID = "payloadID";

        String URL_PARAM_KEY_NETWORK_ID = "NetworkId";

        String URL_PARAM_KEY_ARIBA_NETWORK_ID = "AribaNetworkUserId";

        interface Session {
            String BROWSER_FORM_POST = "browserFormPost";

            String ARIBA_CART_NOT_LOADED = "aribaCartNotLoaded";

            String ARIBA_CUSTOMER_SETUP_REQUEST = "aribaCustomerSetupRequest";

            String ARIBA_MULTI_COUNTRY_CUSTOMER = "aribaMutiCountryCustomer";
        }

        interface SetupRequestParams {
            String EDIT_CART = "EditCart";

            String BUYER_COOKIE = "BuyerCookie";

            String BROWSER_FORM_POST = "BrowserFormPost";

            String PRODUCT_CODE = "ProductCode";

            String CART_CODE = "CartCode";

            String LANG_CODE = "Lang";
        }
    }

    interface Webtrekk {
        String TEASER_TRACKING_FAF_ONSITE = "faf-s";

        String TEASER_TRACKING_FAF_RECO = "faf-r";

        String TEASER_TRACKING_FAF_CAMPAIGN = "faf-c-f";

        String TEASER_TRACKING_ONS = "ons";
    }

    interface Product {
        String ATTRIBUTE_CODE_ORDER_NOTE = "5382";

        String ATTRIBUTE_CODE_ORDER_NOTE_ARTICLE = "BestellhinweisArtikel_text";

        String ATTRIBUTE_CODE_DELIVERY_NOTE = "5334";

        String ATTRIBUTE_CODE_DELIVERY_NOTE_ARTICLE = "LieferhinweisArtikel_text";

        String ATTRIBUTE_CODE_ASSEMBLY_NOTE = "Montagehinweis_text";

        String ATTRIBUTE_CODE_USAGE_NOTE = "5383";

        String ATTRIBUTE_CODE_FAMILY_DESCRIPTION = "5321";

        String ATTRIBUTE_CODE_FAMILY_DESCRIPTION_BULLET = "5322";

        String ATTRIBUTE_CODE_SERIES_DESCRIPTION = "5323";

        String ATTRIBUTE_CODE_SERIES_DESCRIPTION_BULLET = "5324";

        String ATTRIBUTE_CODE_ARTICLE_DESCRIPTION = "5325";

        String ATTRIBUTE_CODE_ARTICLE_DESCRIPTION_BULLET = "5326";

        String ATTRIBUTE_MAX_NAME_LENGTH = "180";

        String PRODUCT_CODE_REGEX = "^\\d{8}$";

        String WALDOM = "BTR";

        String RS = "RSP";

        interface Availability {
            String STATUS_GREEN = "green";

            String STATUS_LGREEN = "lgreen";

            String STATUS_YELLOW = "yellow";

            String STATUS_ORANGE = "orange";
        }

        interface ReplacementReasonCode {
            String CALIBRATED_INSTRUMENTS = "G_A";

            String NOT_CALIBRATED_INSTRUMENTS = "G_B";
        }
    }

    interface PromotionLabels {
        String USED = "used";

        String OFFER = "offer";

        String CALIBRATION_SERVICE = "calibrationService";

        String BESTSELLER = "bestseller";
    }

    interface PromotionLabelRanking {
        int HOT_OFFER = 1;

        int OFFER = 5;

        int NEW = 7;

        int OUTLET = 8;
    }

    interface Catalog {
        String DISTRELEC_PRODUCT_CATALOG_ID = "distrelecProductCatalog";

        String CATALOG_PLUS_ID = "distrelecCatalogPlusProductCatalog";

        String DEFAULT_CATALOG_ID = "Default";
    }

    interface CatalogVersion {
        String STAGED = "Staged";

        String ONLINE = "Online";
    }

    interface CustomerDataUpdate {
        String CUSTOMER_EMAIL_UPDATE = "customer_email_update";

        String CUSTOMER_PASSWORD_UPDATE = "customer_password_update";

        String CUSTOMER_PASSWORD_RESET = "customer_password_reset";
    }

    interface Marketing {
        String RECORD_NOT_FOUND = "NOT_FOUND";

        String ERROR_FETCHING_DETAILS = "ERROR_FETCHING_DETAILS";

        String ZDIS_SUBSCRIPTION_PA = "ZDIS_SUBSCRIPTION_PA";

        String CONTACT_ORIGIN_HYBRIS = "SAP_HYBRIS_CONSUMER";

        String CONTACT_PERMISSION_ORIGIN_EMAIL = "EMAIL";

        String COMMUNICATION_MEDIUM_EMAIL = "EMAIL";

        String COMMUNICATION_MEDIUM_PHONE = "PHONE";

        String COMMUNICATION_MEDIUM_SMS = "SMS";

        String COMMUNICATION_MEDIUM_WEB = "WEB";

        String COMMUNICATION_MEDIUM_POST = "PAPER";

        String CONTACT_PERMISSION_ORIGIN_PHONE = "PHONE";

        String CONTACT_PERMISSION_ORIGIN_MOBILE = "MOBILE";

        String SAP_ERP_CONTACT = "SAP_ERP_CONTACT";

        String MARKETING_AREA = "DISTRELEC";

        String INTEGRATION_CONTACT_MARKETING_AREA = "DISTRELEC";

        interface Permission {
            String YES = "Y";

            String NO = "N";
        }

        interface ConfigKeys {
            String FORCE_SYNCHRONIZATION = "ymkt.customer.sevice.force.synchronization";

            String ENABLED_FORCE_SYNCHRONIZATION = "ymkt.customer.sevice.synchronization.enabled";

            String COMMUNICATION_CATEGORY = "N";

            String DOUBLE_OPT_IN = "doubleoptIn";
        }
    }

    interface Quote {
        interface Status {
            String FAILED = "FAILED";

            String SUCCESSFUL = "SUCCESSFUL";

            String ERROR = "ERROR";

            String LIMIT_EXCEEDED = "LIMIT_EXCEEDED";
        }

        interface UrlConstructs {
            String RESUBMIT_QUOTATION_PATH = "/request-quotation/resubmit-quotation/";

            String CHILD_QUOTE_DETAILS_PAGE_PATH = "/my-account/quote-history/quote-details/";
        }
    }

    interface UrlTags {
        String PRODUCT_FAMILY = "pf";
    }

    interface SalesOrg {
        String SALES_ORG_7650 = "7650";
    }

    interface CountryIsoCode {

        String GERMANY = "DE";

        String GREAT_BRITAIN = "GB";

        String GREECE = "GR";

        String NORTHERN_IRELAND = "XI";

        String SWITZERLAND = "CH";

        String LIECHTENSTEIN = "LI";

        String ITALY = "IT";

        String VATICAN = "VA";

        String SAN_MARINO = "SM";

        String FRANCE = "FR";

        String BELGIUM = "BE";

        String NETHERLANDS = "NL";

        String EXPORT = "EX";
    }

    interface Headless {
        String HEADLESS_CART = "headless-cart";
    }

    interface UTMParams {

        String UTM_SOURCE = "&utm_source=";

        String UTM_MEDIUM = "&utm_medium=";

        String UTM_CAMPAIGN = "&utm_campaign=";

        String UTM_TERM = "&utm_term=";
    }
}
