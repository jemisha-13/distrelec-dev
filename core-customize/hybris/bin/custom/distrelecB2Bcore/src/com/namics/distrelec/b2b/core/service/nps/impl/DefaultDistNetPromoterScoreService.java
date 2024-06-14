/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.nps.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService;
import com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;

/**
 * {@code DefaultDistNetPromoterScoreService}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DefaultDistNetPromoterScoreService implements DistNetPromoterScoreService {

    @Autowired
    private DistNetPromoterScoreDao distNetPromoterScoreDao;

    /**
     * Create a new instance of {@code DefaultDistNetPromoterScoreService}
     */
    public DefaultDistNetPromoterScoreService() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#createNPS(com.namics.distrelec.b2b.core.service.nps.data.
     * DistNetPromoterScoreData)
     */
    @Override
    public void createNPS(final DistNetPromoterScoreData npsData) {
        getDistNetPromoterScoreDao().createNPS(npsData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#updateNPS(com.namics.distrelec.b2b.core.service.nps.data.
     * DistNetPromoterScoreData)
     */
    @Override
    public void updateNPS(final DistNetPromoterScoreData npsData) {
        final DistNetPromoterScoreModel npsModel = getNPSByCode(npsData.getCode());
        npsModel.setText(npsData.getText());
        npsModel.setReason(npsData.getReason());
        npsModel.setSubReason(npsData.getSubreason());
        npsModel.setValue(Integer.valueOf(npsData.getValue()));
        getDistNetPromoterScoreDao().updateNPS(npsModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#getNPSByCode(String)
     */
    @Override
    public DistNetPromoterScoreModel getNPSByCode(final String code) {
        return getDistNetPromoterScoreDao().getByCode(code);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#findAll()
     */
    @Override
    public List<DistNetPromoterScoreModel> findAll() {
        return getDistNetPromoterScoreDao().findAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#findByType(com.namics.distrelec.b2b.core.enums.NPSType)
     */
    @Override
    public List<DistNetPromoterScoreModel> findByType(final NPSType type) {
        return getDistNetPromoterScoreDao().findByType(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#findExported(boolean)
     */
    @Override
    public List<DistNetPromoterScoreModel> findExported(final boolean exported) {
        return getDistNetPromoterScoreDao().findExported(exported);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#findByCustomer(java.lang.String)
     */
    @Override
    public List<DistNetPromoterScoreModel> findByCustomer(final String erpCustomerID) {
        return getDistNetPromoterScoreDao().findByCustomer(erpCustomerID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#findByEmailSinceDate(java.lang.String, java.util.Date)
     */
    @Override
    public List<DistNetPromoterScoreModel> findByEmailSinceDate(final String email, final Date fromDate) {
        return getDistNetPromoterScoreDao().findByEmailSinceDate(email, fromDate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.DistNetPromoterScoreService#getLastSubmittedNPSDate(java.lang.String, java.util.Date)
     */
    @Override
    public Date getLastSubmittedNPSDate(final String email, final Date fromDate) {
        final List<DistNetPromoterScoreModel> list = findByEmailSinceDate(email, fromDate);
        return list.isEmpty() ? null : list.get(0).getCreationtime();
    }

    public DistNetPromoterScoreDao getDistNetPromoterScoreDao() {
        return distNetPromoterScoreDao;
    }

    public void setDistNetPromoterScoreDao(final DistNetPromoterScoreDao distNetPromoterScoreDao) {
        this.distNetPromoterScoreDao = distNetPromoterScoreDao;
    }
}
