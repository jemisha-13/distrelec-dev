/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.converters;

import com.namics.distrelec.b2b.core.model.WtAdvancedParameterModel;
import com.namics.distrelec.b2b.facades.product.data.WtAdvancedParameterData;
import com.namics.distrelec.b2b.facades.product.data.WtBasicParameterData;
import de.hybris.platform.converters.Populator;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WtAdvancedParameterDataPopulator implements Populator<WtAdvancedParameterModel, WtAdvancedParameterData> {

    private static final Logger LOG = Logger.getLogger(WtAdvancedParameterDataPopulator.class);

    @Override
    public void populate(final WtAdvancedParameterModel source, final WtAdvancedParameterData target) {
        target.setKey(source.getKey());
        final List<WtBasicParameterData> entries = new ArrayList<WtBasicParameterData>();
        final WtBasicParameterData entry = new WtBasicParameterData();
        try {
            entry.setKey(URLEncoder.encode(source.getId() != null ? source.getId() : "", "UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
        }
        try {

            entry.setValue(URLEncoder.encode(source.getValue() != null ? source.getValue() : "", "UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
        }
        entries.add(entry);
        target.setIdValues(entries);
    }
}
