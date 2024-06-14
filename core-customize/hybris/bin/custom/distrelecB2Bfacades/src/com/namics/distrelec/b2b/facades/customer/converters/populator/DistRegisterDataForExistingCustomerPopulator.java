package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;

import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistRegisterDataForExistingCustomerPopulator implements Populator<RegisterData, DistExistingCustomerRegisterData> {
    @Override
    public void populate(final RegisterData source, final DistExistingCustomerRegisterData target) throws ConversionException {
        target.setEmail(source.getLogin());
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setLogin(source.getLogin());
        target.setPassword(source.getPassword());
        target.setTitleCode(source.getTitleCode());
        target.setCustomerId(source.getCustomerId());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setMobileNumber(source.getMobileNumber());
        target.setFaxNumber(source.getFaxNumber());
        target.setNewsletterOption(source.getMarketingConsent());
        target.setNpsConsent(source.getMarketingConsent());
        target.setPhoneMarketingOption(source.getMarketingConsent());
        target.setMarketingCookieEnabled(source.getIsMarketingCookieEnabled());
        target.setPhoneConsent(source.getPhoneConsent());
        target.setSmsConsent(source.getSmsConsent());
        target.setPostConsent(source.getPostConsent());
        target.setPersonalisationConsent(source.getPersonalisationConsent());
        target.setProfilingConsent(source.getProfilingConsent());
        target.setCountryCode(source.getCountryCode());
    }
}
