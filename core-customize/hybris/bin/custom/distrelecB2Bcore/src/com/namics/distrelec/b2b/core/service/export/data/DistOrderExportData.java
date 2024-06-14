/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.export.data;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;

/**
 * DistOrderExport Data Object
 * 
 */
@DelimitedFile(delimiter = ";")
public class DistOrderExportData extends AbstractDistExportData {

    private String orderCode;
    private String mySinglePrice;
    private String mySubtotal;

    public String getOrderCode() {
        return orderCode;
    }

    @DelimitedFileMethod(position = 12, name = "orderCode", nullValue = "\u0000")
    public void setOrderCode(final String orderCode) {
        this.orderCode = orderCode;
    }

    public String getMySinglePrice() {
        return mySinglePrice;
    }

    @DelimitedFileMethod(position = 13, name = "My Single Price", nullValue = "\u0000")
    public void setMySinglePrice(final String mySinglePrice) {
        this.mySinglePrice = mySinglePrice;
    }

    public String getMySubtotal() {
        return mySubtotal;
    }

    @DelimitedFileMethod(position = 14, name = "My Subtotal", nullValue = "\u0000")
    public void setMySubtotal(final String mySubtotal) {
        this.mySubtotal = mySubtotal;
    }
}
