/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter.impl.before;

import java.util.Map;

import com.namics.distrelec.b2b.core.dataimport.batch.converter.BeforeEachConverting;

/**
 * BeforeEachPunchOutFilterManufacturer.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public class BeforeEachPunchOutFilterManufacturer implements BeforeEachConverting {

    @Override
    public void beforeEach(final Map<Integer, String> row) {
        row.put(Integer.valueOf(2), row.get(Integer.valueOf(2)).toLowerCase());
    }

}
