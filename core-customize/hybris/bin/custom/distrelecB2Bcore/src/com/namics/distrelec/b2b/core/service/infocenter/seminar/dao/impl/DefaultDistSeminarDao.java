/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;
import com.namics.distrelec.b2b.core.model.DistSeminarModel;
import com.namics.distrelec.b2b.core.model.DistSeminarRegistrationModel;
import com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * {@code DefaultDistSeminarDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DefaultDistSeminarDao implements DistSeminarDao {

    private static final String SEMINAR_QUERY = "select {pk} from {" + DistSeminarModel._TYPECODE + "} where {" + DistSeminarModel.UID + "}=?suid";

    @Autowired
    private ModelService modelService;
    @Autowired
    private FlexibleSearchService flexibleSearchService;

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao#getSeminarForUID(java.lang.String)
     */
    @Override
    public DistSeminarModel getSeminarForUID(final String uid) {
        validateParameterNotNull(uid, "Seminar UID must not be null!");
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(SEMINAR_QUERY);
        searchQuery.addQueryParameter("suid", uid);

        return getFlexibleSearchService().searchUnique(searchQuery);
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao#removeSeminar(java.lang.String)
     */
    @Override
    public void removeSeminar(final String uid) {
        final DistSeminarModel seminar = getSeminarForUID(uid);
        modelService.remove(seminar);
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao#createSeminar(com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData)
     */
    @Override
    public void createSeminar(final DistSeminarData source) {
        validateParameterNotNull(source, "Seminar Data must not be null!");

        final DistSeminarModel seminar = modelService.create(DistSeminarModel.class);
        seminar.setUid(source.getUid() == null ? UUID.randomUUID().toString() : source.getUid());
        seminar.setTopic(source.getTopic());
        seminar.setDate(source.getDate());
        seminar.setRegistrationDelay(source.getRegistrationDelay());
        seminar.setShortDesc(source.getShortDesc());
        seminar.setContent(source.getContent());
        seminar.setCostDesc(source.getCostDesc());
        seminar.setPrerequisites(source.getPrerequisites());
        seminar.setNote(source.getNote());
        seminar.setLocation(source.getLocation());
        seminar.setSchedule(source.getSchedule());
        seminar.setSpeakers(source.getSpeakers());

        modelService.save(seminar);
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao#registerForSeminar(java.lang.String, com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData)
     */
    @Override
    public void registerForSeminar(final String seminarUID, final DistSeminarRegistrationData seminarRegistrationData) {
        validateParameterNotNull(seminarUID, "Seminar UID must not be null!");
        validateParameterNotNull(seminarRegistrationData, "Seminar Registration Data must not be null!");

        final DistSeminarModel seminar = getSeminarForUID(seminarUID);
        final DistSeminarRegistrationModel registration = modelService.create(DistSeminarRegistrationModel.class);
        registration.setSeminar(seminar);
        registration.setUid(UUID.randomUUID().toString());
        // Start of fields to remove
        registration.setTopic(seminarRegistrationData.getTopic());
        registration.setSeminarDate(seminarRegistrationData.getDate());
        // End of fields to remove
        registration.setCompanyName(seminarRegistrationData.getCompanyName());
        registration.setCustomerNumber(seminarRegistrationData.getCustomerNumber());
        registration.setSalutation(seminarRegistrationData.getSalutation());
        registration.setLastName(seminarRegistrationData.getLastName());
        registration.setFirstName(seminarRegistrationData.getFirstName());
        registration.setDepartment(seminarRegistrationData.getDepartment());
        registration.setStreet(seminarRegistrationData.getStreet());
        registration.setNumber(seminarRegistrationData.getNumber());
        registration.setPobox(seminarRegistrationData.getPobox());
        registration.setZip(seminarRegistrationData.getZip());
        registration.setPlace(seminarRegistrationData.getPlace());
        registration.setCountry(seminarRegistrationData.getCountry());
        registration.setDirectPhone(seminarRegistrationData.getDirectPhone());
        registration.setEMail(seminarRegistrationData.geteMail());
        registration.setFax(seminarRegistrationData.getFax());
        registration.setComment(seminarRegistrationData.getComment());

        modelService.save(registration);
    }

    /* Getters & Setters */

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
