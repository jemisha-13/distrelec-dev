/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;

/**
 * <p>
 * There are situations,when you receive a importValue like <code>value1:value2</code>, but you need some default values to extract the
 * values, so that impex is receiving <code>value1:defaultA:defaultB:value2</code>.
 * </p>
 * 
 * 
 * 
 * <p>
 * This decorator fills in the fields passed in the header by [f2=defaultA][f3=defaultB] in the string and returns the result string. Splits
 * the originalValue from impex with missing positions given by the modifiers.
 * </p>
 * 
 * <p>
 * 
 * <pre>
 * Map:{2-&gt;test, 3-&gt;other}
 * originalValue="first:fourth"
 * resultValue=first:test:other:fourth
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * Here an example, how it works for impex:
 * 
 * <pre>
 * INSERT_UPDATE MyRelation;source(code)[unique=true];target($ClassMain_ClassificationClass,$ClassMain_ClassificationAttribute)[cellDecorator=com.namics.hybris.toolbox.impex.DefaultValueFillerDecorator][f1=][f2=][f4=];sequenceNumber
 * ;ATTRGRUPPE_TECHNISCH;MEL_CLA_Notebook_Computer_PC-Welt:audioausgang;0
 * </pre>
 * 
 * </p>
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class DefaultValueFillerDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(DefaultValueFillerDecorator.class.getName());

    public static final String MODIFIER_PREFIX = "f";
    public static final int MAX_NUM_OF_MODIFIERS = 10;

    @SuppressWarnings("unchecked")
    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        final AbstractColumnDescriptor abstractColumnDescriptor = getColumnDescriptor(); // the descriptor of the whole header
        final String importValue = paramMap.get(Integer.valueOf(paramInt));

        final Map<Integer, String> fillValueMap = createValueMap(abstractColumnDescriptor.getHeader().getColumns().get(paramInt - 1).getDescriptorData()
                .getModifiers());
        final String importResultValue = createResultValue(importValue, fillValueMap, ":");
        LOG.debug("Transformation:" + importValue + "->" + importResultValue);
        return importResultValue;
    }

    private String createKey(final int num) {
        return MODIFIER_PREFIX + num;
    }

    /**
     * Creates a value <code>Map</code> with the position numbers as keys and the values to insert in the importValue as value.
     * 
     * @param parameterMap
     *            The modifier Map from ImpEx.
     * @return The number map.
     */
    public SortedMap<Integer, String> createValueMap(final Map<String, String> parameterMap) {
        final SortedMap<Integer, String> resultMap = new TreeMap<Integer, String>();

        for (int i = 0; i < MAX_NUM_OF_MODIFIERS; i++) {
            final String parameterKey = createKey(i);
            if (parameterMap.containsKey(parameterKey)) {
                resultMap.put(Integer.valueOf(i), parameterMap.get(parameterKey));
            }
        }

        return resultMap;
    }

    /**
     * <p>
     * Splits the <code>originalValue</code> and fills in the missing positions with the values from <code>insertedValueMap</code>.
     * </p>
     * 
     * <p>
     * Map:{2-&gt;test, 3-&gt;other} originalValue="first:fourth" resultValue=first:test:other:fourth
     * </p>
     */
    public String createResultValue(final String originalValue, final Map<Integer, String> insertedValueMap, final String delimiter) {
        final List<String> resultList = new ArrayList<String>();
        final String[] parts = StringUtils.split(originalValue, delimiter);
        int partIndex = 0;
        for (int i = 0; i < MAX_NUM_OF_MODIFIERS; i++) {
            if (insertedValueMap.containsKey(Integer.valueOf(i))) {
                final String valueToInsert = insertedValueMap.get(Integer.valueOf(i));
                resultList.add(valueToInsert);
            } else {
                if (partIndex > (parts.length - 1)) {
                    break;
                }
                resultList.add(parts[partIndex]);
                partIndex++;
            }
        }

        return StringUtils.collectionToDelimitedString(resultList, delimiter);
    }

}
