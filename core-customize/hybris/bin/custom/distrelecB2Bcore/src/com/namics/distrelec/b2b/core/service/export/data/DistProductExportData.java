/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.export.data;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;

@DelimitedFile(delimiter = ";")
public class DistProductExportData extends AbstractDistExportData {

    private String salesUnit;

    @DelimitedFileMethod(position = 8, name = "Packing Size", nullValue = "")
    public void setSalesUnit(final String salesUnit) {
        this.salesUnit = salesUnit;
    }

    public String getSalesUnit() {
        return salesUnit;
    }
}
