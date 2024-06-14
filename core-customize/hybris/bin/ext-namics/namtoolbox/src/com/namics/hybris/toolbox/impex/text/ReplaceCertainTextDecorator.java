/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.text;

import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;

/**
 * <p>
 * Replaces a machting text in a string with the desired value.
 * </p>
 * 
 * 
 * @author mwegener, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class ReplaceCertainTextDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(ReplaceCertainTextDecorator.class.getName());

    public static final String TARGET_MODIFIER = "target";
    public static final String REPLACEMENT_MODIFIER = "replacement";
    public static final String REPLACE_WITH_WHITESPACE = "whitespaceReplace";

    private String target = "";
    private String replacement = "";

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        target = column.getDescriptorData().getModifier(TARGET_MODIFIER);
        replacement = column.getDescriptorData().getModifier(REPLACEMENT_MODIFIER);
        final String whitespaceReplace = column.getDescriptorData().getModifier(REPLACE_WITH_WHITESPACE);

        if (target == null) {
            target = "";
        }
        if (replacement == null) {
            replacement = "";
        }
        if (whitespaceReplace != null && "true".equals(whitespaceReplace)) {
            replacement = " ";
        }

        if ("".equals(target)) {
            throw new HeaderValidationException("The modifier [" + TARGET_MODIFIER + "] in " + ReplaceCertainTextDecorator.class + " must be set.", -1);
        }

    }

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        final String importValue = paramMap.get(Integer.valueOf(paramInt));
        final String resultValue = importValue.replaceAll(target, replacement);
        LOG.debug(resultValue);
        return resultValue;
    }

}
