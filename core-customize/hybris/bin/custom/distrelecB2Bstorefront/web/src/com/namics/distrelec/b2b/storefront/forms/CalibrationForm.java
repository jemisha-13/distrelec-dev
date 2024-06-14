/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.Min;

/**
 * {@code CalibrationForm}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public class CalibrationForm extends PriceDiffForm {

    private int entryNumber = -1;
    private long newQty;

    @Min(0)
    public int getEntryNumber() {
        return entryNumber;
    }

    public void setEntryNumber(final int entryNumber) {
        this.entryNumber = entryNumber;
    }

    @Min(0)
    public long getNewQty() {
        return newQty;
    }

    public void setNewQty(final long newQty) {
        this.newQty = newQty;
    }
}
