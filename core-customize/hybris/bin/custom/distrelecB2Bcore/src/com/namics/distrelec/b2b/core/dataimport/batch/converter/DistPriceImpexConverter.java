/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.dataimport.batch.converter;

import java.util.Map;

import de.hybris.platform.acceleratorservices.dataimport.batch.converter.ImpexConverter;

/**
 * Interface for converting a CSV file for prices into ImpEx.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 */
public interface DistPriceImpexConverter extends ImpexConverter {

    /**
     * Retrieves the ImpEx header for INSERT/UPDATE.
     * 
     * @return ImpEx import header for INSERT/UPDATE
     */
    String getInsertUpdateHeader();

    /**
     * Retrieves the ImpEx header for REMOVE.
     * 
     * @return ImpEx import header for REMOVE
     */
    String getRemoveHeader();

    /**
     * Return the version of the price impex converter. The versions that we have are:
     * <u>
     * <li>6 scales: may be used for both SAP and MOVEX cases</li>
     * <li>10 scales: can be used in SAP case because MOVEX does not support more than 6 scales.</li>
     * </u>
     * @return the version of the price impex converter
     */
    public String getVersion();

    /**
     * Converts a CSV row for INSERT/UPDATE to ImpEx.
     * 
     * @param row
     *            a CSV row containing column indexes and values
     * @param sequenceId
     *            the sequence ID
     * @return a converted ImpEx line
     */
    String convertInsertUpdate(final Map<Integer, String> row, final Long sequenceId, final String erpSequenceId);

    /**
     * Converts a CSV row for REMOVE to ImpEx.
     * 
     * @param row
     *            a CSV row containing column indexes and values
     * @param sequenceId
     *            the sequence ID
     * @return a converted ImpEx line
     */
    String convertRemove(final Map<Integer, String> row, final Long sequenceId, String erpSequenceId);

    /**
     * Do not use the normal converter method!
     * 
     * @see com.namics.distrelec.b2b.core.dataimport.batch.converter.DistPriceImpexConverter#convertInsertUpdate(java.util.Map, java.lang.Long)
     * @see com.namics.distrelec.b2b.core.dataimport.batch.converter.DistPriceImpexConverter#convertRemove(java.util.Map, java.lang.Long)
     */
    @Override
    @Deprecated
    String convert(final Map<Integer, String> row, final Long sequenceId);
}
