/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * AbstractCountryElementConverter.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class AbstractCountryElementConverter {
    private static final Logger LOG = LogManager.getLogger(AbstractCountryElementConverter.class);

    private CommonI18NService commonI18NService;

    private NamicsCommonI18NService namicsCommonI18NService;

    private ModelService modelService;

    public static final Pattern MULTI_LINE_VALUE_PATTERN = Pattern.compile("#NEWLINE#");
    public static final String MULTI_LINE_VALUE_REPLACEMENT = "<br/>";

    public static final List<String> MULTI_LINE_ATTRIBUTE_CODES = Arrays.asList( //
            DistManufacturerModel.GLOBALSUPPORTREPAIRINFO, //
            DistManufacturerCountryModel.SUPPORTREPAIRINFO);

    /**
     * get the CountryModel for a given iso code or create a new one if not exist
     * 
     * @param countryIsoCode
     * @return the CountryModel if exist or create one otherwise
     */
    protected CountryModel getOrCreateCountryForIsoCode(final String countryIsoCode) {
        CountryModel countryModel = null;
        try {
            countryModel = commonI18NService.getCountry(countryIsoCode.toUpperCase(Locale.ROOT));
        } catch (final UnknownIdentifierException e) {
            countryModel = new CountryModel();
            countryModel.setIsocode(countryIsoCode);
            getModelService().save(countryModel);
        }

        return countryModel;
    }

    /**
     * Write the localized values to a model by it's attribute qualifier.
     * 
     * @param model
     * @param attributeQualifier
     * @param value
     * @param language
     */
    protected void writeLocalizedValueForAttribute(final ItemModel model, final String attributeQualifier, final Object value, final String language) {
        final LanguageModel languageModel = namicsCommonI18NService.getLanguageModelByIsocodePim(language);
        if (languageModel == null) {
            LOG.error("Could not find language model with isocode pim {}", new Object[] { language });
            return;
        }

        Object attributeValue = value;
        if (MULTI_LINE_ATTRIBUTE_CODES.contains(attributeQualifier) && value instanceof String) {
            Matcher matcher = MULTI_LINE_VALUE_PATTERN.matcher((String) value);
            if (matcher.matches()) {
                attributeValue = matcher.replaceAll(MULTI_LINE_VALUE_REPLACEMENT);
            }
        }

        final Locale locale = commonI18NService.getLocaleForLanguage(languageModel);
        final Map valueMap = new HashMap<Locale, List<String>>();
        valueMap.put(locale, attributeValue);
        try {
            modelService.setAttributeValue(model, attributeQualifier, valueMap);
        } catch (final IllegalArgumentException e) {
            LOG.error("Could not write value {} for attribute {} on model {}", new Object[] { valueMap, attributeQualifier, model.getClass() });
        }
    }

    /**
     * Write the values to a model by it's attribute qualifier.
     * 
     * @param model
     * @param attributeQualifier
     * @param value
     */
    protected void writeValueForAttribute(final ItemModel model, final String attributeQualifier, final Object value) {
        try {
            Object attributeValue = value;
            if (MULTI_LINE_ATTRIBUTE_CODES.contains(attributeQualifier) && value instanceof String) {
                Matcher matcher = MULTI_LINE_VALUE_PATTERN.matcher((String) value);
                if (matcher.matches()) {
                    attributeValue = matcher.replaceAll(MULTI_LINE_VALUE_REPLACEMENT);
                }
            }

            getModelService().setAttributeValue(model, attributeQualifier, attributeValue);
        } catch (final IllegalArgumentException e) {
            LOG.error("Could not write value {} for attribute {} on model {}", new Object[] { value, attributeQualifier, model.getClass() });
        }
    }

    @Autowired
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    @Autowired
    public void setNamicsCommonI18NService(final NamicsCommonI18NService namicsCommonI18NService) {
        this.namicsCommonI18NService = namicsCommonI18NService;
    }

    @Autowired
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public NamicsCommonI18NService getNamicsCommonI18NService() {
        return namicsCommonI18NService;
    }

    public ModelService getModelService() {
        return modelService;
    }
}
