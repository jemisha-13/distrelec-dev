/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.converters;

import com.namics.distrelec.b2b.core.model.WtBasicParameterModel;
import com.namics.distrelec.b2b.facades.product.data.WtBasicParameterData;
import de.hybris.platform.converters.Populator;
import org.apache.log4j.Logger;
import org.springframework.web.util.UriUtils;

public class WtBasicParameterDataPopulator implements Populator<WtBasicParameterModel, WtBasicParameterData> {

    private static final Logger LOG = Logger.getLogger(WtBasicParameterDataPopulator.class);

    @Override
    public void populate(final WtBasicParameterModel source, final WtBasicParameterData target) {
        target.setKey(UriUtils.encodeFragment(source.getKey() != null ? source.getKey() : "", "UTF-8"));
        target.setValue(UriUtils.encodeFragment(source.getValue() != null ? source.getValue() : "", "UTF-8"));
    }

}
