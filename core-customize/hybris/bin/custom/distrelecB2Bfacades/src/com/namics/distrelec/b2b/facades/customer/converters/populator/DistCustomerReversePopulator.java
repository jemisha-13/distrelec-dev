package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2bacceleratorservices.company.CompanyB2BCommerceService;
import de.hybris.platform.b2bcommercefacades.company.converters.populators.B2BCustomerReversePopulator;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Distrelec customer reverse populator.
 *
 * @author daehusir, Distrelec
 *
 */
public class DistCustomerReversePopulator extends B2BCustomerReversePopulator {

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistrelecCodelistService codelistService;

    @Autowired
    private AddressReversePopulator addressReversePopulator;

    @Autowired
    private CompanyB2BCommerceService companyB2BCommerceService;

    @Override
    public void populate(final CustomerData source, final B2BCustomerModel target) {
        super.populate(source, target);
        if (StringUtils.isNotBlank(source.getFunctionCode())) {
            target.setDistFunction(codelistService.getDistFunction(source.getFunctionCode()));
        }

        if (target.getContactAddress() != null) {
            addressReversePopulator.populate(source.getContactAddress(), target.getContactAddress());
            modelService.save(target.getContactAddress());
        } else {
            final AddressModel contactAddress = modelService.create(AddressModel.class);
            addressReversePopulator.populate(source.getContactAddress(), contactAddress);
            contactAddress.setOwner(target);
        }
        if (target.isNewsletter() != source.isNewsletter()) {
            target.setNewsletter(source.isNewsletter());
        }

        if (target.isPhoneMarketingConsent() != source.isSubscribePhoneMarketing()) {
            target.setPhoneMarketingConsent(source.isSubscribePhoneMarketing());
        }
    }
}
