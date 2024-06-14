package com.namics.distrelec.occ.core.v2.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.namics.distrelec.b2b.facades.user.DistUserFacade;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.CustomerType;

@Component
public class AddressHelper extends AbstractHelper {
    private static final String B2C_ADDRESS_MAPPING = "id,firstName,lastName,titleCode,phone,fax,cellphone,line1,line2,town,postalCode,region(DEFAULT),country(isocode),billingAddress,shippingAddress,additionalAddress,pobox,codiceFiscale";

    private static final String B2B_ADDRESS_MAPPING = "id,companyName,companyName2,companyName3,fax,phone,cellphone,line1,line2,town,postalCode,region(DEFAULT),country(isocode),billingAddress,shippingAddress,pobox";

    private static final String CHECKOUT_B2C_ADDRESS_MAPPING = "id,firstName,lastName,titleCode,line1,line2,town,postalCode,region(DEFAULT),country(isocode),billingAddress,shippingAddress,additionalAddress,pobox,codiceFiscale";

    private static final String CHECKOUT_B2B_ADDRESS_MAPPING = "id,companyName,companyName2,companyName3,line1,line2,town,postalCode,region(DEFAULT),country(isocode),billingAddress,shippingAddress,pobox";

    public static final String CHECKOUT_GUEST_ADDRESS_MAPPING = "firstName,lastName,titleCode,line1,line2,town,postalCode,region(isocode),country(isocode),billingAddress,shippingAddress,additionalAddress,pobox,codiceFiscale";

    private static final String CHECKOUT_B2E_ADDRESS_MAPPING = "id,firstName,lastName,titleCode,line1,line2,town,postalCode,region(DEFAULT),country(isocode),billingAddress,shippingAddress,additionalAddress,pobox,codiceFiscale,email";

    @Autowired
    private DistUserFacade userFacade;

    public String getAddressMappingByCustomerType(CustomerType customerType, boolean isCheckout) {

        switch (customerType) {
            case B2C:
                return isCheckout ? CHECKOUT_B2C_ADDRESS_MAPPING : B2C_ADDRESS_MAPPING;
            case B2B:
            case B2B_KEY_ACCOUNT:
                return isCheckout ? CHECKOUT_B2B_ADDRESS_MAPPING : B2B_ADDRESS_MAPPING;
            case B2E:
                return CHECKOUT_B2E_ADDRESS_MAPPING;
            default:
                return null;
        }
    }

    public AddressData createAddress(final AddressData addressData) {
        addressData.setShippingAddress(true);
        addressData.setVisibleInAddressBook(true);
        userFacade.addAddress(addressData);
        if (addressData.isDefaultAddress()) {
            userFacade.setDefaultAddress(addressData);
        }
        return addressData;
    }
}
