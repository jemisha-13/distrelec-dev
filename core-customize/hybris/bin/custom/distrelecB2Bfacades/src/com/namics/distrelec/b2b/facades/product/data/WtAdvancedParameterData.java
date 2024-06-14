/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class WtAdvancedParameterData extends WtBasicParameterData {
    private static final Logger LOG = LogManager.getLogger(WtAdvancedParameterData.class);

    private List<WtBasicParameterData> idValues;

    public List<WtBasicParameterData> getIdValues() {
        return idValues;
    }

    public void setIdValues(final List<WtBasicParameterData> idValues) {
        this.idValues = idValues;
    }

    public void addIdValue(final String id, final String value) {
        final WtBasicParameterData entry = new WtBasicParameterData();
        entry.setKey(id);
        try {
            entry.setValue(URLEncoder.encode(value != null ? value : "", "UTF-8"));
        } catch (final UnsupportedEncodingException e) {
            LOG.error(e.getMessage());
        }
        this.idValues.add(entry);
    }
}
