/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.infocenter.converter;

import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;
import com.namics.distrelec.b2b.core.model.DistSeminarModel;
import com.namics.distrelec.b2b.core.model.DistSeminarRegistrationModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code DistSeminarConverter}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistSeminarConverter extends AbstractPopulatingConverter<DistSeminarModel, DistSeminarData> {

    @Override
    protected DistSeminarData createTarget() {
        return new DistSeminarData();
    }

    @Override
    public void populate(final DistSeminarModel source, final DistSeminarData target) {
        super.populate(source, target);
        target.setUid(source.getUid());
        target.setTopic(source.getTopic());
        target.setDate(source.getDate());
        target.setRegistrationDelay(source.getRegistrationDelay());
        target.setShortDesc(source.getShortDesc());
        target.setContent(source.getContent());
        target.setLocation(source.getLocation());
        target.setSchedule(source.getSchedule());
        target.setSpeakers(source.getSpeakers());
        target.setCostDesc(source.getCostDesc());
        target.setNote(source.getNote());
        target.setPrerequisites(source.getPrerequisites());
        // target.setRegistrations(getRegistrations(source.getRegistrations()));
    }

    /**
     * Converts a list of {@code DistSeminarRegistrationModel} to a list of {@code DistSeminarRegistrationData}
     *
     * @param registrations
     *            the list of {@code DistSeminarRegistrationModel} to convert
     * @return a list of {@code DistSeminarRegistrationData}
     */
    protected List<DistSeminarRegistrationData> getRegistrations(final List<DistSeminarRegistrationModel> registrations) {
        if (registrations != null && !registrations.isEmpty()) {
            final List<DistSeminarRegistrationData> regList = new ArrayList<DistSeminarRegistrationData>();
            for (final DistSeminarRegistrationModel registrationModel : registrations) {
                final DistSeminarRegistrationData seminarRegistration = convertRegistration(registrationModel);
                if (seminarRegistration != null) {
                    regList.add(seminarRegistration);
                }
            }
        }

        return Collections.<DistSeminarRegistrationData> emptyList();
    }

    /**
     * Converts a {@code DistSeminarRegistrationModel} to {@code DistSeminarRegistrationData}
     *
     * @param source
     *            the {@code DistSeminarRegistrationModel} to convert
     * @return a new instance of {@code DistSeminarRegistrationData}
     */
    protected DistSeminarRegistrationData convertRegistration(final DistSeminarRegistrationModel source) {
        if (source != null) {
            final DistSeminarRegistrationData target = new DistSeminarRegistrationData();
            target.setCustomerNumber(source.getCustomerNumber());
            target.setCompanyName(source.getCompanyName());
            target.setSalutation(source.getSalutation());
            target.setFirstName(source.getFirstName());
            target.setLastName(source.getLastName());
            target.setTopic(source.getTopic());
            target.setDate(source.getSeminarDate());
            target.setStreet(source.getStreet());
            target.setNumber(source.getNumber());
            target.setPobox(source.getPobox());
            target.setPlace(source.getPlace());
            target.setCountry(source.getCountry());
            target.setDirectPhone(source.getDirectPhone());
            target.seteMail(source.getEMail());
            target.setFax(source.getFax());

            return target;
        }

        return null;
    }

}
