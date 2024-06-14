/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.data;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;

public interface DistCmsDataFactory extends CMSDataFactory {

    DistRestrictionData createRestrictionData(final DistManufacturerModel manufacturer);
}
