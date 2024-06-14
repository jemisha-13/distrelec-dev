/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.infocenter.seminar.dao;

import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;
import com.namics.distrelec.b2b.core.model.DistSeminarModel;

/**
 * {@code DistSeminarDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public interface DistSeminarDao {

    public DistSeminarModel getSeminarForUID(final String uid);

    public void removeSeminar(final String uid);

    public void createSeminar(final DistSeminarData seminarData);

    public void registerForSeminar(final String seminarUID, final DistSeminarRegistrationData seminarRegistrationData);

}
