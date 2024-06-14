/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import org.dom4j.Element;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;

/**
 * Interface with methods to convert an XML element into a hybris model.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 * 
 * @param <T>
 *            type of the resulting model
 */
public interface PimImportElementConverter<T> {

    String ALL_LANGUAGES_CODE = "zxx";

    String ATTRIBUTE_ID = "ID";
    String ATTRIBUTE_USER_TYPE_ID = "UserTypeID";
    String ATTRIBUTE_PARENT_ID = "ParentID";

    String PIM_DATE_FORMAT_SHORT = "yyyy-MM-dd";
    String PIM_DATE_LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String getId(Element element);

    void convert(Element source, T target, ImportContext importContext, String hash);

}
