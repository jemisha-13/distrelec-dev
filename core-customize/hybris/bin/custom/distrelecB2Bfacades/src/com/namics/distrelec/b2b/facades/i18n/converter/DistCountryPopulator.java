/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2011 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.facades.i18n.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Accelerator specific country data converter implementation.
 *
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 */
public class DistCountryPopulator extends AbstractPopulatingConverter<CountryModel, CountryData> {

    private static final List<String> EU_COUNTRIES = Arrays.asList(
                                                                   //
                                                                   "AT", "BE", "BG", "HR", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU", "IE", "IT", "LV",
                                                                   "LI", "LT", "LU", "MT", "MC", "NL", "PL", "PT", "RO",
                                                                   "SK", "SI", "ES", "SE");

    @Autowired
    @Qualifier("regionConverter")
    private Converter<RegionModel, RegionData> regionDataConverter;

    @Override
    protected CountryData createTarget() {
        return new CountryData();
    }

    @Override
    public void populate(final CountryModel source, final CountryData target) {
        if (source != null) {
            super.populate(source, target);
            target.setIsocode(source.getIsocode());
            target.setName(source.getName());
            target.setNameEN(source.getName(Locale.ENGLISH));
            target.setEuropean(EU_COUNTRIES.contains(source.getIsocode()));
            final List<RegionData> regions = new ArrayList<>();
            for (final RegionModel region : source.getRegions()) {
                regions.add(getRegionDataConverter().convert(region));
            }
            target.setRegions(regions);
        }
    }

    public Converter<RegionModel, RegionData> getRegionDataConverter() {
        return regionDataConverter;
    }

    public void setRegionDataConverter(final Converter<RegionModel, RegionData> regionDataConverter) {
        this.regionDataConverter = regionDataConverter;
    }
}
