/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export;

import java.util.Map;

import de.hybris.platform.cms2.model.site.CMSSiteModel;

/**
 * Interface specifying methods to provide parameters for a FlexibleSearch.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 * 
 * @param <T>
 *            specifies any parameter type required to create the FlexibleSearch parameters
 */
public interface DistFlexibleSearchParameterProvider<T> {

    Map<String, Object> getParameters(T configParameter);

    Map<String, Object> getPriceExportParameters(CMSSiteModel cmsSite);

}
