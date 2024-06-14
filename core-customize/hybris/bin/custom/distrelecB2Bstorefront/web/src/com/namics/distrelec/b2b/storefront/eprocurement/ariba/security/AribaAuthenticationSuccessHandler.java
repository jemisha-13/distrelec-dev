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
package com.namics.distrelec.b2b.storefront.eprocurement.ariba.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.model.eprocurement.AribaCustomerConfigModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.cxml.generated.CXML;
import com.namics.distrelec.b2b.facades.eprocurement.DistAribaFacade;
import com.namics.distrelec.b2b.storefront.controllers.pages.CartPageController;
import com.namics.distrelec.b2b.storefront.security.StorefrontAuthenticationSuccessHandler;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Success handler initializing user settings and ensuring the cart is handled correctly.
 */
public class AribaAuthenticationSuccessHandler extends StorefrontAuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AribaAuthenticationSuccessHandler.class);
    public static final String ARIBA_LOGIN_REDIRECT = "aribaLoginRedirect";

    @Autowired
    private DistAribaFacade distAribaFacade;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    @Qualifier("distEProcurementCustomerConfigService")
    private DistEProcurementCustomerConfigService customerConfigService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private SessionService sessionService;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
            throws IOException, ServletException {

        final AribaCustomerConfigModel aribaConfig = (AribaCustomerConfigModel) customerConfigService.getCustomerConfig();
        final CXML cXmlPunchOutSetupRequest = distAribaFacade.parseAribaSetupRequest();
        final Map<String, String> setupRequestParams = distAribaFacade.getAribaSetupRequestParameters(cXmlPunchOutSetupRequest);
        boolean useBasketFromCustomer = (aribaConfig == null) ? false : BooleanUtils.toBoolean(aribaConfig.getUseBasketFromCustomer());
        useBasketFromCustomer = useBasketFromCustomer || distAribaFacade.haveItemOutData(cXmlPunchOutSetupRequest);
        final String cartCode = distAribaFacade.setUpAribaCart(cXmlPunchOutSetupRequest, useBasketFromCustomer,
                setupRequestParams.get(DistConstants.Ariba.SetupRequestParams.CART_CODE));
        if (StringUtils.isNotEmpty(cartCode)) {
            LOG.info("Ariba cartcode = " + cartCode);
        }
        final String productCode = setupRequestParams.get(DistConstants.Ariba.SetupRequestParams.PRODUCT_CODE);
        String splitChar = configurationService.getConfiguration().getString("supplierpart.split.char");
        String[] productCodes = configurationService.getConfiguration().getString("skip.ariba.customer.supplierpart.ids").split(splitChar);
        setCartEditableOnUI(request, setupRequestParams);
       
        if (useBasketFromCustomer) {
            request.getSession().setAttribute(ARIBA_LOGIN_REDIRECT, CartPageController.PAGE_CART);
        } else if (null != productCode && StringUtils.isNotBlank(productCode)) {
            
                CountryModel country = null;
                if(LOG.isInfoEnabled()){
                    LOG.info("getting country using isocode :{}", productCode);
                }
                
                try {
                    try {
                        country = getCommonI18NService().getCountry(productCode.toUpperCase());

                    } catch (Exception ex) {
                        LOG.error("Can not find country for given isocode :", ex);
                    }
                    
                    if (null == country) {
                        // try lower in case
                        country = getCommonI18NService().getCountry(productCode.toLowerCase());
                    }
                   
                    if (null != country) {
                        
                        getSessionService().setAttribute(DistConstants.Ariba.Session.ARIBA_MULTI_COUNTRY_CUSTOMER, Boolean.TRUE);
                    }
                } catch (Exception ex) {
                    LOG.error("Can not find base site for :", ex);
                }
                
                if (null != productCodes && StringUtils.isNotBlank(productCode) && Arrays.asList(productCodes).contains(productCode.toUpperCase())) {
                    request.getSession().setAttribute(ARIBA_LOGIN_REDIRECT, "/");
                } else {
                    try {
                        ProductModel productModel = getDistProductService().getProductForCode(productCode);
                        if (null != productModel && StringUtils.isNotEmpty(productModel.getCode())) {
                            request.getSession().setAttribute(ARIBA_LOGIN_REDIRECT, "/p/" + productCode);
                        } else {
                            request.getSession().setAttribute(ARIBA_LOGIN_REDIRECT, "/");
                        }
                    }catch(Exception ex) {
                        LOG.error("Product with code {} not found in Ariba punchout setup.",productCode);
                        request.getSession().setAttribute(ARIBA_LOGIN_REDIRECT, "/");
                    }
                }
            
        }else {
            if(LOG.isInfoEnabled()){
                LOG.info("Set ARIBA_MULTI_COUNTRY_CUSTOMER in session to true", productCode);
            }
        }

        // DISTRELEC-6049 session language setup
        final String language = setupRequestParams.get(DistConstants.Ariba.SetupRequestParams.LANG_CODE);
        if (StringUtils.isNotBlank(language)) {
            distAribaFacade.setUpAribaLanguage(language);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    /**
     * @param request
     * @param setupRequestParams
     */
    private void setCartEditableOnUI(final HttpServletRequest request, final Map<String, String> setupRequestParams) {
        request.getSession().setAttribute(DistConstants.Ariba.SetupRequestParams.EDIT_CART,
                setupRequestParams.get(DistConstants.Ariba.SetupRequestParams.EDIT_CART));
    }

    public DistAribaFacade getDistAribaFacade() {
        return distAribaFacade;
    }

    public void setDistAribaFacade(final DistAribaFacade distAribaFacade) {
        this.distAribaFacade = distAribaFacade;
    }

    public DistEProcurementCustomerConfigService getCustomerConfigService() {
        return customerConfigService;
    }

    public void setCustomerConfigService(final DistEProcurementCustomerConfigService customerConfigService) {
        this.customerConfigService = customerConfigService;
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(DistProductService distProductService) {
        this.distProductService = distProductService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

}
