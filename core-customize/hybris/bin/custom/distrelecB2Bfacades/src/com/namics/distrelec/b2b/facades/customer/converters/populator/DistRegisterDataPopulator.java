package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistRegisterDataPopulator implements Populator<RegisterData, DistRegisterData> {

    @Override
    public void populate(RegisterData source, DistRegisterData target) throws ConversionException {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setLogin(source.getLogin());
        target.setEmail(source.getLogin());
        target.setPassword(source.getPassword());
        target.setTitleCode(source.getTitleCode());
        target.setCustomerId(source.getCustomerId());
        target.setAdditionalAddressCompany(source.getAdditionalAddress());
        target.setCountryCode(source.getCountryCode());
        target.setCurrencyCode(source.getCurrencyCode());
        target.setPhoneNumber(source.getPhoneNumber());
        target.setMobileNumber(source.getMobileNumber());
        target.setFaxNumber(source.getFaxNumber());
        target.setStreetName(source.getStreetName());
        target.setPostalCode(source.getPostalCode());
        target.setTown(source.getTown());
        target.setNewsletterOption(Boolean.TRUE.equals(source.getMarketingConsent()));
        target.setPhoneMarketingOption(source.getMarketingConsent());
        target.setNpsConsent(source.getMarketingConsent());
        target.setInvoiceEmail(source.getInvoiceEmail());
        target.setVat4(source.getVat4());
        target.setLegalEmail(source.getLegalEmail());
        target.setPhoneConsent(source.getPhoneConsent());
        target.setPostConsent(source.getPostConsent());
        target.setSmsConsent(source.getSmsConsent());
        target.setPersonalisationConsent(source.getPersonalisationConsent());
        target.setProfilingConsent(source.getProfilingConsent());
        target.setPersonalisedRecommendationConsent(source.getPersonalisedRecommendationConsent());
        target.setCustomerSurveysConsent(source.getCustomerSurveysConsent());
        target.setMarketingCookieEnabled(source.getIsMarketingCookieEnabled());
    }
}
