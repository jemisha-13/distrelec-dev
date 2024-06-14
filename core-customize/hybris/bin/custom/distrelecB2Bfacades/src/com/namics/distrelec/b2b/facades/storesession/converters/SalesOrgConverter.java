/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.storesession.converters;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.facades.storesession.data.SalesOrgData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.springframework.util.Assert;

/**
 * Converts a {@link DistSalesOrgModel} to its POJO {@link SalesOrgData}.
 *
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 *
 */
public class SalesOrgConverter extends AbstractPopulatingConverter<DistSalesOrgModel, SalesOrgData> {

    @Override
    protected SalesOrgData createTarget() {
        return new SalesOrgData();
    }

    @Override
    public void populate(final DistSalesOrgModel source, final SalesOrgData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        target.setCode(source.getCode());
        target.setBrand(source.getBrand().getCode());
        target.setNativeName(source.getName());
        target.setCountryIsocode(source.getCountry().getIsocode());
        target.setCountryNativeName(source.getCountry().getName());
        target.setErpSystem(source.getErpSystem().getCode());
        target.setAdminManagingSubUsers(source.isAdminManagingSubUsers());
        target.setInvoiceVisibleToAll(source.isInvoiceVisibleToAll());
        target.setOrderVisibleToAll(source.isOrderVisibibleToAll());
        target.setQuotationVisibibleToAll(source.isQuotationVisibibleToAll());
        target.setOfflineRegistrationAllowed(source.isOfflineRegistrationAllowed());
        target.setRegisteringNewContactToExistingCustomerAllowed(source.isRegisteringNewContactToExistingCustomerAllowed());
        target.setCalibrationInfoDeactivated(source.isCalibrationInfoDeactivated());
        super.populate(source, target);
    }

}
