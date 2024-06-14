/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter;

import java.util.Map;

/**
 * Call some business logic before a single row gets converted to an ImpEx line.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public interface BeforeEachConverting {

    /**
     * Executes some business logic before a single row gets converted.
     * 
     * @param row
     *            the current row
     */
    void beforeEach(final Map<Integer, String> row);
}
