/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Quote.Status.*;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.distrelec.webservice.if18.v1.*;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.event.DistCatalogPlusPriceRequestEvent;
import com.namics.distrelec.b2b.core.event.DistQuoteProductPriceEvent;
import com.namics.distrelec.b2b.core.inout.erp.QuotationService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistQuotationStatusModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.distrelec.b2b.core.service.search.pagedata.QuotationHistoryPageableData;
import com.namics.distrelec.b2b.core.util.DistDateTimeUtils;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationHistoryData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.b2b.facades.product.data.quote.QuotationRequestData;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Default implementation of {@link DistProductPriceQuotationFacade}
 *
 * @author pbueschi, Namics AG
 *
 */
public class DefaultDistProductPriceQuotationFacade implements DistProductPriceQuotationFacade {

    private static final SearchPageData<QuotationHistoryData> EMPTY_SEARCH_RESULT = new SearchPageData<>();
    static {
        EMPTY_SEARCH_RESULT.setPagination(new DistPaginationData());
        EMPTY_SEARCH_RESULT.setResults(Collections.EMPTY_LIST);
    }

    @Autowired
    @Qualifier("quotationRequestConverter")
    private Converter<QuotationsResponse, QuotationRequestData> quotationRequestConverter;

    @Autowired
    @Qualifier("distCodelistService")
    private DistrelecCodelistService codelistService;

    @Autowired
    @Qualifier("erp.quotationService")
    private QuotationService quotationService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private EventService eventService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private ProductService productService;

    @Autowired
    private Converter<ProductModel, ProductData> productConverter;

    @Autowired
    @Qualifier("quotationsResponsePopulator")
    private Converter<ReadQuotationsResponse, QuotationData> quotationsResponsePopulator;

    @Autowired
    @Qualifier("quotationHistoryPopulator")
    private Converter<QuotationsResponse, QuotationHistoryData> quotationHistoryPopulator;

    @Autowired
    private DistCartService cartService;

    @Autowired
    private DistPriceDataFactory defaultDistPriceDataFactory;

    @Override
    public void sendProductPriceQuotation(final DistQuoteProductPriceEvent event) {
        if (getDistSalesOrgService().getCurrentSalesOrg().getErpSystem() == DistErpSystem.SAP) {
            getQuotationService().createQuotation(event.getProduct().getCode(), event.getQuantity().intValue(), event.getComment());
        }
        setUpAdditionalEventData(event);
        getEventService().publishEvent(event);
    }

    @Override
    public void sendCatalogPlusProductPriceRequest(final DistCatalogPlusPriceRequestEvent distCatalogPlusPriceRequestEvent) {
        setUpAdditionalEventData(distCatalogPlusPriceRequestEvent);
        getEventService().publishEvent(distCatalogPlusPriceRequestEvent);
    }

    protected void setUpAdditionalEventData(final AbstractCommerceUserEvent userEvent) {
        userEvent.setBaseStore(getBaseStoreService().getCurrentBaseStore());
        userEvent.setSite(getBaseSiteService().getCurrentBaseSite());
        userEvent.setCustomer((CustomerModel) getUserService().getCurrentUser());
    }

    @Override
    public List<QuotationHistoryData> getQuotationHistory(final String status) {
        final QuotationHistoryPageableData quotationHistoryPageableData = new QuotationHistoryPageableData();
        quotationHistoryPageableData.setPageSize(100);
        quotationHistoryPageableData.setCurrentPage(1);
        quotationHistoryPageableData.setSort("byRequestDate");
        return getQuotationHistory(quotationHistoryPageableData, status).getResults();
    }

    @Override
    public SearchPageData<QuotationHistoryData> getQuotationHistory(final QuotationHistoryPageableData pageableData, final String statuses) {
        final SearchPageData<QuotationsResponse> searchResult = getQuotationService().searchQuotations(pageableData, statuses, true);
        if (CollectionUtils.isEmpty(searchResult.getResults())) {
            return EMPTY_SEARCH_RESULT;
        }

        if (!quotationVisibleToAll()) {
            final B2BCustomerModel contact = (B2BCustomerModel) getUserService().getCurrentUser();
            pageableData.setContactId(contact.getErpContactID());
        }

        final SearchPageData<QuotationHistoryData> searchData = new SearchPageData<>();
        searchData.setPagination(searchResult.getPagination());
        searchData.setSorts(searchResult.getSorts());
        searchData.setResults(Converters.convertAll(searchResult.getResults(), getQuotationHistoryPopulator()));
        return searchData;
    }

    @Override
    public QuoteStatusData resubmitQuotation(final String previousQuoteId) {
        final QuotationData previousQuotation = getQuotationDetails(previousQuoteId);

        final CreateQuotationRequest createQuotationRequest = createResubmitQuotationRequest(previousQuotation);
        previousQuotation.getQuotationEntries().forEach(quotationEntry -> {

            final QuotationArticlesRequest quotationArticlesRequest = new QuotationArticlesRequest();
            quotationArticlesRequest.setArticleDescription(quotationEntry.getArticleDescription());
            quotationArticlesRequest.setArticleNumber(quotationEntry.getItemNumber());
            quotationArticlesRequest.setCustomerReferenceItemLevel(quotationEntry.getCustomerReference());
            quotationArticlesRequest.setQuantity(BigInteger.valueOf(quotationEntry.getQuantity()));
            quotationArticlesRequest.setYourItemRef(quotationEntry.getYourItemRef());

            createQuotationRequest.getQuotationArticles().add(quotationArticlesRequest);
        });

        return resubmitQuotation(createQuotationRequest);
    }

    private CreateQuotationRequest createResubmitQuotationRequest(final QuotationData previousQuotation) {
        final CreateQuotationRequest createQuotationRequest = new CreateQuotationRequest();
        createQuotationRequest.setCustomerId(previousQuotation.getCustomerId());

        final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
        createQuotationRequest.setContactId(currentUser.getErpContactID());
        createQuotationRequest.setQuotationNote(previousQuotation.getQuotationNote());
        createQuotationRequest.setPreviousQuote(previousQuotation.getQuotationId());
        createQuotationRequest.setPOnumber(previousQuotation.getPoNumber());
        createQuotationRequest.setIsTender(previousQuotation.getIsTender());
        createQuotationRequest.setCustomerName(previousQuotation.getCustomerName());
        createQuotationRequest.setYourHeaderRef(previousQuotation.getYourHeaderRef());

        if (previousQuotation.getQuotationRequestDate() != null) {
            createQuotationRequest.setReqDate(SoapConversionHelper.convertDate(previousQuotation.getReqDate()));
        }

        createQuotationRequest.setExpireDate(getQuotationExpiryDate());

        final DistSalesOrgModel salesOrgModel = distSalesOrgService.getCurrentSalesOrg();
        createQuotationRequest.setSalesOrganization(salesOrgModel.getCode());

        return createQuotationRequest;
    }

    private QuoteStatusData resubmitQuotation(final CreateQuotationRequest resubmitCreateRequest) {
        QuoteStatusData quoteStatusData = new QuoteStatusData();
        final B2BCustomerModel user = (B2BCustomerModel) getUserService().getCurrentUser();

        if (checkIfQuoteRequestCountWithinLimit(user)) {
            final CreateQuotationResponse response = getQuotationService().resubmitQuotation(resubmitCreateRequest);

            if (null != response) {
                incrementQuotesRequested(user);
                setQuoteStatusAndCode(quoteStatusData, SUCCESSFUL, SUCCESSFUL);
            } else {
                setQuoteStatusAndCode(quoteStatusData, FAILED, FAILED);
            }
        } else {
            setQuoteStatusAndCode(quoteStatusData, FAILED, LIMIT_EXCEEDED);
        }
        return quoteStatusData;
    }

    private void incrementQuotesRequested(final B2BCustomerModel user) {
        user.setQuotesRequested((null != user.getQuotesRequested() ? user.getQuotesRequested() : 0) + 1);
        user.setLastQuoteRequestDate(Instant.now().toDate());
        getModelService().save(user);
    }

    @Override
    public boolean checkIfUserWithinQuoteSubmissionLimit() {
        final B2BCustomerModel user = (B2BCustomerModel) getUserService().getCurrentUser();
        return null != user && checkIfQuoteRequestCountWithinLimit(user);
    }

    @Override
    public void incrementQuotesRequestedCounter() {
        final B2BCustomerModel user = (B2BCustomerModel) getUserService().getCurrentUser();
        incrementQuotesRequested(user);
    }

    @Override
    public QuotationData getQuotationDetails(final String quotationId) {
        // Get customer id
        final B2BUnitModel company = ((B2BCustomerModel) getUserService().getCurrentUser()).getDefaultB2BUnit();
        // get salesOrg
        final DistSalesOrgModel salesOrgModel = distSalesOrgService.getCurrentSalesOrg();
        // forwarding request to service
        final ReadQuotationsResponse readQuotationsResponse = getQuotationService().getQuotationDetails(quotationId, company.getErpCustomerID(),
                                                                                                        salesOrgModel.getCode());

        return readQuotationsResponse == null ? null : getQuotationsResponsePopulator().convert(readQuotationsResponse);
    }

    @Override
    public QuoteStatusData createCartQuotation() {
        final CartModel cart = getCartService().getSessionCart();
        final B2BCustomerModel user = (B2BCustomerModel) getUserService().getCurrentUser();
        CreateQuotationResponse createQuotationsResponse = null;
        QuoteStatusData quoteResponse = new QuoteStatusData();
        if (checkIfQuoteRequestCountWithinLimit(user) && null != cart && null != cart.getEntries() && cart.getEntries().size() > 0) {
            createQuotationsResponse = getQuotationService().createCartQuotation(cart, user);

            if (null != createQuotationsResponse && null != createQuotationsResponse.getQuotationId()) {
                incrementQuotesRequested(user);
                setQuoteStatusAndCode(quoteResponse, SUCCESSFUL, SUCCESSFUL);
            } else {
                setQuoteStatusAndCode(quoteResponse, FAILED, FAILED);
            }
        } else {
            setQuoteStatusAndCode(quoteResponse, FAILED, LIMIT_EXCEEDED);
        }

        return quoteResponse;
    }

    private void setQuoteStatusAndCode(QuoteStatusData quoteResponse, String failed, String limitExceeded) {
        quoteResponse.setCode(failed);
        quoteResponse.setStatus(limitExceeded);
        quoteResponse.setName(StringUtils.EMPTY);
        quoteResponse.setMessage(StringUtils.EMPTY);
    }

    protected boolean checkIfQuoteRequestCountWithinLimit(final B2BCustomerModel b2bUser) {
        final int totalQotesRequested = null != b2bUser.getQuotesRequested() ? b2bUser.getQuotesRequested() : 0;
        final Date lastQuoteRequestDate = DistDateTimeUtils
                                                           .getDateAtMidnightStart(null != b2bUser.getLastQuoteRequestDate() ? b2bUser.getLastQuoteRequestDate()
                                                                                                                             : Instant.now().toDate());
        final Date todayDate = DistDateTimeUtils.getDateAtMidnightStart(DistDateTimeUtils.getDateAtMidnightEnd());
        final int maxAllowedQuotes = getConfigurationService().getConfiguration().getInt(DistConfigConstants.Quote.ATTRIBUTE_MAXIMUM_ALLOWED_QUOTES);
        if (lastQuoteRequestDate.before(todayDate)) {
            b2bUser.setQuotesRequested(0);
        }
        return (lastQuoteRequestDate.before(todayDate) || totalQotesRequested < maxAllowedQuotes) ? true : false;
    }

    @Override
    public List<QuoteStatusData> getQuoteStatuses() {
        try {
            final List<DistQuotationStatusModel> statuses = getCodelistService().getAllDistQuotationStatus();
            if (CollectionUtils.isNotEmpty(statuses)) {
                return statuses
                               .stream()
                               .map(status -> {
                                   QuoteStatusData quoteStatusData = new QuoteStatusData();
                                   quoteStatusData.setCode(status.getCode());
                                   quoteStatusData.setName(status.getName());
                                   return quoteStatusData;
                               }).collect(Collectors.toList());
            }
        } catch (final Exception exp) {
            // NOP
        }
        return Collections.<QuoteStatusData> emptyList();
    }

    /**
     * Checks whether or not the quotation are visible to all contacts of the company. The current user can see all company's quotations if
     * the sales organization configuration allows to see all quotations or the current user is ADMIN
     *
     * @return {@code true} if quotations are visible to all contacts or if the current user is ADMIN user. {@code false} otherwise.
     */
    protected boolean quotationVisibleToAll() {
        final DistSalesOrgModel salesOrg = getDistSalesOrgService().getCurrentSalesOrg();
        final UserModel currentUser = getUserService().getCurrentUser();
        return salesOrg.isQuotationVisibibleToAll() || (salesOrg.isAdminManagingSubUsers()
                && getUserService().isMemberOfGroup(currentUser, getUserService().getUserGroupForUID(B2BConstants.B2BADMINGROUP)));
    }

    protected BigInteger getQuotationExpiryDate() {
        final int expiryInDays = getConfigurationService().getConfiguration().getInt(DistConfigConstants.Quote.ATTRIBUTE_EXPIRY_DAYS, 30);
        final Date today = new Date();
        final BigInteger expiryDate = SoapConversionHelper.convertDate(DateUtils.addDays(today, expiryInDays));
        return expiryDate;
    }

    // BEGIN GENERATED CODE

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public Converter<QuotationsResponse, QuotationRequestData> getQuotationRequestConverter() {
        return quotationRequestConverter;
    }

    public void setQuotationRequestConverter(final Converter<QuotationsResponse, QuotationRequestData> quotationRequestConverter) {
        this.quotationRequestConverter = quotationRequestConverter;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public QuotationService getQuotationService() {
        return quotationService;
    }

    public void setQuotationService(final QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public Converter<ProductModel, ProductData> getProductConverter() {
        return productConverter;
    }

    public void setProductConverter(final Converter<ProductModel, ProductData> productConverter) {
        this.productConverter = productConverter;
    }

    public Converter<ReadQuotationsResponse, QuotationData> getQuotationsResponsePopulator() {
        return quotationsResponsePopulator;
    }

    public void setQuotationsResponsePopulator(final Converter<ReadQuotationsResponse, QuotationData> quotationsResponsePopulator) {
        this.quotationsResponsePopulator = quotationsResponsePopulator;
    }

    public Converter<QuotationsResponse, QuotationHistoryData> getQuotationHistoryPopulator() {
        return quotationHistoryPopulator;
    }

    public void setQuotationHistoryPopulator(final Converter<QuotationsResponse, QuotationHistoryData> quotationHistoryPopulator) {
        this.quotationHistoryPopulator = quotationHistoryPopulator;
    }

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(final DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    public DistCartService getCartService() {
        return cartService;
    }

    public void setCartService(final DistCartService cartService) {
        this.cartService = cartService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistPriceDataFactory getPriceDataFactory() {
        return defaultDistPriceDataFactory;
    }

    public void setPriceDataFactory(DistPriceDataFactory defaultDistPriceDataFactory) {
        this.defaultDistPriceDataFactory = defaultDistPriceDataFactory;
    }

    // END GENERATED CODE
}
