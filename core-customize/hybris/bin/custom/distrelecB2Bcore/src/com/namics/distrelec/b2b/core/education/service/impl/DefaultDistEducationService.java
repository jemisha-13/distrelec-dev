/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.education.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.education.data.DistEducationRegistrationData;
import com.namics.distrelec.b2b.core.education.service.DistEducationService;
import com.namics.distrelec.b2b.core.model.DistEducationRegistrationModel;

import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DefaultDistEducationService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DefaultDistEducationService implements DistEducationService {

    @Autowired
    private ModelService modelService;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.education.service.DistEducationService#register(com.namics.distrelec.b2b.core.education.data.
     * DistEducationRegistrationData)
     */
    @Override
    public void register(final DistEducationRegistrationData educationRegistrationData) {
        final DistEducationRegistrationModel educationRegistration = modelService.create(DistEducationRegistrationModel.class);
        educationRegistration.setUid(UUID.randomUUID().toString());
        populate(educationRegistrationData, educationRegistration);
        modelService.save(educationRegistration);
    }

    /**
     * Populate the {@code DistEducationRegistrationModel} from the {@code DistEducationRegistrationData}
     * 
     * @param source
     *            the {@code DistEducationRegistrationData} source data
     * @param target
     *            the {@code DistEducationRegistrationModel} target data
     */
    private void populate(final DistEducationRegistrationData source, final DistEducationRegistrationModel target) {
        // Contact data
        target.setEducationArea(source.getProfileArea());
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
    }
}
