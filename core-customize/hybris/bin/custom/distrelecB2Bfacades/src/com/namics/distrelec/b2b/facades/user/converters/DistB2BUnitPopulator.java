package com.namics.distrelec.b2b.facades.user.converters;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BUnitPopulator;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DistB2BUnitPopulator extends B2BUnitPopulator {

    @Autowired
    @Qualifier("countryConverter")
    private Converter<CountryModel, CountryData> countryConverter;

    @Override
    protected void populateUnit(final B2BUnitModel source, final B2BUnitData target) {
        super.populateUnit(source, target);
        target.setVatID(source.getVatID());
        target.setErpCustomerId(source.getErpCustomerID());
        target.setCountry(source.getCountry() == null ? null : countryConverter.convert(source.getCountry()));
        target.setOnlinePriceCalculation(BooleanUtils.isTrue(source.getOnlinePriceCalculation()));
    }
}
