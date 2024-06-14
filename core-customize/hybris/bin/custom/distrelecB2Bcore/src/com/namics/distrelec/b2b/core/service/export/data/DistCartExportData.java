/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.export.data;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;

/**
 * DistCartExport Data Object
 * 
 */
@DelimitedFile(delimiter = ";")
public class DistCartExportData extends AbstractDistExportData {

    private String mySinglePrice;
    private String mySubtotal;
    private String listSinglePrice;
    private String listSubtotal;

    public String getMySinglePrice() {
        return mySinglePrice;
    }

    @DelimitedFileMethod(position = 12, name = "My Single Price", nullValue = "\u0000")
    public void setMySinglePrice(final String mySinglePrice) {
        this.mySinglePrice = mySinglePrice;
    }

    public String getMySubtotal() {
        return mySubtotal;
    }

    @DelimitedFileMethod(position = 13, name = "My Subtotal", nullValue = "\u0000")
    public void setMySubtotal(final String mySubtotal) {
        this.mySubtotal = mySubtotal;
    }

    public String getListSinglePrice() {
        return listSinglePrice;
    }

    @DelimitedFileMethod(position = 14, name = "List Single Price", nullValue = "\u0000")
    public void setListSinglePrice(final String listSinglePrice) {
        this.listSinglePrice = listSinglePrice;
    }

    public String getListSubtotal() {
        return listSubtotal;
    }

    @DelimitedFileMethod(position = 15, name = "List Subtotal", nullValue = "\u0000")
    public void setListSubtotal(final String listSubtotal) {
        this.listSubtotal = listSubtotal;
    }
}
