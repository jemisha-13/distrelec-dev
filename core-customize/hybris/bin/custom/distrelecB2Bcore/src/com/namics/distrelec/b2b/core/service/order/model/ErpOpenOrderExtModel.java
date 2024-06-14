/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.model;

import java.util.Date;
import java.util.List;

import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Represent an open order already placed in ERP system. <br>
 * 
 * 
 * @author DAEBERSANIF, Namics AG
 * @since Distrelec 1.1
 * 
 */
public class ErpOpenOrderExtModel {

    private String erpOrderId;
    private Date orderDate;
    private Date orderCloseDate;
    private String orderStatus;

    private String erpCustomerId;
    private String erpContactId;

    private String erpShippingAddressId;
    private String erpBillingAddressId;

    private AddressModel erpShippingAddress;
    private AddressModel erpBillingAddress;

    private PaymentModeModel erpPaymentMode;

    private DeliveryModeModel erpDeliveryMode;

    // TODO: this field is not present in the IF-15 ReadAllOpenOrders method.
    private boolean freeShippingPromotion;

    private String orderReferenceHeaderLevel;

    private boolean editableByAllContacts;

    private List<Date> possibleClosingDates;

    public String getErpCustomerId() {
        return erpCustomerId;
    }

    public void setErpCustomerId(final String erpCustomerId) {
        this.erpCustomerId = erpCustomerId;
    }

    public String getErpContactId() {
        return erpContactId;
    }

    public void setErpContactId(final String erpContactId) {
        this.erpContactId = erpContactId;
    }

    public String getErpOrderId() {
        return erpOrderId;
    }

    public void setErpOrderId(final String erpOrderId) {
        this.erpOrderId = erpOrderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(final Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Date getOrderCloseDate() {
        return orderCloseDate;
    }

    public void setOrderCloseDate(final Date orderCloseDate) {
        this.orderCloseDate = orderCloseDate;
    }

    public boolean isEditableByAllContacts() {
        return editableByAllContacts;
    }

    public void setEditableByAllContacts(final boolean editableByAllContacts) {
        this.editableByAllContacts = editableByAllContacts;
    }

    public String getErpShippingAddressId() {
        return erpShippingAddressId;
    }

    public void setErpShippingAddressId(final String erpShippingAddressId) {
        this.erpShippingAddressId = erpShippingAddressId;
    }

    public String getErpBillingAddressId() {
        return erpBillingAddressId;
    }

    public void setErpBillingAddressId(final String erpBillingAddressId) {
        this.erpBillingAddressId = erpBillingAddressId;
    }

    public String getOrderReferenceHeaderLevel() {
        return orderReferenceHeaderLevel;
    }

    public void setOrderReferenceHeaderLevel(final String orderReferenceHeaderLevel) {
        this.orderReferenceHeaderLevel = orderReferenceHeaderLevel;
    }

    public boolean isFreeShippingPromotion() {
        return freeShippingPromotion;
    }

    public void setFreeShippingPromotion(final boolean freeShippingPromotion) {
        this.freeShippingPromotion = freeShippingPromotion;
    }

    public List<Date> getPossibleClosingDates() {
        return possibleClosingDates;
    }

    public void setPossibleClosingDates(final List<Date> possibleClosingDates) {
        this.possibleClosingDates = possibleClosingDates;
    }

    public AddressModel getErpShippingAddress() {
        return erpShippingAddress;
    }

    public void setErpShippingAddress(final AddressModel erpShippingAddress) {
        this.erpShippingAddress = erpShippingAddress;
    }

    public AddressModel getErpBillingAddress() {
        return erpBillingAddress;
    }

    public void setErpBillingAddress(final AddressModel erpBillingAddress) {
        this.erpBillingAddress = erpBillingAddress;
    }

    public PaymentModeModel getErpPaymentMode() {
        return erpPaymentMode;
    }

    public void setErpPaymentMode(PaymentModeModel erpPaymentMode) {
        this.erpPaymentMode = erpPaymentMode;
    }

    public DeliveryModeModel getErpDeliveryMode() {
        return erpDeliveryMode;
    }

    public void setErpDeliveryMode(DeliveryModeModel erpDeliveryMode) {
        this.erpDeliveryMode = erpDeliveryMode;
    }

}
