/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.text;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.namics.hybris.toolbox.PlaceholderUtil;

import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.util.Config;

/**
 * <p>
 * Decorates a string with an arbitrary prefix and sufix.
 * </p>
 * 
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class PrefixSufixDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(PrefixSufixDecorator.class.getName());

    public static final String PREFIX_MODIFIER = "prefix";
    public static final String SUFIX_MODIFIER = "sufix";
    public static final String WHITESPACE_MODIFIER = "whitespace";
    public static final String PROCESS_EMPTY_VALUE_MODIFIER = "processEmptyValue";

    private String prefix = "";
    private String sufix = "";
    private boolean whitespace;
    private boolean processEmptyValue = true;

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        prefix = column.getDescriptorData().getModifier(PREFIX_MODIFIER);
        sufix = column.getDescriptorData().getModifier(SUFIX_MODIFIER);
        if (column.getDescriptorData().getModifier(WHITESPACE_MODIFIER) != null) {
            whitespace = column.getDescriptorData().getModifier(WHITESPACE_MODIFIER).equals("true");
        }
        if (column.getDescriptorData().getModifier(PROCESS_EMPTY_VALUE_MODIFIER) != null
                && column.getDescriptorData().getModifier(PROCESS_EMPTY_VALUE_MODIFIER).equals("false")) {
            processEmptyValue = false;
        }

        if (prefix == null) {
            prefix = "";
        }
        if (sufix == null) {
            sufix = "";
        }

        if ("".equals((prefix + sufix).trim())) {
            throw new HeaderValidationException("At least one of the both modifiers [" + PREFIX_MODIFIER + "] or [" + SUFIX_MODIFIER + "] in "
                    + PrefixSufixDecorator.class + " must be set.", -1);
        }

        if (whitespace) {
            prefix = prefix + " ";
            sufix = " " + sufix;
        }

    }

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        String importValue = paramMap.get(Integer.valueOf(paramInt));

        if (importValue == null) {
            importValue = "";
        }

        final Map<String, Object> parameters = new HashMap<String, Object>(Config.getAllParameters());
        prefix = PlaceholderUtil.parseStringValue(prefix, parameters);
        sufix = PlaceholderUtil.parseStringValue(sufix, parameters);
        String resultValue = null;

        if ("".equals(importValue) && !processEmptyValue) {
            return resultValue;
        } else if ("".equals(importValue) && processEmptyValue) {
            resultValue = prefix + sufix;
        } else {
            resultValue = prefix + importValue + sufix;
        }

        LOG.debug(resultValue);
        return resultValue;
    }

}
