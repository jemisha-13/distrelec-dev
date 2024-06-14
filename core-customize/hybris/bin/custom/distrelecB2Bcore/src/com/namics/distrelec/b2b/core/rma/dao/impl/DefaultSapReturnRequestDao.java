/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.rma.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistRMAReasonModel;
import com.namics.distrelec.b2b.core.model.rma.SapReturnRequestEntryModel;
import com.namics.distrelec.b2b.core.model.rma.SapReturnRequestModel;
import com.namics.distrelec.b2b.core.rma.ReturnRequestData;
import com.namics.distrelec.b2b.core.rma.ReturnRequestEntryData;
import com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;


/**
 * {@code DefaultSapReturnRequestDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 2.0
 */
public class DefaultSapReturnRequestDao extends AbstractItemDao implements SapReturnRequestDao {

    private static final String ORDER_BY_DATE = " ORDER BY {" + SapReturnRequestModel.CREATIONTIME + "} DESC";

    private static final String FIND_RETURN_REQS_BY_USER = "SELECT {" + SapReturnRequestModel.PK + "} FROM {" + SapReturnRequestModel._TYPECODE
            + "} WHERE {" + SapReturnRequestModel.CUSTOMERID + "} = ?customerId";

    private static final String FIND_RETURN_REQS_BY_ORDER = FIND_RETURN_REQS_BY_USER + " AND {" + SapReturnRequestModel.ORDERNUMBER + "} = ?orderNum";

    private static final String FIND_RETURN_REQS_BY_USER_AND_CODE = FIND_RETURN_REQS_BY_USER + " AND {" + SapReturnRequestModel.CODE + "} = ?reqCode";

    private static final String FIND_RETURN_REQS_ALL = "SELECT {" + SapReturnRequestModel.PK + "} FROM {" + SapReturnRequestModel._TYPECODE + "}"
            + ORDER_BY_DATE;

    private static final String FIND_RETURN_REQS_FROM_DATE = "SELECT {" + SapReturnRequestModel.PK + "} FROM {" + SapReturnRequestModel._TYPECODE
            + "} WHERE {" + SapReturnRequestModel.CREATIONTIME + "} >= ?fromDate " + ORDER_BY_DATE;

    @Autowired
    private DistrelecCodelistService distrelecCodelistService;

    private KeyGenerator keyGenerator;
    
    
    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao#createReturnRequest(de.hybris.platform.core.model.user.CustomerModel, com.namics.distrelec.b2b.core.rma.ReturnRequestData)
     */
    @Override
    public String createReturnRequest(CustomerModel customer, ReturnRequestData requestData) {
        validateParameterNotNull(customer, "Customer must not be null");
        validateParameterNotNull(requestData, "Return request data must not be null");
        return createRMAObject(customer, requestData);
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao#createReturnRequests(de.hybris.platform.core.model.user.CustomerModel, java.util.List)
     */
    @Override
    public List<String> createReturnRequests(CustomerModel customer, List<ReturnRequestData> requestDatas) {
        validateParameterNotNull(customer, "Customer must not be null");
        validateParameterNotNull(requestDatas, "Return request data list must not be null");

        final List<String> rmaCodes = new ArrayList<String>();
        for (final ReturnRequestData requestData : requestDatas) {
            rmaCodes.add(createReturnRequest(customer, requestData));
        }

        return rmaCodes;
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao#getReturnRequests(de.hybris.platform.core.model.user.CustomerModel)
     */
    @Override
    public List<Object> getReturnRequests(CustomerModel customer) {
        validateParameterNotNull(customer, "Customer must not be null");
        return getFlexibleSearchService().search(getFlexibleSearchQuery(FIND_RETURN_REQS_BY_USER + ORDER_BY_DATE, customer)).getResult();
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao#getReturnRequestsByOrder(de.hybris.platform.core.model.user.CustomerModel, de.hybris.platform.core.model.order.OrderModel)
     */
    @Override
    public List<Object> getReturnRequestsByOrder(CustomerModel customer, OrderModel order) {
        validateParameterNotNull(customer, "Customer must not be null");
        validateParameterNotNull(order, "Order must not be null");

        final FlexibleSearchQuery searchQuery = getFlexibleSearchQuery(FIND_RETURN_REQS_BY_ORDER + ORDER_BY_DATE, customer);
        searchQuery.addQueryParameter("orderNum", order.getCode());
        return getFlexibleSearchService().search(searchQuery).getResult();
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao#getReturnRequest(de.hybris.platform.core.model.user.CustomerModel, java.lang.String)
     */
    @Override
    public Object getReturnRequest(CustomerModel customer, String rmaCode) {
        validateParameterNotNull(customer, "Customer must not be null");
        validateParameterNotNull(rmaCode, "The RMA code must be non null");

        final FlexibleSearchQuery searchQuery = getFlexibleSearchQuery(FIND_RETURN_REQS_BY_USER_AND_CODE + ORDER_BY_DATE, customer);
        searchQuery.addQueryParameter("reqCode", rmaCode);
        final List<Object> result = getFlexibleSearchService().search(searchQuery).getResult();
        return result.isEmpty() ? null : result.get(0);
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao#findAll()
     */
    @Override
    public List<Object> findAll() {
        return getFlexibleSearchService().search(new FlexibleSearchQuery(FIND_RETURN_REQS_ALL)).getResult();
    }

    /* (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.rma.dao.SapReturnRequestDao#getReturnRequestsFromDate(java.util.Date)
     */
    @Override
    public List<Object> getReturnRequestsFromDate(Date fromDate) {
        if (fromDate == null) {
            return findAll();
        }

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_RETURN_REQS_FROM_DATE);
        searchQuery.addQueryParameter("fromDate", fromDate);

        return getFlexibleSearchService().search(searchQuery).getResult();
    }

    
    /**
     * Create an RMA object for the specified RMA data and store it in the database
     * 
     * @param customer
     *            the owner of the RMA request
     * @param requestData
     *            the data of the RMA request
     * @return the generated code for the newly created RMA Object
     */
    protected String createRMAObject(final CustomerModel customer, final ReturnRequestData requestData) {
        final SapReturnRequestModel sapReturnRequest = getModelService().create(SapReturnRequestModel.class);
        sapReturnRequest.setCode(getKeyGenerator().generate().toString());
        sapReturnRequest.setOrderNumber(requestData.getOrderCode());
        sapReturnRequest.setPurchaseDate(requestData.getPurchaseDate());
        // Set the customer data
        sapReturnRequest.setCustomerId(customer.getCustomerID());
        sapReturnRequest.setEmail(customer.getContactEmail());
        sapReturnRequest.setSalesOrgCode(((B2BCustomerModel) customer).getDefaultB2BUnit().getSalesOrg().getCode());

        // Creating RMA request entries
        if (CollectionUtils.isNotEmpty(requestData.getEntries())) {
            final Set<SapReturnRequestEntryModel> sapReturnRequestEntries = new LinkedHashSet<>();
            for (final ReturnRequestEntryData requestEntryData : requestData.getEntries()) {
                final SapReturnRequestEntryModel sapReturnRequestEntry = getModelService().create(SapReturnRequestEntryModel.class);
                sapReturnRequestEntry.setArticleNumber(requestEntryData.getProductNumber());
                sapReturnRequestEntry.setQuantity(String.valueOf(requestEntryData.getQuantity()));
                sapReturnRequestEntry.setNote(requestEntryData.getNote());
                sapReturnRequestEntry.setSerialNumbers(requestEntryData.getSerialNumbers());
                sapReturnRequestEntry.setPacking(requestEntryData.getPackaging());
                sapReturnRequestEntry.setReplacement(Boolean.valueOf(requestEntryData.isReplacement()));
                // Create the reason
                final DistRMAReasonModel reason = getDistrelecCodelistService().getDistRMAReason(requestEntryData.getReason());
                sapReturnRequestEntry.setReason(reason);
                sapReturnRequestEntries.add(sapReturnRequestEntry);
            }
            sapReturnRequest.setSapReturnRequestEntries(sapReturnRequestEntries);
        }

        AddressModel address = customer.getContactAddress();

        if (address == null) {
            for (final AddressModel addr : customer.getContactAddresses()) {
                if (addr != null) {
                    address = addr;
                    break;
                }
            }
        }

        if (address != null) {
            sapReturnRequest.setFirstName(address.getFirstname());
            sapReturnRequest.setLastName(address.getLastname());
            sapReturnRequest.setPhoneNumber(address.getPhone1() != null ? address.getPhone1() : address.getCellphone() != null ? address.getCellphone()
                    : "No contact phone number available");
            sapReturnRequest.setCompany(address.getCompany());
        }

        // Save the RMA object
        getModelService().save(sapReturnRequest);
        return sapReturnRequest.getCode();
    }

    private FlexibleSearchQuery getFlexibleSearchQuery(final String query, final CustomerModel customer) {
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query);
        flexibleSearchQuery.addQueryParameter("customerId", customer.getCustomerID());
        return flexibleSearchQuery;
    }
    
    public DistrelecCodelistService getDistrelecCodelistService() {
        return distrelecCodelistService;
    }

    public void setDistrelecCodelistService(DistrelecCodelistService distrelecCodelistService) {
        this.distrelecCodelistService = distrelecCodelistService;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }
    
}
