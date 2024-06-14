package com.namics.distrelec.b2b.core.i18n;

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.i18n.impl.CommerceLanguageResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Collection;

public class DistrelecCommerceLanguageResolver extends CommerceLanguageResolver {

    private static final Logger LOG = Logger.getLogger(DistrelecCommerceLanguageResolver.class);

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public LanguageModel getLanguage(String isoCode) {
        Collection<LanguageModel> languages = getCommerceCommonI18NService().getAllLanguages();

        if (languages.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No supported languages found for the current site, get all session languages instead.");
            }

            // Fallback to all languages
            languages = getCommonI18NService().getAllLanguages();
        }

        Assert.notEmpty(languages, "No supported languages found for the current site.");

        // Look for the language with a matching iso code
        LanguageModel languageModel = null;
        for (final LanguageModel language : languages) {
            if (StringUtils.equals(language.getIsocode(), isoCode)) {
                languageModel = language;
                break;
            }
        }

        if (languageModel == null) {
            languageModel = getCommerceCommonI18NService().getDefaultLanguage();
        }

        languageModel = switchLanguageWithCountrySpecificIfAvailable(languageModel);

        Assert.notNull(languageModel, "Language to set is not supported.");

        return languageModel;
    }

    private LanguageModel switchLanguageWithCountrySpecificIfAvailable(LanguageModel currentLanguage){
        String currentCountryIsocode = cmsSiteService.getCurrentSite().getCountry().getIsocode();
        String languageSpecificIsocode = currentLanguage.getIsocode() + "_" + currentCountryIsocode;
        return getCommonI18NService().getAllLanguages().stream()
                .filter(language -> language.getIsocode().equals(languageSpecificIsocode))
                .findFirst().orElse(currentLanguage);
    }
}
