/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.data.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.data.DistRestrictionData;

import de.hybris.platform.cms2.servicelayer.data.impl.DefaultRestrictionData;

public class DefaultDistRestrictionData extends DefaultRestrictionData implements DistRestrictionData {

    private DistManufacturerModel manufacturer;

    @Override
    public void setManufacturer(final DistManufacturerModel manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Override
    public DistManufacturerModel getManufacturer() {
        return this.manufacturer;
    }

    @Override
    public boolean hasManufacturer() {
        return this.manufacturer != null;
    }
}
