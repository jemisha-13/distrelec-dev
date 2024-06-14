/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.helper;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Product.PRODUCT_CODE_REGEX;
import static com.namics.distrelec.b2b.core.util.LocalDateUtil.convertLocalDateToDate;
import static com.namics.distrelec.occ.core.v2.controller.OrdersController.PURCHASE_BLOCKED_PRODUCT_CODES;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistOrderStatusModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.order.exceptions.PurchaseBlockedException;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOrderHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.QuotationHistoryPageableData;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.DistCancelledOrderPrepayment;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationHistoriesData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationHistoryData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.occ.core.v2.controller.exceptions.OrderException;
import com.namics.distrelec.occ.core.v2.enums.ShowMode;
import com.namics.distrelec.occ.core.v2.forms.OrderHistoryForm;
import com.namics.distrelec.occ.core.v2.forms.QuotationHistoryForm;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderHistoriesData;
import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commercewebservicescommons.dto.order.*;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceValidationException;

@Component
public class OrdersHelper extends AbstractHelper {

    private static final Logger LOG = LogManager.getLogger(OrdersHelper.class);

    private static final int DEFAULT_SEARCH_MAX_SIZE = 100;

    private static final List<String> QUOTATION_HISTORY_SORT_TYPE_LIST_MIN = List.of("byPONumber",
                                                                                     "byRequestDate",
                                                                                     "byExpiryDate",
                                                                                     "byStatus",
                                                                                     "byTotalPrice");

    private static final List<OrderStatus> ORDER_STATUS_LIST = List.of(OrderStatus.ERP_STATUS_RECIEVED, OrderStatus.ERP_STATUS_IN_PROGRESS,
                                                                       OrderStatus.ERP_STATUS_PARTIALLY_SHIPPED, OrderStatus.ERP_STATUS_SHIPPED,
                                                                       OrderStatus.ERP_STATUS_CANCELLED);

    private static final List<String> ORDER_HISTORY_SORT_TYPE_LIST_MIN = List.of("byDate", "byStatus", "byTotalPrice");

    private static final String PAYMENT_ERROR_MESSAGE_BASIC_KEY = "checkout.error.payment.";

    private static final String PAYMENT_ERROR_MESSAGE_KEY = "checkout.error.payment";

    private static final String PAGE_CHECKOUT_PAYMENT = "/checkout/payment";

    @Resource(name = "i18NService")
    private I18NService i18NService;

    @Resource(name = "cmsSiteService")
    private CMSSiteService cmsSiteService;

    @Resource(name = "erp.orderHistoryFacade")
    private OrderHistoryFacade orderHistoryFacade;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Resource(name = "distCodelistService")
    private DistrelecCodelistService codelistService;

    @Resource(name = "orderHistoryFormValidator")
    private Validator orderHistoryFormValidator;

    @Autowired
    private DistProductPriceQuotationFacade distProductPriceQuotationFacade;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private DistCustomerFacade customerFacade;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private DistNewsletterFacade distNewsletterFacade;

    @Autowired
    @Qualifier("b2bCheckoutFacade")
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistB2BCartFacade b2bCartFacade;

    @Autowired
    private DistB2BOrderFacade distB2BOrderFacade;

    @Autowired
    private DistCartService cartService;

    // Get order history based on search criteria
    public OrderHistoryListWsDTO searchOrderHistory(final String fields, final OrderHistoryForm orderHistoryForm) {
        final OrderHistoriesData orderHistoriesData = getUserOrderHistory(orderHistoryForm);
        return getDataMapper().map(orderHistoriesData, OrderHistoryListWsDTO.class, fields);
    }

    // Get order history for user on page load
    public OrderHistoryListWsDTO searchOrderHistory(final int pageSize, final String fields) {
        final OrderHistoriesData orderHistoriesData = getUserOrderHistory(pageSize);
        return getDataMapper().map(orderHistoriesData, OrderHistoryListWsDTO.class, fields);
    }

    private OrderHistoriesData getUserOrderHistory(final int pageSize) {

        OrderHistoryForm orderHistoryForm = createDefaultOrderHistoryForm();
        orderHistoryForm.setPageSize(pageSize);
        return getUserOrderHistory(orderHistoryForm);
    }

    private OrderHistoryForm createDefaultOrderHistoryForm() {
        OrderHistoryForm form = new OrderHistoryForm();
        form.setSort("byDate");
        form.setPage(0);
        form.setStatus("ALL");
        form.setSortType("DESC");
        return form;
    }

    public OrderHistoriesData getUserOrderHistory(final OrderHistoryForm orderHistoryForm) {
        OrderHistoriesData orderHistoriesData = new OrderHistoriesData();
        if (!ObjectUtils.isEmpty(orderHistoryForm)) {
            final Errors errors = new BeanPropertyBindingResult(orderHistoryForm, "orderHistoryForm");
            orderHistoryFormValidator.validate(orderHistoryForm, errors);
            if (errors.hasErrors()) {
                LOG.error("Binding ERRORS: {}", errors.getFieldErrors());
                throw new WebserviceValidationException(errors);
            }

            setOrderHistorySorts(orderHistoryForm);

            if (orderHistoryForm.getPage() == null || orderHistoryForm.getPage() < 1) {
                orderHistoryForm.setPage(0);
            } else {
                orderHistoryForm.setPage(orderHistoryForm.getPage() - 1);
            }
            if (orderHistoryForm.getPageSize() == null || orderHistoryForm.getPageSize() < 0) {
                orderHistoryForm.setPageSize(10);
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
            final DistOrderHistoryPageableData pageableData = createOrderHistoryPageableData(orderHistoryForm);
            orderHistoriesData = createOrderHistoriesData(orderHistoryFacade.getOrderHistory(pageableData, orderStatuses));
        }
        return orderHistoriesData;
    }

    private void setOrderHistorySorts(final OrderHistoryForm orderHistoryForm) {
        if (orderHistoryForm.getSort() != null) {
            final String[] tab = orderHistoryForm.getSort().split(":");
            if (tab.length >= 1) {
                orderHistoryForm.setSort(tab[0]);
            }

            if (tab.length >= 2) {
                if ("ASC".equalsIgnoreCase(tab[1]) || "DESC".equalsIgnoreCase(tab[1])) {
                    orderHistoryForm.setSortType(tab[1].toUpperCase());
                }
            }

            if (!ORDER_HISTORY_SORT_TYPE_LIST_MIN.contains(orderHistoryForm.getSort())) {
                orderHistoryForm.setSort("byDate");
            }
        } else {
            orderHistoryForm.setSort("byDate");
        }
    }

    private String getStatusCode(final String status) {
        return ORDER_STATUS_LIST.stream()
                                .map(OrderStatus::getCode)
                                .filter(code -> code.equalsIgnoreCase(status))
                                .findFirst()
                                .orElse("ALL");
    }

    protected OrderHistoriesData createOrderHistoriesData(final SearchPageData<OrderHistoryData> result) {
        final OrderHistoriesData orderHistoriesData = new OrderHistoriesData();

        result.getPagination().setCurrentPage(result.getPagination().getCurrentPage() + 1);

        orderHistoriesData.setOrders(result.getResults());
        orderHistoriesData.setSorts(result.getSorts());
        orderHistoriesData.setPagination(result.getPagination());

        return orderHistoriesData;
    }

    protected DistOrderHistoryPageableData createOrderHistoryPageableData(final OrderHistoryForm orderHistoryForm) {
        final DistOrderHistoryPageableData pageableData = new DistOrderHistoryPageableData();
        pageableData.setCurrentPage(orderHistoryForm.getPage());
        pageableData.setSort(orderHistoryForm.getSort());

        if (ShowMode.ALL == orderHistoryForm.getShow()) {
            pageableData.setPageSize(DEFAULT_SEARCH_MAX_SIZE);
        } else {
            pageableData.setPageSize(orderHistoryForm.getPageSize());
        }

        pageableData.setOrderNumber(orderHistoryForm.getOrderNumber() != null ? orderHistoryForm.getOrderNumber().toUpperCase().replace('*', '%') : null);
        if (StringUtils.isEmpty(orderHistoryForm.getContactId()) && !getCustomerId().isEmpty()) {
            orderHistoryForm.setContactId(getCustomerId());
        }

        if (StringUtils.isNotEmpty(orderHistoryForm.getFilterContactId())) {
            pageableData.setFilterContactId(orderHistoryForm.getFilterContactId());
        }

        if (StringUtils.isNotEmpty(orderHistoryForm.getFromDate())) {
            try {
                pageableData.setFromDate(parseLocalSpecificDate(orderHistoryForm.getFromDate()));
            } catch (final ParseException e) {
                LOG.warn("Exception occurred during date parsing", e);
            }
        }

        if (StringUtils.isNotEmpty(orderHistoryForm.getToDate())) {
            try {
                pageableData.setToDate(parseLocalSpecificDate(orderHistoryForm.getToDate()));
            } catch (final ParseException e) {
                LOG.warn("Exception occurred during date parsing", e);
            }
        }

        if (isCompanyOrderAdminUser()) {
            pageableData.setContactId(orderHistoryForm.getContactId());
        } else {
            if (!getCustomerId().isEmpty()) {
                pageableData.setContactId(getCustomerId());
            }
        }

        pageableData.setSortType(orderHistoryForm.getSortType());
        // additional filter for sap implementation
        pageableData.setFilterCurrencyCode(orderHistoryForm.getCurrencyCode());
        pageableData.setInvoiceNumber(orderHistoryForm.getInvoiceNumber());
        pageableData.setMaxTotal(orderHistoryForm.getMaxTotal());
        pageableData.setMinTotal(orderHistoryForm.getMinTotal());
        pageableData.setStatus(orderHistoryForm.getStatus());
        pageableData.setReference(orderHistoryForm.getReference());
        pageableData.setProductNumber(orderHistoryForm.getProductNumber());
        return pageableData;
    }

    public QuotationHistoriesWsDTO searchQuotationHistory(final QuotationHistoryForm quotationHistoryForm, final ShowMode showMode,
                                                          final String fields) {
        setQuotationHistorySorts(quotationHistoryForm);
        final SearchPageData<QuotationHistoryData> searchPageData = createSearchPageDataForQuotationHistory(quotationHistoryForm, showMode);
        QuotationHistoriesData quotationHistoriesData = searchQuotationHistory(searchPageData);
        QuotationHistoriesWsDTO response = getDataMapper().map(quotationHistoriesData, QuotationHistoriesWsDTO.class, fields);
        response.setQuotations(getDataMapper().mapAsList(searchPageData.getResults(), QuotationHistoryWsDTO.class, fields));
        response.setQuotationStatuses(getDataMapper().mapAsList(getQuotationStatusCodes(), QuoteStatusWsDTO.class, fields));
        return response;
    }

    private QuotationHistoriesData searchQuotationHistory(final SearchPageData<QuotationHistoryData> searchPageData) {
        QuotationHistoriesData quotationHistoriesData = new QuotationHistoriesData();
        quotationHistoriesData.setSorts(searchPageData.getSorts());
        searchPageData.getPagination().setCurrentPage(searchPageData.getPagination().getCurrentPage() + 1);
        quotationHistoriesData.setPagination(searchPageData.getPagination());
        if ((getCurrentSalesOrg().isAdminManagingSubUsers() && getUser().isAdminUser()) || getCurrentSalesOrg().isQuotationVisibibleToAll()) {
            quotationHistoriesData.setContactsOfCustomer(getB2bCustomerFacade().searchEmployees(null, null, null, true));
        }
        return quotationHistoriesData;
    }

    private SearchPageData<QuotationHistoryData> createSearchPageDataForQuotationHistory(QuotationHistoryForm quotationHistoryForm, ShowMode showMode) {
        final String statusCode = getQuotationStatusCode(quotationHistoryForm.getStatus());
        final QuotationHistoryPageableData pageableData = createPageableData(quotationHistoryForm, showMode);
        return distProductPriceQuotationFacade.getQuotationHistory(pageableData, statusCode);
    }

    private void setQuotationHistorySorts(QuotationHistoryForm quotationHistoryForm) {
        String sort = quotationHistoryForm.getSort();
        int page = quotationHistoryForm.getPage();
        if (StringUtils.isNotEmpty(sort)) {
            final String[] tab = sort.split(":");
            if (tab.length >= 1) {
                quotationHistoryForm.setSort(tab[0]);
            }

            if (tab.length >= 2) {
                if ("ASC".equalsIgnoreCase(tab[1]) || "DESC".equalsIgnoreCase(tab[1])) {
                    quotationHistoryForm.setSortType(tab[1].toUpperCase());
                }
            }

            if (!QUOTATION_HISTORY_SORT_TYPE_LIST_MIN.contains(quotationHistoryForm.getSort())) {
                quotationHistoryForm.setSort("byRequestDate");
            }
        } else {
            quotationHistoryForm.setSort("byRequestDate");
        }
        if (page <= 0) {
            quotationHistoryForm.setPage(1);
        }
    }

    /**
     * Create a new instance of {@code PageableData} and set fields from the {@code OrderHistoryForm}
     *
     * @param quotationHistoryForm
     * @return a new instance of {@code PageableData}
     */
    protected QuotationHistoryPageableData createPageableData(final QuotationHistoryForm quotationHistoryForm, final ShowMode showMode) {
        final QuotationHistoryPageableData pageableData = new QuotationHistoryPageableData();
        pageableData.setCurrentPage(quotationHistoryForm.getPage());
        pageableData.setSort(quotationHistoryForm.getSort());
        pageableData.setPageSize(ShowMode.ALL == showMode ? DEFAULT_SEARCH_MAX_SIZE : quotationHistoryForm.getPageSize());
        pageableData.setQuotationId(quotationHistoryForm.getQuotationId());
        // DISTRELEC-11421 by default we set what we receive from the FE, this will be checked before sending the request to the ERP.
        pageableData.setContactId(quotationHistoryForm.getContactId());
        pageableData.setFilterArticleNumber(normalizeProductCode(quotationHistoryForm.getArticleNumber()));
        pageableData.setQuotationReference(quotationHistoryForm.getQuotationReference());
        pageableData.setFromDate(getQuotationDate(quotationHistoryForm.getFromDate()));
        pageableData.setToDate(getQuotationDate(quotationHistoryForm.getToDate()));
        pageableData.setFilterExpiryFromDate(getQuotationDate(quotationHistoryForm.getExpiryFromDate()));
        pageableData.setFilterExpiryToDate(getQuotationDate(quotationHistoryForm.getExpiryToDate()));
        pageableData.setSortType(quotationHistoryForm.getSortType());
        // additional filter for sap implementation
        pageableData.setFilterCurrencyCode(quotationHistoryForm.getCurrencyCode() == null ? cmsSiteService.getCurrentSite().getDefaultCurrency()
                                                                                                          .getIsocode()
                                                                                          : quotationHistoryForm.getCurrencyCode());
        pageableData.setMaxTotal(quotationHistoryForm.getMaxTotal());
        pageableData.setMinTotal(quotationHistoryForm.getMinTotal());
        pageableData.setStatus(quotationHistoryForm.getStatus());
        return pageableData;
    }

    private Date getQuotationDate(LocalDate dateToConvert) {
        return dateToConvert != null ? convertLocalDateToDate(dateToConvert) : null;
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

    private List<QuoteStatusData> getQuotationStatusCodes() {
        final List<QuoteStatusData> quotationStatuses = distProductPriceQuotationFacade.getQuoteStatuses();
        return quotationStatuses != null ? quotationStatuses : Collections.emptyList();
    }

    protected String normalizeProductCode(final String code) {
        if (StringUtils.isNotBlank(code) && !code.contains("it.code")) {
            String cleanedCode = code.replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
            if (cleanedCode.matches(PRODUCT_CODE_REGEX)) {
                return cleanedCode;
            }
        }
        return null;
    }

    public QuoteStatusData resubmitQuotation(final String previousQuoteId) {
        return distProductPriceQuotationFacade.resubmitQuotation(previousQuoteId);
    }

    public QuotationData getQuotationDetails(final String quotationId) {
        return distProductPriceQuotationFacade.getQuotationDetails(quotationId);
    }

    @Override
    public CustomerData getUser() {
        // The user does not change during the session, then we store it in the
        // session. When a user logs in, then a new session is created,
        // which means that the 'currentUser' attribute will be recalculated.
        final Object obj = getSessionService().getAttribute("currentUserData");
        if (obj != null && !((CustomerData) obj).getUid().equals(getUserService().getCurrentUser().getUid())) {
            getSessionService().removeAttribute("currentUserData");
        }

        return getSessionService().getOrLoadAttribute("currentUserData", () -> customerFacade.getCurrentCustomer());
    }

    private String getCustomerId() {
        return ((B2BCustomerModel) getUserService().getCurrentUser()).getErpContactID();
    }

    private Date parseLocalSpecificDate(final String date) throws ParseException {
        return new SimpleDateFormat(getDataFormatForCurrentCmsSite()).parse(date);
    }

    private String getDataFormatForCurrentCmsSite() {
        final Locale locale = new Locale(i18NService.getCurrentLocale().getLanguage(), cmsSiteService.getCurrentSite().getCountry().getIsocode());
        return messageSource.getMessage("text.store.dateformat.datepicker.selection", null, "MM/dd/yyyy", locale);
    }

    public CheckoutPaymentResponseWsDTO checkoutPaymentSuccess(Map<String, String> allParameters) throws Exception {
        CheckoutPaymentResponseWsDTO response = new CheckoutPaymentResponseWsDTO();
        try {
            // get data from request body
            simulatePaymentNotify(allParameters);
            LOG.debug("{} Successful payment with data from request parameters: {}", getPaymentuserUID(), allParameters);
            final String code = checkoutFacade.handlePaymentSuccessFailure(allParameters);
            response.setOrderCode(code);
        } catch (final PurchaseBlockedException pbe) {
            DistCancelledOrderPrepayment cancelledOrderPrepayment = createDistCancelledOrderPrepayment(pbe);
            distB2BOrderFacade.sendOrderCancellationPrepaymentMail(cancelledOrderPrepayment);
            throw new CartException(String.join(DistConstants.Punctuation.COMMA, pbe.getProductCodes()), PURCHASE_BLOCKED_PRODUCT_CODES);
        } catch (final Exception ise) {
            String message = String.format("{'%s'} For UID: {'%s'}. Successful payment could not be finished", getPaymentuserUID(),
                                           DistConstants.ErrorLogCode.PAYMENT_ERROR.getCode());
            throw new Exception(message, ise);
        }
        return response;
    }

    public CheckoutPaymentResponseWsDTO checkoutPaymentFailed(Map<String, String> allParameters) throws Exception {
        CheckoutPaymentResponseWsDTO response = new CheckoutPaymentResponseWsDTO();
        try {
            simulatePaymentNotify(allParameters);
            LOG.debug("{} Failure payment with data from request parameters: {}", getPaymentuserUID(), allParameters);
            final String failureCode = checkoutFacade.handlePaymentSuccessFailure(allParameters);
            final String failureMessageKey = getFailureMessageKeyByErrorCode(failureCode);
            response.setContinueUrl(PAGE_CHECKOUT_PAYMENT);
            response.setErrorCode(failureMessageKey);
        } catch (final OrderException ise) {
            String message = String.format("{'%s'} For UID: {'%s'}. Failure payment could not be finished", getPaymentuserUID(),
                                           DistConstants.ErrorLogCode.PAYMENT_ERROR.getCode());
            throw new OrderException(message, "order.payment.error", ise);
        }
        return response;
    }

    protected String getFailureMessageKeyByErrorCode(final String errorCode) {
        try {
            messageSource.getMessage(PAYMENT_ERROR_MESSAGE_BASIC_KEY + errorCode, null, i18NService.getCurrentLocale());
            return PAYMENT_ERROR_MESSAGE_BASIC_KEY + errorCode;
        } catch (final NoSuchMessageException e) {
            // Customize error messages only for certain codes that are handled and localized, return others untouched.
            String paymentErrorDescription = getSessionService().getAttribute(DistConstants.Session.PAYMENT_DESCRIPTION).toString();
            if (checkoutFacade.isErrorHandled(paymentErrorDescription)) {
                return PAYMENT_ERROR_MESSAGE_KEY + "." + paymentErrorDescription.replaceAll("[^a-zA-Z]+", "").toLowerCase();
            } else {
                return paymentErrorDescription;
            }
        }
    }

    private DistCancelledOrderPrepayment createDistCancelledOrderPrepayment(PurchaseBlockedException pbe) {
        DistCancelledOrderPrepayment distCancelledOrderPrepayment = new DistCancelledOrderPrepayment();
        distCancelledOrderPrepayment.setUid(getUserService().getCurrentUser().getUid());
        distCancelledOrderPrepayment.setOrderNumber(pbe.getOrderNumber());
        distCancelledOrderPrepayment.setArticleNumbers(pbe.getProductCodes());
        distCancelledOrderPrepayment.setProductNames(pbe.getProductNames());
        return distCancelledOrderPrepayment;
    }

    /**
     * Helper method to simulate the payment notify if the configuration is set to true
     *
     * @param parameters
     */
    protected void simulatePaymentNotify(final Map<String, String> parameters) {
        if (configurationService.getConfiguration().getBoolean("distrelec.payment.simulate.notify", false)) {
            checkoutFacade.handlePaymentNotify(DistUtils.getStringFromMap(parameters, "&", "="), getCurrentCartCurrencyCode());
        }
    }

    /**
     * Get the current cart currency details.
     *
     * @return String
     */
    private String getCurrentCartCurrencyCode() {
        return (cartService.getSessionCart().getCurrency().getIsocode().toUpperCase());
    }

    public String getPaymentuserUID() {
        return "Uid: " + getUserService().getCurrentUser().getUid() + " ";
    }

}
