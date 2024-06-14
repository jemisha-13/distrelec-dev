/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.converters.request;

import java.util.List;

import com.distrelec.webservice.if11.v3.ObjectFactory;
import com.distrelec.webservice.if11.v3.OpenOrderCalculationRequest;
import com.distrelec.webservice.if11.v3.OrderEntryRequest;
import com.distrelec.webservice.if11.v3.ShippingMethodCode;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapConversionHelper;
import com.namics.distrelec.b2b.core.model.DistPaymentMethodModel;
import com.namics.distrelec.b2b.core.model.DistShippingMethodModel;
import com.namics.distrelec.b2b.core.model.delivery.DistDeliveryModeModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * {@code OpenOrderCalculationRequestConverter}
 * 
 * @param <SOURCE>
 *
 * @author fbersani, Distrelec
 * @since Distrelec 2.0
 */
/**
 * @author dattembhurs
 *
 * @param <SOURCE>
 */
public class OpenOrderCalculationRequestConverter<SOURCE extends AbstractOrderModel> extends AbstractPopulatingConverter<SOURCE, OpenOrderCalculationRequest> {

    private DistSalesOrgService distSalesOrgService;

    private ObjectFactory sapObjectFactoryIF11;

    @SuppressWarnings("deprecation")
    @Override
    protected OpenOrderCalculationRequest createTarget() {
        return sapObjectFactoryIF11.createOpenOrderCalculationRequest();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.converters.impl.AbstractPopulatingConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final SOURCE cart, final OpenOrderCalculationRequest request) {
        request.setSalesOrganization(getSalesOrganization(cart));
        request.setOrderId(cart.getErpOpenOrderCode());

        final UserModel user = cart.getUser();

        final B2BCustomerModel contact = (B2BCustomerModel) user;
        request.setContactId(contact.getErpContactID());
        // request.setShippingAddressId(cart.getDeliveryAddress() == null ? null : cart.getDeliveryAddress().getErpAddressID());

        // build the entries
        buildOrderEntriesRequest(cart, request);

        request.setPickupLocation(cart.getPickupLocation() == null ? null : cart.getPickupLocation().getCode());
        // not needed in Elfa request.setFreeShippingPromotion(false);

        request.setFixedDiscountPromotion(null);
        request.setCostCenterId(cart.getCostCenter() == null ? null : cart.getCostCenter());
        request.setCustomerReferenceHeaderLevel(cart.getProjectNumber());

        // not needed in Elfa
        request.setFreeShippingPromotion(false);

        request.setPickupLocation(cart.getPickupLocation() == null ? null : cart.getPickupLocation().getCode());

        request.setOrderCloseDate(cart.getOrderCloseDate() == null ? null : SoapConversionHelper.convertDate(cart.getOrderCloseDate()));

        request.setEditableByAllContacts(cart.getEditableByAllContacts());

        super.populate(cart, request);
    }

    private String getSalesOrganization(AbstractOrderModel cart) throws ConversionException {
        final CMSSiteModel site = (CMSSiteModel) cart.getSite();
        String salesOrg = null;
        if (site == null || site.getSalesOrg() == null) {
            if (getDistSalesOrgService().getCurrentSalesOrg() == null) {
                throw new ConversionException("Sales Org not found for cart: " + cart.getCode());
            }
            salesOrg = getDistSalesOrgService().getCurrentSalesOrg().getCode();
        } else {
            salesOrg = site.getSalesOrg().getCode();
        }

        return salesOrg;
    }

    private void buildOrderEntriesRequest(final AbstractOrderModel cart, final OpenOrderCalculationRequest openOrderCalculationRequest) {

        final List<AbstractOrderEntryModel> newOrderEntries = cart.getNewOrderEntries();
        for (AbstractOrderEntryModel orderEntry : newOrderEntries) {
            final OrderEntryRequest requestOrderEntry = new OrderEntryRequest();
            // TODO: check mapping
            requestOrderEntry.setMaterialNumber(orderEntry.getProduct().getCode());
            requestOrderEntry.setQuantity(orderEntry.getQuantity().longValue());
            requestOrderEntry.setFreeGiftPromotion(orderEntry.getGiveAway() == null ? false : orderEntry.getGiveAway().booleanValue());
            // TODO: further attributes: customerReferenceItemLevel, quotationId
            requestOrderEntry.setCustomerReferenceItemLevel(orderEntry.getCustomerReference());

            openOrderCalculationRequest.getNewOrderEntries().add(requestOrderEntry);
        }
    }

    protected String getPaymentMethodCode(AbstractOrderModel cart) {

        final DistPaymentModeModel paymentMode = (DistPaymentModeModel) cart.getPaymentMode();
        DistPaymentMethodModel erpPaymentMethod = paymentMode == null ? null : paymentMode.getErpPaymentMethod();
        return erpPaymentMethod == null ? null : erpPaymentMethod.getCode();

    }

    protected ShippingMethodCode getShippingMethodCode(AbstractOrderModel cart) {

        final DistDeliveryModeModel deliveryMode = (DistDeliveryModeModel) cart.getDeliveryMode();
        final DistShippingMethodModel erpDeliveryMethod = deliveryMode == null ? null : deliveryMode.getErpDeliveryMethod();
        return erpDeliveryMethod == null ? null : ShippingMethodCode.fromValue(erpDeliveryMethod.getCode());

    }

    // Getters and Setters

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
