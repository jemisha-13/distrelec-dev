/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter.impl.before;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import com.namics.distrelec.b2b.core.dataimport.batch.converter.BeforeEachConverting;
import com.namics.distrelec.b2b.core.util.DistDateTimeUtils;

/**
 * Calculates the start and end date for a bestseller label.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public class BeforeEachProductBestSellers implements BeforeEachConverting {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");

    @Override
    public void beforeEach(final Map<Integer, String> row) {
        final Date today = DistDateTimeUtils.getDateAtMidnightStart();
        final Date endDate = DistDateTimeUtils.getDateAtMidnightEnd(DateUtils.addDays(today, 30));
        row.put(Integer.valueOf(2), getDateFormat().format(today));
        row.put(Integer.valueOf(3), getDateFormat().format(endDate));
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

}
