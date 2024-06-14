/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.impl;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;

import com.namics.distrelec.b2b.core.inout.erp.OrderService;
import com.namics.distrelec.b2b.core.service.order.DistOpenOrderService;
import com.namics.distrelec.b2b.core.service.order.model.ErpOpenOrderExtModel;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

public class DefaultDistOpenOrderService implements DistOpenOrderService {

    private ModelService modelService;
    private OrderService erpOrderService;

    @Override
    public void linkOpenOrderToCart(final AbstractOrderModel cart, final String erpCode) {
        ServicesUtil.validateParameterNotNull(cart, "Cart cannot be null.");
        ServicesUtil.validateParameterNotNull(erpCode, "Open Order code cannot be null.");
        cleanConfirmedOrderEntries(cart);
        setOpenOrderToCart(cart, true, erpCode);
        setBillingAndShippingAddress(cart, erpCode);
        setBillingAndShippingMethod(cart, erpCode);
        getModelService().save(cart);
    }

    private void setBillingAndShippingMethod(final AbstractOrderModel cart, final String erpCode) {
        final ErpOpenOrderExtModel order = erpOrderService.getOpenOrderForErpOrderCode(erpCode);
        if (order == null) {
            return;
        }

        cart.setPaymentMode(order.getErpPaymentMode());
        cart.setDeliveryMode(order.getErpDeliveryMode());

    }

    @Override
    public void linkOpenOrderToCart(final AbstractOrderModel cart) {
        ServicesUtil.validateParameterNotNull(cart, "Cart cannot be null.");

        cleanConfirmedOrderEntries(cart);
        setOpenOrderToCart(cart, true, null);

        getModelService().save(cart);
    }

    @Override
    public void releaseOpenOrderFromCart(final AbstractOrderModel cart) {
        ServicesUtil.validateParameterNotNull(cart, "Cart cannot be null.");

        cleanConfirmedOrderEntries(cart);

        setOpenOrderToCart(cart, false, null);

        cart.setOrderCloseDate(null);
        cart.setPaymentAddress(null);
        cart.setDeliveryAddress(null);
        cart.setPaymentMode(null);
        cart.setDeliveryMode(null);

        getModelService().save(cart);
    }

    @Override
    public void setOpenOrderClosingDate(final AbstractOrderModel cart, final Date date) {
        ServicesUtil.validateParameterNotNull(cart, "Cart cannot be null.");
        ServicesUtil.validateParameterNotNull(date, "Open Order close date cannot be null.");

        setOpenOrderToCart(cart, true, null);
        cart.setOrderCloseDate(date);

        getModelService().save(cart);

    }

    @Override
    public void setOpenOrderClosingDate(AbstractOrderModel cart, String code, Date date) {
        ServicesUtil.validateParameterNotNull(cart, "Cart cannot be null.");
        ServicesUtil.validateParameterNotNull(code, "Open Order Code cannot be null.");
        ServicesUtil.validateParameterNotNull(date, "Open Order close date cannot be null.");
        setOpenOrderToCart(cart, true, code);
        cart.setOrderCloseDate(date);

        getModelService().save(cart);

    }

    @Override
    public void setEditableByAllContacts(final AbstractOrderModel cart, final boolean isEditableByAllAccounts) {
        ServicesUtil.validateParameterNotNull(cart, "Cart cannot be null.");
        setOpenOrderToCart(cart, true, cart.getErpOpenOrderCode());
        cart.setEditableByAllContacts(Boolean.valueOf(isEditableByAllAccounts));
        getModelService().save(cart);
    }

    @Override
    public void modifyDetailsInErp(final ErpOpenOrderExtModel openOrder) {
        erpOrderService.modifyOpenOrderInErp(openOrder);
    }

    /*
     * set the relevant openOrder information on the cart
     */
    private void setOpenOrderToCart(final AbstractOrderModel cart, final boolean openOrder, final String erpOpenOrderCode) {
        cart.setOpenOrder(openOrder);
        cart.setErpOpenOrderCode(erpOpenOrderCode);
    }

    /*
     * remove all the confirmed entries related to the cart
     */
    private void cleanConfirmedOrderEntries(final AbstractOrderModel abstractOrder) {
        final Collection<AbstractOrderEntryModel> confirmedEntries = abstractOrder.getConfirmedOrderEntries();
        if (CollectionUtils.isNotEmpty(confirmedEntries)) {
            // finally remove all the existing confirmed entries
            getModelService().removeAll(confirmedEntries);
            getModelService().refresh(abstractOrder);
        }
    }

    /**
     * This method is setting the billing and shipping address from the open order history <br>
     * In SAP the addresses associated to the open order are a copy, and do not have the same erpAddressId <br>
     * 
     */
    private void setBillingAndShippingAddress(final AbstractOrderModel cart, final String erpCode) {
        final ErpOpenOrderExtModel order = erpOrderService.getOpenOrderForErpOrderCode(erpCode);
        if (order == null) {
            return;
        }

        if (order.getErpBillingAddress() != null && order.getErpShippingAddress() != null
                && order.getErpBillingAddress().equals(order.getErpShippingAddress())) {
            // when billing address is equals to the shipping address
            final AddressModel erpBillingAndShippingAddress = order.getErpBillingAddress();
            if (modelService.isNew(erpBillingAndShippingAddress)) {
                erpBillingAndShippingAddress.setOwner(cart);
            }

            modelService.save(erpBillingAndShippingAddress);
            cart.setPaymentAddress(erpBillingAndShippingAddress);
            cart.setDeliveryAddress(erpBillingAndShippingAddress);

        } else {
            // when billing and shipping address are different
            if (order.getErpBillingAddress() != null) {
                final AddressModel erpBillingAddress = order.getErpBillingAddress();
                if (modelService.isNew(erpBillingAddress)) {
                    erpBillingAddress.setOwner(cart);
                }
                modelService.save(erpBillingAddress);

                cart.setPaymentAddress(erpBillingAddress);
            }

            if (order.getErpShippingAddress() != null) {
                final AddressModel erpShippingAddress = order.getErpBillingAddress();
                if (modelService.isNew(erpShippingAddress)) {
                    erpShippingAddress.setOwner(cart);
                }
                modelService.save(erpShippingAddress);
                cart.setDeliveryAddress(erpShippingAddress);
            }
        }
        modelService.save(cart);

    }

    // spring

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public OrderService getErpOrderService() {
        return erpOrderService;
    }

    public void setErpOrderService(final OrderService erpOrderService) {
        this.erpOrderService = erpOrderService;
    }

}
