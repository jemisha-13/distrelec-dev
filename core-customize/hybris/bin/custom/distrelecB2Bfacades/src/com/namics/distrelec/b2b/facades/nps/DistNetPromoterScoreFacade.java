/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.nps;

import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;

import java.util.Date;
import java.util.List;

/**
 * {@code DistNetPromoterScoreFacade}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public interface DistNetPromoterScoreFacade {

    public void createNPS(final DistNetPromoterScoreData npsData);

    public void updateNPS(final DistNetPromoterScoreData npsData);

    public List<DistNetPromoterScoreData> findAll();

    public List<DistNetPromoterScoreData> findByType(final NPSType type);

    public List<DistNetPromoterScoreData> findExported(final boolean exported);

    public List<DistNetPromoterScoreData> findByCustomer(final String erpCustomerID);

    public List<DistNetPromoterScoreData> findByEmailAfterDate(final String email, final Date date);

    public Date getLastSubmittedNPSDate(final String email);

    public DistNetPromoterScoreData getNPSByCode(final String code);

    public boolean isAllowedToSubmitNPS(final String email);
}
