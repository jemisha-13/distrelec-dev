/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.nps.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.model.feedback.DistNetPromoterScoreModel;
import com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * {@code DefaultDistNetPromoterScoreDao}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, Datwyler
 * @since Distrelec 3.4
 */
public class DefaultDistNetPromoterScoreDao extends AbstractItemDao implements DistNetPromoterScoreDao {

    private static final String QUERY_PREFIX = "SELECT {" + DistNetPromoterScoreModel.PK + "} FROM {" + DistNetPromoterScoreModel._TYPECODE + "}";
    private static final String FIND_BY_CUSTOMER = QUERY_PREFIX + " WHERE {" + DistNetPromoterScoreModel.ERPCUSTOMERID + "}=?"
            + DistNetPromoterScoreModel.ERPCUSTOMERID;
    private static final String FIND_BY_EXPORTED = QUERY_PREFIX + " WHERE {" + DistNetPromoterScoreModel.EXPORTED + "}=?" + DistNetPromoterScoreModel.EXPORTED;

    private static final String FIND_BY_TYPE = QUERY_PREFIX + " WHERE {" + DistNetPromoterScoreModel.TYPE + "}=?" + DistNetPromoterScoreModel.TYPE;

    private static final String FIND_BY_EMAIL = QUERY_PREFIX + " WHERE {" + DistNetPromoterScoreModel.EMAIL + "}=?" + DistNetPromoterScoreModel.EMAIL;
    private static final String FIND_BY_EMAIL_FROM_DATE = FIND_BY_EMAIL + " AND {" + DistNetPromoterScoreModel.CREATIONTIME + "} >=?"
            + DistNetPromoterScoreModel.CREATIONTIME + " ORDER BY {" + DistNetPromoterScoreModel.CREATIONTIME + "} DESC";
    private static final String FIND_BY_CODE_FROM_DATE = QUERY_PREFIX + " WHERE {" + DistNetPromoterScoreModel.CODE + "} =?" + DistNetPromoterScoreModel.CODE;

    private KeyGenerator keyGenerator;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao#createNPS(com.namics.distrelec.b2b.core.service.nps.data.
     * DistNetPromoterScoreData)
     */
    @Override
    public void createNPS(final DistNetPromoterScoreData npsData) {
        validateParameterNotNull(npsData, "NPS data must not be null");
        validateParameterNotNull(npsData.getEmail(), "Customer email must not be null");
        final DistNetPromoterScoreModel npsModel = getModelService().create(DistNetPromoterScoreModel.class);
        npsModel.setCode(keyGenerator.generate().toString());
        npsModel.setErpContactID(npsData.getErpContactID());
        npsModel.setErpCustomerID(npsData.getErpCustomerID());
        npsModel.setFirstname(npsData.getFirstname());
        npsModel.setLastname(npsData.getLastname());
        npsModel.setEmail(npsData.getEmail().trim().toLowerCase());
        npsModel.setDeliveryDate(npsData.getDeliveryDate());
        npsModel.setCompanyName(npsData.getCompanyName());
        npsModel.setOrderNumber(npsData.getOrderNumber());
        npsModel.setSalesOrg(npsData.getSalesOrg());
        npsModel.setDomain(npsData.getDomain());
        npsModel.setType(npsData.getType());
        npsModel.setText(npsData.getText());
        npsModel.setReason(npsData.getReason());
        npsModel.setSubReason(npsData.getSubreason());
        npsModel.setValue(Integer.valueOf(npsData.getValue()));

        getModelService().save(npsModel);
        // After Saving, we set the stored code back to the data object
        npsData.setCode(npsModel.getCode());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao#updateNPS(com.namics.distrelec.b2b.core.model.feedback.
     * DistNetPromoterScoreModel)
     */
    @Override
    public void updateNPS(final DistNetPromoterScoreModel npsModel) {
        validateParameterNotNull(npsModel, "NPS data must not be null");
        validateParameterNotNull(npsModel.getEmail(), "Customer email must not be null");
        getModelService().save(npsModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao#findAll()
     */
    @Override
    public List<DistNetPromoterScoreModel> findAll() {
        return find(QUERY_PREFIX, null, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao#findByType(com.namics.distrelec.b2b.core.enums.NPSType)
     */
    @Override
    public List<DistNetPromoterScoreModel> findByType(final NPSType type) {
        validateParameterNotNull(type, "NPS-Type must not be null");
        return find(FIND_BY_TYPE, DistNetPromoterScoreModel.TYPE, type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao#findExported(boolean)
     */
    @Override
    public List<DistNetPromoterScoreModel> findExported(final boolean exported) {
        return find(FIND_BY_EXPORTED, DistNetPromoterScoreModel.EXPORTED, Boolean.valueOf(exported));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao#findByCustomer(java.lang.String)
     */
    @Override
    public List<DistNetPromoterScoreModel> findByCustomer(final String erpCustomerID) {
        validateParameterNotNull(erpCustomerID, "Customer number must not be null");
        return find(FIND_BY_CUSTOMER, DistNetPromoterScoreModel.ERPCUSTOMERID, erpCustomerID);
    }

    /**
     * Find the {@code DistNetPromoterScoreModel}s using the search query and its parameter.
     * 
     * @param query
     *            the search query
     * @param param
     *            the parameter name
     * @param value
     *            parameter value
     * @return a list of {@code DistNetPromoterScoreModel}
     */
    private List<DistNetPromoterScoreModel> find(final String query, final String param, final Object value) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        if (param != null && value != null) {
            searchQuery.addQueryParameter(param, value);
        }
        return getFlexibleSearchService().<DistNetPromoterScoreModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao#findByEmailSinceDate(java.lang.String, java.util.Date)
     */
    @Override
    public List<DistNetPromoterScoreModel> findByEmailSinceDate(final String email, final Date fromDate) {
        validateParameterNotNull(email, "Customer email must not be null");
        validateParameterNotNull(fromDate, "From date must not be null");
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_BY_EMAIL_FROM_DATE);
        searchQuery.addQueryParameter(DistNetPromoterScoreModel.EMAIL, email.trim().toLowerCase());
        searchQuery.addQueryParameter(DistNetPromoterScoreModel.CREATIONTIME, fromDate);
        return getFlexibleSearchService().<DistNetPromoterScoreModel> search(searchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.nps.dao.DistNetPromoterScoreDao#getByCode(java.lang.String)
     */
    @Override
    public DistNetPromoterScoreModel getByCode(final String code) {
        validateParameterNotNull(code, "Code must not be null");
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_BY_CODE_FROM_DATE);
        searchQuery.addQueryParameter(DistNetPromoterScoreModel.CODE, code);
        return getFlexibleSearchService().<DistNetPromoterScoreModel> searchUnique(searchQuery);
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(final KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
}
