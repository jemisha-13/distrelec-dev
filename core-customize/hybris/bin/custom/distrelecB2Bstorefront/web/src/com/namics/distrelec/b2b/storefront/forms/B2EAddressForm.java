/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.*;

import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;
import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;

/**
 * {@code B2EForm}
 * <p>
 * Form object for the B2EESHOPGROUP members checkout
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Namics Extensions 1.0
 */
public class B2EAddressForm implements NewsletterForm {

    private String addressId;

    private String titleCode;

    private String firstName;

    private String lastName;

    private String email;

    private String streetName;

    private String streetNumber;

    private String postalCode;

    private String town;

    private String countryCode;

    private String regionCode;

    private String phoneNumber;

    private boolean termsOfUse;

    private boolean marketingConsent;

    private boolean npsConsent;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(final String addressId) {
        this.addressId = addressId;
    }

    @Override
    @NotBlank(message = "{register.email.invalid}")
    @Size(min = 1, max = 175, message = "{register.email.invalid}")
    @Email(message = "{register.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    @NotNull(message = "{register.title.invalid}")
    @Size(min = 1, max = 255, message = "{register.title.invalid}")
    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    @NotBlank(message = "{register.strName.invalid}")
    @Size(min = 1, max = 60, message = "{register.strName.invalid}")
    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(final String streetName) {
        this.streetName = streetName;
    }

    @Size(max = 10, message = "{Size.strNumber}")
    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(final String streetNumber) {
        this.streetNumber = streetNumber;
    }

    @NotBlank(message = "{register.postalCode.invalid}")
    @Size(min = 1, max = 10, message = "{register.postalCode.invalid}")
    @DataFormInput(message = "{register.postalCode.validationMessage}", patternKey = "register.postalCode.validationPattern")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    @NotBlank(message = "{register.town.invalid}")
    @Size(min = 1, max = 40, message = "{register.town.invalid}")
    public String getTown() {
        return town;
    }

    public void setTown(final String town) {
        this.town = town;
    }

    @Size(max = 3, message = "{register.country.invalid}")
    @NotNull(message = "{register.country.invalid}")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(final String regionCode) {
        this.regionCode = regionCode;
    }

    @Override
    @NotBlank(message = "{register.firstName.invalid}")
    @Size(min = 1, max = 40, message = "{register.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @Override
    @NotBlank(message = "{register.lastName.invalid}")
    @Size(min = 1, max = 40, message = "{register.lastName.invalid}")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @AssertTrue(message = "{register.tOfUse.invalid}")
    public boolean isTermsOfUse() {
        return termsOfUse;
    }

    public void setTermsOfUse(final boolean termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    @Override
    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(final boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    @Phonenumber(message = "{register.mobileNumber.invalid}")
    @NotBlank(message = "{address.contactPhone.invalid}")
    @Size(max = 30, message = "{Size.phoneNumber}")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean isNpsConsent() {
        return npsConsent;
    }

    public void setNpsConsent(boolean npsConsent) {
        this.npsConsent = npsConsent;
    }

    /**
     * Create a new instance of {@code B2EForm}
     */
    public B2EAddressForm() {
        setTermsOfUse(true);

    }

}
