/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.text;

import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

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
public class MultiReplaceTextDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(MultiReplaceTextDecorator.class.getName());

    public static final String MAPPING_DELIMITER = "mapping-delimiter";
    public static final String REPLACE_MAPPING = "replace-mapping";

    private String mappingDelimiter = "";
    private Map<String, String> replaceMapping = new HashMap<String, String>();

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        mappingDelimiter = column.getDescriptorData().getModifier(MAPPING_DELIMITER);

        int i = 0;
        for (;;) {
            final String key = REPLACE_MAPPING + i;
            if (column.getDescriptorData().getModifiers().containsKey(key)) {
                final String mapping = column.getDescriptorData().getModifier(key);
                final String fromMapping = mapping.split(mappingDelimiter)[0];
                final String toMapping = mapping.split(mappingDelimiter)[1];
                replaceMapping.put(fromMapping, toMapping);
            } else {
                break;
            }
            i++;
        }

        if (mappingDelimiter == null) {
            mappingDelimiter = "->";
        }
        if ("".equals(mappingDelimiter)) {
            throw new HeaderValidationException("The modifier [" + MAPPING_DELIMITER + "] in " + MultiReplaceTextDecorator.class + " must be set.", -1);
        }
        if (replaceMapping.isEmpty()) {
            throw new HeaderValidationException("At least one modifier [" + REPLACE_MAPPING + "] in " + MultiReplaceTextDecorator.class + " must be set.", -1);
        }

    }

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        final String importValue = paramMap.get(Integer.valueOf(paramInt));
        String resultValue = importValue;
        for (String key : replaceMapping.keySet()) {
            final String value = replaceMapping.get(key);
            resultValue = resultValue.replaceAll(key, value);
        }
        LOG.debug(resultValue);
        return resultValue;
    }

}
