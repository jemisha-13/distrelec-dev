/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.nps;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;

/**
 * {@code DistNetPromoterScoreService}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, Datwyler
 * @since Distrelec 3.4,6.4
 */
public interface DistNetPromoterScoreService {

    /**
     * Create a new {@code DistNetPromoterScoreModel} from the specified {@code DistNetPromoterScoreData} and persist it to the data-store.
     * 
     * @param npsData
     */
    public void createNPS(final DistNetPromoterScoreData npsData);

    /**
     * Update an existing {@link DistNetPromoterScoreModel} from the specified {@code DistNetPromoterScoreData} and persist it to the
     * data-store.
     * 
     * @param npsData
     */
    public void updateNPS(final DistNetPromoterScoreData npsData);

    /**
     * Find the {@link DistNetPromoterScoreModel} having the specified code.
     * 
     * @param code
     *            the NPS code
     * @return a {@link DistNetPromoterScoreModel} using code
     */
    public DistNetPromoterScoreModel getNPSByCode(final String code);

    /**
     * Fetch all {@code DistNetPromoterScoreModel} from the data-store.
     * 
     * @return a list of {@code DistNetPromoterScoreModel}
     */
    public List<DistNetPromoterScoreModel> findAll();

    /**
     * Fetch all {@code DistNetPromoterScoreModel} from the data-store which have the specified type.
     * 
     * @param type
     *            the type of {@code DistNetPromoterScoreModel}
     * @return a list of {@code DistNetPromoterScoreModel}
     */
    public List<DistNetPromoterScoreModel> findByType(final NPSType type);

    /**
     * Fetch all {@code DistNetPromoterScoreModel} from the data-store which have the specified exported flag.
     * 
     * @param exported
     * @return a list of {@code DistNetPromoterScoreModel}
     */
    public List<DistNetPromoterScoreModel> findExported(final boolean exported);

    /**
     * Fetch all {@code DistNetPromoterScoreModel} from the data-store which have the specified {@code erpCustomerID}.
     * 
     * @param erpCustomerID
     * @return a list of {@code DistNetPromoterScoreModel}
     */
    public List<DistNetPromoterScoreModel> findByCustomer(final String erpCustomerID);

    /**
     * Find all {@code DistNetPromoterScoreModel} from the data-store which have the specified {@code email} and create after
     * {@code fromDate}
     * 
     * @param email
     *            the customer email address
     * @param fromDate
     *            the starting date
     * @return a list of {@code DistNetPromoterScoreModel}
     */
    public List<DistNetPromoterScoreModel> findByEmailSinceDate(final String email, final Date fromDate);

    /**
     * Returns the last submission date after the specified date.
     * 
     * @param email
     * @param fromDate
     * @return the date of the last submission date after the {@code fromDate}. {@code null} if no submission was done after that date.
     */
    public Date getLastSubmittedNPSDate(final String email, final Date fromDate);
}
