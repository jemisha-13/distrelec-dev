/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.util;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.util.DistLogUtils;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Helper class to manage the format conversion
 * 
 * @author francesco, Distrelec AG, ksperner, Namics AG
 * @since Distrelec Extensions 1.0
 * 
 */
public class SoapConversionHelper {

    private static final Logger LOG = LogManager.getLogger(SoapConversionHelper.class);
    private static final String DATE_FORMAT_CONFIGURATION = "distrelec.sapPi.webservice.dateformat";
    private static final String DEFAULT_DATE_FORMAT = "yyyyMMdd";
    private static final String DEFAULT_SAP_DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * Convert the date in BigInteger with the configured date format
     * 
     * @param date
     * @param dateFormat
     * @return
     */
    public static BigInteger convertDate(final Date date) {
        return date == null ? null : new BigInteger(new SimpleDateFormat(getSoapDateFormat()).format(date));
    }

    public static Date convertDate(final BigInteger date) {
        try {
            return date == null || date.toString().isEmpty() ? null : new SimpleDateFormat(getSoapDateFormat()).parse(date.toString());
        } catch (final ParseException ex) {
            DistLogUtils.logError(LOG, "{} {} Error during date conversion. Invalid date: {}. Expected format: ", ex, ErrorLogCode.CONVERSION_FORMAT_ERROR,
                    ErrorSource.SAP_FAULT, date, getSoapDateFormat());
            return null;
        }
    }

    public static BigInteger getResultSizeFromPaginationData(final PageableData paginationData) {
        return BigInteger.valueOf(paginationData.getPageSize());
    }

    public static BigInteger getResultOffsetFromPaginationData(final PageableData paginationData) {
        return BigInteger.valueOf(paginationData.getCurrentPage() * paginationData.getPageSize() + 1);
    }

    /**
     * Convert timestamp from SAP format "yyyyMMddHHmmss" to {@link java.util.Date}.
     * 
     * @param bigIntDate
     *            The timestamp in SAP PI format
     * @return the timestamp as Date instance
     */
    public static Date convertTimestampFromSap(final BigInteger bigIntDate) {
        try {
            return bigIntDate == null ? null : new SimpleDateFormat(DEFAULT_SAP_DATE_FORMAT).parse(bigIntDate.toString());
        } catch (final ParseException pe) {
            DistLogUtils.logError(LOG, "{} {} Error parsing date. Expected format: {}. Input format: {}", pe, ErrorLogCode.CONVERSION_FORMAT_ERROR,
                    ErrorSource.SAP_FAULT, DEFAULT_SAP_DATE_FORMAT, bigIntDate.toString());
            return null;
        }
    }

    /**
     * Convert timestamp from {@link java.util.Date} to SAP format "yyyyMMddHHmmss".
     * 
     * @param date
     *            The timestamp as Date instance
     * @return the timestamp in SAP PI format
     */
    public static BigInteger convertTimestampToSap(final Date date) {
        return date == null ? null : new BigInteger(new SimpleDateFormat(DEFAULT_SAP_DATE_FORMAT).format(date));
    }

    public static String getSoapDateFormat() {
        final ConfigurationService configurationService = (ConfigurationService) Registry.getApplicationContext().getBean("configurationService");
        if (configurationService != null) {
            return configurationService.getConfiguration().getString(DATE_FORMAT_CONFIGURATION, DEFAULT_DATE_FORMAT);
        }

        return DEFAULT_DATE_FORMAT;
    }
}
