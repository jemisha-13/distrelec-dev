package com.namics.distrelec.b2b.facades.customer.converters.populator;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;

import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistB2BRegisterDataForExistingCustomerPopulator extends DistRegisterDataForExistingCustomerPopulator {

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Override
    public void populate(final RegisterData source, final DistExistingCustomerRegisterData target) throws ConversionException {
        super.populate(source, target);
        final String salesOrgCode = distSalesOrgService.getCurrentSalesOrg().getCode();
        String orgnumber = source.getOrganizationalNumber();
        target.setCompanyName(source.getCompany());
        target.setFunctionCode(source.getFunctionCode());
        target.setVatId(source.getVatId());
        target.setInvoiceEmail(source.getInvoiceEmail());
        if (salesOrgCode.equals(DistConstants.SalesOrg.SALES_ORG_7650) && null != orgnumber) {
            orgnumber = orgnumber.replaceAll("\\s+", "");
        }
        target.setOrganizationalNumber(orgnumber);
        target.setPersonalisedRecommendationConsent(source.getPersonalisedRecommendationConsent());
        target.setCustomerSurveysConsent(source.getCustomerSurveysConsent());
        target.setDuns(source.getDuns());
    }
}
