/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

/**
 * Address form for offline customers to change their address. Used in {@link AddressChangeForm}.
 * 
 * @author dsivakumaran, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class OfflineAddressForm {

    private String companyName;
    private String firstName;
    private String lastName;
    private String department;
    private String street;
    private String number;
    private String pobox;
    private String zip;
    private String place;
    private String country;

    @Size(max = 35, message = "{register.company.invalid}")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    @NotBlank(message = "{address.change.firstName.invalid}")
    @Size(max = 35, message = "{register.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @NotBlank(message = "{address.change.lastName.invalid}")
    @Size(max = 35, message = "{register.firstName.invalid}")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(final String department) {
        this.department = department;
    }

    @NotBlank(message = "{address.change.street.invalid}")
    @Size(max = 60, message = "{Size.strName}")
    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @Size(max = 10, message = "{Size.strNumber}")
    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    @Size(max = 10, message = "{register.poBox.invalid}")
    public String getPobox() {
        return pobox;
    }

    public void setPobox(final String pobox) {
        this.pobox = pobox;
    }

    @NotBlank(message = "{address.change.zip.invalid}")
    @Size(max = 10, message = "{register.postalCode.invalid}")
    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    @Size(max = 40, message = "{register.town.invalid}")
    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    @NotBlank(message = "{address.change.country.invalid}")
    @Size(max = 3, message = "{register.country.invalid}")
    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }
}
