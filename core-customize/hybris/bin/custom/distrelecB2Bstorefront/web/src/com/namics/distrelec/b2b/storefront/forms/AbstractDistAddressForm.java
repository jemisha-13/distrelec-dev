/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.namics.distrelec.b2b.storefront.validation.annotations.BizPostalCode;
import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;

/**
 * AbstractDistAddressForm
 *
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 *
 */

@BizPostalCode(message = "register.postalCode.validationMessage", pattern = "register.postalCode.validationPattern")
public abstract class AbstractDistAddressForm {
    private String addressId;

    private String line1;

    private String line2;

    private String town;

    private String postalCode;

    private String poBox;

    private String countryIso;

    private String regionIso;

    private Boolean shippingAddress;

    private Boolean billingAddress;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(final String addressId) {
        this.addressId = addressId;
    }

    @NotBlank(message = "{register.strName.invalid}")
    @Size(max = 60, message = "{Size.strName}")
    public String getLine1() {
        return line1;
    }

    public void setLine1(final String line1) {
        this.line1 = line1;
    }

    @Size(max = 10, message = "{Size.strNumber}")
    public String getLine2() {
        return line2;
    }

    public void setLine2(final String line2) {
        this.line2 = line2;
    }

    @NotBlank(message = "{register.town.invalid}")
    @Size(max = 40, message = "{register.town.invalid}")
    public String getTown() {
        return town;
    }

    public void setTown(final String town) {
        this.town = town;
    }

    @NotBlank(message = "{register.postalCode.invalid}")
    @Size(max = 10, message = "{register.postalCode.invalid}")
    @DataFormInput(message = "{register.postalCode.validationMessage}", patternKey = "register.postalCode.validationPattern")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    @NotNull(message = "{address.country.invalid}")
    public String getCountryIso() {
        return countryIso;
    }

    public void setCountryIso(final String countryIso) {
        this.countryIso = countryIso;
    }

    public String getRegionIso() {
        return regionIso;
    }

    public void setRegionIso(final String regionIso) {
        this.regionIso = regionIso;
    }

    public Boolean getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(final Boolean shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Boolean getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(final Boolean billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Size(max = 10, message = "{register.poBox.invalid}")
    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(final String poBox) {
        this.poBox = poBox;
    }
}
