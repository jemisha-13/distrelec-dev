/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.infocenter.seminar.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData;
import com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData;
import com.namics.distrelec.b2b.core.model.DistSeminarModel;
import com.namics.distrelec.b2b.core.service.infocenter.seminar.DistInfoCenterService;
import com.namics.distrelec.b2b.core.service.infocenter.seminar.dao.DistSeminarDao;


/**
 * {@code DefaultDistInfoCenterService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DefaultDistInfoCenterService implements DistInfoCenterService {

    @Autowired
    private DistSeminarDao distSeminarDao;

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.infocenter.seminar.DistInfoCenterService#getSeminarForUID(java.lang.String)
     */
    @Override
    public DistSeminarModel getSeminarForUID(final String uid) {
        return distSeminarDao.getSeminarForUID(uid);
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.infocenter.seminar.DistInfoCenterService#removeSeminar(java.lang.String)
     */
    @Override
    public void removeSeminar(final String uid) {
        distSeminarDao.removeSeminar(uid);
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.infocenter.seminar.DistInfoCenterService#createSeminar(com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarData)
     */
    @Override
    public void createSeminar(final DistSeminarData seminarData) {
        distSeminarDao.createSeminar(seminarData);
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.infocenter.seminar.DistInfoCenterService#registerForSeminar(java.lang.String, com.namics.distrelec.b2b.core.infocenter.seminar.registration.data.DistSeminarRegistrationData)
     */
    @Override
    public void registerForSeminar(final String seminarUID, final DistSeminarRegistrationData seminarRegistrationData) {
        distSeminarDao.registerForSeminar(seminarUID, seminarRegistrationData);
    }

    /* Getters & Setters */

    public DistSeminarDao getDistSeminarDao() {
        return distSeminarDao;
    }

    public void setDistSeminarDao(final DistSeminarDao distSeminarDao) {
        this.distSeminarDao = distSeminarDao;
    }

}
