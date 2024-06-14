/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * {@code SapVoucherEvent}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public class SapVoucherEvent extends AbstractDistrelecCustomerEvent<BaseSiteModel> {

    private AbstractOrderModel targetOrder;
    private String datePattern;

    /**
     * Create a new instance of {@code SapVoucherEvent}
     */
    public SapVoucherEvent() {
        super();
    }

    /**
     * Create a new instance of {@code SapVoucherEvent}
     * 
     * @param targetOrder
     */
    public SapVoucherEvent(final AbstractOrderModel targetOrder, final String datePattern) {
        this.targetOrder = targetOrder;
        this.datePattern = datePattern;
    }

    public AbstractOrderModel getTargetOrder() {
        return targetOrder;
    }

    public void setTargetOrder(final AbstractOrderModel targetOrder) {
        this.targetOrder = targetOrder;
    }

    public String getDatePattern() {
        return datePattern;
    }

    public void setDatePattern(final String datePattern) {
        this.datePattern = datePattern;
    }
}
