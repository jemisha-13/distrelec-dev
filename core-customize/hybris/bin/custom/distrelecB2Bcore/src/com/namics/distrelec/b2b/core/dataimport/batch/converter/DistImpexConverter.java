/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter;

import java.util.Map;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;
import de.hybris.platform.util.CSVReader;

/**
 * DistImpexConverter extends the {@link ImpexConverter} with 2 hooks
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 * 
 */
public interface DistImpexConverter extends ImpexConverter {

    @Deprecated
    @Override
    String convert(Map<Integer, String> row, Long sequenceId);

    String convert(Map<Integer, String> row, Long sequenceId, String erpSequenceId);

    void before(final BatchHeader header, final CSVReader csvReader);
    
    void beforeEach(final Map<Integer, String> row);
}
