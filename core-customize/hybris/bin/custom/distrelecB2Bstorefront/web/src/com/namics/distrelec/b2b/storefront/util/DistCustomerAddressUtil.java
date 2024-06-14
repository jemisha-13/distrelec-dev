package com.namics.distrelec.b2b.storefront.util;

import static de.hybris.platform.commerceservices.enums.CustomerType.B2B;
import static de.hybris.platform.commerceservices.enums.CustomerType.B2C;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.hybris.platform.commercefacades.user.data.CustomerData;

public class DistCustomerAddressUtil {

    public static String getCustomerAddress(CustomerData currentCustomer) {
        if (currentCustomer == null) {
            return EMPTY;
        }

        String line = EMPTY;
        String zip = EMPTY;
        String town = EMPTY;

        if (B2C.equals(currentCustomer.getCustomerType()) && currentCustomer.getContactAddress() != null) {
            line = currentCustomer.getContactAddress().getLine1();
            zip = currentCustomer.getContactAddress().getPostalCode();
            town = currentCustomer.getContactAddress().getTown();
        } else if (B2B.equals(currentCustomer.getCustomerType()) && currentCustomer.getUnit() != null && isNotEmpty(currentCustomer.getUnit().getAddresses())) {
            if (currentCustomer.getUnit().getAddresses().get(0) != null) {
                line = currentCustomer.getUnit().getAddresses().get(0).getLine1();
                zip = currentCustomer.getUnit().getAddresses().get(0).getPostalCode();
                town = currentCustomer.getUnit().getAddresses().get(0).getTown();
            }
        }

        StringBuilder address = new StringBuilder(EMPTY);
        if (isNotBlank(line) && isNotBlank(zip) && isNotBlank(town)) {
            address.append(line);
            address.append(", ");
            address.append(zip);
            address.append(", ");
            address.append(town);
        }

        return address.toString();
    }
}
