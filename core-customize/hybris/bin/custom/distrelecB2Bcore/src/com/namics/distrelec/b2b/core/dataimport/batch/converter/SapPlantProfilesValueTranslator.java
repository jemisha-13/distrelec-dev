/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.dataimport.batch.converter;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;

/**
 * {@code SapPlantProfilesValueTranslator}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.5
 */
public class SapPlantProfilesValueTranslator extends AbstractValueTranslator {


    /* (non-Javadoc)
     * @see de.hybris.platform.impex.jalo.translators.AbstractValueTranslator#exportValue(java.lang.Object)
     */
    @Override
    public String exportValue(final Object value) throws JaloInvalidParameterException {
        return value == null ? "" : value.toString();
    }

    /* (non-Javadoc)
     * @see de.hybris.platform.impex.jalo.translators.AbstractValueTranslator#importValue(java.lang.String, de.hybris.platform.jalo.Item)
     */
    @Override
    public Object importValue(final String valueExpr, final Item toItem) throws JaloInvalidParameterException {
        clearStatus();
        if (StringUtils.isBlank(valueExpr)) {
            return null;
        }

        final StringBuilder builder = new StringBuilder(valueExpr.replace("&|", "|"));
        if (builder.charAt(builder.length() - 1) == '&') {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }
}
