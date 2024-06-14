/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.context.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.occ.core.constants.YcommercewebservicesConstants;
import com.namics.distrelec.occ.core.context.ContextInformationLoader;
import com.namics.distrelec.occ.core.exceptions.RecalculationException;
import com.namics.distrelec.occ.core.exceptions.UnsupportedCurrencyException;
import com.namics.distrelec.occ.core.exceptions.UnsupportedLanguageException;

import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.webservicescommons.util.YSanitizer;

/**
 * Default context information loader
 */
public class DefaultContextInformationLoader implements ContextInformationLoader {
    private static final String[] urlSplitters = { "/v2/" };

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContextInformationLoader.class);

    private CommonI18NService commonI18NService;

    private CommerceCommonI18NService commerceCommonI18NService;

    private BaseStoreService baseStoreService;

    private CartService cartService;

    private CalculationService calculationService;

    @Autowired
    private DistUserService userService;

    @Override
    public LanguageModel setLanguageFromRequest(final HttpServletRequest request) throws UnsupportedLanguageException {
        final String languageString = request.getParameter(YcommercewebservicesConstants.HTTP_REQUEST_PARAM_LANGUAGE);
        LanguageModel languageToSet = null;

        if (StringUtils.isNotBlank(languageString)) {
            try {
                languageToSet = getCommonI18NService().getLanguage(languageString);
            } catch (final UnknownIdentifierException e) {
                throw new UnsupportedLanguageException("Language  " + YSanitizer.sanitize(languageString) + " is not supported", e);
            }
        }

        if (isNull(languageToSet)) {
            languageToSet = getCommerceCommonI18NService().getDefaultLanguage();
        }

        final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();

        if (nonNull(currentBaseStore)) {
            final Collection<LanguageModel> storeLanguages = getStoresLanguages(currentBaseStore);

            if (storeLanguages.isEmpty()) {
                throw new UnsupportedLanguageException("Current base store supports no languages!");
            }

            if (!storeLanguages.contains(languageToSet)) {
                throw new UnsupportedLanguageException(languageToSet);
            }
        }

        Locale localeToSet = getCommerceCommonI18NService().getLocaleForLanguage(languageToSet);
        if (languageOrLocaleNotMatching(languageToSet, localeToSet)) {
            getCommerceCommonI18NService().setCurrentLanguage(languageToSet);
        }
        return languageToSet;
    }

    private boolean languageOrLocaleNotMatching(LanguageModel languageToSet, Locale localeToSet) {
        return nonNull(languageToSet) && !languageToSet.equals(getCommerceCommonI18NService().getCurrentLanguage())
                || nonNull(localeToSet) && !localeToSet.equals(getCommerceCommonI18NService().getCurrentLocale());
    }

    protected Collection<LanguageModel> getStoresLanguages(final BaseStoreModel currentBaseStore) {
        if (currentBaseStore == null) {
            throw new IllegalStateException("No current base store was set!");
        }
        return currentBaseStore.getLanguages() == null ? Collections.<LanguageModel> emptySet() : currentBaseStore.getLanguages();
    }

    @Override
    public CurrencyModel setCurrencyFromRequest(final HttpServletRequest request) throws UnsupportedCurrencyException, RecalculationException {
        final String currencyString = request.getParameter(YcommercewebservicesConstants.HTTP_REQUEST_PARAM_CURRENCY);
        CurrencyModel currencyToSet = null;

        if (!StringUtils.isBlank(currencyString)) {
            try {
                currencyToSet = getCommonI18NService().getCurrency(currencyString);
            } catch (final UnknownIdentifierException e) {
                throw new UnsupportedCurrencyException("Currency " + YSanitizer.sanitize(currencyString) + " is not supported", e);
            }
        }

        UserModel currentUser = userService.getCurrentUser();
        if (!userService.isAnonymousUser(currentUser) && nonNull(currentUser.getSessionCurrency())) {
            currencyToSet = currentUser.getSessionCurrency();
        }

        if (currencyToSet == null) {
            currencyToSet = getCommerceCommonI18NService().getDefaultCurrency();
        }

        final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();

        if (nonNull(currentBaseStore)) {
            final List<CurrencyModel> storeCurrencies = getCommerceCommonI18NService().getAllCurrencies();

            if (CollectionUtils.isEmpty(storeCurrencies)) {
                throw new UnsupportedCurrencyException("Current base store supports no currencies!");
            }

            if (isUnsupportedByCurrentBaseStoreAndUser(currencyToSet, currentUser, storeCurrencies)) {
                throw new UnsupportedCurrencyException(currencyToSet);
            }
        }

        if (currencyToSet != null && !currencyToSet.equals(getCommerceCommonI18NService().getCurrentCurrency())) {
            getCommerceCommonI18NService().setCurrentCurrency(currencyToSet);
            recalculateCart(currencyString);
            LOG.debug("{} set as current currency", currencyToSet);
        }

        return currencyToSet;
    }

    private boolean isUnsupportedByCurrentBaseStoreAndUser(CurrencyModel currencyToSet, UserModel currentUser, List<CurrencyModel> storeCurrencies) {
        return !storeCurrencies.contains(currencyToSet) && (nonNull(currentUser.getSessionCurrency()) && currentUser.getSessionCurrency() != currencyToSet);
    }

    /**
     * Recalculates cart when currency has changed
     */
    protected void recalculateCart(final String currencyString) throws RecalculationException {
        if (getCartService().hasSessionCart()) {
            final CartModel cart = getCartService().getSessionCart();
            if (cart != null) {
                try {
                    getCalculationService().recalculate(cart);
                } catch (final CalculationException e) {
                    throw new RecalculationException(e, YSanitizer.sanitize(currencyString));
                }
            }
        }
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }

    @Required
    public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public CalculationService getCalculationService() {
        return calculationService;
    }

    @Required
    public void setCalculationService(final CalculationService calculationService) {
        this.calculationService = calculationService;
    }
}
