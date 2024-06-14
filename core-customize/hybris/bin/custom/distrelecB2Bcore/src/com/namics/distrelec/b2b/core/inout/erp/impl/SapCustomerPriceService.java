/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.ws.WebServiceException;

import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.sap.v1.CurrencyCode;
import com.distrelec.webservice.sap.v1.CustomerPriceArticles;
import com.distrelec.webservice.sap.v1.CustomerPriceArticlesResponse;
import com.distrelec.webservice.sap.v1.CustomerPriceRequest;
import com.distrelec.webservice.sap.v1.CustomerPriceResponse;
import com.distrelec.webservice.sap.v1.ObjectFactory;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.CustomerPriceService;
import com.namics.distrelec.b2b.core.inout.erp.impl.cache.SapCustomerPriceCacheKey;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;

import de.hybris.platform.commercefacades.product.data.ProductPriceData;
import de.hybris.platform.commercefacades.product.data.VolumePriceData;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Customer price service for SAP Erp
 * 
 * @author francesco, Distrelec AG
 * @since Distrelec Extensions 1.0
 * 
 */
public class SapCustomerPriceService extends AbstractSapService implements CustomerPriceService {

    private static final Logger LOG = LogManager.getLogger(SapCustomerPriceService.class);

    private SIHybrisV1Out webServiceClient;

    // The SOAP Requests object factory
    @Autowired
    private ObjectFactory sapObjectFactory;
    @Autowired
    private CommonI18NService commonI18NService;

    public SapCustomerPriceService() {
        super(DistConstants.CacheName.CUSTOMER_PRICE);
    }

    @Override
    public Set<ProductPriceData> getPricesForPricesList(final String userId, final String salesOrg, final String currency, final String productCode,
            final List<PriceInformation> pricesList) {

        final CustomerPriceRequest request = new CustomerPriceRequest();
        request.setSalesOrganization(salesOrg);
        request.setCurrencyCode(CurrencyCode.valueOf(currency));
        request.setCustomerId(userId);
        request.setLanguage(getLocaleFromSession());
        // build the request for SAP webservice call
        for (final PriceInformation priceList : pricesList) {
            final PriceRow row = (PriceRow) priceList.getQualifierValue(PriceRow.PRICEROW);
            final CustomerPriceArticles article = new CustomerPriceArticles();
            article.setArticleNumber(productCode);
            article.setQuantity(row.getMinqtd().longValue());
            request.getArticles().add(article);
        }

        final CustomerPriceResponse response = new CustomerPriceResponse();
        final List<CustomerPriceArticles> unchachedProductArticlePrices = new ArrayList<>();
        final List<CustomerPriceArticlesResponse> cachedProductArticlePrices = new ArrayList<>();

        // first retrieve from cache
        getCustomerPricesFromCache(request, unchachedProductArticlePrices, cachedProductArticlePrices);

        if (!unchachedProductArticlePrices.isEmpty()) {
            // retrieve from SAP only for uncached products
            fetchUncachedAvailabilities(request.getSalesOrganization(), request.getCurrencyCode().toString(), request.getCustomerId(),
                    unchachedProductArticlePrices, cachedProductArticlePrices);
        }
        // generate the response
        response.getArticles().addAll(cachedProductArticlePrices);

        // convert the response
        return convertResponse(response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.CustomerPriceService#getOnlinePriceList(java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public Set<ProductPriceData> getOnlinePriceList(final String userId, final String salesOrg, final String currency, final String productCode,
            final Long minQuantity) {
        return getOnlinePriceList(userId, salesOrg, currency, Collections.singletonMap(productCode, minQuantity));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.erp.CustomerPriceService#getOnlinePriceList(java.lang.String, java.lang.String,
     * java.lang.String, java.util.List)
     */
    @Override
    public Set<ProductPriceData> getOnlinePriceList(final String userId, final String salesOrg, final String currency,
            final Map<String, Long> productCodesAndMinQuantities) {
        if (MapUtils.isEmpty(productCodesAndMinQuantities)) {
            return Collections.EMPTY_SET;
        }

        final CustomerPriceRequest request = new CustomerPriceRequest();
        request.setSalesOrganization(salesOrg);
        request.setCurrencyCode(CurrencyCode.valueOf(currency));
        request.setCustomerId(userId);
        request.setLanguage(getLocaleFromSession());
        for (final Entry<String, Long> productCodeAndMinQuantity : productCodesAndMinQuantities.entrySet()) {
            final CustomerPriceArticles article = new CustomerPriceArticles();
            article.setArticleNumber(productCodeAndMinQuantity.getKey());
            article.setQuantity(productCodeAndMinQuantity.getValue());
            request.getArticles().add(article);
        }

        final CustomerPriceResponse response = new CustomerPriceResponse();
        final List<CustomerPriceArticles> unchachedProductArticlePrices = new ArrayList<>();
        final List<CustomerPriceArticlesResponse> cachedProductArticlePrices = new ArrayList<>();

        // first retrieve from cache
        getCustomerPricesFromCache(request, unchachedProductArticlePrices, cachedProductArticlePrices);

        if (!unchachedProductArticlePrices.isEmpty()) {
            // retrieve from SAP only for uncached products
            fetchUncachedAvailabilities(request.getSalesOrganization(), request.getCurrencyCode().toString(), request.getCustomerId(),
                    unchachedProductArticlePrices, cachedProductArticlePrices);
        }
        // generate the response
        response.getArticles().addAll(cachedProductArticlePrices);

        // convert the response
        return convertResponse(response);
    }

    /**
     * Converts the SAP soap response in an two dimensional HashMap . <br/>
     * 
     * @param response
     *            The soap response.
     * @return The response is a multimap that contains [productCode: [quantity,price]]
     */
    private Set<ProductPriceData> convertResponse(final CustomerPriceResponse response) {

        final Set<ProductPriceData> result = new HashSet<ProductPriceData>();

        String previuousArticleCode = "";
        // variable to store the prices/quantity
        Set<VolumePriceData> articlePrices = new HashSet<VolumePriceData>();

        final int totalArticles = response.getArticles().size();
        int processedArticles = 1;
        for (final CustomerPriceArticlesResponse article : response.getArticles()) {
            ProductPriceData ppData = new ProductPriceData();
            if (!previuousArticleCode.isEmpty() && !previuousArticleCode.equals(article.getArticleNumber())) {
                // add the previus article prices to the result list.                
                ppData.setArticleNumber(previuousArticleCode);
                ppData.setVolumePriceData(articlePrices);
                result.add(ppData);
                if (result.stream().anyMatch(prodKey -> prodKey.getArticleNumber().equals(article.getArticleNumber()))) {
                    // article code already present in the result list
                    articlePrices = result.stream().filter(prodKey -> prodKey.getArticleNumber().equals(article.getArticleNumber())).findAny().map(ProductPriceData::getVolumePriceData).get();
                } else {
                    // new article code in the response
                    articlePrices = new HashSet<VolumePriceData>();
                }
            }
            // store the actual quantity and price
            VolumePriceData vpData = new VolumePriceData();
            vpData.setPriceWithoutVat(article.getPriceWithoutVat());
            vpData.setQuantity(Integer.valueOf(String.valueOf(article.getQuantity())));
            vpData.setPricePerX(article.getPRICEPERX());
            vpData.setPricePerXBaseQty(article.getPRICEPERXBASEQT().longValue());
            vpData.setPricePerXUOM(article.getPRICEPERXUOM());
            vpData.setPricePerXUOMDesc(article.getPricePerXUoMdescription());
            vpData.setPricePerXUOMQty(article.getPRICEPERXUOMQT().longValue());
            vpData.setPriceWithVat(article.getPriceWithVat());
            vpData.setVatPercentage(article.getVatPercentage());
            vpData.setVat(null != article.getVat() ? article.getVat() : BigDecimal.ZERO);
            
            articlePrices.add(vpData);
            ppData.setArticleNumber(article.getArticleNumber());
            ppData.setVolumePriceData(articlePrices);

            if (totalArticles == processedArticles) {
                // adding the last item
                result.add(ppData);
            }
            processedArticles++;
            previuousArticleCode = article.getArticleNumber();
        }

        return result;
    }

    /*
     * check cache for every product price. if found: add price to list. else: add to uncached list
     */
    private void getCustomerPricesFromCache(final CustomerPriceRequest request, final List<CustomerPriceArticles> uncachedProductArticlePrices,
            final List<CustomerPriceArticlesResponse> cachedProductArticlePrices) {

        request.getArticles().forEach(article -> {
            final SapCustomerPriceCacheKey key = new SapCustomerPriceCacheKey(request.getSalesOrganization(), request.getCurrencyCode().toString(),
                    request.getCustomerId(), article.getArticleNumber(), String.valueOf(article.getQuantity()));
            final CustomerPriceArticlesResponse value = getFromCache(key, CustomerPriceArticlesResponse.class);
            if (value == null) {
                // value not found in cache
                final CustomerPriceArticles uncachedArticle = new CustomerPriceArticles();
                uncachedArticle.setArticleNumber(article.getArticleNumber());
                uncachedArticle.setQuantity(article.getQuantity());
                uncachedProductArticlePrices.add(uncachedArticle);
            } else {
                cachedProductArticlePrices.add(value);
            }
        });
    }

    /*
     * fetch availabilities for products customer prices from SAP PI, add them to the cache and to the availabilities list.
     */
    private void fetchUncachedAvailabilities(final String salesOrganization, final String currency, final String customerCode,
            final List<CustomerPriceArticles> uncachedProductArticlePrices, final List<CustomerPriceArticlesResponse> cachedProductArticlePrices) {

        // Build soap request for uncached products
        final CustomerPriceRequest customerPriceRequest = buildSOAPRequest(salesOrganization, currency, customerCode, uncachedProductArticlePrices);
        // Execute request
        final CustomerPriceResponse customerPriceResponse = executeSOAPRequest(customerPriceRequest);

        if (null != customerPriceResponse && customerPriceResponse.getArticles() != null) {
            customerPriceResponse.getArticles().forEach(articlePrices -> {
                if (null != getCache()) {
                    // cache the article price
                    putIntoCache(new SapCustomerPriceCacheKey(salesOrganization, currency, customerCode, articlePrices.getArticleNumber(),
                            String.valueOf(articlePrices.getQuantity())), articlePrices);
                }
                // Add the retrieved product price to the cachedList
                cachedProductArticlePrices.add(articlePrices);
            });
        }
    }

    private CustomerPriceRequest buildSOAPRequest(final String salesOrganization, final String currency, final String customerCode,
            final List<CustomerPriceArticles> uncachedProductArticlePrices) {
        final CustomerPriceRequest customerPriceRequest = sapObjectFactory.createCustomerPriceRequest();
        customerPriceRequest.setSalesOrganization(salesOrganization);
        customerPriceRequest.setCurrencyCode(CurrencyCode.valueOf(currency));
        customerPriceRequest.setCustomerId(customerCode);
        customerPriceRequest.setLanguage(getLocaleFromSession());
        for (final CustomerPriceArticles article : uncachedProductArticlePrices) {
            customerPriceRequest.getArticles().add(article);
        }
        return customerPriceRequest;
    }

    private CustomerPriceResponse executeSOAPRequest(final CustomerPriceRequest customerPriceRequest) {
        CustomerPriceResponse customerPriceResponse = null;
        final long startTime = System.currentTimeMillis();
        try {
            customerPriceResponse = getWebServiceClient().if07CustomerPrice(customerPriceRequest);
        } catch (final P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if07CustomerPrice", faultMessage);
        } catch (final WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if07CustomerPrice", wsEx);
        }
        final long endTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Call to SAP PI IF-07 CustomerPrice took " + (endTime - startTime) + "ms");
        }

        return customerPriceResponse;
    }
    protected String getLocaleFromSession() {
        LanguageModel currentLanguage = getCommonI18NService().getCurrentLanguage();
        if(currentLanguage != null){
            String currentLanguageIsocode = currentLanguage.getIsocode().toUpperCase();
            if(currentLanguageIsocode.contains("_")){
                currentLanguageIsocode = currentLanguageIsocode.substring(0, currentLanguageIsocode.indexOf("_"));
            }
            return currentLanguageIsocode;
        }else{
            return "EN";
        }
    }
    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    @Required
    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public ObjectFactory getSapObjectFactory() {
        return sapObjectFactory;
    }

    public void setSapObjectFactory(ObjectFactory sapObjectFactory) {
        this.sapObjectFactory = sapObjectFactory;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
    
    

}
