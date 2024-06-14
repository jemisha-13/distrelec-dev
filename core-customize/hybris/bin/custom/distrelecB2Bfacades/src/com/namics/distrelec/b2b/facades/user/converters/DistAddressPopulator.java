/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.user.converters;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;

import de.hybris.platform.commercefacades.user.converters.populator.AddressPopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Distrelec Address Converter.
 *
 * @author daehusir, Distrelec
 *
 */
public class DistAddressPopulator extends AddressPopulator {

    @Autowired
    @Qualifier("countryConverter")
    private Converter<CountryModel, CountryData> countryConverter;

    @Autowired
    private DistUserService userService;

    @Autowired
    private DistCheckoutFacade distCheckoutFacade;

    protected AddressData createTarget() {
        return new AddressData();
    }

    @Override
    public void populate(final AddressModel source, final AddressData target) {
        super.populate(source, target);

        final AddressData addressData = target;
        addressData.setCompanyName2(source.getCompanyName2());
        addressData.setCompanyName3(source.getCompanyName3());
        addressData.setPobox(source.getPobox());
        addressData.setContactAddress(source.getContactAddress());
        if (source.getDistDepartment() != null) {
            addressData.setDepartment(source.getDistDepartment().getRelevantName());
            addressData.setDepartmentEN(source.getDistDepartment().getRelevantName(Locale.ENGLISH));
            addressData.setDepartmentCode(source.getDistDepartment().getCode());
        } else if (source.getDepartment() != null) {
            addressData.setDepartment(source.getDepartment());
            addressData.setDepartmentEN(source.getDepartment());
        }

        addressData.setPhone1(source.getPhone1());
        addressData.setPhone2(source.getPhone2());
        addressData.setPhone(source.getPhone1());
        addressData.setFax(source.getFax());
        addressData.setAdditionalAddress(source.getAdditionalAddressCompany());
        addressData.setErpAddressID(source.getErpAddressID());

        // Override the CountryData from super class to have an instance of DistCountryData
        if (source.getCountry() != null) {
            target.setCountry(countryConverter.convert(source.getCountry()));
        }

        final RegionModel regionModel = source.getRegion();
        if (regionModel != null) {
            final RegionData regionData = new RegionData();
            regionData.setIsocode(regionModel.getIsocode());
            regionData.setName(regionModel.getName());
            regionData.setNameEN(regionModel.getName(Locale.ENGLISH));
            regionData.setCountryIso(addressData.getCountry().getIsocode());
            addressData.setRegion(regionData);
        }

        final UserModel customer = userService.getCurrentUser();
        addressData.setDefaultBilling(source.equals(customer.getDefaultPaymentAddress()));
        addressData.setDefaultShipping(source.equals(customer.getDefaultShipmentAddress()));
    }
}
