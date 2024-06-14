/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.user.converters.populator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Address reverse poplulator.
 *
 * @author daehusir, Distrelec
 *
 */
public class DistAddressReversePopulator extends AddressReversePopulator {

    @Autowired
    private DistrelecCodelistService codelistService;

    @Override
    public void populate(final AddressData source, final AddressModel target) {
        super.populate(source, target);

        target.setPobox(source.getPobox());
        target.setContactAddress(source.isContactAddress());
        if (StringUtils.isNotEmpty(source.getDepartmentCode())) {
            target.setDistDepartment(codelistService.getDistDepartment((source).getDepartmentCode()));
        }
        target.setCellphone(source.getCellphone());
        target.setFax(source.getFax());
        target.setCompanyName2(source.getCompanyName2());
        target.setCompanyName3(source.getCompanyName3());
        target.setAdditionalAddressCompany(source.getAdditionalAddress());
        target.setEmail(source.getEmail());

        if (source.getCountry() != null && (source).getRegion() != null) {
            final String countryIso = source.getCountry().getIsocode();
            try {
                final CountryModel countryModel = getCommonI18NService().getCountry(countryIso);
                final String regionIso = source.getRegion().getIsocode();

                try {
                    final RegionModel regionModel = getCommonI18NService().getRegion(countryModel, regionIso);
                    target.setRegion(regionModel);
                } catch (final UnknownIdentifierException e) {
                    throw new ConversionException("No region with the code " + regionIso + " found.", e);
                } catch (final AmbiguousIdentifierException e) {
                    throw new ConversionException("More than one region with the code " + regionIso + " found.", e);
                }
            } catch (final UnknownIdentifierException e) {
                throw new ConversionException("No country with the code " + countryIso + " found.", e);
            } catch (final AmbiguousIdentifierException e) {
                throw new ConversionException("More than one country with the code " + countryIso + " found.", e);
            }
        } else {
            target.setRegion(null);
        }
    }
}
