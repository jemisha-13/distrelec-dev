/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.classification;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;

/**
 * Generates a qualifier for a ProductFeature based on its ClassificationAttributeAssignment.
 * 
 * @author rhusi, namics ag
 * @since MGB MEL 1.0
 * 
 * @deprecated Finally, I found out that you can do the same with impex "qualifier[default=,virtual=true,allowNull=true]" :)
 * 
 */
public class ProductFeatureQualifierDecorator extends AbstractImpExCSVCellDecorator {

    @SuppressWarnings("unchecked")
    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        String qualifier = null;
        final AbstractColumnDescriptor abstractColumnDescriptor = getColumnDescriptor();
        final Collection<AbstractColumnDescriptor> columns = abstractColumnDescriptor.getHeader().getColumnsByQualifier("classificationAttributeAssignment");
        if (columns.size() == 1) {
            final AbstractColumnDescriptor classAttAssignabstractColumnDescriptor = columns.iterator().next();
            final String definitionSrc = classAttAssignabstractColumnDescriptor.getDefinitionSrc();
            final String att = definitionSrc.substring(definitionSrc.indexOf('(') + 1, definitionSrc.indexOf(')'));
            final String value = paramMap.get(Integer.valueOf(classAttAssignabstractColumnDescriptor.getValuePosition()));
            final FlexibleSearch flexibleSearch = FlexibleSearch.getInstance();
            final List<ClassAttributeAssignment> result = flexibleSearch.search(
                    "SELECT {PK} FROM {ClassAttributeAssignment} WHERE {" + att + "}='" + value + "'", ClassAttributeAssignment.class).getResult();
            if (CollectionUtils.isEmpty(result)) {
                throw new JaloSystemException("Could not find ClassAttributeAssignment for attribute " + att + " and value " + value + "!");
            } else if (result.size() == 1) {
                final String classificationAttribute = result.get(0).getClassificationAttribute().getCode();
                final String classificationClass = result.get(0).getClassificationClass().getCode();
                final String systemVersion = result.get(0).getSystemVersion().getFullVersionName();

                qualifier = systemVersion + "/" + classificationClass + "." + classificationAttribute;
            } else {
                throw new JaloSystemException("Found more than one ClassAttributeAssignment for attribute " + att + " with value " + value
                        + "! Expected only one!");
            }
        } else {
            throw new JaloSystemException("Neccessary attribute 'classificationAttributeAssignment' not found in header definition!");
        }
        return qualifier;
    }
}
