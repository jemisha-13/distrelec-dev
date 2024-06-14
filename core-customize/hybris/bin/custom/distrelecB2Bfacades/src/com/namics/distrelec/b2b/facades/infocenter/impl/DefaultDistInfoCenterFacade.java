/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.infocenter.impl;

import com.namics.distrelec.b2b.core.event.DistAddressChangeEvent;
import com.namics.distrelec.b2b.core.event.DistCatalogOrderEvent;
import com.namics.distrelec.b2b.core.event.DistSeminarRegistrationRequestEvent;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;
import com.namics.distrelec.b2b.core.model.DistSeminarModel;
import com.namics.distrelec.b2b.core.model.DistSeminarRegistrationModel;
import com.namics.distrelec.b2b.core.service.infocenter.seminar.DistInfoCenterService;
import com.namics.distrelec.b2b.facades.infocenter.DistInfoCenterFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class DefaultDistInfoCenterFacade implements DistInfoCenterFacade {

    @Autowired
    private EventService eventService;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private UserService userService;

    @Autowired
    private DistInfoCenterService distInfoCenterService;

    @Autowired
    private Converter<DistSeminarModel, DistSeminarData> seminarConverter;

    @Override
    public void changeAddress(final DistAddressChangeEvent addressChangeEvent) {
        setUpAdditionalEventData(addressChangeEvent);
        getEventService().publishEvent(addressChangeEvent);
    }

    @Override
    public void registerForSeminar(final DistSeminarRegistrationData registrationData) {
        final DistSeminarRegistrationModel registration = getModelService().create(DistSeminarRegistrationModel.class);

        registration.setUid(UUID.randomUUID().toString());
        registration.setTopic(registrationData.getTopic());
        registration.setSeminarDate(registrationData.getDate());
        registration.setCompanyName(registrationData.getCompanyName());
        registration.setCustomerNumber(registrationData.getCustomerNumber());
        registration.setSalutation(registrationData.getSalutation());
        registration.setLastName(registrationData.getLastName());
        registration.setFirstName(registrationData.getFirstName());
        registration.setDepartment(registrationData.getDepartment());
        registration.setStreet(registrationData.getStreet());
        registration.setNumber(registrationData.getNumber());
        registration.setPobox(registrationData.getPobox());
        registration.setZip(registrationData.getZip());
        registration.setPlace(registrationData.getPlace());
        registration.setCountry(registrationData.getCountry());
        registration.setDirectPhone(registrationData.getDirectPhone());
        registration.setEMail(registrationData.geteMail());
        registration.setFax(registrationData.getFax());
        registration.setComment(registrationData.getComment());

        getModelService().save(registration);
    }

    @Override
    public void registerForSeminar(final String seminarUID, final DistSeminarRegistrationData registrationData) {
        getDistInfoCenterService().registerForSeminar(seminarUID, registrationData);
    }

    @Override
    public void registerForSeminar(final DistSeminarRegistrationRequestEvent seminarRegistrationEvent) {
        setUpAdditionalEventData(seminarRegistrationEvent);
        seminarRegistrationEvent.setCustomer((CustomerModel) getUserService().getCurrentUser());
        getEventService().publishEvent(seminarRegistrationEvent);
    }

    @Override
    public void registerForSeminar(final String seminarUID, final DistSeminarRegistrationRequestEvent registrationEvent) {
        registerForSeminar(seminarUID, createRegistrationData(registrationEvent));
        registerForSeminar(registrationEvent);
    }

    @Override
    public void orderCatalog(final DistCatalogOrderEvent catalogOrderEvent) {
        setUpAdditionalEventData(catalogOrderEvent);
        getEventService().publishEvent(catalogOrderEvent);
    }

    @Override
    public DistSeminarData getSeminarForUID(final String uid) {
        return getSeminarConverter().convert(getDistInfoCenterService().getSeminarForUID(uid));
    }

    @Override
    public void createSeminar(final DistSeminarData seminarData) {
        getDistInfoCenterService().createSeminar(seminarData);
    }

    protected void setUpAdditionalEventData(final AbstractCommerceUserEvent<BaseSiteModel> event) {
        event.setBaseStore(getBaseStoreService().getCurrentBaseStore());
        event.setSite(getBaseSiteService().getCurrentBaseSite());
    }

    protected DistSeminarRegistrationData createRegistrationData(final DistSeminarRegistrationRequestEvent event) {
        final DistSeminarRegistrationData seminarRegistration = new DistSeminarRegistrationData();
        BeanUtils.copyProperties(event, seminarRegistration);
        return seminarRegistration;
    }

    /* Getters & Setters */

    public EventService getEventService() {
        return eventService;
    }

    public void setEventService(final EventService eventService) {
        this.eventService = eventService;
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

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public DistInfoCenterService getDistInfoCenterService() {
        return distInfoCenterService;
    }

    public void setDistInfoCenterService(final DistInfoCenterService distInfoCenterService) {
        this.distInfoCenterService = distInfoCenterService;
    }

    public Converter<DistSeminarModel, DistSeminarData> getSeminarConverter() {
        return seminarConverter;
    }

    public void setSeminarConverter(final Converter<DistSeminarModel, DistSeminarData> seminarConverter) {
        this.seminarConverter = seminarConverter;
    }
}
