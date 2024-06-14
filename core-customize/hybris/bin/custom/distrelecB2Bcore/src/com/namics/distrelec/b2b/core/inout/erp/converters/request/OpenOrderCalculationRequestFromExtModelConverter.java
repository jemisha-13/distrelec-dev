/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.converters.request;


import com.distrelec.webservice.if11.v3.ObjectFactory;
import com.distrelec.webservice.if11.v3.OpenOrderCalculationRequest;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class OpenOrderCalculationRequestFromExtModelConverter<SOURCE extends ErpOpenOrderExtModel>
        extends AbstractPopulatingConverter<SOURCE, OpenOrderCalculationRequest> {

    private DistSalesOrgService distSalesOrgService;
    private ObjectFactory sapObjectFactoryIF11;

    @SuppressWarnings("deprecation")
    @Override
    protected OpenOrderCalculationRequest createTarget() {
        return getSapObjectFactoryIF11().createOpenOrderCalculationRequest();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.commerceservices.converter.impl.AbstractConverter#convert(java.lang.Object)
     */
    @Override
    public OpenOrderCalculationRequest convert(final ErpOpenOrderExtModel source) throws ConversionException {
        return convert(source, createTarget());
    }

    @SuppressWarnings("deprecation")
    @Override
    public OpenOrderCalculationRequest convert(final ErpOpenOrderExtModel order, final OpenOrderCalculationRequest request) throws ConversionException {

        request.setSalesOrganization(getSalesOrganization());
        request.setOrderId(order.getErpOrderId());

        request.setContactId(order.getErpContactId());
        request.setShippingAddressId(order.getErpShippingAddressId());

        request.setCustomerReferenceHeaderLevel(order.getOrderReferenceHeaderLevel());

        request.setEditableByAllContacts(order.isEditableByAllContacts());

        // List<OrderEntryRequest> dummyNewEntries = new ArrayList<OrderEntryRequest>();
        // dummyNewEntries.add(null);
        // request.getNewOrderEntries().addAll(dummyNewEntries);

        request.setOrderCloseDate(order.getOrderCloseDate() == null ? null : SoapConversionHelper.convertDate(order.getOrderCloseDate()));
        return request;
    }

    private String getSalesOrganization() throws ConversionException {

        if (getDistSalesOrgService().getCurrentSalesOrg() == null) {
            throw new ConversionException("Sales Org not found");
        }
        return getDistSalesOrgService().getCurrentSalesOrg().getCode();

    }

    // Getters & Setters

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public ObjectFactory getSapObjectFactoryIF11() {
        return sapObjectFactoryIF11;
    }

    public void setSapObjectFactoryIF11(ObjectFactory sapObjectFactoryIF11) {
        this.sapObjectFactoryIF11 = sapObjectFactoryIF11;
    }

}
