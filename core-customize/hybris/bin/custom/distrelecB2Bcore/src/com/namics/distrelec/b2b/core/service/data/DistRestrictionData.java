/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.data;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.cms2.servicelayer.data.RestrictionData;

public interface DistRestrictionData extends RestrictionData {

    public abstract void setManufacturer(final DistManufacturerModel manufacturer);

    public abstract DistManufacturerModel getManufacturer();

    public abstract boolean hasManufacturer();
}
