/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.distrelec.webservice.sap.v1.InvoiceStatus;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.enums.QuoteStatus;
import com.namics.distrelec.b2b.core.inout.erp.RMAService;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.obsolescence.service.DistObsolescenceService;
import com.namics.distrelec.b2b.core.rma.CreateRMAOrderEntryDataForm;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.rma.ReturnRequestData;
import com.namics.distrelec.b2b.core.rma.ReturnRequestEntryData;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.distrelec.b2b.core.service.search.pagedata.QuotationHistoryPageableData;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Product;
import com.namics.distrelec.b2b.facades.bomtool.DistBomToolFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.export.DistExportFacade;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.order.OrderCsvUtil;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.invoice.InvoiceHistoryFacade;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationHistoryData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import com.namics.distrelec.b2b.facades.rma.DistReturnRequestFacade;
import com.namics.distrelec.b2b.facades.rma.data.DistRMAPackagingData;
import com.namics.distrelec.b2b.facades.rma.data.DistRMAReasonData;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.facades.user.data.DistConsentStatus;
import com.namics.distrelec.b2b.facades.user.data.DistMarketingConsentData;
import com.namics.distrelec.b2b.facades.user.data.ObsolescenceTempData;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.AccountBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.AddressFormHelper;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.controllers.util.WritePDFDocumentsUtil;
import com.namics.distrelec.b2b.storefront.forms.*;
import com.namics.distrelec.b2b.storefront.forms.util.SelectOption;
import com.namics.distrelec.b2b.storefront.response.ObsolescenceTempWrapper;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BOrderApprovalData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.commercefacades.user.exceptions.PasswordMismatchException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.exceptions.WorkflowActionDecideException;

/**
 * Controller for home page.
 */
@Controller
@RequestMapping("/my-account")
public class AccountPageController extends AbstractSearchPageController {

    private static final Logger LOG = LogManager.getLogger(AccountPageController.class);

    private static final PhoneNumberUtil PHONENUMBERUTIL = PhoneNumberUtil.getInstance();

    /**
     * Address types
     *
     * @author dathusir, Distrelec
     * @since Distrelec 1.0
     */
    public enum AddressType {
        b2bshipping,
        b2bbilling,
        b2c
    }

    // URLs
    private static final String MY_ACCOUNT_PAGE_URL = "/my-account";

    // Redirects
    public static final String REDIRECT_MY_ACCOUNT = REDIRECT_PREFIX + "/my-account";

    public static final String REDIRECT_TO_LOGIN_DATA_PAGE = addFasterizeCacheControlParameter(REDIRECT_MY_ACCOUNT + "/my-account-information");

    public static final String REDIRECT_TO_COMPANY_INFO_PAGE = addFasterizeCacheControlParameter(REDIRECT_MY_ACCOUNT + "/company/information");

    public static final String REDIRECT_TO_ADDRESS_PAGE = addFasterizeCacheControlParameter(REDIRECT_MY_ACCOUNT + "/addresses");

    public static final String REDIRECT_TO_PAYMENT_INFO_PAGE = addFasterizeCacheControlParameter(REDIRECT_MY_ACCOUNT + "/payment-and-delivery-options");

    public static final String REDIRECT_TO_SAVE_BOMTOOL_ENTRIES = addFasterizeCacheControlParameter(REDIRECT_MY_ACCOUNT + "/savedBomEntries");

    // Anchors
    public static final String ANCHOR_NEWSLETTER_FORM = "#updateNewsletterForm";

    /**
     * We use this suffix pattern because of an issue with Spring 3.1 where a Uri value is incorrectly extracted if it contains on or more
     * '.' characters. Please see https://jira.springsource.org/browse/SPR-6164 for a discussion on the issue and future resolution.
     */
    private static final String ADDRESS_CODE_PATH_VARIABLE_PATTERN = "{addressCode:.*}";

    private static final String WORKFLOW_ACTION_CODE_PATH_VARIABLE_PATTERN = "{workflowActionCode:.*}";

    private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";

    // CMS Pages
    private static final String LOGIN_DATA_CMS_PAGE = "login-data";

    private static final String ADDRESSES_CMS_PAGE = "addresses";

    private static final String ADD_EDIT_ADDRESS_CMS_PAGE = "add-edit-address";

    private static final String PAYMENT_DELIVERY_OPTIONS_CMS_PAGE = "payment-and-delivery-options";

    private static final String ORDER_HISTORY_CMS_PAGE = "order-history";

    private static final String QUOTATION_HISTORY_CMS_PAGE = "quotation-history";

    private static final String QUOTATION_DETAILS_CMS_PAGE = "quotation-details";

    private static final String OPEN_ORDER_HISTORY_CMS_PAGE = "open-orders";

    private static final String OPEN_ORDER_DETAIL_CMS_PAGE = "open-order-details";

    private static final String INVOICE_HISTORY_CMS_PAGE = "invoice-history";

    private static final String ORDER_DETAIL_CMS_PAGE = "order-details";

    private static final String RETURN_ITEMS_CMS_PAGE = "return-items";

    private static final String RETURN_ITEMS_CONFIRMATION_CMS_PAGE = "return-items-confirmation";

    private static final String ORDER_APPROVAL_CMS_PAGE = "order-approval";

    private static final String MARKETING_PREFRENCE_CMS_PAGE = "preference-center";

    // Order history
    private static final String ORDER_HISTORY_PAGE_URL = "/order-history";

    private static final String ORDER_HISTORY_NEXT_PAGE_URL = ORDER_HISTORY_PAGE_URL + "/next";

    private static final String ORDER_HISTORY_DETAILS_PAGE_URL = ORDER_HISTORY_PAGE_URL + "/order-details/" + ORDER_CODE_PATH_VARIABLE_PATTERN;

    private static final String ORDER_HISTORY_RETURN_ITEMS_PAGE_URL = ORDER_HISTORY_DETAILS_PAGE_URL + "/return-items";

    private static final String ORDER_HISTORY_DETAILS_DOWNLOAD_URL = ORDER_HISTORY_DETAILS_PAGE_URL + "/download/{exportFormat:.*}/*";

    private static final List<String> ORDER_HISTORY_SORT_TYPE_LIST = Collections.unmodifiableList(Arrays.asList("byDate:asc", "byDate:desc",
                                                                                                                "byStatus:asc", "byStatus:desc",
                                                                                                                "byTotalPrice:asc",
                                                                                                                "byTotalPrice:desc"));

    private static final List<String> ORDER_HISTORY_SORT_TYPE_LIST_MIN = Collections.unmodifiableList(Arrays.asList("byDate", "byStatus",
                                                                                                                    "byTotalPrice"));

    private static final List<OrderStatus> ORDER_STATUS_LIST = Collections.unmodifiableList(Arrays.asList(/* OrderStatus.OPEN, */ OrderStatus.ERP_STATUS_RECIEVED,
                                                                                                          OrderStatus.ERP_STATUS_IN_PROGRESS,
                                                                                                          OrderStatus.ERP_STATUS_PARTIALLY_SHIPPED,
                                                                                                          OrderStatus.ERP_STATUS_SHIPPED,
                                                                                                          OrderStatus.ERP_STATUS_CANCELLED));

    // Open Order history
    private static final String OPEN_ORDER_HISTORY_PAGE_URL = "/open-orders";

    private static final String OPEN_ORDER_HISTORY_NEXT_PAGE_URL = OPEN_ORDER_HISTORY_PAGE_URL + "/next";

    private static final String OPEN_ORDER_HISTORY_DETAILS_PAGE_URL = OPEN_ORDER_HISTORY_PAGE_URL + "/open-order-details/" + ORDER_CODE_PATH_VARIABLE_PATTERN;

    private static final List<String> OPEN_ORDER_HISTORY_SORT_TYPE_LIST = Collections.unmodifiableList(Arrays.asList("byDate:asc", "byDate:desc",
                                                                                                                     "byStatus:asc",
                                                                                                                     "byTotalPrice:asc",
                                                                                                                     "byTotalPrice:desc"));

    // Quote History
    private static final String QUOTATION_HISTORY_PAGE_URL = "/quote-history";

    private static final String QUOTATION_HISTORY_NEXT_PAGE_URL = QUOTATION_HISTORY_PAGE_URL + "/next";

    private static final String QUOTATION_CODE_PATH_VARIABLE_PATTERN = "{quotationCode:.*}";

    private static final String QUOTATION_HISTORY_DETAIL_PAGE_URL = QUOTATION_HISTORY_PAGE_URL + "/quote-details/" + QUOTATION_CODE_PATH_VARIABLE_PATTERN;

    private static final List<QuoteStatus> QUOTE_STATUS_LIST = Collections.unmodifiableList(Arrays.asList(QuoteStatus.INPROGRESS,
                                                                                                          QuoteStatus.OFFERED,
                                                                                                          QuoteStatus.ORDERED,
                                                                                                          QuoteStatus.EXPIRED));

    private static final List<String> QUOTATION_HISTORY_SORT_TYPE_LIST = Collections.unmodifiableList(Arrays.asList("byPONumber:asc",
                                                                                                                    "byPONumber:desc",
                                                                                                                    "byRequestDate:asc",
                                                                                                                    "byRequestDate:desc",
                                                                                                                    "byExpiryDate:asc",
                                                                                                                    "byExpiryDate:desc",
                                                                                                                    "byStatus:asc",
                                                                                                                    "byStatus:desc",
                                                                                                                    "byTotalPrice:asc",
                                                                                                                    "byTotalPrice:desc"));

    private static final List<String> QUOTATION_HISTORY_SORT_TYPE_LIST_MIN = Collections.unmodifiableList(Arrays.asList("byPONumber",
                                                                                                                        "byRequestDate",
                                                                                                                        "byExpiryDate",
                                                                                                                        "byStatus",
                                                                                                                        "byTotalPrice"));

    // Order approval dashboard (B2B ADMIN)
    private static final String ORDER_APPROVAL_PAGE_URL = "/order-approval";

    private static final String REDIRECT_ORDER_APPROVAL_PAGE_URL = addFasterizeCacheControlParameter(REDIRECT_MY_ACCOUNT + ORDER_APPROVAL_PAGE_URL);

    private static final String ORDER_APPROVAL_DETAILS_PAGE_URL = ORDER_APPROVAL_PAGE_URL + "/order-details/" + ORDER_CODE_PATH_VARIABLE_PATTERN + "/workflow/"
                                                                  + WORKFLOW_ACTION_CODE_PATH_VARIABLE_PATTERN;

    private static final String ORDER_APPROVAL_DECISION_URL = ORDER_APPROVAL_PAGE_URL + "/approval-decision";

    private static final String REDIRECT_ORDER_APPROVAL_ORDER_CONFIRMATION_PAGE_URL = REDIRECT_PREFIX + "/checkout/orderConfirmation";

    // Order approval requests (B2B customer)
    private static final String ORDER_APPROVAL_REQUESTS_PAGE_URL = "/order-approval-requests";

    private static final String ORDER_APPROVAL_REQUEST_DETAILS_PAGE_URL = ORDER_APPROVAL_REQUESTS_PAGE_URL + "/order-details/"
                                                                          + ORDER_CODE_PATH_VARIABLE_PATTERN + "/workflow/"
                                                                          + WORKFLOW_ACTION_CODE_PATH_VARIABLE_PATTERN;

    private static final String ORDER_APPROVAL_ORDER_REFERENCE_UPDATE_URL = ORDER_APPROVAL_DECISION_URL + "/order-detail/update/order-reference";

    // Invoice history
    private static final String INVOICE_HISTORY_PAGE_URL = "/invoice-history";

    private static final String INVOICE_HISTORY_NEXT_PAGE_URL = INVOICE_HISTORY_PAGE_URL + "/next";

    private static final String SHOW_INVOICE_HISTORY_DOCUMENT_URL = INVOICE_HISTORY_PAGE_URL + "/invoice-document-url";

    private static final List<String> INVOICE_HISTORY_SORT_TYPE_LIST = Collections.unmodifiableList(Arrays.asList("byDate:asc", "byDate:desc",
                                                                                                                  "byDueDate:asc",
                                                                                                                  "byDueDate:desc",
                                                                                                                  "byStatus:asc",
                                                                                                                  "byStatus:desc",
                                                                                                                  "byTotalPrice:asc",
                                                                                                                  "byTotalPrice:desc"));

    private static final List<String> INVOICE_HISTORY_SORT_TYPE_LIST_MIN = Collections.unmodifiableList(Arrays.asList("byDate", "byDueDate",
                                                                                                                      "byStatus",
                                                                                                                      "byTotalPrice"));

    private static final List<String> INVOICE_STATUS_LIST = Collections.unmodifiableList(Arrays.asList(InvoiceStatus.OPEN.name(),
                                                                                                       InvoiceStatus.PAID.name()));

    // Addresses
    private static final List<String> ADRESSES_B2C_SORT_TYPE_LIST = Collections.unmodifiableList(Arrays.asList("byCity", "byName"));

    private static final List<String> ADRESSES_B2B_SORT_TYPE_LIST = Collections.unmodifiableList(Arrays.asList("byCity", "byName",
                                                                                                               "byCompany"));

    private static final String CREDIT_CARD_PAYMENT_MODE = "CreditCard";

    private static final String SHIPPING_MODE = "shippingMode";

    private static final String RMA_RETURN_CODES_WITH_ASSISTANCE_CONIG = "rma.reasonCodesWithAssistance";

    private static final String SAVED_BOM_TOOL_ENTRIES_CMS_PAGE = "savedBomEntries";

    private static final String INVOICE_HISTORY_DEFAULT_DATE_PATTERN = "MM/dd/yyyy";

    private String defaultTitle = "mr_and_ms";

    @Resource(name = "erp.orderHistoryFacade")
    private OrderHistoryFacade orderHistoryFacade;

    @Resource(name = "erp.invoiceHistoryFacade")
    private InvoiceHistoryFacade invoiceHistoryFacade;

    @Resource(name = "b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Resource(name = "userFacade")
    private DistUserFacade userFacade;

    @Resource(name = "distReturnRequestFacade")
    private DistReturnRequestFacade returnRequestFacade;

    @Resource(name = "accountBreadcrumbBuilder")
    private AccountBreadcrumbBuilder accountBreadcrumbBuilder;

    @Resource(name = "distCodelistService")
    private DistrelecCodelistService codelistService;

    @Resource(name = "erp.rmaService")
    private RMAService rmaService;

    @Resource(name = "distRMAReasonConverter")
    private Converter<DistCodelistModel, DistRMAReasonData> distRMAReasonConverter;

    @Resource(name = "distRMAPackagingConverter")
    private Converter<DistCodelistModel, DistRMAPackagingData> distRMAPackagingConverter;

    @Resource
    private DistNewsletterFacade newsletterFacade;

    @Resource(name = "eventService")
    private EventService eventService;

    @Resource(name = "userService")
    private UserService userService;

    @Autowired
    private DistExportFacade distExportFacade;

    @Autowired
    private ProductService productService;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private Validator validator;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade cartFacade;

    @Autowired
    private DistObsolescenceService distObsolescenceService;

    @Autowired
    private DistBomToolFacade distBomToolFacade;

    @Autowired
    private SessionService sessionService;

    @ModelAttribute("allowedToPlaceOpenOrders")
    public boolean isAllowedToPlaceOpenOrders() {
        return !getUserService().isAnonymousUser(getUserService().getCurrentUser())
                && getB2bCustomerFacade().getCurrentCustomer().isAllowedToPlaceOpenOrders();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String account() throws CMSItemNotFoundException {
        final CustomerData currentCustomer = getB2bCustomerFacade().getCurrentCustomer();
        if (CustomerType.B2C.equals(currentCustomer.getCustomerType())) {
            return REDIRECT_TO_LOGIN_DATA_PAGE;
        } else {
            return REDIRECT_TO_COMPANY_INFO_PAGE;
        }
    }

    @RequestMapping(value = "/savedBomEntries", method = RequestMethod.GET)
    public String savedBomEntries(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        String resourceENName = getMessageSource().getMessage("text.savebomuploads", null, getI18nService().getCurrentLocale());
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, simpleBreadcrumbBuilder.getBreadcrumbs(resourceENName, resourceENName));
        final ContentPageModel uploadPage = getContentPageForLabelOrId(SAVED_BOM_TOOL_ENTRIES_CMS_PAGE);
        storeCmsPageInModel(model, uploadPage);
        setMetaRobots(model, uploadPage);
        model.addAttribute("bomEntries", distBomToolFacade.getSavedBomToolEntries());
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.BOMTOOL);
        return ControllerConstants.Views.Pages.Account.SavedBomToolPage;
    }

    @RequestMapping(value = "/my-account-information", method = RequestMethod.GET)
    public String loginData(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {

        prepareFormData(model);
        ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(LOGIN_DATA_CMS_PAGE);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);
        final String updateMessage = getSessionService().getAttribute(WebConstants.UPDATE_SUCCESS);
        if (!StringUtils.isBlank(updateMessage)) {
            getSessionService().removeAttribute(WebConstants.UPDATE_SUCCESS);
            model.addAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList(updateMessage));
        }

        final String emailSuccess = getSessionService().getAttribute(WebConstants.EMAIL_UPDATE_SUCCESS);
        if (!StringUtils.isBlank(emailSuccess)) {
            getSessionService().removeAttribute(WebConstants.EMAIL_UPDATE_SUCCESS);
            model.addAttribute("emailChangeStatus", "success");
        }
        final String emailToUnsubscribe = getSessionService().getAttribute(ContentPageController.EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE);
        if (!StringUtils.isBlank(emailToUnsubscribe)) {
            getSessionService().removeAttribute(ContentPageController.EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE);
            model.addAttribute(ContentPageController.EMAIL_PARAMETER_NAME_FOR_UNSUBSCRIBE, emailToUnsubscribe);
        }
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.preferences.account.info"));

        model.addAttribute("divisions", newsletterFacade.getDivisions());
        model.addAttribute("roles", newsletterFacade.getRoles());
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountLoginDataPage;
    }

    @RequestMapping(value = "/preference-center", method = RequestMethod.GET)
    public String prefrenceCenter(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        prepareFormData(model);
        ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(MARKETING_PREFRENCE_CMS_PAGE);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        DistMarketingConsentData marketingProfileData = new DistMarketingConsentData();
        marketingProfileData.setStatus(DistConsentStatus.ERROR_FETCHING_DETAILS);
        model.addAttribute("marketingProfileData", marketingProfileData);
        model.addAttribute("optedForObsolescence", customerData.isOptedForObsolescence());
        model.addAttribute("allCatSelected", customerData.isAllObsolCatSelected());
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.preferences.communication"));
        model.addAttribute("categories", customerData.getCategories());
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountPrefrenceCenterPage;
    }

    @RequestMapping(value = "/preference-center", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean prefrenceCenter(@RequestBody final ConsentProfileForm consentForm, final Model model,
                                   final HttpServletRequest request) throws CMSItemNotFoundException {
        DistMarketingConsentData marketingConsent = new DistMarketingConsentData();
        marketingConsent.setEmailConsent(consentForm.isEmailConsent());
        marketingConsent.setPhoneConsent(consentForm.isPhoneConsent());
        marketingConsent.setSmsConsent(consentForm.isSmsConsent());
        marketingConsent.setPostConsent(consentForm.isPostConsent());
        marketingConsent.setKnowHowConsent(consentForm.isKnowHowConsent());
        marketingConsent.setCustomerSurveysConsent(consentForm.isCustomerSurveysConsent());
        marketingConsent.setPersonalisedRecommendationConsent(consentForm.isPersonalisedRecommendationConsent());
        marketingConsent.setObsolescenceConsent(consentForm.isKnowHowConsent());
        marketingConsent.setSaleAndClearanceConsent(consentForm.isSaleAndClearanceConsent());
        marketingConsent.setProfilingConsent(consentForm.isProfilingConsent());
        marketingConsent.setPersonalisationConsent(consentForm.isPersonalisationConsent());
        marketingConsent.setSelectAllemailConsents(consentForm.isSelectAllemailConsents());
        marketingConsent.setNewsLetterConsent(consentForm.isNewsLetterConsent());
        marketingConsent.setTermsAndConditionsConsent(consentForm.isTermsAndConditionsConsent());
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        marketingConsent.setUid(customerData.getEmail());
        marketingConsent.setPhoneNumber(customerData.getContactAddress().getPhone());
        marketingConsent.setMobileNumber(customerData.getContactAddress().getCellphone());
        if (CollectionUtils.isNotEmpty(consentForm.getObsoleCategories())) {
            List<ObsolescenceTempData> obsoleData = null;
            try {
                if (consentForm.getObsoleCategories() != null) {
                    ObsolescenceTempWrapper obsoleCategories = new ObsolescenceTempWrapper();
                    obsoleCategories.setObsoleCategories(consentForm.getObsoleCategories());
                    obsoleData = distObsolescenceService.changeObsolPreference(obsoleCategories.getObsoleCategories());
                    GlobalMessages.addInfoMessage(model, "text.account.loginData.confirmationUpdated");
                }
            } catch (final Exception e) {
                logError(LOG, "{} Can not update user profile for the customer with Email: {}", e, ErrorLogCode.UPDATE_USER_ERROR.getCode(),
                         customerData.getUid());
                GlobalMessages.addErrorMessage(model, "form.global.error");
            }
        }
        return Boolean.FALSE;
    }

    @RequestMapping(value = "/changeObsolPreference", method = { RequestMethod.GET,
                                                                 RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ObsolescenceTempData> changeObsolPreference(@RequestBody ObsolescenceTempWrapper obsoleCategories, final RedirectAttributes redirectAttributes,
                                                            final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        String returnAction = ControllerConstants.Views.Pages.Account.AccountLoginDataPage;
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        List<ObsolescenceTempData> obsoleData = null;
        try {
            obsoleData = distObsolescenceService.changeObsolPreference(obsoleCategories.getObsoleCategories());
            GlobalMessages.addInfoMessage(model, "text.account.loginData.confirmationUpdated");

        } catch (final Exception e) {
            logError(LOG, "{} Can not update user profile for the customer with Email: {}", e, ErrorLogCode.UPDATE_USER_ERROR.getCode(), customerData.getUid());
            GlobalMessages.addErrorMessage(model, "form.global.error");
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        addGlobalModelAttributes(model, request);
        return obsoleData;

    }

    @RequestMapping(value = "/update-profile", method = RequestMethod.POST)
    public String updateNewProfile(@Valid final UpdateProfileForm updateProfileForm, final BindingResult bindingResult,
                                   final Model model, final RedirectAttributes redirectAttributes,
                                   final HttpServletRequest request) throws CMSItemNotFoundException, NumberParseException {
        addGlobalModelAttributes(model, request);
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            prepareFormDataForProfileUpdateError(model);
            ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(LOGIN_DATA_CMS_PAGE);
            storeCmsPageInModel(model, contentPageForLabelOrId);
            setUpMetaDataForContentPage(model, contentPageForLabelOrId);
            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.preferences.account.info"));
            model.addAttribute("updateProfileForm", updateProfileForm);

            updateObsolescenceCategoriesOnError(model);
            return ControllerConstants.Views.Pages.Account.AccountLoginDataPage;
        }
        if (!StringUtils.isBlank(updateProfileForm.getPhone())) {
            final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(updateProfileForm.getPhone(), getCurrentCountry().getIsocode());
            updateProfileForm.setPhone(phoneNumberToString(phoneNumber));
        }
        if (!StringUtils.isBlank(updateProfileForm.getMobilePhone())) {
            final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(updateProfileForm.getMobilePhone(), getCurrentCountry().getIsocode());
            updateProfileForm.setMobilePhone(phoneNumberToString(mobileNumber));
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return updateProfile(updateProfileForm, bindingResult, model, redirectAttributes);
    }

    @RequestMapping(value = "/migrated/update-profile", method = RequestMethod.POST)
    public String updateMigratedProfile(@Valid final UpdateMigratedProfileForm updateProfileForm, final BindingResult bindingResult, final Model model,
                                        final RedirectAttributes redirectAttributes,
                                        final HttpServletRequest request) throws CMSItemNotFoundException, NumberParseException {
        if (!StringUtils.isBlank(updateProfileForm.getPhone())) {
            final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(updateProfileForm.getPhone(), getCurrentCountry().getIsocode());
            updateProfileForm.setPhone(phoneNumberToString(phoneNumber));
        }
        if (!StringUtils.isBlank(updateProfileForm.getMobilePhone())) {
            final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(updateProfileForm.getMobilePhone(), getCurrentCountry().getIsocode());
            updateProfileForm.setMobilePhone(phoneNumberToString(mobileNumber));
        }
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return updateProfile(updateProfileForm, bindingResult, model, redirectAttributes);
    }

    /**
     *
     * @param cartCode
     * @param model
     * @param request
     * @param redirectAttributes
     * @param response
     * @return String
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = { "/cart/{cartCode:.*}" }, method = RequestMethod.GET)
    public String showCartById(@PathVariable("cartCode") final String cartCode, final Model model, final HttpServletRequest request,
                               final RedirectAttributes redirectAttributes, final HttpServletResponse response)
                                                                                                                throws CMSItemNotFoundException {
        if (!getCartFacade().allowedToAccessCartWithCode(cartCode)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            throw new UnknownIdentifierException("No cart with code " + cartCode + " exists!");
        }

        return REDIRECT_PREFIX + "/cart";
    }

    /**
     * Update Profile.
     *
     * @param updateProfileForm
     *            the form object
     * @param bindingResult
     *            the binding results
     * @param model
     *            the model
     * @param redirectAttributes
     *            the redirect attributes
     * @return the continue URL
     * @throws CMSItemNotFoundException
     */
    protected String updateProfile(final UpdateProfileForm updateProfileForm, final BindingResult bindingResult, final Model model,
                                   final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        String returnAction = ControllerConstants.Views.Pages.Account.AccountLoginDataPage;
        final boolean migrated = updateProfileForm instanceof UpdateMigratedProfileForm ? true : false;

        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        customerData.setTitleCode(updateProfileForm.getTitleCode());
        customerData.setFirstName(updateProfileForm.getFirstName());
        customerData.setLastName(updateProfileForm.getLastName());
        customerData.setFunctionCode(updateProfileForm.getFunctionCode());

        if (migrated) {
            customerData.setEmail(((UpdateMigratedProfileForm) updateProfileForm).getEmail());
        }

        if (customerData.getContactAddress() != null) {
            final AddressData contactAddress = customerData.getContactAddress();
            contactAddress.setFirstName(updateProfileForm.getFirstName());
            contactAddress.setLastName(updateProfileForm.getLastName());
            contactAddress.setDepartmentCode(updateProfileForm.getDepartmentCode());
            contactAddress.setPhone(updateProfileForm.getPhone());
            contactAddress.setCellphone(updateProfileForm.getMobilePhone());
            contactAddress.setFax(updateProfileForm.getFax());

            if (migrated) {
                contactAddress.setEmail(((UpdateMigratedProfileForm) updateProfileForm).getEmail());
            }

            customerData.setContactAddress(contactAddress);
        } else {
            final AddressData contactAddress = new AddressData();
            contactAddress.setFirstName(updateProfileForm.getFirstName());
            contactAddress.setLastName(updateProfileForm.getLastName());
            contactAddress.setDepartmentCode(updateProfileForm.getDepartmentCode());
            contactAddress.setPhone(updateProfileForm.getPhone());
            contactAddress.setCellphone(updateProfileForm.getMobilePhone());
            contactAddress.setFax(updateProfileForm.getFax());

            if (migrated) {
                contactAddress.setEmail(((UpdateMigratedProfileForm) updateProfileForm).getEmail());
            }

            customerData.setContactAddress(contactAddress);
        }

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            prepareFormDataForProfileUpdateError(model);

            if (migrated) {
                model.addAttribute("updateMigratedProfileForm", updateProfileForm);
                model.addAttribute("updateLoginForm", new UpdateLoginForm());
            } else {
                model.addAttribute("updateProfileForm", updateProfileForm);
                model.addAttribute("updateEmailForm", new UpdateEmailForm());
            }

            model.addAttribute("isMigratedUser", Boolean.valueOf(migrated));
            model.addAttribute("updatePasswordForm", new UpdatePasswordForm());

            final UpdateNewsletterForm updateNewsletterForm = new UpdateNewsletterForm();
            updateNewsletterForm.setMarketingConsent(false);
            model.addAttribute("updateNewsletterForm", updateNewsletterForm);

            updateObsolescenceCategoriesOnError(model);
        } else {
            try {
                getB2bCustomerFacade().updateProfile(customerData);
                getSessionService().setAttribute(WebConstants.UPDATE_SUCCESS, "text.account.loginData.confirmationUpdated");
                returnAction = REDIRECT_TO_LOGIN_DATA_PAGE;
            } catch (final DuplicateUidException e) {
                logError(LOG, "{} {} Can not update user profile due to Duplicate UID(Email): {}", e, ErrorLogCode.UPDATE_USER_ERROR, ErrorSource.DB,
                         customerData.getEmail());
                GlobalMessages.addErrorMessage(model, "registration.error.account.exists.title");
                prepareFormDataForProfileUpdateError(model);
            } catch (final ErpCommunicationException e) {
                logError(LOG, "{} {} Can not update user profile because of an ERP communication error for the customer with Email: {}", e,
                         ErrorLogCode.UPDATE_USER_ERROR, ErrorSource.SAP_FAULT, customerData.getEmail());
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("form.global.error.erpcommunication"));
                returnAction = REDIRECT_TO_LOGIN_DATA_PAGE;
            } catch (final Exception e) {
                logError(LOG, "{} Can not update user profile for the customer with Email: {}", e, ErrorLogCode.UPDATE_USER_ERROR.getCode(),
                         customerData.getEmail());
                redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("form.global.error"));
                returnAction = REDIRECT_TO_LOGIN_DATA_PAGE;
            }

        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(LOGIN_DATA_CMS_PAGE);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.preferences.account.info"));

        return returnAction;
    }

    private void updateObsolescenceCategoriesOnError(final Model model) {
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        model.addAttribute("optedForObsolescence", customerData.isOptedForObsolescence());
        model.addAttribute("allCatSelected", customerData.isAllObsolCatSelected());
        model.addAttribute("categories", customerData.getCategories());
    }

    @RequestMapping(value = "/update-email", method = RequestMethod.POST)
    public String updateEmail(@Valid final UpdateEmailForm updateEmailForm, final BindingResult bindingResult, final Model model,
                              final RedirectAttributes redirectAttributes,
                              final HttpServletRequest request) throws CMSItemNotFoundException {

        getB2bCustomerFacade().removeForgotPasswordToken();
        String returnAction = REDIRECT_TO_LOGIN_DATA_PAGE;

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            prepareFormDataForEmailUpdateError(model, false);
            ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(LOGIN_DATA_CMS_PAGE);
            storeCmsPageInModel(model, contentPageForLabelOrId);
            setUpMetaDataForContentPage(model, contentPageForLabelOrId);
            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.preferences.account.info"));
            updateObsolescenceCategoriesOnError(model);
            returnAction = ControllerConstants.Views.Pages.Account.AccountLoginDataPage;
        } else {
            updateLogin(redirectAttributes, updateEmailForm.getEmail(), updateEmailForm.getPassword());
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        addGlobalModelAttributes(model, request);
        return returnAction;
    }

    @RequestMapping(value = "/update-login", method = RequestMethod.POST)
    public String updateLogin(@Valid final UpdateLoginForm updateLoginForm, final BindingResult bindingResult, final Model model,
                              final RedirectAttributes redirectAttributes,
                              final HttpServletRequest request) throws CMSItemNotFoundException {

        String returnAction = REDIRECT_TO_LOGIN_DATA_PAGE;

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            prepareFormDataForEmailUpdateError(model, true);
            ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(LOGIN_DATA_CMS_PAGE);
            storeCmsPageInModel(model, contentPageForLabelOrId);
            setUpMetaDataForContentPage(model, contentPageForLabelOrId);
            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.preferences.account.info"));
            updateObsolescenceCategoriesOnError(model);
            returnAction = ControllerConstants.Views.Pages.Account.AccountLoginDataPage;
        } else {
            updateLogin(redirectAttributes, updateLoginForm.getLogin(), updateLoginForm.getPassword());
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        addGlobalModelAttributes(model, request);
        return returnAction;
    }

    protected void updateLogin(final RedirectAttributes redirectAttributes, final String newLogin, final String password) {
        try {
            final boolean isValidEmail = EmailValidator.getInstance().isValid(newLogin);
            getB2bCustomerFacade().changeUid(newLogin.toLowerCase(new Locale(getCurrentLanguage().getIsocode())), password);

            // temporary solution to set oryginal UID - with new version of commerceservices it will not be necessary
            final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
            customerData.setDisplayUid(newLogin);
            if (isValidEmail) {
                customerData.setEmail(newLogin);
                if (customerData.getContactAddress() != null) {
                    customerData.getContactAddress().setEmail(newLogin);
                }
            }

            // DISTRELEC-4543
            // old code: customerFacade.updateFullProfile(customerData);
            getB2bCustomerFacade().updateProfile(customerData);
            getSessionService().setAttribute(WebConstants.UPDATE_SUCCESS, "text.account.loginData.confirmationUpdated");
            getSessionService().setAttribute(WebConstants.EMAIL_UPDATE_SUCCESS, "success");
            // Replace the spring security authentication with the new UID
            final String newUid = getB2bCustomerFacade().getCurrentCustomer().getUid().toLowerCase();
            final Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
            final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(newUid, null,
                                                                                                                  oldAuthentication.getAuthorities());
            newAuthentication.setDetails(oldAuthentication.getDetails());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        } catch (final DuplicateUidException e) {
            redirectAttributes.addFlashAttribute(GlobalMessages.INFO_MESSAGES_HOLDER, Collections.singletonList("text.account.loginData.emailNotChanged"));
        } catch (final PasswordMismatchException passwordMismatchException) {
            redirectAttributes.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("profile.currentPassword.invalid"));
        }
    }

    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public String updatePassword(@Valid final UpdatePasswordForm updatePasswordForm, final BindingResult bindingResult, final Model model,
                                 final RedirectAttributes redirectAttributes,
                                 final HttpServletRequest request) throws CMSItemNotFoundException {

        addGlobalModelAttributes(model, request);

        String errorPage = handlePasswordError(bindingResult, model);
        if (errorPage != null) {
            return errorPage;
        }
        if (updatePasswordForm.getNewPassword().equals(updatePasswordForm.getCheckNewPassword())) {
            try {
                getB2bCustomerFacade().changePassword(updatePasswordForm.getCurrentPassword(), updatePasswordForm.getNewPassword());
                getSessionService().setAttribute(WebConstants.UPDATE_SUCCESS, "text.account.confirmation.password.updated");

            } catch (final PasswordMismatchException localException) {
                bindingResult.rejectValue("currentPassword", "profile.currentPassword.invalid", new Object[] {}, "profile.currentPassword.invalid");
                GlobalMessages.addErrorMessage(model, "profile.currentPassword.invalid");
            }
        } else {
            bindingResult.rejectValue("checkNewPassword", "validation.checkPwd.equals", new Object[] {}, "validation.checkPwd.equals");
            GlobalMessages.addErrorMessage(model, "validation.checkPwd.equals");
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        errorPage = handlePasswordError(bindingResult, model);
        if (errorPage != null) {
            return errorPage;
        }
        return REDIRECT_TO_LOGIN_DATA_PAGE;

    }

    private String handlePasswordError(final BindingResult bindingResult, final Model model) throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            if (!model.containsAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER)) {
                GlobalMessages.addErrorMessage(model, "form.global.error");
            }
            prepareFormDataForPasswordUpdateError(model);
            ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(LOGIN_DATA_CMS_PAGE);
            storeCmsPageInModel(model, contentPageForLabelOrId);
            setUpMetaDataForContentPage(model, contentPageForLabelOrId);
            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.preferences.account.info"));
            updateObsolescenceCategoriesOnError(model);
            return ControllerConstants.Views.Pages.Account.AccountLoginDataPage;
        }
        return null;
    }

    @RequestMapping(value = "/update-newsletter", method = RequestMethod.POST)
    public String updateNewsletter(@Valid final UpdateNewsletterForm updateNewsletterForm, final BindingResult bindingResult, final Model model,
                                   final RedirectAttributes redirectAttributes,
                                   final HttpServletRequest request) throws CMSItemNotFoundException {

        String returnAction = ControllerConstants.Views.Pages.Account.AccountLoginDataPage;
        boolean isSubscriptionUpdated = false;
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            prepareFormDataForNewsletterUpdateError(model);
            updateObsolescenceCategoriesOnError(model);
        } else {
            final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
            LOG.info("Received from {} the form: {}", customerData.getEmail(), updateNewsletterForm);
            try {
                customerData.setNewsletter(updateNewsletterForm.isMarketingConsent());
                customerData.setSubscribePhoneMarketing(updateNewsletterForm.isSubscribePhoneMarketing());
                customerData.setNpsConsent(updateNewsletterForm.isNpsConsent());
                if (!updateNewsletterForm.isMarketingConsent()) {
                    customerData.setNpsConsent(false);
                    updateNewsletterForm.setNpsConsent(false);
                }
                customerData.setFunctionCode(updateNewsletterForm.getRole());
                customerData.setDepartment(updateNewsletterForm.getDivision());
                if (null != customerData.getContactAddress()) {
                    customerData.getContactAddress().setDepartmentCode(updateNewsletterForm.getDivision());
                }
                if (StringUtils.isEmpty(customerData.getTitleCode())) {
                    customerData.setTitleCode(defaultTitle);
                }
                getB2bCustomerFacade().updateFullProfile(customerData);
                getB2bCustomerFacade().updateProfile(customerData);
            } catch (final DuplicateUidException e) {
                redirectAttributes.addFlashAttribute(GlobalMessages.INFO_MESSAGES_HOLDER,
                                                     Collections.singletonList("text.account.loginData.newsletterNotChanged"));
            } catch (final Exception e) {
                LOG.error("Error updating user profile in ERP.", e);
            }

            if (isSubscriptionUpdated && isConsentConfirmationRequired()
                    && (updateNewsletterForm.isMarketingConsent() && (updateNewsletterForm.isNpsConsent()))) {
                getSessionService().setAttribute(WebConstants.CONSENT_CONFIRMATION_SENT, "text.account.consent.confirmation.sent");
            } else if (!isSubscriptionUpdated) {
                getSessionService().setAttribute(WebConstants.CONSENT_CONFIRMATION_FAILED, "text.account.consent.update.failed");
            } else {
                getSessionService().setAttribute(WebConstants.CONSENT_CONFIRMATION_SUCCESS, "success");
                getSessionService().setAttribute(WebConstants.UPDATE_SUCCESS, "text.account.loginData.confirmationUpdated");
            }
            returnAction = REDIRECT_TO_LOGIN_DATA_PAGE;
        }

        ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(LOGIN_DATA_CMS_PAGE);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.preferences.account.info"));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return returnAction;
    }

    @RequestMapping(value = "/addresses", method = RequestMethod.GET)
    public String getAddresses(@Valid final String sort, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {

        // Payment addresses
        model.addAttribute("paymentAddresses", userFacade.getAddressBookPaymentEntries());

        // Delivery addresses
        model.addAttribute("deliveryAddresses", getDeliveryAdresses(sort, model));

        final CustomerType customerType = getB2bCustomerFacade().getCurrentCustomer().getCustomerType();
        model.addAttribute("customerType", customerType);

        ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(ADDRESSES_CMS_PAGE);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);
        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", "text.account.addresses"));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountAddressesPage;
    }

    private List<AddressData> filterEmptyAddresses(List<AddressData> paymentAddresses) {
        List<AddressData> filteredPaymentAddress = new ArrayList<>();
        for (AddressData paymentAddress : paymentAddresses) {
            if (isPaymentAddressValid(paymentAddress)) {
                filteredPaymentAddress.add(paymentAddress);
            }
        }
        return filteredPaymentAddress;
    }

    private boolean isPaymentAddressValid(AddressData billingAddress) {
        if (StringUtils.isEmpty(billingAddress.getLine1()) && StringUtils.isEmpty(billingAddress.getLine2())
                && StringUtils.isEmpty(billingAddress.getPostalCode()) && StringUtils.isEmpty(billingAddress.getTown())) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = "/add-address", method = RequestMethod.GET)
    public String addAddress(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {

        model.addAttribute("countries", checkoutFacade.getDeliveryCountriesAndRegions());
        model.addAttribute("titles", userFacade.getTitles());

        AbstractDistAddressForm addressForm = null;
        if (CustomerType.B2C.equals(getB2bCustomerFacade().getCurrentCustomer().getCustomerType())) {
            addressForm = new B2CAddressForm();
            addressForm.setShippingAddress(Boolean.TRUE);
            addressForm.setBillingAddress(Boolean.FALSE);
        } else { // B2B and B2B key account
            addressForm = new B2BShippingAddressForm();
            addressForm.setShippingAddress(Boolean.TRUE);
            addressForm.setBillingAddress(Boolean.FALSE);
        }
        setCountryOnForm(addressForm);

        model.addAttribute(getFormClassName(addressForm), addressForm);
        ContentPageModel contentPageForLabelOrId = getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE);
        storeCmsPageInModel(model, contentPageForLabelOrId);
        setUpMetaDataForContentPage(model, contentPageForLabelOrId);

        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", null);
        breadcrumbs.add(new Breadcrumb("/my-account/addresses",
                                       getMessageSource().getMessage("text.account.addresses", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(
                        new Breadcrumb("#", getMessageSource().getMessage("text.account.addresses.addEditAddress", null, getI18nService().getCurrentLocale()),
                                       null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountAddEditAddressPage;
    }

    @RequestMapping(value = "/edit-address/{addressType}/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String editAddress(@PathVariable("addressType") final AddressType addressType, @PathVariable("addressCode") final String addressCode,
                              final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request) throws CMSItemNotFoundException {

        AbstractDistAddressForm addressForm = null;
        switch (addressType) {
            case b2bshipping:
                addressForm = new B2BShippingAddressForm();
                addressForm.setShippingAddress(Boolean.TRUE);
                break;
            case b2bbilling:
                addressForm = new B2BBillingAddressForm();
                addressForm.setBillingAddress(Boolean.TRUE);
                break;
            case b2c:
                addressForm = new B2CAddressForm();
                break;
            default:
                return REDIRECT_TO_ADDRESS_PAGE;
        }
        setCountryOnForm(addressForm);

        final AddressData addressData = userFacade.getAddressForCode(addressCode);
        AddressFormHelper.populateForm(addressData, addressForm);

        // Address not found
        if (addressForm.getAddressId() == null) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("text.account.addresses.editAddressError"));
            return REDIRECT_TO_ADDRESS_PAGE;
        }

        model.addAttribute("canEditCompanyName", Boolean.valueOf(getCustomerFacade().canEditCompanyName(addressData)));

        model.addAttribute("countries", checkoutFacade.getDeliveryCountriesAndRegions());
        model.addAttribute("titles", userFacade.getTitles());
        model.addAttribute("departmentData", userFacade.getDepartments());
        model.addAttribute("functionData", userFacade.getFunctions());

        model.addAttribute(getFormClassName(addressForm), addressForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));

        final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.accountDetails", null);
        breadcrumbs.add(new Breadcrumb("/my-account/addresses",
                                       getMessageSource().getMessage("text.account.addresses", null, getI18nService().getCurrentLocale()), null));
        breadcrumbs.add(
                        new Breadcrumb("#", getMessageSource().getMessage("text.account.addresses.addEditAddress", null, getI18nService().getCurrentLocale()),
                                       null));
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("customerType", getB2bCustomerFacade().getCurrentCustomer().getCustomerType().getCode());
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountAddEditAddressPage;
    }

    // B2C version
    @RequestMapping(value = "/edit-address/b2c", method = RequestMethod.POST)
    public String addOrEditAddress(@Valid final B2CAddressForm addressForm, final BindingResult bindingResult, final Model model,
                                   final RedirectAttributes redirectModel,
                                   final HttpServletRequest request) throws CMSItemNotFoundException, NumberParseException {
        if (!StringUtils.isBlank(addressForm.getContactPhone())) {
            final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(addressForm.getContactPhone(), getCurrentCountry().getIsocode());
            addressForm.setContactPhone(phoneNumberToString(phoneNumber));
        }
        if (!StringUtils.isBlank(addressForm.getMobileNumber())) {
            final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(addressForm.getMobileNumber(), getCurrentCountry().getIsocode());
            addressForm.setMobileNumber(phoneNumberToString(mobileNumber));
        }
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return editAddress(addressForm, bindingResult, model, redirectModel);
    }

    // B2B Billing version
    @RequestMapping(value = "/edit-address/b2bbilling", method = RequestMethod.POST)
    public String addOrEditAddress(@Valid final B2BBillingAddressForm addressForm, final BindingResult bindingResult, final Model model,
                                   final RedirectAttributes redirectModel,
                                   final HttpServletRequest request) throws CMSItemNotFoundException, NumberParseException {
        if (!StringUtils.isBlank(addressForm.getPhoneNumber())) {
            final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(addressForm.getPhoneNumber(), getCurrentCountry().getIsocode());
            addressForm.setPhoneNumber(phoneNumberToString(phoneNumber));
        }
        if (!StringUtils.isBlank(addressForm.getMobileNumber())) {
            final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(addressForm.getMobileNumber(), getCurrentCountry().getIsocode());
            addressForm.setMobileNumber(phoneNumberToString(mobileNumber));
        }
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return editAddress(addressForm, bindingResult, model, redirectModel);
    }

    // B2B Billing version
    @RequestMapping(value = "/edit-address/b2bshipping", method = RequestMethod.POST)
    public String addOrEditAddress(@Valid final B2BShippingAddressForm addressForm, final BindingResult bindingResult, final Model model,
                                   final RedirectAttributes redirectModel,
                                   final HttpServletRequest request) throws CMSItemNotFoundException, NumberParseException {
        if (!StringUtils.isBlank(addressForm.getPhoneNumber())) {
            final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(addressForm.getPhoneNumber(), getCurrentCountry().getIsocode());
            addressForm.setPhoneNumber(phoneNumberToString(phoneNumber));
        }
        if (!StringUtils.isBlank(addressForm.getMobileNumber())) {
            final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(addressForm.getMobileNumber(), getCurrentCountry().getIsocode());
            addressForm.setMobileNumber(phoneNumberToString(mobileNumber));
        }
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return editShippingAddress(addressForm, bindingResult, model, redirectModel);
    }

    protected String editShippingAddress(final B2BShippingAddressForm addressForm, final BindingResult bindingResult, final Model model,
                                         final RedirectAttributes redirectModel) throws CMSItemNotFoundException, NumberParseException {
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            model.addAttribute("countries", checkoutFacade.getDeliveryCountriesAndRegions());
            model.addAttribute("titles", userFacade.getTitles());

            return ControllerConstants.Views.Pages.Account.AccountAddEditAddressPage;
        }

        AddressData addressData = null;
        if (StringUtils.isEmpty(addressForm.getAddressId())) {
            addressData = new AddressData();
        } else {
            addressData = userFacade.getAddressForCode(addressForm.getAddressId());
        }
        AddressFormHelper.populateAddressData(addressForm, addressData);
        final CountryData countryData = new CountryData();
        countryData.setIsocode(addressForm.getCountryIso());
        addressData.setCountry(countryData);

        if (StringUtils.isNotBlank(addressForm.getPhoneNumber())) {
            final PhoneNumber phoneNumber = PHONENUMBERUTIL.parse(addressForm.getPhoneNumber(), getCurrentCountry().getIsocode());
            addressForm.setPhoneNumber(phoneNumberToString(phoneNumber));
            addressData.setPhone(addressForm.getPhoneNumber());
        }
        if (StringUtils.isNotBlank(addressForm.getMobileNumber())) {
            final PhoneNumber mobileNumber = PHONENUMBERUTIL.parse(addressForm.getMobileNumber(), getCurrentCountry().getIsocode());
            addressForm.setMobileNumber(phoneNumberToString(mobileNumber));
            addressData.setCellphone(addressForm.getMobileNumber());
        }

        if (StringUtils.isNotBlank(addressForm.getRegionIso())) {
            final RegionData regionData = new RegionData();
            regionData.setIsocode(addressForm.getRegionIso());
            addressData.setRegion(regionData);
        }

        // New address or update?
        if (StringUtils.isBlank(addressData.getId())) {
            try {
                userFacade.addAddress(addressData);
            } catch (final ErpCommunicationException e) {
                logError(LOG, "{} {} Can not add address for the customer with Email: {} ", e, ErrorLogCode.ADDRESS_ERROR, ErrorSource.SAP_FAULT,
                         addressData.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("form.global.error.erpcommunication"));
                return REDIRECT_TO_ADDRESS_PAGE;
            } catch (final Exception e) {
                logError(LOG, "{} Can not add address for the customer with Email: {} ", e, ErrorLogCode.ADDRESS_ERROR, addressData.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("text.account.addresses.addAddressError"));
                return REDIRECT_TO_ADDRESS_PAGE;
            }
        } else {
            try { // if it is a b2b key account, we will not be able to edit the address
                userFacade.editAddress(addressData);
            } catch (final ErpCommunicationException e) {
                logError(LOG, "{} {} Can not edit address for the customer with Email: {}", e, ErrorLogCode.ADDRESS_ERROR, ErrorSource.SAP_FAULT,
                         addressData.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("form.global.error.erpcommunication"));
                return REDIRECT_TO_ADDRESS_PAGE;
            } catch (final Exception e) {
                logError(LOG, "{} Can not edit address for the customer with Email: {}", e, ErrorLogCode.ADDRESS_ERROR.getCode(), addressData.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("text.account.addresses.editAddressError"));
                return REDIRECT_TO_ADDRESS_PAGE;
            }
        }

        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("text.account.addresses.confirmationUpdated"));
        return REDIRECT_TO_ADDRESS_PAGE;
    }

    protected String editAddress(final AbstractDistAddressForm addressForm, final BindingResult bindingResult, final Model model,
                                 final RedirectAttributes redirectModel) throws CMSItemNotFoundException {

        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, "form.global.error");
            storeCmsPageInModel(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ADD_EDIT_ADDRESS_CMS_PAGE));
            model.addAttribute("countries", checkoutFacade.getDeliveryCountriesAndRegions());
            model.addAttribute("titles", userFacade.getTitles());

            return ControllerConstants.Views.Pages.Account.AccountAddEditAddressPage;
        }

        AddressData addressData = null;
        if (StringUtils.isEmpty(addressForm.getAddressId())) {
            addressData = new AddressData();
        } else {
            addressData = userFacade.getAddressForCode(addressForm.getAddressId());
        }
        AddressFormHelper.populateAddressData(addressForm, addressData);
        final CountryData countryData = new CountryData();
        countryData.setIsocode(addressForm.getCountryIso());
        addressData.setCountry(countryData);
        if (StringUtils.isNotBlank(addressForm.getRegionIso())) {
            final RegionData regionData = new RegionData();
            regionData.setIsocode(addressForm.getRegionIso());
            addressData.setRegion(regionData);
        }

        // New address or update?
        if (StringUtils.isBlank(addressData.getId())) {
            try {
                userFacade.addAddress(addressData);
            } catch (final ErpCommunicationException e) {
                logError(LOG, "{} {} Can not add address for the customer with Email: {} ", e, ErrorLogCode.ADDRESS_ERROR, ErrorSource.SAP_FAULT,
                         addressData.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("form.global.error.erpcommunication"));
                return REDIRECT_TO_ADDRESS_PAGE;
            } catch (final Exception e) {
                logError(LOG, "{} Can not add address for the customer with Email: {} ", e, ErrorLogCode.ADDRESS_ERROR, addressData.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("text.account.addresses.addAddressError"));
                return REDIRECT_TO_ADDRESS_PAGE;
            }
        } else {
            try { // if it is a b2b key account, we will not be able to edit the address
                userFacade.editAddress(addressData);
            } catch (final ErpCommunicationException e) {
                logError(LOG, "{} {} Can not edit address for the customer with Email: {}", e, ErrorLogCode.ADDRESS_ERROR, ErrorSource.SAP_FAULT,
                         addressData.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("form.global.error.erpcommunication"));
                return REDIRECT_TO_ADDRESS_PAGE;
            } catch (final Exception e) {
                logError(LOG, "{} Can not edit address for the customer with Email: {}", e, ErrorLogCode.ADDRESS_ERROR.getCode(), addressData.getEmail());
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("text.account.addresses.editAddressError"));
                return REDIRECT_TO_ADDRESS_PAGE;
            }
        }

        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("text.account.addresses.confirmationUpdated"));
        return REDIRECT_TO_ADDRESS_PAGE;
    }

    @RequestMapping(value = "/remove-address/" + ADDRESS_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String removeAddress(@PathVariable("addressCode") final String addressCode, final RedirectAttributes redirectModel, final Model model,
                                final HttpServletRequest request) {
        addGlobalModelAttributes(model, request);

        final AddressData addressData = new AddressData();
        addressData.setId(addressCode);
        try {
            userFacade.removeAddress(addressData);
        } catch (final ErpCommunicationException e) {
            logError(LOG, "{} {} Can not remove address for the customer with Email: {}", e, ErrorLogCode.ADDRESS_ERROR, ErrorSource.SAP_FAULT,
                     addressData.getEmail());
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("form.global.error.erpcommunication"));
            return REDIRECT_TO_ADDRESS_PAGE;
        } catch (final Exception e) {
            logError(LOG, "{} Can not remove address for the customer with Email: {}", e, ErrorLogCode.ADDRESS_ERROR.getCode(), addressData.getEmail());
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.address.error"));
            return REDIRECT_TO_ADDRESS_PAGE;
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        redirectModel.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.address.removed"));
        return REDIRECT_TO_ADDRESS_PAGE;
    }

    @RequestMapping(value = "/set-default-address", method = { RequestMethod.POST, RequestMethod.GET }, produces = { "application/json" })
    public String setDefaultAddress(@RequestParam("addressCode") final String addressCode,
                                    @RequestParam(value = "billing", defaultValue = "false") final boolean billingAddress,
                                    @RequestParam(value = "shipping", defaultValue = "false") final boolean shippingAddress, final Model model,
                                    final HttpServletRequest request) throws CMSItemNotFoundException {
        String status = "OK";
        String message = "account.confirmation.default.address.changed";
        if (billingAddress || shippingAddress) {
            try {
                final AddressData addressData = new AddressData();
                addressData.setDefaultAddress(true);
                addressData.setBillingAddress(billingAddress);
                addressData.setShippingAddress(shippingAddress);
                addressData.setId(addressCode);
                userFacade.setDefaultAddress(addressData);

                if (checkoutFacade.hasCheckoutCart()) {
                    if (billingAddress) {
                        checkoutFacade.setPaymentAddress(addressData);
                    }
                    if (shippingAddress) {
                        checkoutFacade.setDeliveryAddress(addressData);
                    }
                }
            } catch (final Exception exp) {
                status = "NOK";
                message = "general.unknown.identifier";
            }
        }

        model.addAttribute("status", status);
        model.addAttribute("message", message);

        request.getSession().setAttribute(SHIPPING_MODE, "shipping");

        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Fragments.Account.SetDefaultAddressesJson;
    }

    // Get the delivery and payment method of the current user.
    @RequestMapping(value = "/payment-and-delivery-options", method = RequestMethod.GET)
    public String getPaymentAndDeliveryOptions(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);

        if (!isPaymentOptionsEditable()) {
            return addFasterizeCacheControlParameter(REDIRECT_MY_ACCOUNT);
        }

        CustomerData distB2BCustomerData = getB2bCustomerFacade().getCurrentCustomer();
        model.addAttribute("customerData", distB2BCustomerData);

        // Search the DistPaymentModeData available for the current user
        final Collection<DistPaymentModeData> paymentInfoDatas = userFacade.getSupportedPaymentModesForUser();
        model.addAttribute("paymentOptions", paymentInfoDatas);
        addPaymentModeModelAttributes(model);

        // Set default Payment method
        if (StringUtils.isNotEmpty(distB2BCustomerData.getDefaultPaymentMode())) {
            model.addAttribute("defaultPaymentMode", distB2BCustomerData.getDefaultPaymentMode());

            // Set Default Payment Info
            if (null != distB2BCustomerData.getDefaultPaymentInfo() && StringUtils.isNotEmpty(distB2BCustomerData.getDefaultPaymentInfo().getId())) {
                model.addAttribute("defaultPaymentInfo", distB2BCustomerData.getDefaultPaymentInfo().getId());
            }
        } else {
            String defaultPaymentMode = getDefaultPaymentMode(paymentInfoDatas);
            model.addAttribute("defaultPaymentMode", defaultPaymentMode);

            // Payment Info
            if (CREDIT_CARD_PAYMENT_MODE.equalsIgnoreCase(defaultPaymentMode)) {
                List<CCPaymentInfoData> creditsCardList = distB2BCustomerData.getCcPaymentInfos();
                model.addAttribute("defaultPaymentInfo", getDefaultPaymentInfo(creditsCardList));
            } else {
                model.addAttribute("defaultPaymentInfo", StringUtils.EMPTY);
            }
        }

        // Search the CCPaymentInfoData available for the current user
        final List<CCPaymentInfoData> creditsCardsOptions = userFacade.getCCPaymentInfos(true);
        if (null != creditsCardsOptions && creditsCardsOptions.size() > 0) {
            model.addAttribute("ccPaymentInfos", creditsCardsOptions);
        } else {
            List<CCPaymentInfoData> creditsCardList = distB2BCustomerData.getCcPaymentInfos();
            if (null != creditsCardList && creditsCardList.size() > 0) {
                model.addAttribute("ccPaymentInfos", creditsCardList);
            }
        }

        final Collection<DeliveryModeData> deliveryInfoDatas = userFacade.getSupportedDeliveryModesForUser();
        model.addAttribute("shippingOptions", deliveryInfoDatas);
        model.addAttribute("selectedShippingOption", getDefaultDeliveryMode(deliveryInfoDatas));

        storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_DELIVERY_OPTIONS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PAYMENT_DELIVERY_OPTIONS_CMS_PAGE));

        if (isPaymentOptionsEditable()) {
            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.settings", "text.account.paymentOptions"));
        }

        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountPaymentAndDeliveryOptionsPage;
    }

    private String getDefaultDeliveryMode(Collection<DeliveryModeData> deliveryInfoDatas) {
        if (!CollectionUtils.isEmpty(deliveryInfoDatas) && deliveryInfoDatas.size() == 1) {
            return (deliveryInfoDatas.iterator().next()).getCode();
        } else if (!CollectionUtils.isEmpty(deliveryInfoDatas) && deliveryInfoDatas.size() > 1) {
            for (DeliveryModeData deliveryInfoData : deliveryInfoDatas) {
                if (BooleanUtils.isTrue(deliveryInfoData.getDefaultDeliveryMode())) {
                    return deliveryInfoData.getCode();
                }
            }
        }
        return null;
    }

    private String getDefaultPaymentMode(Collection<DistPaymentModeData> supportedPaymentModes) {
        if (!CollectionUtils.isEmpty(supportedPaymentModes) && supportedPaymentModes.size() == 1) {
            return (supportedPaymentModes.iterator().next()).getCode();
        } else if (!CollectionUtils.isEmpty(supportedPaymentModes) && supportedPaymentModes.size() > 1) {
            for (DistPaymentModeData paymentMode : supportedPaymentModes) {
                if (paymentMode.getDefaultPaymentMode()) {
                    return paymentMode.getCode();
                }

            }
        }
        return null;
    }

    private String getDefaultPaymentInfo(List<CCPaymentInfoData> creditsCardList) {
        if (!CollectionUtils.isEmpty(creditsCardList) && creditsCardList.size() == 1) {
            return (creditsCardList.iterator().next()).getId();
        } else if (!CollectionUtils.isEmpty(creditsCardList) && !creditsCardList.isEmpty() && creditsCardList.size() > 1) {
            for (CCPaymentInfoData paymentInfoDate : creditsCardList) {
                if (paymentInfoDate.isDefaultPaymentInfo()) {
                    return paymentInfoDate.getId();
                }
            }
        }
        return null;
    }

    /**
     * This method is used to set the payment option if no Credit card payment info is present.
     *
     * @param paymentOption
     * @return String
     */
    @RequestMapping(value = "/set-payment-options", method = RequestMethod.POST)
    public String setDefaultPaymentOption(@RequestParam("paymentOption") final String paymentOption) {

        if (!StringUtils.isEmpty(paymentOption)) {
            userFacade.setDefaultPaymentMode(paymentOption);
        }

        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    /**
     * This method is used to set the default payment Info.
     *
     * @param paymentOption
     * @param paymentInfo
     * @return String
     */
    @RequestMapping(value = "/set-payment-info", method = RequestMethod.POST)
    public String setDefaultPaymentInfo(@RequestParam("paymentOption") final String paymentOption,
                                        @RequestParam(value = "paymentInfo", required = false) final String paymentInfo) {

        if (!StringUtils.isEmpty(paymentOption) && !StringUtils.isEmpty(paymentInfo)) {

            // Set the default payment method
            if (userFacade.setDefaultPaymentMode(paymentOption)) {

                // Set the default payment Info
                userFacade.setDefaultPaymentInfo(paymentInfo);
            }

        }

        if (StringUtils.isEmpty(paymentInfo) && !StringUtils.isEmpty(paymentOption)) {
            userFacade.setDefaultPaymentMode(paymentOption);
        }

        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    // Set the new default shipping option
    @RequestMapping(value = "/set-delivery-option", method = RequestMethod.POST)
    public String setDefaultShippingOption(@RequestParam("shippingOption") final String shippingOption) {
        userFacade.setDefaultDeliveryMode(shippingOption);

        // Redirect to the payment and delivery page
        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    // Remove a payment info (credit card)
    @RequestMapping(value = "/removePaymentInfo/{paymentInfoId:.*}", method = RequestMethod.POST)
    public String removePaymentInfo(@PathVariable("paymentInfoId") final String paymentInfoId,
                                    final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {
        userFacade.removeCCPaymentInfo(paymentInfoId);

        redirectAttributes.addFlashAttribute(GlobalMessages.CONF_MESSAGES_HOLDER, Collections.singletonList("account.confirmation.payment.details.removed"));
        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    @RequestMapping(value = "/request-invoice-payment-mode", method = RequestMethod.POST)
    public String requestInvoicePaymentMode() {
        sendInvoicePaymentModeRequest();
        return REDIRECT_TO_PAYMENT_INFO_PAGE;
    }

    /**
     * The list of order approval requests for a admin B2B customer
     *
     * @param orderApprovalForm
     * @param bindingResult
     * @param model
     * @return page of the order approval request list
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = ORDER_APPROVAL_PAGE_URL, method = RequestMethod.GET)
    public String orderApprovalDashboard(final OrderApprovalForm orderApprovalForm, final BindingResult bindingResult, final Model model,
                                         final HttpServletRequest request) throws CMSItemNotFoundException {
        if (!getBaseStoreService().getCurrentBaseStore().getOrderApprovalEnabled()) {
            return FORWARD_TO_404;
        }

        addGlobalModelAttributes(model, request);

        final List<WorkflowActionStatus> WorkflowActionStatusList = new ArrayList<>(Arrays.asList(WorkflowActionStatus.values()));
        WorkflowActionStatusList.remove(WorkflowActionStatus.COMPLETED);

        getOrderApprovals(orderApprovalForm, bindingResult, model, new WorkflowActionType[] { WorkflowActionType.START },
                          WorkflowActionStatusList.toArray(new WorkflowActionStatus[WorkflowActionStatusList.size()]), request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountOrderApprovalPage;
    }

    /**
     * The list of order approval requests for a normal B2B customer
     *
     * @param orderApprovalForm
     * @param bindingResult
     * @param model
     * @return page of the order approval request list
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = ORDER_APPROVAL_REQUESTS_PAGE_URL, method = RequestMethod.GET)
    public String myOrderApprovalRequests(final OrderApprovalForm orderApprovalForm, final BindingResult bindingResult, final HttpServletRequest request,
                                          final Model model) throws CMSItemNotFoundException {
        if (!getBaseStoreService().getCurrentBaseStore().getOrderApprovalEnabled()) {
            return FORWARD_TO_404;
        }

        addGlobalModelAttributes(model, request);
        getOrderApprovals(orderApprovalForm, bindingResult, model, new WorkflowActionType[] { WorkflowActionType.START }, WorkflowActionStatus.values(),
                          request);
        return ControllerConstants.Views.Pages.Account.AccountOrderApprovalPage;
    }

    /**
     * Order approval details view for B2B customer
     */
    @RequestMapping(value = { ORDER_APPROVAL_DETAILS_PAGE_URL, ORDER_APPROVAL_REQUEST_DETAILS_PAGE_URL }, method = RequestMethod.GET)
    public String orderApprovalDetails(@PathVariable("orderCode") final String orderCode, @PathVariable("workflowActionCode") final String workflowActionCode,
                                       final Model model, final RedirectAttributes redirectModel, final HttpServletRequest request)
                                                                                                                                    throws CMSItemNotFoundException {

        if (!getBaseStoreService().getCurrentBaseStore().getOrderApprovalEnabled()) {
            return FORWARD_TO_404;
        }

        try {
            final B2BOrderApprovalData orderApprovalDetails = getOrderFacade().getOrderApprovalDetailsForCode(workflowActionCode);
            model.addAttribute("orderApprovalData", orderApprovalDetails);
            model.addAttribute("orderData", orderApprovalDetails.getB2bOrderData());

            prepareFormDataForOrderDetails(model);

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", null);
            breadcrumbs.add(new Breadcrumb(MY_ACCOUNT_PAGE_URL + (isB2BAdminUser() ? ORDER_APPROVAL_PAGE_URL : ORDER_APPROVAL_REQUESTS_PAGE_URL),
                                           getMessageSource().getMessage("text.account.orderApprovalDashboard", null, getI18nService().getCurrentLocale()),
                                           null));
            breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.order.orderBreadcrumb", new Object[] { orderCode }, "Order {0}",
                                                                              getI18nService().getCurrentLocale()),
                                           null));

            model.addAttribute("breadcrumbs", breadcrumbs);

        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load an order approval that does not exist or is not visible!", e);
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("text.account.orderApproval.canNotLoad"));
            return REDIRECT_ORDER_APPROVAL_PAGE_URL;
        }
        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountOrderApprovalDetailsPage;
    }

    /**
     * The admin customer may accept/reject an order approval request.
     *
     * @param orderApprovalDecisionForm
     * @param bindingResult
     * @param model
     * @return the order approval request page URL
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = ORDER_APPROVAL_DECISION_URL, method = RequestMethod.POST)
    public String orderApprovalDecision(@ModelAttribute("orderApprovalDecisionForm") final OrderApprovalDecisionForm orderApprovalDecisionForm,
                                        final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
                                                                                                                                      throws CMSItemNotFoundException {
        if (!getBaseStoreService().getCurrentBaseStore().getOrderApprovalEnabled()) {
            return FORWARD_TO_404;
        }

        if (bindingResult.hasErrors()) {
            LOG.error("{} {} Binding ERRORS: {}", ErrorLogCode.ORDER_RELATED_ERROR, ErrorSource.HYBRIS_BINDING_ERROR, bindingResult.getFieldErrors());
            GlobalMessages.addErrorMessage(model, "form.global.error");
        }

        try {
            String errorMessageKey = null;
            final String decision = orderApprovalDecisionForm.getApproverSelectedDecision() != null
                                                                                                    ? orderApprovalDecisionForm.getApproverSelectedDecision()
                                                                                                                               .trim().toUpperCase()
                                                                                                    : null;

            // Checking errors
            if (StringUtils.isEmpty(decision) || (!"APPROVE".equals(decision) && !"REJECT".equals(decision))) {
                errorMessageKey = "text.account.orderApproval.missingDecision";
            } else {
                if ("APPROVE".equals(decision) && !"true".equalsIgnoreCase(orderApprovalDecisionForm.getTermsAndConditions())) {
                    errorMessageKey = "text.account.orderApproval.acceptTermsAndConditions";
                } else if ("REJECT".equals(decision) && StringUtils.isEmpty(orderApprovalDecisionForm.getComments())) {
                    errorMessageKey = "text.account.orderApproval.addApproverComments";
                }
            }

            if (errorMessageKey != null) {
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, errorMessageKey);
                try {
                    final B2BOrderApprovalData orderApprovalDetails = getOrderFacade()
                                                                                      .getOrderApprovalDetailsForCode(orderApprovalDecisionForm.getWorkFlowActionCode());
                    if (orderApprovalDetails != null && orderApprovalDetails.getB2bOrderData() != null) {
                        final OrderData order = orderApprovalDetails.getB2bOrderData();
                        return REDIRECT_ORDER_APPROVAL_PAGE_URL + "/order-details/" + order.getCode() + "/workflow/"
                               + orderApprovalDecisionForm.getWorkFlowActionCode();
                    } else {
                        return REDIRECT_ORDER_APPROVAL_PAGE_URL;
                    }
                } catch (final Exception e) {
                    logError(LOG, "{} Can not save order approval decision.", e, ErrorLogCode.ORDER_RELATED_ERROR);
                    return REDIRECT_ORDER_APPROVAL_PAGE_URL;
                }

            }
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
            // No error found, then proceed approval
            final B2BOrderApprovalData b2bOrderApprovalData = new B2BOrderApprovalData();
            b2bOrderApprovalData.setSelectedDecision(decision);
            b2bOrderApprovalData.setApprovalComments(orderApprovalDecisionForm.getComments());
            b2bOrderApprovalData.setWorkflowActionModelCode(orderApprovalDecisionForm.getWorkFlowActionCode());

            try {
                getOrderFacade().setOrderApprovalDecision(b2bOrderApprovalData);
            } catch (final WorkflowActionDecideException e) {
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER,
                                                Collections.singletonList("text.account.orderApproval.decisionAlreadyDone"));
                return REDIRECT_ORDER_APPROVAL_PAGE_URL;
            }

            if ("REJECT".equals(decision)) {
                redirectModel.addFlashAttribute(GlobalMessages.INFO_MESSAGES_HOLDER, Collections.singletonList("text.account.orderApproval.rejected"));
                return REDIRECT_ORDER_APPROVAL_PAGE_URL;
            } else {
                final B2BOrderApprovalData orderApprovalDetails = getOrderFacade()
                                                                                  .getOrderApprovalDetailsForCode(orderApprovalDecisionForm.getWorkFlowActionCode());
                getSessionService().setAttribute(DistConstants.Session.CHECKOUT_USER, orderApprovalDetails.getB2bOrderData().getB2bCustomerData().getUid());
                getSessionService().setAttribute(DistConstants.Session.ORDER_APROVAL_CONFIRMATION, Boolean.TRUE);
                return addFasterizeCacheControlParameter(
                                                         REDIRECT_ORDER_APPROVAL_ORDER_CONFIRMATION_PAGE_URL + "/"
                                                         + orderApprovalDetails.getB2bOrderData().getCode());
            }

        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load an order approval that does not exist or is not visible!", e);
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("text.account.orderApproval.canNotLoad"));
            return REDIRECT_ORDER_APPROVAL_PAGE_URL;
        }
    }

    /**
     * Retrieve the order history for the current user
     */
    @RequestMapping(value = OPEN_ORDER_HISTORY_PAGE_URL, method = RequestMethod.GET)
    public String openOrderHistory(@Valid final OrderHistoryForm orderHistoryForm, final HttpServletRequest request,
                                   final Model model) throws CMSItemNotFoundException {

        // Retrieve data from sap
        getOpenOrderHistory(orderHistoryForm, model, request);

        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", "text.account.openOrderHistory"));
        model.addAttribute("sortCode", orderHistoryForm.getSort() + ":" + orderHistoryForm.getSortType().toLowerCase());
        model.addAttribute("showMode", orderHistoryForm.getShow());
        model.addAttribute("sortTypeList", OPEN_ORDER_HISTORY_SORT_TYPE_LIST);

        storeCmsPageInModel(model, getContentPageForLabelOrId(OPEN_ORDER_HISTORY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(OPEN_ORDER_HISTORY_CMS_PAGE));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountOpenOrderHistoryPage;
    }

    /**
     * Retrieve the order history for the current user
     */
    @RequestMapping(value = ORDER_HISTORY_PAGE_URL, method = { RequestMethod.GET, RequestMethod.POST })
    public String orderHistory(@Valid final OrderHistoryForm orderHistoryForm, final BindingResult bindingResult, final HttpServletRequest request,
                               final Model model, final HttpSession session)
                                                                             throws CMSItemNotFoundException {

        getOrderHistory(orderHistoryForm, bindingResult, request, model);

        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", "text.account.orderHistory"));
        model.addAttribute("stateList", ORDER_STATUS_LIST);
        model.addAttribute("sortCode", orderHistoryForm.getSort() + ":" + orderHistoryForm.getSortType().toLowerCase());
        model.addAttribute("showMode", orderHistoryForm.getShow());
        model.addAttribute("sortTypeList", ORDER_HISTORY_SORT_TYPE_LIST);

        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_HISTORY_CMS_PAGE));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountOrderHistoryPage;
    }

    @RequestMapping(value = ORDER_HISTORY_NEXT_PAGE_URL, method = RequestMethod.GET, produces = { "application/json" })
    public String nextOrderHistoryPage(@Valid final OrderHistoryForm orderHistoryForm, final BindingResult bindingResult, final HttpServletRequest request,
                                       final Model model,
                                       final HttpSession session) {
        addGlobalModelAttributes(model, request);

        getOrderHistory(orderHistoryForm, bindingResult, request, model);
        return ControllerConstants.Views.Fragments.Account.ShowMoreOrders;
    }

    @RequestMapping(value = OPEN_ORDER_HISTORY_DETAILS_PAGE_URL, method = RequestMethod.GET)
    public String openOrderDetails(@PathVariable("orderCode") final String orderCode, final Model model,
                                   final HttpServletRequest request) throws CMSItemNotFoundException {

        try {
            final OrderData openOrderDetails = orderHistoryFacade.getOpenOrderDetailsForCode(orderCode);

            if (openOrderDetails == null) {
                throw new UnknownIdentifierException("No Open Orders found with code " + orderCode);
            }

            model.addAttribute("orderData", openOrderDetails);
            model.addAttribute("returnRequestForm", new ReturnRequestForm());

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", null);
            breadcrumbs.add(new Breadcrumb(MY_ACCOUNT_PAGE_URL + OPEN_ORDER_HISTORY_PAGE_URL,
                                           getMessageSource().getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.order.orderBreadcrumb", new Object[] { openOrderDetails.getCode() },
                                                                              "Order {0}", getI18nService().getCurrentLocale()),
                                           null));
            model.addAttribute("breadcrumbs", breadcrumbs);

        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load a open order that does not exist or is not visible", e);
            GlobalMessages.addErrorMessage(model, "text.account.order.notfound");
            return addFasterizeCacheControlParameter(REDIRECT_MY_ACCOUNT + OPEN_ORDER_HISTORY_PAGE_URL);
        }

        storeCmsPageInModel(model, getContentPageForLabelOrId(OPEN_ORDER_DETAIL_CMS_PAGE));
        getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(OPEN_ORDER_DETAIL_CMS_PAGE));
        addGlobalModelAttributes(model, request);
        return ControllerConstants.Views.Pages.Account.AccountOpenOrderDetailsPage;
    }

    /**
     * Retrieve the order having the specified code
     *
     * @param orderCode
     *            the order code
     * @param model
     * @return the page of the order details
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = ORDER_HISTORY_DETAILS_PAGE_URL, method = RequestMethod.GET)
    public String orderDetails(@PathVariable("orderCode") final String orderCode, final Model model,
                               final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        try {
            final OrderData orderDetails = orderHistoryFacade.getOrderForCode(orderCode);

            if (orderDetails == null) {
                throw new UnknownIdentifierException("No Order found with code " + orderCode);
            }

            model.addAttribute("orderData", orderDetails);
            final boolean isRMARaised = !orderDetails.getEntries().stream().filter(entries -> (null != entries && null != entries.getRmaData()
                    && null != entries.getRmaData().getRmas() && !entries.getRmaData().getRmas().isEmpty())).collect(Collectors.toList()).isEmpty();
            model.addAttribute("isRMARaised", isRMARaised);
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().populateOrderReturnData(digitalDatalayer, orderDetails);

            List<Product> digitalDataProducts = orderDetails.getEntries().stream()
                                                            .map(OrderEntryData::getProduct)
                                                            .map(product -> {
                                                                return populateProductDTMObjects(product);
                                                            }).collect(Collectors.toList());

            digitalDatalayer.setProduct(digitalDataProducts);

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", null);
            breadcrumbs.add(new Breadcrumb(MY_ACCOUNT_PAGE_URL + ORDER_HISTORY_PAGE_URL,
                                           getMessageSource().getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage("text.account.order.orderBreadcrumb", new Object[] { orderDetails.getCode() },
                                                                              "Order {0}", getI18nService().getCurrentLocale()),
                                           null));
            model.addAttribute("breadcrumbs", breadcrumbs);

        } catch (final UnknownIdentifierException e) {
            LOG.error("Attempted to load a order that does not exist or is not visible", e);
            GlobalMessages.addErrorMessage(model, "text.account.order.notfound");
            return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL;
        } catch (final Exception e) {
            LOG.error("Attempt to load a order failed", e);
            GlobalMessages.addErrorMessage(model, "text.account.order.notfound");
            return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL;
        }

        final ContentPageModel contentPage = getContentPageForLabelOrId(ORDER_DETAIL_CMS_PAGE);
        storeCmsPageInModel(model, contentPage);
        getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
        setUpMetaDataForContentPage(model, contentPage);
        return ControllerConstants.Views.Pages.Account.AccountOrderDetailsPage;
    }

    @RequestMapping(value = ORDER_HISTORY_DETAILS_DOWNLOAD_URL, method = RequestMethod.GET)
    public void downloadOrderDetails(@PathVariable("orderCode") final String orderCode, @PathVariable("exportFormat") final String exportFormat,
                                     final HttpServletResponse response) throws CMSItemNotFoundException {
        final OrderData orderDetails = orderHistoryFacade.getOrderForCode(orderCode);

        if (orderDetails == null) {
            LOG.warn("No Order found with code " + orderCode);
            return;
        }

        final String format = StringUtils.isBlank(exportFormat) ? getConfigurationService().getConfiguration().getString("distrelec.exportCsvFileNameSuffix")
                                                                : exportFormat;

        final String exportFileNamePrefix = getConfigurationService().getConfiguration().getString("distrelec.exportOrderFileNamePrefix");
        final File downloadFile = "csv".equalsIgnoreCase(exportFormat) ? exportOrderToCSV(orderDetails, exportFileNamePrefix)
                                                                       : distExportFacade.exportOrder(orderDetails, format, exportFileNamePrefix);

        try {
            setUpDownloadFile(response, downloadFile, format);
        } catch (final IOException e) {
            logError(LOG, "Could not set up file {} for download", e, downloadFile.getPath());
        }
    }

    /**
     * Retrieve the invoice history for the current user
     */
    @RequestMapping(value = INVOICE_HISTORY_PAGE_URL, method = { RequestMethod.GET, RequestMethod.POST })
    public String invoiceHistory(@Valid final InvoiceHistoryForm invoiceHistoryForm, final BindingResult bindingResult, final HttpServletRequest request,
                                 final Model model)
                                                    throws CMSItemNotFoundException {

        // Retrieve data from database
        getInvoiceHistory(invoiceHistoryForm, bindingResult, request, model);

        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", "text.account.invoiceHistory"));
        model.addAttribute("stateList", INVOICE_STATUS_LIST);
        model.addAttribute("sortCode", invoiceHistoryForm.getSort() + ":" + invoiceHistoryForm.getSortType().toLowerCase());
        model.addAttribute("showMode", invoiceHistoryForm.getShow());
        model.addAttribute("sortTypeList", INVOICE_HISTORY_SORT_TYPE_LIST);

        storeCmsPageInModel(model, getContentPageForLabelOrId(INVOICE_HISTORY_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(INVOICE_HISTORY_CMS_PAGE));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountInvoiceHistoryPage;
    }

    /**
     * Retrieve the invoice history for the current user
     *
     * @deprecated this method is obsolete since we have a redirection rule on web server side.
     */
    @Deprecated
    @RequestMapping(value = SHOW_INVOICE_HISTORY_DOCUMENT_URL, method = RequestMethod.GET)
    public void showInvoiceHistoryPdfDocument(final HttpServletRequest request, final HttpServletResponse response) {
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentType("application/pdf");

        try {
            WritePDFDocumentsUtil.writePdftoResponse(request, response);
        } catch (final Exception e) {
            LOG.error("Unable to read pdf document ", e);
        }
    }

    @RequestMapping(value = INVOICE_HISTORY_NEXT_PAGE_URL, method = RequestMethod.GET, produces = { "application/json" })
    public String nextInvoiceHistoryPage(@Valid final InvoiceHistoryForm invoiceHistoryForm, final BindingResult bindingResult,
                                         final HttpServletRequest request, final Model model) {
        addGlobalModelAttributes(model, request);
        getInvoiceHistory(invoiceHistoryForm, bindingResult, request, model);
        return ControllerConstants.Views.Fragments.Account.ShowMoreInvoices;
    }

    @GetMapping(value = ORDER_HISTORY_RETURN_ITEMS_PAGE_URL)
    public String returnItemsPage(@PathVariable("orderCode") final String orderCode, final Model model,
                                  final HttpServletRequest request) throws CMSItemNotFoundException {

        try {
            final OrderData orderDetails = orderHistoryFacade.getOrderForCode(orderCode);

            if (orderDetails == null) {
                throw new UnknownIdentifierException("No Order found with code " + orderCode);
            }

            getDistDigitalDatalayerFacade().populateOrderReturnData(getDigitalDatalayerFromModel(model),
                                                                    orderDetails);

            // Check the order status
            if (orderDetails.getStatus() != OrderStatus.ERP_STATUS_RECIEVED && orderDetails.getStatus() != OrderStatus.ERP_STATUS_SHIPPED) {
                return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + orderDetails.getCode();
            }

            model.addAttribute("orderData", orderDetails);
            model.addAttribute("isValidReturnClaim", BooleanUtils.toBoolean(orderDetails.getValidForReturn()));
            model.addAttribute("canSubmitRmaRequest", canSubmitRmaRequest(orderDetails));

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", null);
            final String orderHistoryURL = MY_ACCOUNT_PAGE_URL + ORDER_HISTORY_PAGE_URL;
            breadcrumbs.add(new Breadcrumb(orderHistoryURL,
                                           getMessageSource().getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb(orderHistoryURL + "/order-details/" + orderDetails.getCode(), getMessageSource().getMessage(
                                                                                                                                       "text.account.order.orderBreadcrumb",
                                                                                                                                       new Object[] { orderDetails.getCode() },
                                                                                                                                       "Order {0}",
                                                                                                                                       getI18nService().getCurrentLocale()),
                                           null));
            breadcrumbs.add(new Breadcrumb("#",
                                           getMessageSource().getMessage("text.account.order.returnItemsBreadcrumb", null, "Return Items",
                                                                         getI18nService().getCurrentLocale()),
                                           null));
            model.addAttribute("breadcrumbs", breadcrumbs);

        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load a order that does not exist or is not visible", e);
            GlobalMessages.addErrorMessage(model, "text.account.order.notfound");
            return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL;
        }

        CreateRMARequestForm createRMARequestForm = (CreateRMARequestForm) model.asMap().get("submittedForm");
        if (createRMARequestForm == null) {
            createRMARequestForm = new CreateRMARequestForm();
        }
        model.addAttribute("createRMARequestForm", createRMARequestForm);

        storeCmsPageInModel(model, getContentPageForLabelOrId(RETURN_ITEMS_CMS_PAGE));
        getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(RETURN_ITEMS_CMS_PAGE));
        addGlobalModelAttributes(model, request);
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        return ControllerConstants.Views.Pages.Account.AccountReturnItemsPage;
    }

    private boolean canSubmitRmaRequest(OrderData orderDetails) {
        return orderDetails.getEntries().stream()
                           .map(OrderEntryData::getRmaData)
                           .anyMatch(rmaData -> !rmaData.isNotAllowed());
    }

    /**
     * Submit a return request for some ordered product
     *
     * @param createRMARequestForm
     *            the data of the return request
     * @param model
     * @return the account return request page
     */
    @PostMapping(value = ORDER_HISTORY_RETURN_ITEMS_PAGE_URL)
    public String returnProducts(@Valid @ModelAttribute final CreateRMARequestForm createRMARequestForm, final BindingResult bindingResult, final Model model,
                                 final RedirectAttributes redirectAttributes) {

        try {

            final OrderData orderDetails = orderHistoryFacade.getOrderForCode(createRMARequestForm.getOrderId());
            if (orderDetails == null) {
                LOG.warn("No Order found with code " + createRMARequestForm.getOrderId());
                redirectAttributes.addFlashAttribute("errorMsgKey", "lightboxreturnrequest.process.notfound");
                return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + createRMARequestForm.getOrderId() + "/return-items";
            }

            if (orderDetails.getStatus() != OrderStatus.ERP_STATUS_SHIPPED && orderDetails.getStatus() != OrderStatus.ERP_STATUS_RECIEVED) {
                LOG.warn("The order " + createRMARequestForm.getOrderId() + " is not yet shipped");
                redirectAttributes.addFlashAttribute("errorMsgKey", "lightboxreturnrequest.process.notshipped");
                return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + orderDetails.getCode() + "/return-items";
            }

            final boolean isRmaRaised = isRmaRaised(createRMARequestForm.getOrderItems());
            if (isRmaRaised) {
                LOG.error("RMA already raised");
                redirectAttributes.addFlashAttribute("isRmaRaised", isRmaRaised);
                return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + orderDetails.getCode() + "/return-items";
            }

            if (BooleanUtils.isFalse(orderDetails.getValidForReturn())) {
                LOG.error("Date to raise return request is expired.");
                redirectAttributes.addFlashAttribute("isValidReturnClaim", BooleanUtils.toBoolean(orderDetails.getValidForReturn()));
                return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + orderDetails.getCode() + "/return-items";
            }

            final boolean invalidQuantity = isReturnRaisedQuantityAvailable(orderDetails.getEntries(), createRMARequestForm.getOrderItems(), bindingResult);
            if (invalidQuantity) {
                LOG.error("Return Quantity entered is greater than available Quantity.");
                redirectAttributes.addFlashAttribute("invalidQuantity", invalidQuantity);
                return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + orderDetails.getCode() + "/return-items";
            }

            if (bindingResult.hasErrors()) {
                LOG.error("Binding ERRORS: {}", bindingResult.getFieldErrors());
                redirectAttributes.addFlashAttribute("errorMsgKey", "form.global.error");
                return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + orderDetails.getCode() + "/return-items";
            }

            final CreateRMAResponseData createRMAResponseData = returnRequestFacade.createRMACreateRequest(createRMARequestForm);
            if (createRMAResponseData != null) {
                DistReturnRequestFacade.UserRMARequestDataWrapper userRMARequestDataWrapper = new DistReturnRequestFacade.UserRMARequestDataWrapper();
                userRMARequestDataWrapper.setCreateRMARequestForm(createRMARequestForm);
                userRMARequestDataWrapper.setRmaId(createRMAResponseData.getRmaNumber());
                getReturnRequestFacade().sendUserReturnRequestEmail(userRMARequestDataWrapper);
                final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
                getDistDigitalDatalayerFacade().populateReturnResponse(digitalDatalayer, createRMARequestForm, createRMAResponseData);
            } else {
                redirectAttributes.addFlashAttribute("errorMsgKey", "lightboxreturnrequest.process.error");
                return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + createRMARequestForm.getOrderId() + "/return-items";
            }
            final String createRMARequestJson = convertCreateRMARequestFormToJson(createRMARequestForm, orderDetails);
            redirectAttributes.addFlashAttribute("createRMAResponseData", createRMAResponseData);
            redirectAttributes.addFlashAttribute("createRMARequestJson", createRMARequestJson);
        } catch (final Exception wse) {
            LOG.error("An error has occur while creating the RMA object", wse);
            redirectAttributes.addFlashAttribute("errorMsgKey", "lightboxreturnrequest.process.error");
            return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + createRMARequestForm.getOrderId() + "/return-items";
        }
        redirectAttributes.addFlashAttribute("isRmaCreated", true);
        return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + createRMARequestForm.getOrderId() + "/return-items/confirmation";
    }

    private String convertCreateRMARequestFormToJson(@ModelAttribute @Valid CreateRMARequestForm createRMARequestForm, OrderData orderDetails) {
        createRMARequestForm.setOrderDate(orderDetails.getOrderDate());
        createRMARequestForm.setOrderItems(createRMARequestForm.getOrderItems().stream()
                                                               .filter(this::isReturnOrderItemSubmitted)
                                                               .collect(Collectors.toList()));
        return convertObjectToJson(createRMARequestForm);
    }

    @GetMapping(value = ORDER_HISTORY_RETURN_ITEMS_PAGE_URL + "/confirmation")
    public String returnItemsConfirmationPage(@PathVariable final String orderCode, final Model model,
                                              final HttpServletRequest request) throws CMSItemNotFoundException {
        try {
            final OrderData orderDetails = orderHistoryFacade.getOrderForCode(orderCode);
            final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();

            if (orderDetails == null) {
                throw new UnknownIdentifierException("No Order found with code " + orderCode);
            }

            if (isInitialRedirectAfterSubmitting(model)) {
                saveDataToSession(orderCode, model);
            } else {
                if (isDataFromTheSameOrder(orderCode)) {
                    retrieveDataFromSession(model);
                } else {
                    clearDataFromSession();
                    return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL + "/order-details/" + orderCode + "/return-items";
                }
            }

            model.addAttribute("email",
                               (null != customerData && StringUtils.isNotEmpty(customerData.getEmail())) ? customerData.getEmail() : StringUtils.EMPTY);
            model.addAttribute("orderData", orderDetails);

            final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", null);
            final String orderHistoryURL = MY_ACCOUNT_PAGE_URL + ORDER_HISTORY_PAGE_URL;
            breadcrumbs.add(new Breadcrumb(orderHistoryURL,
                                           getMessageSource().getMessage("text.account.orderHistory", null, getI18nService().getCurrentLocale()), null));
            breadcrumbs.add(new Breadcrumb(orderHistoryURL + "/order-details/" + orderDetails.getCode(), getMessageSource().getMessage(
                                                                                                                                       "text.account.order.orderBreadcrumb",
                                                                                                                                       new Object[] { orderDetails.getCode() },
                                                                                                                                       "Order {0}",
                                                                                                                                       getI18nService().getCurrentLocale()),
                                           null));
            breadcrumbs.add(new Breadcrumb("#",
                                           getMessageSource().getMessage("text.account.order.returnItemsBreadcrumb", null, "Return Items",
                                                                         getI18nService().getCurrentLocale()),
                                           null));
            model.addAttribute("breadcrumbs", breadcrumbs);
        } catch (final UnknownIdentifierException e) {
            LOG.warn("Attempted to load a order that does not exist or is not visible", e);
            GlobalMessages.addErrorMessage(model, "text.account.order.notfound");
            return REDIRECT_MY_ACCOUNT + ORDER_HISTORY_PAGE_URL;
        }

        storeCmsPageInModel(model, getContentPageForLabelOrId(RETURN_ITEMS_CONFIRMATION_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(RETURN_ITEMS_CONFIRMATION_CMS_PAGE));
        addGlobalModelAttributes(model, request);

        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
        getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);

        return ControllerConstants.Views.Pages.Account.AccountReturnRequestPage;
    }

    private boolean isInitialRedirectAfterSubmitting(Model model) {
        return model.containsAttribute("createRMAResponseData") && model.containsAttribute("createRMARequestJson");
    }

    private boolean isDataFromTheSameOrder(@PathVariable String orderCode) {
        return StringUtils.equals(orderCode, sessionService.getAttribute("rmaOrderCode"));
    }

    private void clearDataFromSession() {
        sessionService.removeAttribute("createRMAResponseData");
        sessionService.removeAttribute("createRMARequestJson");
    }

    private void retrieveDataFromSession(Model model) {
        model.addAttribute("createRMAResponseData", sessionService.getAttribute("createRMAResponseData"));
        model.addAttribute("createRMARequestJson", sessionService.getAttribute("createRMARequestJson"));
    }

    private void saveDataToSession(@PathVariable String orderCode, Model model) {
        sessionService.setAttribute("createRMAResponseData", model.getAttribute("createRMAResponseData"));
        sessionService.setAttribute("createRMARequestJson", model.getAttribute("createRMARequestJson"));
        sessionService.setAttribute("rmaOrderCode", orderCode);
    }

    @RequestMapping(value = "/product-box", method = { RequestMethod.GET, RequestMethod.POST }, produces = { "application/json" })
    public String showProductBox(@RequestParam(value = "value", required = true, defaultValue = "true") final boolean value, final Model model,
                                 final HttpServletRequest request) {
        final boolean result = getCustomerFacade().updateShowProductBox(value);
        model.addAttribute("status", result ? "OK" : "NOK");
        model.addAttribute("message", result ? StringUtils.EMPTY : "unknown error");
        return ControllerConstants.Views.Pages.Account.AccountProductBoxJson;
    }

    private boolean isReturnOrderItemSubmitted(CreateRMAOrderEntryDataForm orderEntryDataForm) {
        return orderEntryDataForm.getArticleNumber() != null &&
                orderEntryDataForm.getQuantity() != null &&
                orderEntryDataForm.getReturnReasonID() != null;
    }

    private boolean shouldProvideReturnAssistance(CreateRMARequestForm createRMARequestForm) {
        String returnReasonCodesWithAssistanceConfig = getConfigurationService().getConfiguration().getString(RMA_RETURN_CODES_WITH_ASSISTANCE_CONIG);
        String[] returnReasonCodesWithAssistanceConfigList = null;

        if (StringUtils.isNotEmpty(returnReasonCodesWithAssistanceConfig)) {
            returnReasonCodesWithAssistanceConfigList = returnReasonCodesWithAssistanceConfig.split(",");
        }

        Set<Integer> reasonCodesRequiringReturnAssistance = new HashSet<>();

        if (returnReasonCodesWithAssistanceConfigList != null) {
            for (String returnReasonCodeText : returnReasonCodesWithAssistanceConfigList) {
                try {
                    Integer returnReasonCode = Integer.parseInt(returnReasonCodeText);
                    reasonCodesRequiringReturnAssistance.add(returnReasonCode);
                } catch (NumberFormatException e) {
                    LOG.error("Return reason code {} from config {} cannot be parsed into integer", returnReasonCodeText,
                              RMA_RETURN_CODES_WITH_ASSISTANCE_CONIG);
                }
            }
        } else {
            LOG.error("Config item {} is not set", RMA_RETURN_CODES_WITH_ASSISTANCE_CONIG);
        }

        return createRMARequestForm.getOrderItems().stream()
                                   .filter(orderItem -> !StringUtils.isEmpty(orderItem.getReturnReasonID()))
                                   .map(CreateRMAOrderEntryDataForm::getReturnReasonID)
                                   .map(Integer::parseInt)
                                   .anyMatch(reasonCodesRequiringReturnAssistance::contains);

    }

    protected void prepareFormDataForProfileUpdateError(final Model model) {
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        model.addAttribute("customerType", customerData.getCustomerType().getCode());
        model.addAttribute("titleData", userFacade.getTitles());
        model.addAttribute("departmentData", userFacade.getDepartments());
        model.addAttribute("functionData", userFacade.getFunctions());

        // Budget Data
        model.addAttribute("budget", customerData.getBudget());

        // Permission Data
        customerData.getPermissions();

        // Mail Data
        model.addAttribute("currentEmail", customerData.getDisplayUid());
        model.addAttribute("updateEmailForm", new UpdateEmailForm());

        // Password Data
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());

        // Newsletter Data
        final UpdateNewsletterForm updateNewsletterForm = new UpdateNewsletterForm();
        updateNewsletterForm.setMarketingConsent(false);
        model.addAttribute("updateNewsletterForm", updateNewsletterForm);
    }

    protected void prepareFormDataForEmailUpdateError(final Model model, final boolean isMigrated) {
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        model.addAttribute("customerType", customerData.getCustomerType().getCode());
        model.addAttribute("titleData", userFacade.getTitles());

        // Profile Data
        UpdateProfileForm updateProfileForm = new UpdateProfileForm();
        if (isMigrated) {
            updateProfileForm = new UpdateMigratedProfileForm();
            ((UpdateMigratedProfileForm) updateProfileForm).setEmail(customerData.getEmail());
        }
        updateProfileForm.setTitleCode(customerData.getTitleCode());
        updateProfileForm.setFirstName(customerData.getFirstName());
        updateProfileForm.setLastName(customerData.getLastName());
        updateProfileForm.setFunctionCode(customerData.getFunctionCode());

        if (customerData.getContactAddress() != null) {
            final AddressData contactAddress = customerData.getContactAddress();
            updateProfileForm.setPhone(contactAddress.getPhone());
            updateProfileForm.setDepartmentCode(contactAddress.getDepartmentCode());
            updateProfileForm.setMobilePhone(contactAddress.getCellphone());
            updateProfileForm.setFax(contactAddress.getFax());
        }

        if (isMigrated) {
            model.addAttribute("updateMigratedProfileForm", updateProfileForm);
            model.addAttribute(new UpdateEmailForm());
        } else {
            model.addAttribute("updateProfileForm", updateProfileForm);
            model.addAttribute(new UpdateLoginForm());
        }

        model.addAttribute("isMigratedUser", Boolean.valueOf(isMigrated));

        // Budget Data
        model.addAttribute("budget", customerData.getBudget());

        // Permission Data
        customerData.getPermissions();

        // Mail Data
        model.addAttribute("currentEmail", customerData.getDisplayUid());

        // Password Data
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());

        // Newsletter Data
        final UpdateNewsletterForm updateNewsletterForm = new UpdateNewsletterForm();
        updateNewsletterForm.setMarketingConsent(false);
        model.addAttribute("updateNewsletterForm", updateNewsletterForm);
    }

    protected void prepareFormDataForPasswordUpdateError(final Model model) {
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        model.addAttribute("customerType", customerData.getCustomerType().getCode());
        model.addAttribute("titleData", userFacade.getTitles());

        // Profile Data
        final UpdateProfileForm updateProfileForm = new UpdateProfileForm();
        updateProfileForm.setTitleCode(customerData.getTitleCode());
        updateProfileForm.setFirstName(customerData.getFirstName());
        updateProfileForm.setLastName(customerData.getLastName());
        updateProfileForm.setFunctionCode(customerData.getFunctionCode());

        if (customerData.getContactAddress() != null) {
            final AddressData contactAddress = customerData.getContactAddress();
            updateProfileForm.setDepartmentCode(contactAddress.getDepartmentCode());
            updateProfileForm.setPhone(contactAddress.getPhone());
            updateProfileForm.setMobilePhone(contactAddress.getCellphone());
            updateProfileForm.setFax(contactAddress.getFax());
        }

        model.addAttribute("updateProfileForm", updateProfileForm);

        // Budget Data
        model.addAttribute("budget", customerData.getBudget());

        // Permission Data
        customerData.getPermissions();

        // Mail Data
        model.addAttribute("currentEmail", customerData.getDisplayUid());
        model.addAttribute("updateEmailForm", new UpdateEmailForm());

        // Newsletter Data
        final UpdateNewsletterForm updateNewsletterForm = new UpdateNewsletterForm();
        updateNewsletterForm.setMarketingConsent(false);
        model.addAttribute("updateNewsletterForm", updateNewsletterForm);
    }

    protected void prepareFormDataForNewsletterUpdateError(final Model model) {
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        model.addAttribute("customerType", customerData.getCustomerType().getCode());
        model.addAttribute("titleData", userFacade.getTitles());

        // Profile Data
        final UpdateProfileForm updateProfileForm = new UpdateProfileForm();
        updateProfileForm.setTitleCode(customerData.getTitleCode());
        updateProfileForm.setFirstName(customerData.getFirstName());
        updateProfileForm.setLastName(customerData.getLastName());
        updateProfileForm.setFunctionCode(customerData.getFunctionCode());

        if (customerData.getContactAddress() != null) {
            final AddressData contactAddress = customerData.getContactAddress();
            updateProfileForm.setDepartmentCode(contactAddress.getDepartmentCode());
            updateProfileForm.setPhone(contactAddress.getPhone());
            updateProfileForm.setMobilePhone(contactAddress.getCellphone());
            updateProfileForm.setFax(contactAddress.getFax());
        }

        model.addAttribute("updateProfileForm", updateProfileForm);

        // Budget Data
        model.addAttribute("budget", customerData.getBudget());

        // Permission Data
        customerData.getPermissions();

        // Mail Data
        model.addAttribute("currentEmail", customerData.getDisplayUid());
        model.addAttribute("updateEmailForm", new UpdateEmailForm());

        // Password Data
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());
    }

    protected void prepareFormData(final Model model) {
        final CustomerData customerData = getB2bCustomerFacade().getCurrentCustomer();
        final boolean isMigratedUser = !EmailValidator.getInstance().isValid(customerData.getUid());
        model.addAttribute("customerType", customerData.getCustomerType().getCode());
        model.addAttribute("customerId", customerData.getUnit().getErpCustomerId());
        model.addAttribute("isMigratedUser", Boolean.valueOf(isMigratedUser));
        model.addAttribute("titleData", userFacade.getTitles());
        model.addAttribute("departmentData", userFacade.getDepartments());
        model.addAttribute("functionData", userFacade.getFunctions());

        // Profile Data
        UpdateProfileForm updateProfileForm = null;
        if (isMigratedUser) {
            model.addAttribute("updateLoginForm", new UpdateLoginForm());
            updateProfileForm = new UpdateMigratedProfileForm();
            ((UpdateMigratedProfileForm) updateProfileForm).setEmail(customerData.getEmail());
        } else {
            model.addAttribute("updateEmailForm", new UpdateEmailForm());
            updateProfileForm = new UpdateProfileForm();
        }
        updateProfileForm.setTitleCode(customerData.getTitleCode());
        updateProfileForm.setFirstName(customerData.getFirstName());
        updateProfileForm.setLastName(customerData.getLastName());
        updateProfileForm.setFunctionCode(customerData.getFunctionCode());

        if (customerData.getContactAddress() != null) {
            final AddressData contactAddress = customerData.getContactAddress();
            updateProfileForm.setDepartmentCode(contactAddress.getDepartmentCode());
            updateProfileForm.setPhone(contactAddress.getPhone());
            updateProfileForm.setMobilePhone(contactAddress.getCellphone());
            updateProfileForm.setFax(contactAddress.getFax());
        }

        model.addAttribute(isMigratedUser ? "updateMigratedProfileForm" : "updateProfileForm", updateProfileForm);

        // Budget Data
        model.addAttribute("budget", customerData.getBudget());

        // Mail Data
        model.addAttribute("currentEmail", customerData.getDisplayUid());

        // Password Data
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());
        final String connfirmationSent = getSessionService().getAttribute(WebConstants.CONSENT_CONFIRMATION_SENT);
        if (!StringUtils.isBlank(connfirmationSent)) {
            getSessionService().removeAttribute(WebConstants.CONSENT_CONFIRMATION_SENT);
            model.addAttribute(GlobalMessages.WARN_MESSAGES_HOLDER, Collections.singletonList(connfirmationSent));
            model.addAttribute("consentStatus", "info");
        }
        final String consentUpdateFailed = getSessionService().getAttribute(WebConstants.CONSENT_CONFIRMATION_FAILED);
        if (!StringUtils.isBlank(consentUpdateFailed)) {
            getSessionService().removeAttribute(WebConstants.CONSENT_CONFIRMATION_FAILED);
            model.addAttribute("consentStatus", "error");
            model.addAttribute(GlobalMessages.WARN_MESSAGES_HOLDER, Collections.singletonList(consentUpdateFailed));
        }
        final String consentUpdateSuccess = getSessionService().getAttribute(WebConstants.CONSENT_CONFIRMATION_SUCCESS);
        if (!StringUtils.isBlank(consentUpdateSuccess)) {
            getSessionService().removeAttribute(WebConstants.CONSENT_CONFIRMATION_SUCCESS);
            model.addAttribute("consentStatus", "success");
        }
        // Newsletter Data
        final UpdateNewsletterForm updateNewsletterForm = new UpdateNewsletterForm();

        updateNewsletterForm.setSubscribePhoneMarketing(customerData.isSubscribePhoneMarketing());
        updateNewsletterForm.setMarketingConsent(false);

        model.addAttribute("updateNewsletterForm", updateNewsletterForm);
        model.addAttribute("optedForObsolescence", customerData.isOptedForObsolescence());
        model.addAttribute("allCatSelected", customerData.isAllObsolCatSelected());
        model.addAttribute("categories", customerData.getCategories());
    }

    protected void prepareFormDataForOrderDetails(final Model model) {

        if (!model.containsAttribute("returnRequestForm")) {
            model.addAttribute("returnRequestForm", new ReturnRequestForm());
        }

        if (!model.containsAttribute("orderApprovalDecisionForm")) {
            model.addAttribute("orderApprovalDecisionForm", new OrderApprovalDecisionForm());
        }

        if (!model.containsAttribute("reviewForm")) {
            model.addAttribute("reviewForm", new CheckoutReviewForm());
        }
    }

    @InitBinder
    protected void initBinder(final HttpServletRequest request, final ServletRequestDataBinder binder) {
        DateFormat dateFormat;
        String pattern = getTextStoreDateFormatForConversion();
        final Locale locale = getCurrentLocale();

        try {
            dateFormat = new SimpleDateFormat(StringUtils.isNotBlank(pattern) ? pattern : INVOICE_HISTORY_DEFAULT_DATE_PATTERN, locale);
        } catch (final IllegalArgumentException e) {
            dateFormat = new SimpleDateFormat(INVOICE_HISTORY_DEFAULT_DATE_PATTERN, locale);
        }
        final CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }

    private String getTextStoreDateFormatForConversion() {
        String pattern = null;
        final Locale locale = getCurrentLocale();
        try {
            pattern = getMessageSource().getMessage("text.store.dateformat", null, new Locale(locale.getLanguage(), getCurrentCountry().getIsocode()));
        } catch (final Exception exp) {
            try {
                pattern = getMessageSource().getMessage("text.store.dateformat", null, locale);
            } catch (final Exception exp1) {
                // NOP
            }
        }
        return pattern;
    }

    /**
     * Convert the {@code ReturnRequestForm} parameter to a {@code ReturnRequestData}
     *
     * @param returnRequestForm
     * @return a new instance of {@code ReturnRequestData}
     */
    protected ReturnRequestData createReturnRequestData(final ReturnRequestForm returnRequestForm) {
        final ReturnRequestData returnReqData = new ReturnRequestData();
        returnReqData.setOrderCode(returnRequestForm.getOrderCode());
        returnReqData.setPurchaseDate(returnRequestForm.getPurchaseDate());

        if (CollectionUtils.isNotEmpty(returnRequestForm.getProducts())) {
            returnReqData.setEntries(returnRequestForm.getProducts().stream().map(formRow -> {
                final ReturnRequestEntryData entry = new ReturnRequestEntryData();
                entry.setQuantity(formRow.getQuantity());
                entry.setNote(formRow.getNote());
                entry.setProductNumber(formRow.getProductCode());
                entry.setPackaging(formRow.getPackaging());
                entry.setReason(formRow.getReason());
                entry.setSerialNumbers(formRow.getSerialNumbers());
                entry.setReplacement(formRow.isReplacement());
                return entry;
            }).collect(Collectors.toList()));
        }

        return returnReqData;
    }

    /**
     * Find the order approval requests.
     *
     * @param orderApprovalForm
     *            the form parameters to use for query filter
     * @param bindingResult
     *            the binding result
     * @param model
     *            the model
     * @param wfActionTypes
     *            the array of {@codeWorkflowActionType}
     * @param wfActionStatus
     *            the array of {@code WorkflowActionStatus}
     * @throws CMSItemNotFoundException
     */
    protected void getOrderApprovals(final OrderApprovalForm orderApprovalForm, final BindingResult bindingResult, final Model model,
                                     final WorkflowActionType[] wfActionTypes, final WorkflowActionStatus[] wfActionStatus, final HttpServletRequest request)
                                                                                                                                                              throws CMSItemNotFoundException {
        if (orderApprovalForm.getSort() != null) {
            final String tab[] = orderApprovalForm.getSort().split(":");
            if (tab.length > 1) {
                orderApprovalForm.setSort(tab[0]);
            }

            if (tab.length >= 2) {
                orderApprovalForm.setSortType(tab[1]);
            }

            if (!ORDER_HISTORY_SORT_TYPE_LIST_MIN.contains(orderApprovalForm.getSort())) {
                orderApprovalForm.setSort("byDate");
            }
        } else {
            orderApprovalForm.setSort("byDate");
        }

        final PageableData pageableData = createPageableData(orderApprovalForm.getPage(), orderApprovalForm.getPageSize(), orderApprovalForm.getSort(),
                                                             orderApprovalForm.getShow(), orderApprovalForm.getSortType());
        final SearchPageData<? extends B2BOrderApprovalData> searchPageData = getOrderFacade().getPagedOrdersForApproval(wfActionTypes, wfActionStatus,
                                                                                                                         pageableData);
        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (DistPaginationData) searchPageData.getPagination(), model, true);
        storeSearchResultToModel(model, searchPageData, orderApprovalForm.getShow());

        model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", "text.account.orderApprovalDashboard"));
        storeCmsPageInModel(model, getContentPageForLabelOrId(ORDER_APPROVAL_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ORDER_APPROVAL_CMS_PAGE));
        model.addAttribute("orderApprovalDashboardForm", orderApprovalForm);
        model.addAttribute("currentOrder", orderApprovalForm.getSort() + ":" + orderApprovalForm.getSortType().toLowerCase());
        model.addAttribute("sortTypeList", ORDER_HISTORY_SORT_TYPE_LIST);

        if (bindingResult.hasErrors()) {
            LOG.error("Binding ERRORS: {}", bindingResult.getFieldErrors());
            GlobalMessages.addErrorMessage(model, "form.global.error");
        }

    }

    protected void getOrderHistory(final OrderHistoryForm orderHistoryForm, final BindingResult bindingResult, final HttpServletRequest request,
                                   final Model model) {

        Date fromDate = null;
        Date toDate = null;
        if (StringUtils.isNotEmpty(orderHistoryForm.getFromDate())) {
            try {
                fromDate = parseLocalSpecificDate(orderHistoryForm.getFromDate());
            } catch (final ParseException e) {
                bindingResult.rejectValue("fromDate", "validate.error.date.format");
            }
        }

        if (StringUtils.isNotEmpty(orderHistoryForm.getToDate())) {
            try {
                toDate = parseLocalSpecificDate(orderHistoryForm.getToDate());
            } catch (final ParseException e) {
                bindingResult.rejectValue("toDate", "validate.error.date.format");
            }
        }

        if ((fromDate != null) && (toDate != null) && fromDate.after(toDate)) {
            bindingResult.rejectValue("fromDate", "validate.error.date.before");
        }

        if (orderHistoryForm.getOrderNumber() != null && !orderHistoryForm.getOrderNumber().trim().matches("[A-Za-z0-9\\*]*")) {
            bindingResult.rejectValue("orderNumber", "validate.error.alphanumeric");
        }

        // if (orderHistoryForm.getReference() != null && !orderHistoryForm.getReference().trim().matches("[A-Za-z0-9\\*]*")) {
        // bindingResult.rejectValue("reference", "validate.error.alphanumeric");
        // }

        if (bindingResult.hasErrors()) {
            LOG.error("Binding ERRORS: {}", bindingResult.getFieldErrors());
            GlobalMessages.addErrorMessage(model, "form.global.error");
            model.addAttribute("orderHistoryForm", orderHistoryForm);
            return;
        }

        if (orderHistoryForm.getSort() != null) {
            final String tab[] = orderHistoryForm.getSort().split(":");
            if (tab.length >= 1) {
                orderHistoryForm.setSort(tab[0]);
            }

            if (tab.length >= 2) {
                orderHistoryForm.setSortType(tab[1]);
            }

            if (!ORDER_HISTORY_SORT_TYPE_LIST_MIN.contains(orderHistoryForm.getSort())) {
                orderHistoryForm.setSort("byDate");
            }
        } else {
            orderHistoryForm.setSort("byDate");
        }

        if (orderHistoryForm.getPage() < 0) {
            orderHistoryForm.setPage(0);
        }

        // Creating the filter
        final String[] orderStatuses;

        orderHistoryForm.setStatus(getStatusCode(orderHistoryForm.getStatus()));

        if ("ALL".equals(orderHistoryForm.getStatus())) {
            final List<DistOrderStatusModel> distOrderStatusList = codelistService.getAllDistOrderStatus();
            orderStatuses = new String[distOrderStatusList.size()];
            for (int i = 0; i < distOrderStatusList.size(); i++) {
                orderStatuses[i] = distOrderStatusList.get(i).getCode();
            }
        } else {
            final DistOrderStatusModel orderStatus = codelistService.getDistOrderStatusForHybrisOrderStatusCode(orderHistoryForm.getStatus());
            orderStatuses = new String[] { orderStatus != null ? orderStatus.getCode() : null };
        }

        // Handle paged search results
        final DistOrderHistoryPageableData pageableData = createPageableData(orderHistoryForm);
        final SearchPageData<OrderHistoryData> searchPageData = orderHistoryFacade.getOrderHistory(pageableData, orderStatuses);
        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (DistPaginationData) searchPageData.getPagination(), model, true);
        storeSearchResultToModel(model, searchPageData, orderHistoryForm.getShow());
        if (isCompanyOrderAdminUser()) {
            model.addAttribute("contactsOfCustomer", getB2bCustomerFacade().searchEmployees(null, null, null, true));
        }
        model.addAttribute("orderHistoryForm", orderHistoryForm);

        // Populate RMA data in digital datalayer
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populateReturnData(digitalDatalayer, searchPageData);
    }

    /**
     * Retrieve the quotation history from the ERP
     *
     * @param quoteHistoryForm
     * @param bindingResult
     * @param request
     * @param model
     * @return the quotation history page
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = QUOTATION_HISTORY_PAGE_URL, method = { RequestMethod.GET, RequestMethod.POST })
    public String quotationHistory(@Valid final QuotationHistoryForm quoteHistoryForm, final BindingResult bindingResult, final HttpServletRequest request,
                                   final Model model)
                                                      throws CMSItemNotFoundException {

        if (getBaseStoreService().getCurrentBaseStore().getQuotationsEnabled()) {
            // Retrieve data from database
            getQuotationHistory(quoteHistoryForm, bindingResult, request, model);

            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", "text.account.quotes.myquotes"));
            model.addAttribute("stateList", getQuotationStatusCodes());
            model.addAttribute("sortTypeList", QUOTATION_HISTORY_SORT_TYPE_LIST);
            model.addAttribute("sortCode", quoteHistoryForm.getSort() + ":" + quoteHistoryForm.getSortType().toLowerCase());
            model.addAttribute("showMode", quoteHistoryForm.getShow());

            storeCmsPageInModel(model, getContentPageForLabelOrId(QUOTATION_HISTORY_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(QUOTATION_HISTORY_CMS_PAGE));
            addGlobalModelAttributes(model, request);
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
            return ControllerConstants.Views.Pages.Account.AccountQuotationHistoryPage;
        } else {
            return FORWARD_TO_404;
        }
    }

    /**
     * Retrieve the quotation details from the ERP
     *
     * @param quotationId
     *            the quotation ID.
     * @param model
     * @param request
     * @return the quotation detail page.
     * @throws CMSItemNotFoundException
     */
    @RequestMapping(value = QUOTATION_HISTORY_DETAIL_PAGE_URL, method = { RequestMethod.GET, RequestMethod.POST })
    public String quotationDetails(@PathVariable("quotationCode") final String quotationId, final Model model,
                                   final HttpServletRequest request) throws CMSItemNotFoundException {
        if (getBaseStoreService().getCurrentBaseStore().getQuotationsEnabled()) {
            // Retrieve data from database
            getQuotationDetails(quotationId, request, model);

            model.addAttribute("breadcrumbs", accountBreadcrumbBuilder.getBreadcrumbs("text.account.orderManager", "text.account.quotes.myquotes"));
            model.addAttribute("stateList", QUOTE_STATUS_LIST);

            storeCmsPageInModel(model, getContentPageForLabelOrId(QUOTATION_DETAILS_CMS_PAGE));
            setUpMetaDataForContentPage(model, getContentPageForLabelOrId(QUOTATION_DETAILS_CMS_PAGE));
            addGlobalModelAttributes(model, request);
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().populatePageType(digitalDatalayer, DigitalDatalayerConstants.PageType.MYACCOUNTPAGE);
            return ControllerConstants.Views.Pages.Account.AccountQuotationDetails;
        } else {
            return FORWARD_TO_404;
        }
    }

    @PostMapping(value = "/order-detail/update/order-reference")
    public ResponseEntity<?> updateOrderReference(@Valid OrderReferenceForm orderReferenceForm, BindingResult bindingResult) throws CMSItemNotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        boolean isUpdated = orderHistoryFacade.updateProjectNumber(orderReferenceForm.getOrderCode(), orderReferenceForm.getWorkflowCode(),
                                                                   orderReferenceForm.getOrderReference());
        return isUpdated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    /**
     * Fetch the quotation having the specified ID from the ERP.
     *
     * @param quotationId
     *            the ID of the quotation.
     * @param request
     * @param model
     */
    protected void getQuotationDetails(final String quotationId, final HttpServletRequest request, final Model model) {
        final QuotationData quotationData = getDistProductPriceQuotationFacade().getQuotationDetails(quotationId);
        model.addAttribute("quotationData", quotationData);
    }

    protected void getQuotationHistory(final QuotationHistoryForm quotationHistoryForm, final BindingResult bindingResult, final HttpServletRequest request,
                                       final Model model) {
        if (quotationHistoryForm.getFromDate() != null && quotationHistoryForm.getToDate() != null) {
            if (quotationHistoryForm.getFromDate().after(quotationHistoryForm.getToDate())) {
                bindingResult.rejectValue("fromDate", "validate.error.date.before");
            }
        }

        if (bindingResult.hasErrors()) {
            LOG.error("Binding ERRORS: {}", bindingResult.getFieldErrors());
            GlobalMessages.addErrorMessage(model, "form.global.error");
            model.addAttribute("quotationHistoryForm", quotationHistoryForm);
            return;
        }

        if (quotationHistoryForm.getSort() != null) {
            final String tab[] = quotationHistoryForm.getSort().split(":");
            if (tab.length >= 1) {
                quotationHistoryForm.setSort(tab[0]);
            }

            if (tab.length >= 2) {
                quotationHistoryForm.setSortType(tab[1]);
            }

            if (!QUOTATION_HISTORY_SORT_TYPE_LIST_MIN.contains(quotationHistoryForm.getSort())) {
                quotationHistoryForm.setSort("byRequestDate");
            }
        } else {
            quotationHistoryForm.setSort("byRequestDate");
        }

        if (quotationHistoryForm.getPage() <= 0) {
            quotationHistoryForm.setPage(1);
        }

        // Creating the filter
        final String statusCode = getQuotationStatusCode(quotationHistoryForm.getStatus());
        // Handle paged search results
        final QuotationHistoryPageableData pageableData = createPageableData(quotationHistoryForm);
        final SearchPageData<QuotationHistoryData> searchPageData = getDistProductPriceQuotationFacade().getQuotationHistory(pageableData, statusCode);
        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (DistPaginationData) searchPageData.getPagination(), model, true);
        storeSearchResultToModel(model, searchPageData, quotationHistoryForm.getShow());
        if ((getCurrentSalesOrg().isAdminManagingSubUsers() && getUser().isAdminUser()) || getCurrentSalesOrg().isQuotationVisibibleToAll()) {
            model.addAttribute("contactsOfCustomer", getB2bCustomerFacade().searchEmployees(null, null, null, true));
        }
        model.addAttribute("quotationHistoryForm", quotationHistoryForm);
    }

    protected void getOpenOrderHistory(final OrderHistoryForm orderHistoryForm, final Model model, final HttpServletRequest request) {

        if (orderHistoryForm.getPage() < 0) {
            orderHistoryForm.setPage(0);
        }

        // Handle paged search results
        final DistOrderHistoryPageableData pageableData = createOpenOrderPageableData(orderHistoryForm);
        // webservice call for getting the list of all open orders
        final SearchPageData<OrderHistoryData> searchPageData = orderHistoryFacade.getOpenOrders(pageableData);
        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (DistPaginationData) searchPageData.getPagination(), model, true);
        storeSearchResultToModel(model, searchPageData, orderHistoryForm.getShow());
        model.addAttribute("orderHistoryForm", orderHistoryForm);
    }

    protected void getInvoiceHistory(final InvoiceHistoryForm invoiceHistoryForm, final BindingResult bindingResult, final HttpServletRequest request,
                                     final Model model) {
        prefillDateFields(invoiceHistoryForm);

        if (invoiceHistoryForm.getFromDate() != null && invoiceHistoryForm.getToDate() != null) {
            if (invoiceHistoryForm.getFromDate().after(invoiceHistoryForm.getToDate())) {
                bindingResult.rejectValue("fromDate", "validate.error.date.before");
            }
        }

        if (invoiceHistoryForm.getFromDueDate() != null && invoiceHistoryForm.getToDueDate() != null) {
            if (invoiceHistoryForm.getFromDueDate().after(invoiceHistoryForm.getToDueDate())) {
                bindingResult.rejectValue("fromDueDate", "validate.error.date.before");
            }
        }

        if (invoiceHistoryForm.getOrderNumber() != null) {
            final Pattern pattern = Pattern.compile("[A-Za-z0-9\\*]*");
            final Matcher matcher = pattern.matcher(invoiceHistoryForm.getOrderNumber());
            if (!matcher.matches()) {
                bindingResult.rejectValue("orderNumber", "validate.error.alphanumeric");
            }
        }

        if (bindingResult.hasErrors()) {
            LOG.error("Binding ERRORS: {}", bindingResult.getFieldErrors());
            GlobalMessages.addErrorMessage(model, "form.global.error");
            model.addAttribute("invoiceHistoryForm", invoiceHistoryForm);
            return;
        }

        if (invoiceHistoryForm.getSort() == null || !(INVOICE_HISTORY_SORT_TYPE_LIST.contains(invoiceHistoryForm.getSort())
                || INVOICE_HISTORY_SORT_TYPE_LIST_MIN.contains(invoiceHistoryForm.getSort()))) {
            invoiceHistoryForm.setSort("byDate");
            invoiceHistoryForm.setSortType("DESC");
        } else {
            final String tab[] = invoiceHistoryForm.getSort().split(":");
            if (tab.length >= 1) {
                invoiceHistoryForm.setSort(tab[0]);
            }
            if (tab.length >= 2) {
                invoiceHistoryForm.setSortType(tab[1].toUpperCase());
            }
            if (!INVOICE_HISTORY_SORT_TYPE_LIST_MIN.contains(invoiceHistoryForm.getSort())) {
                invoiceHistoryForm.setSort("byDate");
            }
            if (invoiceHistoryForm.getSortType() == null
                    || !("ASC".equals(invoiceHistoryForm.getSortType()) || "DESC".equals(invoiceHistoryForm.getSortType()))) {
                invoiceHistoryForm.setSortType("DESC");
            }
        }

        if (invoiceHistoryForm.getArticleNumber() != null) {
            final String filteredArticleNumber = invoiceHistoryForm.getArticleNumber().replaceAll("-", StringUtils.EMPTY);
            invoiceHistoryForm.setArticleNumber(filteredArticleNumber);
        }

        if (invoiceHistoryForm.getPage() < 0) {
            invoiceHistoryForm.setPage(0);
        }

        // final DistInvoiceHistoryPageableData pageableData = createPageableData( invoiceHistoryForm);
        // final SearchPageData<DistB2BInvoiceHistoryData> searchPageData = invoiceHistoryFacade.getInvoiceHistory(pageableData);

        final DistOnlineInvoiceHistoryPageableData pageableDataIF12 = createPageableDataIF12(invoiceHistoryForm);
        final SearchPageData<DistB2BInvoiceHistoryData> searchPageData = invoiceHistoryFacade.getInvoiceSearchHistory(pageableDataIF12); // IF12

        // Setting the URLs of the previous and next pages
        final String searchURL = storeContinueUrl(request);
        updatePagination(searchURL, (DistPaginationData) searchPageData.getPagination(), model, true);
        storeSearchResultToModel(model, searchPageData, invoiceHistoryForm.getShow());
        if (isCompanyInvoiceAdminUser()) {
            model.addAttribute("contactsOfCustomer", getB2bCustomerFacade().searchEmployees(null, null, null, true));
        }
        model.addAttribute("invoiceHistoryForm", invoiceHistoryForm);
        model.addAttribute("conversionFormat", getTextStoreDateFormatForModel());
    }

    private String getTextStoreDateFormatForModel() {
        String conversionFormat = getTextStoreDateFormatForConversion();
        if (StringUtils.isBlank(conversionFormat)) {
            conversionFormat = INVOICE_HISTORY_DEFAULT_DATE_PATTERN;
        }
        return conversionFormat;
    }

    private void prefillDateFields(final InvoiceHistoryForm invoiceHistoryForm) {
        final ZoneId zoneId = ZoneId.systemDefault();

        if (StringUtils.isEmpty(invoiceHistoryForm.getOrderNumber()) && StringUtils.isEmpty(invoiceHistoryForm.getInvoiceNumber())
                && StringUtils.isEmpty(invoiceHistoryForm.getArticleNumber()) && invoiceHistoryForm.getMinTotal() == null
                && invoiceHistoryForm.getMaxTotal() == null && invoiceHistoryForm.getFromDueDate() == null && invoiceHistoryForm.getToDueDate() == null) {

            if (invoiceHistoryForm.getFromDate() == null && invoiceHistoryForm.getToDate() == null) {
                final LocalDate fromDate = LocalDate.now().minusMonths(1);
                final LocalDate toDate = LocalDate.now();
                invoiceHistoryForm.setFromDate(Date.from(fromDate.atStartOfDay(zoneId).toInstant()));
                invoiceHistoryForm.setToDate(Date.from(toDate.atStartOfDay(zoneId).toInstant()));
            }
        }

        if (invoiceHistoryForm.getFromDate() == null && !(invoiceHistoryForm.getToDate() == null)) {
            final LocalDate date = invoiceHistoryForm.getToDate().toInstant().atZone(zoneId).toLocalDate().minusMonths(1);
            invoiceHistoryForm.setFromDate(Date.from(date.atStartOfDay(zoneId).toInstant()));
        }

        if (!(invoiceHistoryForm.getFromDate() == null) && invoiceHistoryForm.getToDate() == null) {
            final LocalDate date = invoiceHistoryForm.getFromDate().toInstant().atZone(zoneId).toLocalDate().plusMonths(1);
            invoiceHistoryForm.setToDate(Date.from(date.atStartOfDay(zoneId).toInstant()));
        }

        if (invoiceHistoryForm.getFromDueDate() == null && !(invoiceHistoryForm.getToDueDate() == null)) {
            final LocalDate date = invoiceHistoryForm.getToDueDate().toInstant().atZone(zoneId).toLocalDate().minusMonths(1);
            invoiceHistoryForm.setFromDueDate(Date.from(date.atStartOfDay(zoneId).toInstant()));
        }

        if (!(invoiceHistoryForm.getFromDueDate() == null) && invoiceHistoryForm.getToDueDate() == null) {
            final LocalDate date = invoiceHistoryForm.getFromDueDate().toInstant().atZone(zoneId).toLocalDate().plusMonths(1);
            invoiceHistoryForm.setToDueDate(Date.from(date.atStartOfDay(zoneId).toInstant()));
        }
    }

    @ExceptionHandler(UnknownIdentifierException.class)
    public String handleUnknownIdentifierException(final UnknownIdentifierException exception, final HttpServletRequest request) {
        request.setAttribute("exception", exception);
        final String uuidString = java.util.UUID.randomUUID().toString();
        ERROR_PAGE_LOG.debug("a technical error occured [uuid: {}], IP Address: {}. {}", uuidString, request.getRemoteAddr(), exception.getMessage());
        request.setAttribute("uuid", uuidString);
        return FORWARD_PREFIX + "/" + "notFound";
    }

    /**
     * Export an order to CSV
     *
     * @param orderData
     *            the order data
     * @param exportFileNamePrefix
     *            the prefix for the export file name
     * @return the export file
     */
    protected File exportOrderToCSV(final OrderData orderData, final String exportFileNamePrefix) {
        if (orderData != null) {
            try {
                final String filePath = System.getProperty("HYBRIS_TEMP_DIR", System.getProperty("java.io.tmpdir"));
                final File file = new File(getFullFileName(filePath, exportFileNamePrefix, ".csv"));
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
                bufferedWriter.write(OrderCsvUtil.toCSV(orderData, null));
                bufferedWriter.close();
                return file;
            } catch (final Exception e) {
                LOG.error("Can not export order to CSV!", e);
            }
        }

        return null;
    }

    /**
     * Create filename with configured path and filename extended by current session id.
     */
    protected String getFullFileName(final String filePath, final String fileNamePrefix, final String fileNameSuffix) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", new Locale(getCurrentLanguage().getIsocode()));
        final StringBuilder fullFileName = new StringBuilder();
        fullFileName.append(filePath);
        if (StringUtils.isNotBlank(filePath) && !StringUtils.right(filePath, 1).equals("/")) {
            fullFileName.append("/");
        }
        fullFileName.append(fileNamePrefix.replace(' ', '_'));
        fullFileName.append("_");
        fullFileName.append(simpleDateFormat.format(new Date()));
        fullFileName.append(fileNameSuffix);
        return fullFileName.toString();
    }

    protected List<AddressData> getDeliveryAdresses(final String sort, final Model model) {
        String sortBy = null;
        String sortType = null;

        if (sort != null) {
            final String tab[] = sort.split(":");
            if (tab.length >= 1) {
                sortBy = tab[0];
            }

            if (tab.length >= 2) {
                sortType = tab[1];
            }

            if (CustomerType.B2C.equals(getB2bCustomerFacade().getCurrentCustomer().getCustomerType())) {
                if (!ADRESSES_B2C_SORT_TYPE_LIST.contains(sortBy)) {
                    sortBy = ADRESSES_B2C_SORT_TYPE_LIST.get(0);
                }
            } else { // B2B
                if (!ADRESSES_B2B_SORT_TYPE_LIST.contains(sortBy)) {
                    sortBy = ADRESSES_B2B_SORT_TYPE_LIST.get(0);
                }
            }
        } else {
            if (CustomerType.B2C.equals(getB2bCustomerFacade().getCurrentCustomer().getCustomerType())) {
                sortBy = ADRESSES_B2C_SORT_TYPE_LIST.get(0);
            } else { // B2B
                sortBy = ADRESSES_B2B_SORT_TYPE_LIST.get(0);
            }
        }

        // set attributes
        model.addAttribute("sortBy", sortBy);

        if (!"desc".equalsIgnoreCase(sortType)) {
            sortType = "asc";
        }
        model.addAttribute("sortType", sortType);

        // Filters
        if (CustomerType.B2C.equals(getB2bCustomerFacade().getCurrentCustomer().getCustomerType())) {
            model.addAttribute("sortByList", ADRESSES_B2C_SORT_TYPE_LIST);
        } else { // B2B
            model.addAttribute("sortByList", ADRESSES_B2B_SORT_TYPE_LIST);
        }

        // Get delivery address
        return userFacade.getAddressBookDeliveryEntries(sortBy, sortType);

    }

    protected String getStatusCode(final String status) {
        return ORDER_STATUS_LIST.stream().map(os -> os.getCode()).filter(code -> code.equalsIgnoreCase(status)).findFirst().orElse("ALL");
    }

    /**
     * @return a list of {@code SelectOption} holding the quote statuses <tt>code</tt> and <tt>name</tt>
     */
    protected List<SelectOption> getQuotationStatusCodes() {
        final List<QuoteStatusData> quotationStatuses = getDistProductPriceQuotationFacade().getQuoteStatuses();
        if (CollectionUtils.isNotEmpty(quotationStatuses)) {
            return quotationStatuses.stream().map(status -> new SelectOption(status.getCode(), status.getName())).collect(Collectors.toList());
        }

        return Collections.<SelectOption> emptyList();
    }

    protected String getQuotationStatusCode(final String status) {
        if (StringUtils.isNotBlank(status) && !"ALL".equalsIgnoreCase(status)) {
            try {
                return codelistService.getDistQuotationStatus(status).getCode();
            } catch (final Exception exp) {
                // NOP
            }
        }

        return null;
    }

    public DistB2BCartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(final DistB2BCartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    /**
     * This method check whether the quantity ordered in return request does not exceeds the remaining available quantity.
     *
     * @param orderEntryList
     * @param createRMAOrderEntryDataList
     * @return boolean
     */
    private boolean isReturnRaisedQuantityAvailable(final List<OrderEntryData> orderEntryList,
                                                    final List<CreateRMAOrderEntryDataForm> createRMAOrderEntryDataList, final BindingResult bindingResult) {
        boolean invalidQuantity = false;
        int index = 0;
        for (final CreateRMAOrderEntryDataForm createRMAOrderEntryDataForm : createRMAOrderEntryDataList) {
            if (!StringUtils.isEmpty(createRMAOrderEntryDataForm.getReturnReasonID())) {
                final OrderEntryData orderEntryData = orderEntryList.stream()
                                                                    .filter(orderData -> orderData.getItemPosition()
                                                                                                  .equalsIgnoreCase(createRMAOrderEntryDataForm.getItemNumber()))
                                                                    .findFirst().orElse(null);
                if (createRMAOrderEntryDataForm.getQuantity() == null
                        || (null != orderEntryData && createRMAOrderEntryDataForm.getQuantity() > orderEntryData.getRmaData().getRemainingReturnQty())) {
                    // Ordered Quantity exceeds the remaining available quantity.
                    invalidQuantity = true;
                    bindingResult.rejectValue("orderItems[" + index + "].quantity", "return.quantity.invalid", "Return quantity is not valid");
                }
            }
            index++;
        }

        return invalidQuantity;

    }

    private boolean isRmaRaised(final List<CreateRMAOrderEntryDataForm> orderItems) {

        // Check if the return request has been raised or not
        boolean isRmaRaised = false;
        for (final CreateRMAOrderEntryDataForm createRMAOrderEntryDataForm : orderItems) {
            if (createRMAOrderEntryDataForm.isRmaRaised()) {
                // Return request raised
                isRmaRaised = true;
                return isRmaRaised;
            }
        }
        return isRmaRaised;
    }

    /**
     * Convert the Java Object to JSON.
     *
     * @param obj
     * @return String
     */
    private String convertObjectToJson(final Object obj) {
        String jsonInString = StringUtils.EMPTY;
        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsonInString = mapper.writeValueAsString(obj);

        } catch (final JsonGenerationException e) {
            LOG.warn("Exception occurred during JSON generation", e);
        } catch (final JsonMappingException e) {
            LOG.warn("Exception occurred during JSON mapping", e);
        } catch (final IOException e) {
            LOG.warn("Exception occurred during conversion of object to JSON", e);
        }

        return jsonInString;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public OrderHistoryFacade getOrderHistoryFacade() {
        return orderHistoryFacade;
    }

    public void setOrderHistoryFacade(OrderHistoryFacade orderHistoryFacade) {
        this.orderHistoryFacade = orderHistoryFacade;
    }

    public InvoiceHistoryFacade getInvoiceHistoryFacade() {
        return invoiceHistoryFacade;
    }

    public void setInvoiceHistoryFacade(InvoiceHistoryFacade invoiceHistoryFacade) {
        this.invoiceHistoryFacade = invoiceHistoryFacade;
    }

    public DistCheckoutFacade getCheckoutFacade() {
        return checkoutFacade;
    }

    public void setCheckoutFacade(DistCheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    public DistUserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(final DistUserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public DistReturnRequestFacade getReturnRequestFacade() {
        return returnRequestFacade;
    }

    public void setReturnRequestFacade(DistReturnRequestFacade returnRequestFacade) {
        this.returnRequestFacade = returnRequestFacade;
    }

    public AccountBreadcrumbBuilder getAccountBreadcrumbBuilder() {
        return accountBreadcrumbBuilder;
    }

    public void setAccountBreadcrumbBuilder(AccountBreadcrumbBuilder accountBreadcrumbBuilder) {
        this.accountBreadcrumbBuilder = accountBreadcrumbBuilder;
    }

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    public RMAService getRmaService() {
        return rmaService;
    }

    public void setRmaService(RMAService rmaService) {
        this.rmaService = rmaService;
    }

    public Converter<DistCodelistModel, DistRMAReasonData> getDistRMAReasonConverter() {
        return distRMAReasonConverter;
    }

    public void setDistRMAReasonConverter(Converter<DistCodelistModel, DistRMAReasonData> distRMAReasonConverter) {
        this.distRMAReasonConverter = distRMAReasonConverter;
    }

    public Converter<DistCodelistModel, DistRMAPackagingData> getDistRMAPackagingConverter() {
        return distRMAPackagingConverter;
    }

    public void setDistRMAPackagingConverter(Converter<DistCodelistModel, DistRMAPackagingData> distRMAPackagingConverter) {
        this.distRMAPackagingConverter = distRMAPackagingConverter;
    }

    @Override
    public DistNewsletterFacade getNewsletterFacade() {
        return newsletterFacade;
    }

    @Override
    public void setNewsletterFacade(DistNewsletterFacade newsletterFacade) {
        this.newsletterFacade = newsletterFacade;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public DistExportFacade getDistExportFacade() {
        return distExportFacade;
    }

    public void setDistExportFacade(DistExportFacade distExportFacade) {
        this.distExportFacade = distExportFacade;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public static String phoneNumberToString(final PhoneNumber phoneNumber) {
        return PHONENUMBERUTIL.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL);
    }
}
