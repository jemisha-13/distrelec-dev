package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.LANGUAGE_PARAM;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Implemented based on FactFinderChannelServiceImpl#getLanguage()
 */
class LanguageParamsPopulator implements Populator<SearchRequestTuple, MultiValuedMap<String, String>> {

    @Autowired
    private DistCommerceCommonI18NService distCommerceCommonI18NService;

    @Override
    public void populate(SearchRequestTuple searchRequestTuple, MultiValuedMap<String, String> params) throws ConversionException {
        LanguageModel language = distCommerceCommonI18NService.getCurrentLanguage();
        LanguageModel baseLanguage = distCommerceCommonI18NService.getBaseLanguage(language);
        String langIsoCode = baseLanguage.getIsocode();
        params.put(LANGUAGE_PARAM, langIsoCode);
    }

    void setDistCommerceCommonI18NService(DistCommerceCommonI18NService distCommerceCommonI18NService) {
        this.distCommerceCommonI18NService = distCommerceCommonI18NService;
    }
}
