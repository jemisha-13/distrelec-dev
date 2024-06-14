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
package com.namics.distrelec.b2b.facades.process.email.context;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriUtils;

import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.model.process.DistNpsProcessModel;
import com.namics.distrelec.b2b.core.model.process.RequestInvoicePaymentModeEmailProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Velocity context for a customer email.
 */
public class CustomerEmailContext extends AbstractDistEmailContext {

    private static final String DEFAULT_LANGUAGE = "en";

    private Converter<UserModel, CustomerData> b2bCustomerConverter;

    private Converter<UserModel, CustomerData> customerConverter;

    private CustomerData customerData;

    private String loginToken;

    private UserService userService;

    private CustomerType customerType;

    private RegistrationType registrationType;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof StoreFrontCustomerProcessModel) {
            setLoginToken(((StoreFrontCustomerProcessModel) businessProcessModel).getLoginToken());
            setRegistrationType(((StoreFrontCustomerProcessModel) businessProcessModel).getRegistrationType());
            setCustomerType(((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer().getCustomerType());
        }
        final CustomerModel customer = getCustomer(businessProcessModel);
        if (customer != null) {
            if (customer instanceof B2BCustomerModel) {
                customerData = getB2bCustomerConverter().convert(customer);
            } else {
                customerData = getCustomerConverter().convert(customer);
            }
        }

    }

    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof StoreFrontProcessModel) {
            return ((StoreFrontProcessModel) businessProcessModel).getSite();
        }
        if (businessProcessModel instanceof OrderProcessModel) {
            return ((OrderProcessModel) businessProcessModel).getOrder().getSite();
        }
        if (businessProcessModel instanceof RequestInvoicePaymentModeEmailProcessModel) {
            return ((RequestInvoicePaymentModeEmailProcessModel) businessProcessModel).getSite();
        }
        return null;
    }

    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof StoreFrontCustomerProcessModel) {
            return ((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer();
        }
        if (businessProcessModel instanceof OrderProcessModel) {
            return (CustomerModel) ((OrderProcessModel) businessProcessModel).getOrder().getUser();
        }
        return null;
    }

    @Override
    protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof StoreFrontCustomerProcessModel) {
            final StoreFrontCustomerProcessModel storeFrontCustomerProcess = (StoreFrontCustomerProcessModel) businessProcessModel;
            if (getUserService().isAnonymousUser(storeFrontCustomerProcess.getCustomer()) && storeFrontCustomerProcess.getLanguage() != null) {
                // In case of anonymous user, we need the session language of the web site
                return storeFrontCustomerProcess.getLanguage();
            }
            return getCustomerLanguageOrFallbackToDefault(storeFrontCustomerProcess.getCustomer());
        }
        if (businessProcessModel instanceof OrderProcessModel) {
            final String localeString = ((OrderProcessModel) businessProcessModel).getOrder().getLocale();
            if (localeString != null) {
                final String[] localeStringArray = localeString.split("_");
                return getCommonI18NService().getLanguage(localeStringArray[0]);
            } else {
                return ((OrderProcessModel) businessProcessModel).getOrder().getUser().getSessionLanguage();
            }
        }
        if (businessProcessModel instanceof DistNpsProcessModel) {
            final B2BCustomerModel customer = (B2BCustomerModel) userService.getUserForUID(((DistNpsProcessModel) businessProcessModel).getEmail()
                                                                                                                                       .toLowerCase());
            return getCustomerLanguageOrFallbackToDefault(customer);
        }
        return getFallBackLanguage();
    }

    private LanguageModel getCustomerLanguageOrFallbackToDefault(CustomerModel customer) {
        return customer.getSessionLanguage() != null ? customer.getSessionLanguage() : getFallBackLanguage();
    }

    private LanguageModel getFallBackLanguage() {
        return getCommonI18NService().getLanguage(DEFAULT_LANGUAGE);
    }

    public String getURLEncodedToken() throws UnsupportedEncodingException {
        return URLEncoder.encode(loginToken, "UTF-8");
    }

    public String getActivationUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), false, "/register/doubleoptin/activation",
                                                                      "token=" + getURLEncodedToken());
    }

    public String getSecureActivationUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), true, "/register/doubleoptin/activation", "token=" + getURLEncodedToken());
    }

    public String getDisplayActivationUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), false, "/register/doubleoptin/activation");
    }

    public String getDisplaySecureActivationUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), true, "/register/doubleoptin/activation");
    }

    public String getB2CSuccesUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), true,
                                                                      "/login" + "?qd=" + encodeToUTF8(Base64.encodeBase64String(customerData.getEmail()
                                                                                                                                             .getBytes())));
    }

    public String getB2CCheckoutSuccessUrl() throws UnsupportedEncodingException {
        return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSite(), true,
                                                                      "/login/checkout" + "?qd="
                                                                                           + encodeToUTF8(Base64.encodeBase64String(customerData.getEmail()
                                                                                                                                                .getBytes())));
    }

    private String encodeToUTF8(final String value) {
        String result = UriUtils.encodeFragment(value == null ? "" : value, "UTF-8");
        return result;
    }

    public CustomerData getCustomer() {
        return customerData;
    }

    public Converter<UserModel, CustomerData> getB2bCustomerConverter() {
        return b2bCustomerConverter;
    }

    @Required
    public void setB2bCustomerConverter(final Converter<UserModel, CustomerData> b2bCustomerConverter) {
        this.b2bCustomerConverter = b2bCustomerConverter;
    }

    public Converter<UserModel, CustomerData> getCustomerConverter() {
        return customerConverter;
    }

    @Required
    public void setCustomerConverter(final Converter<UserModel, CustomerData> customerConverter) {
        this.customerConverter = customerConverter;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(final String loginToken) {
        this.loginToken = loginToken;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(final CustomerType customerType) {
        this.customerType = customerType;
    }

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(final RegistrationType registrationType) {
        this.registrationType = registrationType;
    }

}
