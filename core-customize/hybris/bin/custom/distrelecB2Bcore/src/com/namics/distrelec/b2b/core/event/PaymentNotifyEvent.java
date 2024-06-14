/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import java.io.Serializable;
import java.util.Date;

import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;

import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * {@code PaymentNotifyEvent}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.4
 */
public class PaymentNotifyEvent extends AbstractEvent {

    private String cartCode;
    private UserModel user;
    private Date notifyTime;
    private DistPaymentModeModel paymentMode;

    /**
     * Create a new instance of {@code PaymentNotifyEvent}
     */
    public PaymentNotifyEvent() {
        super();
    }

    /**
     * Create a new instance of {@code PaymentNotifyEvent}
     * 
     * @param source
     */
    public PaymentNotifyEvent(final Serializable source) {
        super(source);
    }

    /**
     * Create a new instance of {@code PaymentNotifyEvent}
     * 
     * @param cartCode
     * @param user
     */
    public PaymentNotifyEvent(final String cartCode, final UserModel user, final DistPaymentModeModel paymentMode) {
        this(cartCode, user, new Date(),paymentMode);
    }

    /**
     * Create a new instance of {@code PaymentNotifyEvent}
     * 
     * @param cartCode
     * @param user
     * @param notifyTime
     */
    public PaymentNotifyEvent(final String cartCode, final UserModel user, final Date notifyTime, final DistPaymentModeModel paymentMode) {
        this.cartCode = cartCode;
        this.user = user;
        this.notifyTime = notifyTime;
        this.paymentMode = paymentMode;
    }

    public String getCartCode() {
        return cartCode;
    }

    public void setCartCode(final String cartCode) {
        this.cartCode = cartCode;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(final UserModel user) {
        this.user = user;
    }

    public Date getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(final Date notifyTime) {
        this.notifyTime = notifyTime;
    }

	/**
	 * @return the paymentMode
	 */
	public DistPaymentModeModel getPaymentMode() {
		return paymentMode;
	}

	/**
	 * @param paymentMode the paymentMode to set
	 */
	public void setPaymentMode(DistPaymentModeModel paymentMode) {
		this.paymentMode = paymentMode;
	}
    
    
}
