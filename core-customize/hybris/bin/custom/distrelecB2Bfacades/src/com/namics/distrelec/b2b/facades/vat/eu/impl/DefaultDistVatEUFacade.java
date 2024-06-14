/*
 * Copyright 2000-2017 Distrelec Group AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.vat.eu.impl;

import java.net.SocketTimeoutException;

import javax.xml.ws.WebServiceException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.vatEU.DistEUVatService;
import com.namics.distrelec.b2b.core.service.vatEU.impl.VatEUServiceException;
import com.namics.distrelec.b2b.facades.vat.eu.DistVatEUFacade;

public class DefaultDistVatEUFacade implements DistVatEUFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistVatEUFacade.class);

    @Autowired
    private DistEUVatService vatService;

    @Override
    public boolean validateVatNumber(String vatNumber, final String countryCode) {
        try {
            if (vatNumber != null) {
                vatNumber = vatNumber.replaceAll("\\s+", StringUtils.EMPTY);
                return vatService.validateVat(vatNumber, countryCode);
            } else {
                // unknown vat number is considered as valid
                return true;
            }
        } catch (

        final VatEUServiceException ve) {
            LOG.error("An error occurred in the VAT EU Service call. Message: {}", ve.getMessage());
            return Boolean.TRUE;
        } catch (final SocketTimeoutException e) {
            LOG.error("Timeout occurred in the VAT EU Service call. Could not receive Message", e);
            return Boolean.TRUE;
        } catch (final WebServiceException e) {
            LOG.error("WebServiceException occurred in the VAT EU Service call. Could not receive Message", e);
            return Boolean.TRUE;
        } catch (final Exception e) {
            LOG.error("An Exception occurred in the VAT EU Service call.", e);
            return Boolean.TRUE;
        }
    }

    @Override
    public boolean isVatValidatedExternally(String countryCode) {
        switch (countryCode) {
            case "AT":
            case "BE":
            case "CZ":
            case "DK":
            case "EE":
            case "FI":
            case "FR":
            case "DE":
            case "HU":
            case "IT":
            case "LV":
            case "LT":
            case "NL":
            case "PL":
            case "RO":
            case "SK":
            case "BG":
            case "HR":
            case "CY":
            case "GR":
            case "IE":
            case "LU":
            case "MT":
            case "PT":
            case "SI":
            case "ES":
            case "XI":
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getVatPrefixForCountry(String countryCode) {
        if (StringUtils.equals(countryCode, DistConstants.CountryIsoCode.GREECE)) {
            return "EL";
        }
        return countryCode;
    }

    @Override
    public String getVatWithoutPrefix(String vat, String country) {
        String prefix = getVatPrefixForCountry(country);
        if (StringUtils.isNotEmpty(prefix) && StringUtils.startsWith(vat, prefix)) {
            return vat.substring(prefix.length());
        }
        return vat;
    }
}
