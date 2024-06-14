/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.conv;

import com.namics.distrelec.occ.core.formatters.WsDateFormatter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import java.util.Date;

/**
 * Converter for a specific date format.
 */
public class CustomDateConverter implements SingleValueConverter {
    private WsDateFormatter wsDateFormatter;

    public void setWsDateFormatter(final WsDateFormatter wsDateFormatter) {
        this.wsDateFormatter = wsDateFormatter;
    }

    @Override
    public boolean canConvert(final Class type) {
        return type == Date.class;
    }

    @Override
    public String toString(final Object obj) {
        return wsDateFormatter.toString((Date) obj);

    }

    @Override
    public Object fromString(final String str) {
        return wsDateFormatter.toDate(str);
    }
}
