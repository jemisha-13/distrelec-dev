/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.education.impl;

import com.namics.distrelec.b2b.core.education.data.DistEducationRegistrationData;
import com.namics.distrelec.b2b.core.education.service.DistEducationService;
import com.namics.distrelec.b2b.core.event.AbstractDistrelecCustomerEvent;
import com.namics.distrelec.b2b.core.event.DistEducationRegistrationRequestEvent;
import com.namics.distrelec.b2b.facades.education.DistEducationFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.List;

/**
 * {@code DefaultDistEducationFacade}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 2.0
 */
public class DefaultDistEducationFacade implements DistEducationFacade {

    @Autowired
    private EventService eventService;

    @Autowired
    private DistEducationService educationService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Override
    public void register(final DistEducationRegistrationData educationRegistrationData) {
        getEducationService().register(educationRegistrationData);
        final DistEducationRegistrationRequestEvent event = createRegistrationEvent(educationRegistrationData);
        setUpAdditionalEventData(event);
        getEventService().publishEvent(event);
    }

    @Override
    public List<DistEducationRegistrationData> getAll() {
        return Collections.<DistEducationRegistrationData> emptyList();
    }

    protected void setUpAdditionalEventData(final AbstractDistrelecCustomerEvent<BaseSiteModel> event) {
        event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
        event.setSite(getBaseSiteService().getCurrentBaseSite());
        event.setCustomer((CustomerModel) getUserService().getCurrentUser());
        event.setLanguage(getCommonI18NService().getCurrentLanguage());
    }

    protected DistEducationRegistrationRequestEvent createRegistrationEvent(final DistEducationRegistrationData source) {
        final DistEducationRegistrationRequestEvent target = new DistEducationRegistrationRequestEvent();
        target.setProfileArea(source.getProfileArea());
        target.setContactFirstName(source.getContactFirstName());
        target.setContactLastName(source.getContactLastName());
        target.setContactAddress1(source.getContactAddress1());
        target.setContactAddress2(source.getContactAddress2());
        target.setContactZip(source.getContactZip());
        target.setContactPlace(source.getContactPlace());
        target.setContactCountry(source.getContactCountry());
        target.setContactEmail(source.getContactEmail());
        target.setContactPhoneNumber(source.getContactPhoneNumber());
        target.setContactMobileNumber(source.getContactMobileNumber());

        // Institution data
        target.setInstitutionName(source.getInstitutionName());
        target.setInstitutionAddress1(source.getInstitutionAddress1());
        target.setInstitutionAddress2(source.getInstitutionAddress2());
        target.setInstitutionZip(source.getInstitutionZip());
        target.setInstitutionPlace(source.getInstitutionPlace());
        target.setInstitutionCountry(source.getInstitutionCountry());
        target.setInstitutionPhoneNumber(source.getInstitutionPhoneNumber());
        target.setInstitutionEmail(source.getInstitutionEmail());

        return target;
    }

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
    }

    public DistEducationService getEducationService() {
        return educationService;
    }

    public void setEducationService(final DistEducationService educationService) {
        this.educationService = educationService;
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
