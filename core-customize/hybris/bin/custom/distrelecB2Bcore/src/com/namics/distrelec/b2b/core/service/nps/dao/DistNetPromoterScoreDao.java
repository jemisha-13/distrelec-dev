/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.nps.dao;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;

/**
 * {@code DistNetPromoterScoreDao}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, Datwyler
 * @since Distrelec 3.4, 6.4
 */
public interface DistNetPromoterScoreDao {

    /**
     * Create a new {@code DistNetPromoterScoreModel} from the specified {@code DistNetPromoterScoreData} and persist it to the data-store.
     * 
     * @param npsData
     */
    public void createNPS(final DistNetPromoterScoreData npsData);

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
     * update a {@code DistNetPromoterScoreModel} from the specified {@code DistNetPromoterScoreData} and persist it to the data-store.
     * 
     * @param npsModel
     */
    public void updateNPS(final DistNetPromoterScoreModel npsModel);

    /**
     * Find {@code DistNetPromoterScoreModel} using code
     * 
     * @param code
     *            a unique id DistNetPromoterScoreModel
     * @return {@code DistNetPromoterScoreModel}
     */
    public DistNetPromoterScoreModel getByCode(final String code);
}
