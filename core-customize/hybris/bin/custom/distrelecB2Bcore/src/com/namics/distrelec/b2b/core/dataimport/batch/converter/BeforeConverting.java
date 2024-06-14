/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.util.CSVReader;

/**
 * Call some business logic before the file gets converted to an ImpEx.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public interface BeforeConverting {

    /**
     * Executes some business logic before the converting is started.
     * 
     * @param header
     *            the batch header
     * @param csvReader
     *            the csvReader that is holding the CSV file
     */
    void before(final BatchHeader header, final CSVReader csvReader);
}
