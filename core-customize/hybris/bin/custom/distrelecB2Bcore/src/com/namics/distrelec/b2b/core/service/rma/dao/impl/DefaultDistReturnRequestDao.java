/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.rma.dao.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.rma.ReturnRequestData;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.rma.dao.DistReturnRequestDao;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * {@code DefaultDistReturnRequestDao}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Namics Extensions 1.0
 */
public class DefaultDistReturnRequestDao implements DistReturnRequestDao {

    private static final String FIND_RETURN_REQS_BY_USER = "SELECT {" + ReturnRequestModel.PK + "} FROM {" + ReturnRequestModel._TYPECODE + "} WHERE {"
            + ReturnRequestModel.ORDER + "} = ?customerId";

    private static final String FIND_RETURN_REQS_BY_USER_AND_CODE = FIND_RETURN_REQS_BY_USER + " AND {" + ReturnRequestModel.CODE + "} = ?reqCode";

    private static final String ORDER_BY_DATE = " ORDER BY {" + ReturnRequestModel.CREATIONTIME + "}";

    @Autowired
    private ModelService modelService;
    @Autowired
    private DistrelecCodelistService codelistService;
    @Autowired
    private FlexibleSearchService flexibleSearchService;

    private KeyGenerator keyGenerator;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.rma.dao.DistReturnRequestDao#createReturnRequest(de.hybris.platform.core.model.user.CustomerModel
     * , com.namics.distrelec.b2b.core.rma.ReturnRequestData)
     */
    @Override
    public String createReturnRequest(final CustomerModel customer, final ReturnRequestData requestData) {
        validateParameterNotNull(customer, "Customer must not be null");
        validateParameterNotNull(requestData, "ReturnRequestData must not be null");

        throw new UnsupportedOperationException("Method not yet implemented");

        // return createRMAObject(customer, requestData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.rma.dao.DistReturnRequestDao#createReturnRequests(de.hybris.platform.core.model.user.CustomerModel
     * , java.util.List)
     */
    @Override
    public List<String> createReturnRequests(final CustomerModel customer, final List<ReturnRequestData> requestDatas) {
        validateParameterNotNull(customer, "Customer must not be null");
        validateParameterNotNull(requestDatas, "Return request data list must not be null");

        // TODO complete implementation
        throw new UnsupportedOperationException("Method not yet implemented");

        // final List<String> rmaCodes = new ArrayList<String>();

        // for (final ReturnRequestData requestData : requestDatas) {
        // rmaCodes.add(createReturnRequest(customer, requestData));
        // }

        // return rmaCodes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.rma.dao.DistReturnRequestDao#getReturnRequests(de.hybris.platform.core.model.user.CustomerModel
     * )
     */
    @Override
    public List<Object> getReturnRequests(final CustomerModel customer) {
        validateParameterNotNull(customer, "Customer must not be null");

        // TODO complete implementation
        throw new UnsupportedOperationException("Method not yet implemented");

        // return flexibleSearchService.search(getFlexibleSearchQuery(FIND_RETURN_REQS_BY_USER + ORDER_BY_DATE, customer)).getResult();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.rma.dao.DistReturnRequestDao#getReturnRequestsByOrder(de.hybris.platform.core.model.user.
     * CustomerModel, de.hybris.platform.core.model.order.OrderModel)
     */
    @Override
    public List<Object> getReturnRequestsByOrder(final CustomerModel customer, final OrderModel order) {
        validateParameterNotNull(customer, "Customer must not be null");
        validateParameterNotNull(order, "Order must not be null");

        // TODO complete implementation
        throw new UnsupportedOperationException("Method not yet implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.rma.dao.DistReturnRequestDao#getReturnRequest(de.hybris.platform.core.model.user.CustomerModel,
     * java.lang.String)
     */
    @Override
    public Object getReturnRequest(final CustomerModel customer, final String rmaCode) {
        validateParameterNotNull(customer, "Customer must not be null");
        validateParameterNotNull(rmaCode, "The RMA code must be non null");

        // TODO complete implementation
        throw new UnsupportedOperationException("Method not yet implemented");

        // final Map<String, Object> queryParams = new HashMap<String, Object>();
        // queryParams.put("customerId", customer.getCustomerID());
        // queryParams.put("reqCode", rmaCode);
        // final List<Object> result = flexibleSearchService.search(new FlexibleSearchQuery(FIND_RETURN_REQS_BY_USER_AND_CODE +
        // ORDER_BY_DATE, queryParams)).getResult();

        // return result.isEmpty() ? null : result.get(0);
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
        final ReturnRequestModel returnRequest = modelService.create(ReturnRequestModel.class);
        returnRequest.setCode(getKeyGenerator().generate().toString());

        // TODO complete implementation

        // returnRequest.setArticleNumber(requestData.getProductNumber());
        // returnRequest.setQuantity(String.valueOf(requestData.getQuantity()));
        // returnRequest.setNote(requestData.getNote());
        // returnRequest.setOrderNumber(requestData.getOrderCode());
        // returnRequest.setSerialNumbers(requestData.getSerialNumbers());
        // returnRequest.setPurchaseDate(requestData.getPurchaseDate());
        // returnRequest.setPacking(requestData.getPacking());
        // // Set the customer data
        // returnRequest.setCustomerId(customer.getCustomerID());
        // returnRequest.setEmail(customer.getContactEmail());
        //
        // AddressModel address = customer.getContactAddress();
        //
        // if (address == null) {
        // for (final AddressModel addr : customer.getContactAddresses()) {
        // if (addr != null) {
        // address = addr;
        // break;
        // }
        // }
        // }
        //
        // if (address != null) {
        // returnRequest.setFirstName(address.getFirstname());
        // returnRequest.setLastName(address.getLastname());
        // returnRequest.setPhoneNumber(address.getPhone1());
        // returnRequest.setCompany(address.getCompany());
        // }
        // // Create the reason
        // final DistRMAReasonModel reason = codelistService.getDistRMAReason(requestData.getReason());
        // returnRequest.setReason(reason);

        // Save the RMA object
        modelService.save(returnRequest);

        return returnRequest.getCode();
    }

    protected FlexibleSearchQuery getFlexibleSearchQuery(final String query, final CustomerModel customer) {
        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query);
        flexibleSearchQuery.addQueryParameter("customerId", customer.getCustomerID());

        return flexibleSearchQuery;
    }

    public KeyGenerator getKeyGenerator() {
        return keyGenerator;
    }

    public void setKeyGenerator(final KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistrelecCodelistService getCodelistService() {
        return codelistService;
    }

    public void setCodelistService(final DistrelecCodelistService codelistService) {
        this.codelistService = codelistService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
