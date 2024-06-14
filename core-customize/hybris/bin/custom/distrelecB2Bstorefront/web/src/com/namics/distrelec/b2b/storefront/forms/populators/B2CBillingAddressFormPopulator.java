package com.namics.distrelec.b2b.storefront.forms.populators;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.storefront.controllers.util.AddressFormHelper;
import com.namics.distrelec.b2b.storefront.forms.B2CAddressForm;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

@Component
public class B2CBillingAddressFormPopulator implements Populator<AddressData, B2CAddressForm> {

    @Override
    public void populate(AddressData billingAddress, B2CAddressForm billingAddressForm) throws ConversionException {
        billingAddressForm.setAddressId(billingAddress.getId());
        billingAddressForm.setBillingAddress(Boolean.TRUE);
        billingAddressForm.setShippingAddress(BooleanUtils.toBoolean(billingAddress.isShippingAddress()));
        billingAddressForm.setTitleCode(billingAddress.getTitleCode());
        billingAddressForm.setFirstName(billingAddress.getFirstName());
        billingAddressForm.setLastName(billingAddress.getLastName());
        billingAddressForm.setLine1(billingAddress.getLine1());
        billingAddressForm.setLine2(billingAddress.getLine2());
        billingAddressForm.setTown(billingAddress.getTown());
        billingAddressForm.setPostalCode(billingAddress.getPostalCode());
        if (billingAddress.getCountry() != null) {
            billingAddressForm.setCountryIso(billingAddress.getCountry().getIsocode());
        }
        billingAddressForm.setContactPhone(AddressFormHelper.getPhoneNumber(billingAddress));
        if (billingAddress.getRegion() != null) {
            billingAddressForm.setRegionIso(billingAddress.getRegion().getIsocode());
        }
    }
}
