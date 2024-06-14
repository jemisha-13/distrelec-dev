/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.i18n.impl;

import java.util.*;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Environment;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.i18n.impl.DefaultCommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import org.springframework.beans.factory.annotation.Required;

public class DefaultDistCommerceCommonI18NService extends DefaultCommerceCommonI18NService implements DistCommerceCommonI18NService {

    private SessionService sessionService;

    @Override
    public List<CurrencyModel> getAllCurrencies() {
        final CMSSiteModel cmsSite = (CMSSiteModel) getBaseSiteService().getCurrentBaseSite();
        if (cmsSite != null) {
            return new ArrayList<>(cmsSite.getRegistrationCurrencies());
        }
        return Collections.emptyList();
    }

    @Override
    public CurrencyModel getDefaultCurrency() {
        final CMSSiteModel cmsSite = (CMSSiteModel) getBaseSiteService().getCurrentBaseSite();
        if (cmsSite != null) {
            return cmsSite.getDefaultCurrency();
        }
        return null;
    }

    @Override
    public Collection<LanguageModel> getAllLanguages() {
        final BaseStoreModel store = getBaseStoreService().getCurrentBaseStore();
        if (store == null || store.getLanguages() == null) {
            // Fallback to all system languages
            return getCommonI18NService().getAllLanguages();
        } else {
            Set<LanguageModel> storeLanguages = store.getLanguages();
            boolean isInternalUser = getSessionService().getAttribute(Environment.INTERNAL_USER) != null;
            if (isInternalUser) {
                Set<LanguageModel> languages = new HashSet<>(storeLanguages);
                languages.addAll(store.getUnpublishedLanguages());
                return languages;
            } else {
                return storeLanguages;
            }
        }
    }

    @Override
    public LanguageModel getBaseLanguage(LanguageModel language) {
        String languageIsocode = language.getIsocode();
        if (languageIsocode.contains("_")) {
            String[] languageCodeParts = languageIsocode.split("_");
            String baseLanguageCode = languageCodeParts[0];
            return getCommonI18NService().getLanguage(baseLanguageCode);
        }
        return language;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
