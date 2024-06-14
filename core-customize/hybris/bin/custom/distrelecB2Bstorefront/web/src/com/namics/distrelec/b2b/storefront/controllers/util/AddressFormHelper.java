/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.util;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.storefront.forms.*;

import de.hybris.platform.commercefacades.user.data.AddressData;

/**
 * @author DAEZAMOFINL, Namics AG
 * @since Namics Extensions 1.0
 */
public class AddressFormHelper {

    public static void populateForm(final AddressData addressData, final AbstractDistAddressForm addressForm) {
        addressForm.setAddressId(addressData.getId());

        if (addressForm instanceof B2CAddressForm) {
            final B2CAddressForm b2cAddressForm = (B2CAddressForm) addressForm;
            b2cAddressForm.setAdditionalAddress(addressData.getAdditionalAddress());
            b2cAddressForm.setTitleCode(addressData.getTitleCode());
            b2cAddressForm.setFirstName(addressData.getFirstName());
            b2cAddressForm.setLastName(addressData.getLastName());
            b2cAddressForm.setContactPhone(addressData.getPhone());
            b2cAddressForm.setMobileNumber(addressData.getCellphone());
        } else if (addressForm instanceof AbstractB2BAddressForm) {
            final AbstractB2BAddressForm b2bAddressForm = (AbstractB2BAddressForm) addressForm;
            b2bAddressForm.setCompanyName(addressData.getCompanyName());
            b2bAddressForm.setCompanyName2(addressData.getCompanyName2());
            b2bAddressForm.setCompanyName3(addressData.getCompanyName3());

            if (addressForm instanceof B2BBillingAddressForm) {
                final B2BBillingAddressForm b2bBillingAddressForm = (B2BBillingAddressForm) b2bAddressForm;
                b2bBillingAddressForm.setPhoneNumber(addressData.getPhone());
                b2bBillingAddressForm.setMobileNumber(addressData.getCellphone());
                b2bBillingAddressForm.setFaxNumber(addressData.getFax());
            }

            if (addressForm instanceof B2BShippingAddressForm) {
                final B2BShippingAddressForm b2bShippingAddressForm = (B2BShippingAddressForm) b2bAddressForm;
                b2bShippingAddressForm.setPhoneNumber(addressData.getPhone());
                b2bShippingAddressForm.setMobileNumber(addressData.getCellphone());
                b2bShippingAddressForm.setFaxNumber(addressData.getFax());
            }
        }

        addressForm.setLine1(addressData.getLine1());
        addressForm.setLine2(addressData.getLine2());
        addressForm.setTown(addressData.getTown());
        addressForm.setPostalCode(addressData.getPostalCode());
        addressForm.setPoBox(addressData.getPobox());
        addressForm.setCountryIso(addressData.getCountry().getIsocode());
        addressForm.setRegionIso(addressData.getRegion() != null ? addressData.getRegion().getIsocode() : null);

        addressForm.setBillingAddress(addressData.isBillingAddress());
        addressForm.setShippingAddress(addressData.isShippingAddress());
        if (StringUtils.isBlank(addressForm.getAddressId())) {
            addressForm.setBillingAddress(Boolean.FALSE);
            addressForm.setShippingAddress(Boolean.TRUE);
        }
    }

    public static void populateAddressData(final AbstractDistAddressForm addressForm, final AddressData addressData) {

        // Populate B2C AddressData
        if (addressForm instanceof B2CAddressForm) {
            final B2CAddressForm b2CAddressForm = (B2CAddressForm) addressForm;
            addressData.setAdditionalAddress(b2CAddressForm.getAdditionalAddress());
            addressData.setTitleCode(b2CAddressForm.getTitleCode());
            addressData.setFirstName(b2CAddressForm.getFirstName());
            addressData.setLastName(b2CAddressForm.getLastName());
            addressData.setPhone(b2CAddressForm.getContactPhone());
            addressData.setCellphone(b2CAddressForm.getMobileNumber());
            addressData.setFax(b2CAddressForm.getFax());
        } else if (addressForm instanceof GuestAddressForm) {
            final GuestAddressForm guestAddress = (GuestAddressForm) addressForm;
            addressData.setTitleCode(guestAddress.getTitleCode());
            addressData.setFirstName(guestAddress.getFirstName());
            addressData.setLastName(guestAddress.getLastName());
            addressData.setPhone(guestAddress.getContactPhone());
            addressData.setCellphone(guestAddress.getMobileNumber());
        } else if (addressForm instanceof AbstractB2BAddressForm) {
            // Populate B2B AddressData
            final AbstractB2BAddressForm b2BAddressForm = (AbstractB2BAddressForm) addressForm;
            addressData.setCompanyName(b2BAddressForm.getCompanyName());
            addressData.setCompanyName2(b2BAddressForm.getCompanyName2());
            addressData.setCompanyName3(b2BAddressForm.getCompanyName3());

            if (addressForm instanceof B2BBillingAddressForm) {
                final B2BBillingAddressForm b2BBillingAddressForm = (B2BBillingAddressForm) addressForm;
                addressData.setPhone(b2BBillingAddressForm.getPhoneNumber());
                addressData.setCellphone(b2BBillingAddressForm.getMobileNumber());
                addressData.setFax(b2BBillingAddressForm.getFaxNumber());
            }

            if (addressForm instanceof B2BShippingAddressForm) {
                final B2BShippingAddressForm b2bShippingAddressForm = (B2BShippingAddressForm) addressForm;
                addressData.setCellphone(b2bShippingAddressForm.getMobileNumber());
                addressData.setFax(b2bShippingAddressForm.getFaxNumber());
            }
        }
        // Populate B2C/B2B Common AddressData
        addressData.setLine1(addressForm.getLine1());
        addressData.setLine2(addressForm.getLine2());
        addressData.setPobox(addressForm.getPoBox());
        addressData.setTown(addressForm.getTown());
        addressData.setPostalCode(addressForm.getPostalCode());
        addressData.setShippingAddress(Boolean.TRUE.equals(addressForm.getShippingAddress()));
        addressData.setBillingAddress(Boolean.TRUE.equals(addressForm.getBillingAddress()));
    }

    public static void populateB2EAddressData(final B2EAddressForm form, final AddressData addressData) {
        addressData.setId(form.getAddressId());
        addressData.setShippingAddress(true);
        addressData.setBillingAddress(true);
        addressData.setTitleCode(form.getTitleCode());
        addressData.setFirstName(form.getFirstName());
        addressData.setLastName(form.getLastName());
        addressData.setEmail(form.getEmail());
        addressData.setLine1(form.getStreetName());
        addressData.setLine2(form.getStreetNumber());
        addressData.setPostalCode(form.getPostalCode());
        addressData.setTown(form.getTown());
        addressData.setPhone(form.getPhoneNumber());
    }

    public static void setPhoneNumber(AbstractDistAddressForm form, String telNumber) {
        if (form instanceof B2CAddressForm) {
            final B2CAddressForm b2cAddressForm = (B2CAddressForm) form;
            b2cAddressForm.setContactPhone(telNumber);
        } else if (form instanceof GuestAddressForm) {
            final GuestAddressForm guestAddressForm = (GuestAddressForm) form;
            guestAddressForm.setContactPhone(telNumber);
        } else if (form instanceof B2BBillingAddressForm) {
            final B2BBillingAddressForm b2BBillingAddressForm = (B2BBillingAddressForm) form;
            b2BBillingAddressForm.setPhoneNumber(telNumber);
        }
    }

    public static void setMobileNumber(AbstractDistAddressForm form, String telNumber) {
        if (form instanceof B2CAddressForm) {
            final B2CAddressForm b2cAddressForm = (B2CAddressForm) form;
            b2cAddressForm.setMobileNumber(telNumber);
            b2cAddressForm.setContactPhone(StringUtils.EMPTY);
        } else if (form instanceof GuestAddressForm) {
            final GuestAddressForm guestAddressForm = (GuestAddressForm) form;
            guestAddressForm.setMobileNumber(telNumber);
            guestAddressForm.setContactPhone(StringUtils.EMPTY);
        } else if (form instanceof B2BBillingAddressForm) {
            final B2BBillingAddressForm b2BBillingAddressForm = (B2BBillingAddressForm) form;
            b2BBillingAddressForm.setMobileNumber(telNumber);
            b2BBillingAddressForm.setPhoneNumber(StringUtils.EMPTY);
        }
    }

    public static String getPhoneNumber(AbstractDistAddressForm form) {
        if (form instanceof B2CAddressForm) {
            final B2CAddressForm b2cAddressForm = (B2CAddressForm) form;
            return b2cAddressForm.getContactPhone();
        } else if (form instanceof GuestAddressForm) {
            final GuestAddressForm guestAddressForm = (GuestAddressForm) form;
            return guestAddressForm.getContactPhone();
        } else if (form instanceof B2BBillingAddressForm) {
            final B2BBillingAddressForm b2BBillingAddressForm = (B2BBillingAddressForm) form;
            return b2BBillingAddressForm.getPhoneNumber();
        }
        return StringUtils.EMPTY;
    }

    public static String getPhoneNumber(AddressData addressData) {
        if (StringUtils.isNotBlank(addressData.getCellphone())) {
            return addressData.getCellphone();
        }
        if (StringUtils.isNotBlank(addressData.getPhone1())) {
            return addressData.getPhone1();
        }
        return StringUtils.EMPTY;
    }

}
