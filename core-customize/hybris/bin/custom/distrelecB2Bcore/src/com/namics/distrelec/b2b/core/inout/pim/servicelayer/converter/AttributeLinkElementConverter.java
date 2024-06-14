/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.PimImportInitializer;

import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;

/**
 * Converts a "AttributeLink" XML element into a hybris {@link ClassAttributeAssignmentModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class AttributeLinkElementConverter implements PimImportElementConverter<ClassAttributeAssignmentModel> {

    private static final Logger LOG = LogManager.getLogger(AttributeLinkElementConverter.class);

    private static final String ATTRIBUTE_ATTRIBUTE_ID = "AttributeID";

    private static final Pattern DISPLAY_PATTERN = Pattern.compile("^\\d{3}[A-Z]$");

    private static final int DEFAULT_DISPLAY_PRORITY_NUMBER = 999;
    private static final String DEFAULT_DISPLAY_PRORITY_CHARACTER = "X";

    private static final Map<String, Integer> POSITION_OFFSET = new HashMap<String, Integer>();
    private static final Map<String, ClassificationAttributeVisibilityEnum> VISIBILITY = new HashMap<String, ClassificationAttributeVisibilityEnum>();
    private static final Set<String> SEARCHABLE = new HashSet<String>();

    static {
        POSITION_OFFSET.put("A", Integer.valueOf(1000));
        POSITION_OFFSET.put("B", Integer.valueOf(2000));
        POSITION_OFFSET.put("C", Integer.valueOf(3000));
        POSITION_OFFSET.put("D", Integer.valueOf(4000));
        POSITION_OFFSET.put("X", Integer.valueOf(99000));

        VISIBILITY.put("A", ClassificationAttributeVisibilityEnum.A_VISIBILITY);
        VISIBILITY.put("B", ClassificationAttributeVisibilityEnum.B_VISIBILITY);
        VISIBILITY.put("C", ClassificationAttributeVisibilityEnum.C_VISIBILITY);
        VISIBILITY.put("D", ClassificationAttributeVisibilityEnum.D_VISIBILITY);
        VISIBILITY.put("X", ClassificationAttributeVisibilityEnum.NOT_VISIBLE);

        SEARCHABLE.add("A");
    }

    @Autowired
    private PimImportInitializer<ClassAttributeAssignmentModel> classAttributeAssignmentInitializer;

    @Override
    public String getId(final Element element) {
        return element.attributeValue(ATTRIBUTE_ATTRIBUTE_ID);
    }

    @Override
    public void convert(final Element source, final ClassAttributeAssignmentModel target, final ImportContext importContext, final String hash) {
        classAttributeAssignmentInitializer.initialize(target, importContext);

        final String display = source.valueOf("MetaData/Value[@AttributeID='MA_WEBUSE_ELFA']");
        if (StringUtils.isNotEmpty(display) && !DISPLAY_PATTERN.matcher(display).matches()) {
            LOG.warn("MA_WEBUSE_ELFA [" + display + "] for classification [" + target.getClassificationClass().getCode() + "] attribute ["
                             + target.getClassificationAttribute().getCode() + "] has not expected format");
        }

        target.setPosition(getPosition(display));
        target.setVisibility(getVisibility(display));
        target.setSearchable(getSearchable(display));
    }

    /**
     * Converts the PIM sort field to a numeric position. The sort field is a number/character combination field (e.g. 003C). The character
     * sorts the attributes in 1st priority. (A before B). All attributes within the same character are sorted by the number. Trick: Convert
     * the character to a number (A > 1000, B > 2000) and add the numeric part. Here is an example:
     * <ul>
     * <li>001A > 1001</li>
     * <li>005A > 1005</li>
     * <li>010B > 2010</li>
     * <li>020B > 2020</li>
     * <li>025B > 2025</li>
     * <li>020C > 3010</li>
     * <li>040C > 3040</li>
     * </ul>
     * 
     * @return position
     */
    private Integer getPosition(final String displayPriority) {
        if (!StringUtils.isEmpty(displayPriority)) {
            final int parseInt = extractPriorityNumber(displayPriority);

            final String priorityField = extractPriorityCharacter(displayPriority);
            final Integer offset = POSITION_OFFSET.get(priorityField.toUpperCase(Locale.ENGLISH));
            if (offset != null) {
                return Integer.valueOf(parseInt + offset.intValue());
            }
        }

        return null;
    }

    private int extractPriorityNumber(final String displayPriority) {
        // extract numeric part of the position string
        final String pattern = "(\\d+).*";
        final String position = displayPriority.replaceAll(pattern, "$1");

        try {
            // lets see if it is numeric
            return Integer.parseInt(position);
        } catch (final NumberFormatException e) {
            LOG.warn("cannot extract position from displayPriority [" + displayPriority + "]");
        }
        return DEFAULT_DISPLAY_PRORITY_NUMBER;
    }

    /**
     * Extract the priorityCharacter (e.g. B) from the property displayPriority (020B). Due to bad data quality just use the first
     * non-numeric character.
     * 
     * @return the priorityCharacter or in case of an error the default value
     */
    private String extractPriorityCharacter(final String displayPriority) {
        if (!StringUtils.isEmpty(displayPriority)) {
            final String pattern = "\\d*(\\D)";
            final String priorityCharacter = displayPriority.replaceAll(pattern, "$1").toUpperCase();

            // Check for valid priority character
            if (POSITION_OFFSET.containsKey(priorityCharacter)) {
                return priorityCharacter;
            }
        }

        return DEFAULT_DISPLAY_PRORITY_CHARACTER;
    }

    /**
     * convert value of property displayProirity to visible field
     * 
     * @return the converted visibility value
     */
    private ClassificationAttributeVisibilityEnum getVisibility(final String displayPriority) {
        final String priorityField = extractPriorityCharacter(displayPriority);
        return VISIBILITY.get(priorityField);
    }

    /**
     * convert value of property displayProirity to searchable field
     * 
     * @return the converted searchable value
     */
    private Boolean getSearchable(final String displayPriority) {
        final String prorityFiled = extractPriorityCharacter(displayPriority);
        return Boolean.valueOf(SEARCHABLE.contains(prorityFiled));
    }

}
