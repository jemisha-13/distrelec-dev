/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import com.distrelec.webservice.if18.v1.CreateQuotationRequest;
import com.distrelec.webservice.if18.v1.CreateQuotationResponse;
import com.distrelec.webservice.if18.v1.CurrencyCode;
import com.distrelec.webservice.if18.v1.P1FaultMessage;
import com.distrelec.webservice.if18.v1.QuotationArticlesRequest;
import com.distrelec.webservice.if18.v1.QuotationsResponse;
import com.distrelec.webservice.if18.v1.ReadQuotationsRequest;
import com.distrelec.webservice.if18.v1.ReadQuotationsResponse;
import com.distrelec.webservice.if18.v1.SIHybrisIF18V1Out;
import com.distrelec.webservice.if18.v1.SearchQuotationsRequest;
import com.distrelec.webservice.if18.v1.SearchQuotationsResponse;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.QuotationService;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistPaginationData;
import com.namics.distrelec.b2b.core.service.search.pagedata.QuotationHistoryPageableData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCache;

import javax.xml.ws.WebServiceException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code DefaultSapQuotationService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class SapQuotationService extends AbstractSapService implements QuotationService {

    private static final Logger LOG = LogManager.getLogger(QuotationService.class);
    private static final SearchPageData<QuotationsResponse> EMPTY_SEARCH_RESULT = new SearchPageData<>();
    private static final String DEFAULT_SORT_KEY = "byRequestDate";

    /**
     * Sort criteria map which returns, if the key does not exists, a value mapped to the key {@code DEFAULT_SORT_KEY}
     */
    private static final Map<String, String> SORT_MAPPING = new HashMap<String, String>() {
        @Override
        public String get(final Object key) {
            return super.get(containsKey(key) ? key : DEFAULT_SORT_KEY);
        }
    };
    static {
        EMPTY_SEARCH_RESULT.setResults(Collections.EMPTY_LIST);
        EMPTY_SEARCH_RESULT.setPagination(new DistPaginationData());
        EMPTY_SEARCH_RESULT.getPagination().setTotalNumberOfResults(0);

        // Sort mapping
        SORT_MAPPING.put(DEFAULT_SORT_KEY, "QUOTATION_REQUEST_DATE");
        SORT_MAPPING.put("byPONumber", "PONUMBER");
        SORT_MAPPING.put("byExpiryDate", "QUOTATION_EXPIRY_DATE");
        SORT_MAPPING.put("byStatus", "QUOTATION_STATUS");
        SORT_MAPPING.put("byTotalPrice", "TOTAL");
    }

    @Autowired
    @Qualifier("sapCacheManager")
    private CacheManager distCacheManager;
    private EhCacheCache cache;
    private SIHybrisIF18V1Out webServiceClient;
    @Autowired
    private DistSalesOrgService distSalesOrgService;
    @Autowired
    private UserService userService;
    
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Create a new instance of {@code DefaultSapQuotationService}
     */
    public SapQuotationService() {
        super(DistConstants.CacheName.QUOTATION_SEARCH);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.SapQuotationService#createQuotation(java.lang.String, int, java.lang.String)
     */
    @Override
    public String createQuotation(final String articleNumber, final int quantity, final String quotationNote) {
        final UserModel user = getUserService().getCurrentUser();
        if (getUserService().isAnonymousUser(user)) {
            LOG.info("No quotation possible for anonymous users");
            return null;
        }
        final String salesOrgCode = getDistSalesOrgService().getCurrentSalesOrg().getCode();

        return createQuotation(user, salesOrgCode, articleNumber, quantity, quotationNote);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.SapQuotationService#createCartQuotation(de.hybris.platform.core.model.order.CartModel)
     */
    @Override
    public CreateQuotationResponse createCartQuotation(final CartModel cart,final B2BCustomerModel user) {
        
        
        if (getUserService().isAnonymousUser(user)) {
            LOG.info("No quotation allowed for anonymous users");
            return null;
        }
        final String salesOrgCode = getDistSalesOrgService().getCurrentSalesOrg().getCode();
        CreateQuotationResponse createQuoteResponse = createCartQuotation(user, salesOrgCode, cart);
        
        return createQuoteResponse;
    }

    public CreateQuotationResponse createCartQuotation(final B2BCustomerModel user, final String salesOrgCode, final CartModel cart) {
       
        final CreateQuotationRequest request = new CreateQuotationRequest();
        final B2BUnitModel company = user.getDefaultB2BUnit();
        request.setCustomerId(company.getErpCustomerID());
        
        request.setContactId((user).getErpContactID());
        request.setCustomerName(company.getName());
        request.setQuotationNote(cart.getNote());
        request.setSalesOrganization(salesOrgCode);
        request.setExpireDate(getQuotationExpiryDate());
        
        for(final AbstractOrderEntryModel orderEntry:cart.getEntries()) {
            final QuotationArticlesRequest quotationArticleRequest = new QuotationArticlesRequest();
            quotationArticleRequest.setArticleNumber(orderEntry.getProduct().getCode());
            quotationArticleRequest.setQuantity(BigInteger.valueOf(orderEntry.getQuantity()));
            if(StringUtils.isNotEmpty(orderEntry.getCustomerReference())) {
                quotationArticleRequest.setCustomerReferenceItemLevel(orderEntry.getCustomerReference());
            }
            request.getQuotationArticles().add(quotationArticleRequest);    
        }
        
        // Executing the SOAP call to create the quotation
        return executeCreateQuotationRequest(request) ;
    }

    @Override
    public CreateQuotationResponse resubmitQuotation(CreateQuotationRequest quotationRequest){
        return executeCreateQuotationRequest(quotationRequest);
    }


    public String createQuotation(final String userId, final String articleNumber, final int quantity, final String quotationNote) {
        final B2BCustomerModel user = getUserService().getUserForUID(userId, B2BCustomerModel.class);
        return createQuotation(user, user.getDefaultB2BUnit().getSalesOrg().getCode(), articleNumber, quantity, quotationNote);
    }

    public String createQuotation(final UserModel user, final String salesOrgCode, final String articleNumber, final int quantity, final String quotationNote) {
        final CreateQuotationRequest request = new CreateQuotationRequest();
        final B2BUnitModel company = ((B2BCustomerModel) user).getDefaultB2BUnit();

        request.setCustomerId(company.getErpCustomerID());
        request.setContactId(((B2BCustomerModel) user).getErpContactID());
        request.setCustomerName(company.getName());
        request.setQuotationNote(quotationNote);
        request.setSalesOrganization(salesOrgCode);
        request.setExpireDate(getQuotationExpiryDate());

        final QuotationArticlesRequest quotationArticleRequest = new QuotationArticlesRequest();
        quotationArticleRequest.setArticleNumber(articleNumber);
        quotationArticleRequest.setQuantity(BigInteger.valueOf(quantity));
        request.getQuotationArticles().add(quotationArticleRequest);

        // Executing the SOAP call to create the quotation
        final CreateQuotationResponse response = executeCreateQuotationRequest(request);

        return response == null ? null : response.getQuotationId();
    }

    @Override
    public SearchPageData<QuotationsResponse> searchQuotations(final QuotationHistoryPageableData pageableData, final String status) {
        return searchQuotations(pageableData, status, false);
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.QuotationService#searchQuotations(com.namics.distrelec.b2b.core.service.search.pagedata.
     * QuotationHistoryPageableData, java.lang.String)
     */
    @Override
    public SearchPageData<QuotationsResponse> searchQuotations(final QuotationHistoryPageableData pageableData, final String status, final boolean forceRefresh) {
        final UserModel user = getUserService().getCurrentUser();
        if (getUserService().isAnonymousUser(user)) {
            LOG.info("No quotation possible for anonymous users");
            return null;
        }

        final SearchQuotationsRequest request = new SearchQuotationsRequest();
        final B2BUnitModel company = ((B2BCustomerModel) user).getDefaultB2BUnit();
        request.setCustomerId(company.getErpCustomerID());
        request.setSalesOrganization(getDistSalesOrgService().getCurrentSalesOrg().getCode());
        if (user.getSessionCurrency() != null) {
            request.setCurrencyCode(CurrencyCode.fromValue(user.getSessionCurrency().getIsocode()));
        } else if (company.getCurrency() != null) {
            request.setCurrencyCode(CurrencyCode.fromValue(company.getCurrency().getIsocode()));
        } else {
            final String currencyCode = ((CMSSiteModel) ((B2BCustomerModel) user).getCustomersBaseSite()).getDefaultCurrency().getIsocode();
            request.setCurrencyCode(CurrencyCode.fromValue(currencyCode));
        }

        // Setting the filters.
        request.setFilterContactId(pageableData.getContactId());
        request.setFilterQuotationId(pageableData.getQuotationId());
        request.setFilterArticleNumber(pageableData.getFilterArticleNumber());
        request.setFilterQuotationStatus(status);
        request.setFilterPONumber(pageableData.getFilterPONumber());
        request.setFilterTypeName(pageableData.getFilterTypeName());
        request.setFilterQuotationTotalMax(pageableData.getMaxTotal());
        request.setFilterQuotationTotalMin(pageableData.getMinTotal());

        if (StringUtils.isNotBlank(pageableData.getFilterCurrencyCode())) {
            request.setCurrencyCode(CurrencyCode.fromValue(pageableData.getFilterCurrencyCode()));
        }
        if (pageableData.getFromDate() != null) {
            request.setFilterQuotationRequestDateStart(SoapConversionHelper.convertDate(pageableData.getFromDate()));
        }
        if (pageableData.getToDate() != null) {
            request.setFilterQuotationRequestDateEnd(SoapConversionHelper.convertDate(pageableData.getToDate()));
        }
        if (pageableData.getFilterExpiryFromDate() != null) {
            request.setFilterQuotationExpiryDateStart(SoapConversionHelper.convertDate(pageableData.getFilterExpiryFromDate()));
        }
        if (pageableData.getFilterExpiryToDate() != null) {
            request.setFilterQuotationExpiryDateEnd(SoapConversionHelper.convertDate(pageableData.getFilterExpiryToDate()));
        }

        // Set the result offset and the page size.
        request.setResultOffset(BigInteger.valueOf((pageableData.getCurrentPage() - 1) * pageableData.getPageSize() + 1));
        request.setResultSize(SoapConversionHelper.getResultSizeFromPaginationData(pageableData));

        // Setting the sorting criteria and type.
        request.setSortCriteria(SORT_MAPPING.get(StringUtils.isNotBlank(pageableData.getSort()) ? pageableData.getSort() : DEFAULT_SORT_KEY));
        request.setSortAscending("ASC".equalsIgnoreCase(pageableData.getSortType()));

        // Execute the search request
        return convertSearchResponse(request, getSearchQuotationsResponse(request, forceRefresh));
    }

    private SearchQuotationsResponse getSearchQuotationsResponse(final SearchQuotationsRequest request){
        return getSearchQuotationsResponse(request, false);
    }

    /**
     * Method responsible to retrieve the value from cache or execute soap call
     * 
     * @param request
     * @return a {@code SearchQuotationsResponse}
     * @throws ErpCommunicationException
     */
    private SearchQuotationsResponse getSearchQuotationsResponse(final SearchQuotationsRequest request, final boolean forceRefresh) throws ErpCommunicationException {
        SearchQuotationsResponse response = null;
        if(!forceRefresh) {
             response = getFromCache(request, SearchQuotationsResponse.class);
        }
        if (response == null) {
            try {
                if ((response = executeSearchQuotationRequest(request)) != null) {
                    putIntoCache(request, response);
                }
            } catch (final ErpCommunicationException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        return response;
    }

    /**
     * Execute the SOAP call for the given request.
     * 
     * @param request
     * @return a {@code SearchQuotationsResponse}
     */
    private SearchQuotationsResponse executeSearchQuotationRequest(final SearchQuotationsRequest request) {
        SearchQuotationsResponse searchQuotationResponse = null;

        final long startTime = System.currentTimeMillis();
        try {
            searchQuotationResponse = webServiceClient.if18V1SearchQuotations(request);
        } catch (final P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if18V1SearchQuotations", faultMessage);
        } catch (final WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if18V1SearchQuotations", webServiceException);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Call to SAP PI IF-18 Search Quotations took " + (System.currentTimeMillis() - startTime) + "ms");
        }
        return searchQuotationResponse;
    }

    /**
     * Execute the SOAP call for the given request.
     * 
     * @param request
     * @return a {@code SearchQuotationsResponse}
     */
    private CreateQuotationResponse executeCreateQuotationRequest(final CreateQuotationRequest request) {
        CreateQuotationResponse searchQuotationResponse = null;

        final long startTime = System.currentTimeMillis();
        try {
            searchQuotationResponse = webServiceClient.if18V1CreateQuotation(request);
        } catch (final P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if18CreateQuotation", faultMessage);
        } catch (final WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if18CreateQuotation", webServiceException);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Call to SAP PI IF-18 Create Quotations took " + (System.currentTimeMillis() - startTime) + "ms");
        }
        return searchQuotationResponse;
    }

    /**
     * Convert a {@code SearchQuotationsResponse} to a {@code SearchPageData<QuotationsResponse>}
     * 
     * @param searchResponse
     *            the source data
     * @return a new instance {@code SearchPageData<QuotationsResponse>}
     */
    private SearchPageData<QuotationsResponse> convertSearchResponse(final SearchQuotationsRequest request, final SearchQuotationsResponse searchResponse) {
        if (searchResponse == null) {
            return EMPTY_SEARCH_RESULT;
        }
        final SearchPageData<QuotationsResponse> searchPageData = new SearchPageData<>();
        searchPageData.setResults(searchResponse.getQuotations());
        searchPageData.setPagination(new DistPaginationData());
        final long resultOffset = request.getResultOffset().longValue();
        final long resultTotalSize = searchResponse.getResultTotalSize().longValue();
        final int resultSize = (int) request.getResultSize().longValue();

        searchPageData.getPagination().setTotalNumberOfResults(resultTotalSize);
        searchPageData.getPagination().setPageSize(resultSize);
        searchPageData.getPagination().setNumberOfPages((int) (resultTotalSize / resultSize) + (resultTotalSize % resultSize > 0 ? 1 : 0));
        searchPageData.getPagination().setCurrentPage((int) (resultOffset / resultSize));

        return searchPageData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.QuotationService#getQuotationDetails(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public ReadQuotationsResponse getQuotationDetails(final String quotationId, final String customerId, final String salesOrg) {
        // creating quotation request
        final ReadQuotationsRequest request = createReadQuotationRequest(quotationId, customerId, salesOrg);
        // execute request
        ReadQuotationsResponse readQuotationResponse = getFromCache(request, ReadQuotationsResponse.class);

        try {
            readQuotationResponse = webServiceClient.if18V1ReadQuotation(request);
            putIntoCache(request, readQuotationResponse);
        } catch (final P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if18ReadQuotation", faultMessage);
        } catch (final WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if18ReadQuotation", webServiceException);
        }

        return readQuotationResponse;
    }

    private ReadQuotationsRequest createReadQuotationRequest(final String quotationId, final String customerId, final String salesOrg) {
        final ReadQuotationsRequest readQuotationRequest = new ReadQuotationsRequest();
        readQuotationRequest.setCustomerId(customerId);
        readQuotationRequest.setQuotationId(quotationId);
        readQuotationRequest.setSalesOrganization(salesOrg);
        return readQuotationRequest;
    }
    
    protected BigInteger getQuotationExpiryDate(){
        int expiryInDays = getConfigurationService().getConfiguration().getInt(DistConfigConstants.Quote.ATTRIBUTE_EXPIRY_DAYS, 30);
        final Date today = new Date();
        BigInteger expiryDate = SoapConversionHelper.convertDate(DateUtils.addDays(today, expiryInDays));
        return expiryDate;
    }

    // Getters and Setters

    public SIHybrisIF18V1Out getWebServiceClient() {
        return webServiceClient;
    }

    @Required
    public void setWebServiceClient(final SIHybrisIF18V1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
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

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
