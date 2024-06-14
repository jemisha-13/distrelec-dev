package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;

public class DistB2BRegisterDataPopulator extends DistRegisterDataPopulator {

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private DistCustomerFacade customerFacade;

    @Override
    public void populate(RegisterData source, DistRegisterData target) throws ConversionException {
        super.populate(source, target);
        final String salesOrgCode = distSalesOrgService.getCurrentSalesOrg().getCode();
        String orgnumber = source.getOrganizationalNumber();
        target.setDuns(source.getDuns());
        if (StringUtils.isNotBlank(source.getCompany())) {
            Iterator<String> splittedCompanyNameIterator = customerFacade.formatCompanyName(source.getCompany()).iterator();
            target.setCompanyName(splittedCompanyNameIterator.next());
            target.setCompanyName2(splittedCompanyNameIterator.hasNext() ? splittedCompanyNameIterator.next() : StringUtils.EMPTY);
            target.setCompanyName3(splittedCompanyNameIterator.hasNext() ? splittedCompanyNameIterator.next() : StringUtils.EMPTY);
        }
        target.setFunctionCode(source.getFunctionCode());
        target.setCompanyMatch(source.getCompanyMatch());
        target.setVatId(source.getVatId());

        if (salesOrgCode.equals(DistConstants.SalesOrg.SALES_ORG_7650) && null != orgnumber) {
            orgnumber = orgnumber.replaceAll("\\s+", "");
        }
        target.setOrganizationalNumber(orgnumber);
        target.setCustomerType(CustomerType.B2B);
        target.setRegistrationType(RegistrationType.STANDALONE);
        target.setInvoiceEmail(StringUtils.isBlank(source.getInvoiceEmail()) ? source.getLogin() : source.getInvoiceEmail());
    }
}
