/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.common;

import java.util.List;

import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfCustomParameter;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfString;
import de.factfinder.webservice.ws71.FFsearch.CustomParameter;

/**
 * Helper class to handle "DetailCustomParameters".
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class CustomParameterHelper {

    public static CustomParameter createCustomParameter(final String name, final List<String> values) {
        final CustomParameter customParameter = new CustomParameter();
        customParameter.setName(name);

        final ArrayOfString arrayOfString = new ArrayOfString();
        final List arrayOfStringValues = arrayOfString.getString();
        for (final String value : values) {
            arrayOfStringValues.add(value);
        }
        customParameter.setValues(arrayOfString);
        return customParameter;
    }

    public static void addCustomParameter(final SearchRequest target, final CustomParameter customParameter) {
        ArrayOfCustomParameter arrayOfCustomParameter = target.getSearchParams().getDetailCustomParameters();
        if (arrayOfCustomParameter == null) {
            arrayOfCustomParameter = new ArrayOfCustomParameter();
            target.getSearchParams().setDetailCustomParameters(arrayOfCustomParameter);
        }
        arrayOfCustomParameter.getCustomParameter().add(customParameter);
    }

}
