/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Converts a "Unit" XML element into a hybris {@link ClassificationAttributeUnitModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class UnitElementConverter implements PimImportElementConverter<ClassificationAttributeUnitModel> {

    private static final Logger LOG = LogManager.getLogger(UnitElementConverter.class);

    private static final String NAME = "Name";
    private static final String ATTRIBUTE_BASE_UNIT_ID = "UnitConversion/@BaseUnitID";
    private static final String ATTRIBUTE_FACTOR = "UnitConversion/@Factor";

    @Autowired
    private ConverterLanguageUtil converterLanguageUtil;

    @Override
    public String getId(final Element source) {
        return source.attributeValue(ATTRIBUTE_ID);
    }

    @Override
    public void convert(final Element source, final ClassificationAttributeUnitModel target, final ImportContext importContext, final String hash) {
        String name = source.elementTextTrim(NAME);

        if (StringUtils.isBlank(name)) {
            LOG.warn("Unit name for unit [{}] not found. Using ID instead", target.getCode());
            name = target.getCode();
        }

        Map<Locale, String> localizedNames = getConverterLanguageUtil().getLocalizedValues(source, NAME);
        localizedNames.forEach(((locale, value) -> {
            target.setName(value, locale);
        }));

        // XML-Element "Name" used as symbol
        target.setSymbol(localizedNames.getOrDefault(Locale.ENGLISH, name));

        final String baseUnitId = source.valueOf(ATTRIBUTE_BASE_UNIT_ID);
        if (StringUtils.isNotBlank(baseUnitId)) {
            target.setUnitType(baseUnitId);
        } else {
            // Use own code as unitType if no BaseUnitID set
            target.setUnitType(target.getCode());
        }

        final String factorString = source.valueOf(ATTRIBUTE_FACTOR);
        if (StringUtils.isNotBlank(factorString)) {
            target.setConversionFactor(Double.valueOf(factorString));
        } else {
            // Set conversionFactor to 1.0 if not set
            target.setConversionFactor(Double.valueOf(1.0));
        }

        target.setPimXmlHashMaster(hash);
        target.setPimHashTimestamp(new Date());
    }

    protected ConverterLanguageUtil getConverterLanguageUtil() {
        return converterLanguageUtil;
    }

    protected void setConverterLanguageUtil(ConverterLanguageUtil converterLanguageUtil) {
        this.converterLanguageUtil = converterLanguageUtil;
    }
}
