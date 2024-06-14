/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.catplus.impex.decorator;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.inout.impex.AbstractDistCellDecorator;

import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;

/**
 * MultiReplaceCharDecorator.
 * 
 * @author fbersani, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class MultiReplaceCharDecorator extends AbstractDistCellDecorator {
    private static final Logger LOG = Logger.getLogger(MultiReplaceCharDecorator.class.getName());

    public static final String MAPPING_DELIMITER = "mapping-delimiter";
    // public static final String REPLACE_MAPPING = "replace-mapping";
    public static final String REPLACE_MAPPING_ASCII = "replace-mappingAscii";

    private final Map<String, String> replaceMapping = new HashMap<String, String>();

    // public static void main(String[] args) {
    //
    // final Map<String, String> replaceMappingTest = new HashMap<String, String>();
    // System.out.println(Character.toString((char) 34));
    //
    // replaceMappingTest.put(Character.toString((char) 34), "");
    // final String importValue = "ciaooooo \"prova\" aaaaaaa";
    // String resultValue = importValue;
    // for (String key : replaceMappingTest.keySet()) {
    // final String value = replaceMappingTest.get(key);
    // resultValue = resultValue.replaceAll(key, value);
    // }
    //
    // }

    @Override
    public void init(final AbstractColumnDescriptor column) throws HeaderValidationException {
        super.init(column);

        String mappingDelimiter = column.getDescriptorData().getModifier(MAPPING_DELIMITER);

        int i = 0;

        // CHECK FOR ASCII
        for (;;) {
            final String key = REPLACE_MAPPING_ASCII + i;
            if (column.getDescriptorData().getModifiers().containsKey(key)) {
                final String mapping = column.getDescriptorData().getModifier(key);
                final int fromMappingAscii = Integer.parseInt(mapping.split(mappingDelimiter)[0]);
                final int toMappingAscii = Integer.parseInt(mapping.split(mappingDelimiter)[1]);
                replaceMapping.put(Character.toString((char) fromMappingAscii), Character.toString((char) toMappingAscii));
            } else {
                break;
            }
            i++;
        }

        if (mappingDelimiter == null) {
            mappingDelimiter = "->";
        }
        if ("".equals(mappingDelimiter)) {
            throw new HeaderValidationException("The modifier [" + MAPPING_DELIMITER + "] in " + MultiReplaceCharDecorator.class + " must be set.", -1);
        }
        if (replaceMapping.isEmpty()) {
            throw new HeaderValidationException(
                    "At least one modifier [" + REPLACE_MAPPING_ASCII + "] in " + MultiReplaceCharDecorator.class + " must be set.", -1);
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
