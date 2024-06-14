/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util;

import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;

public class ClassAttributeAssignmentTypeMap {

    public static final Map<String, ClassificationAttributeTypeEnum> TYPE_TRANSLATIONS = new HashMap<String, ClassificationAttributeTypeEnum>();

    public static final String EMBEDDED_NUMBER_PIM_TYPE = "embedded_number";
    public static final String INTEGER_PIM_TYPE = "integer";
    public static final String ISODATE_PIM_TYPE = "isodate";
    public static final String ISODATETIME_PIM_TYPE = "isodatetime";
    public static final String NUMBER_PIM_TYPE = "number";
    public static final String NUMERIC_TEXT_PIM_TYPE = "numeric_text";
    public static final String TEXT_PIM_TYPE = "text";

    static {
        TYPE_TRANSLATIONS.put(EMBEDDED_NUMBER_PIM_TYPE, ClassificationAttributeTypeEnum.STRING);
        TYPE_TRANSLATIONS.put(INTEGER_PIM_TYPE, ClassificationAttributeTypeEnum.NUMBER);
        TYPE_TRANSLATIONS.put(ISODATE_PIM_TYPE, ClassificationAttributeTypeEnum.DATE);
        TYPE_TRANSLATIONS.put(ISODATETIME_PIM_TYPE, ClassificationAttributeTypeEnum.DATE);
        TYPE_TRANSLATIONS.put(NUMBER_PIM_TYPE, ClassificationAttributeTypeEnum.NUMBER);
        TYPE_TRANSLATIONS.put(NUMERIC_TEXT_PIM_TYPE, ClassificationAttributeTypeEnum.STRING);
        TYPE_TRANSLATIONS.put(TEXT_PIM_TYPE, ClassificationAttributeTypeEnum.STRING);
    }

}
