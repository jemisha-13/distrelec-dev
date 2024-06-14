package com.namics.distrelec.populator;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class DistAbstractPageModelToDataRenderingPopulator implements Populator<AbstractPageModel, Map<String, Object>> {

    public static final String LANGUAGE_EN = "languageEN";

    @Autowired
    private CommonI18NService commonI18NService;

    @Override
    public void populate(AbstractPageModel source, Map<String, Object> target) throws ConversionException {
        Locale locale = LocaleUtils.toLocale(commonI18NService.getCurrentLanguage().getIsocode());
        target.put(LANGUAGE_EN, commonI18NService.getLanguage(locale.getLanguage()).getName(Locale.ENGLISH).toLowerCase());
    }

}
