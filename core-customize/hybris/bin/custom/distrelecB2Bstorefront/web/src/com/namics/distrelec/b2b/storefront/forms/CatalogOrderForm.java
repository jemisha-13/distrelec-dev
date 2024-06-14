/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class CatalogOrderForm {
    private String catalog;
    private String companyName;
    private String companyName2;
    private String salutation;
    private String firstName;
    private String lastName;
    private String department;
    private String street;
    private String number;
    private String pobox;
    private String zip;
    private String place;
    private String country;
    private String directPhone;
    private String mobile;
    private String eMail;
    private String fax;
    private String comment;

    // @NotBlank(message = "{catalog.order.catalog.invalid}")
    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(final String catalog) {
        this.catalog = catalog;
    }

    @Size(max = 35)
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    @Size(max = 35)
    public String getCompanyName2() {
        return companyName2;
    }

    public void setCompanyName2(final String companyName2) {
        this.companyName2 = companyName2;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    @NotBlank(message = "{catalog.order.firstname.invalid}")
    @Size(max = 40, message = "{catalog.order.firstname.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @NotBlank(message = "{catalog.order.lastname.invalid}")
    @Size(max = 40, message = "{catalog.order.lastname.invalid}")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Size(max = 35)
    public String getDepartment() {
        return department;
    }

    public void setDepartment(final String department) {
        this.department = department;
    }

    @NotBlank(message = "{catalog.order.street.invalid}")
    @Size(max = 60, message = "{catalog.order.street.invalid}")
    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @NotBlank(message = "{catalog.order.number.invalid}")
    @Size(max = 10, message = "{catalog.order.number.invalid}")
    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    @Size(max = 35)
    public String getPobox() {
        return pobox;
    }

    public void setPobox(final String pobox) {
        this.pobox = pobox;
    }

    @NotBlank(message = "{catalog.order.zip.invalid}")
    @Size(max = 10, message = "{catalog.order.zip.invalid}")
    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    @NotBlank(message = "{catalog.order.place.invalid}")
    @Size(max = 40, message = "{catalog.order.place.invalid}")
    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    @NotBlank(message = "{catalog.order.country.invalid}")
    @Size(max = 3, message = "{register.country.invalid}")
    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    @Size(max = 30, message = "{catalog.order.directphone.invalid}")
    public String getDirectPhone() {
        return directPhone;
    }

    public void setDirectPhone(final String directPhone) {
        this.directPhone = directPhone;
    }

    @Size(max = 30, message = "{catalog.order.mobile.invalid}")
    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    @NotBlank(message = "{catalog.order.email.invalid}")
    @Size(max = 175, message = "{catalog.order.email.invalid}")
    @Email(message = "{catalog.order.email.invalid}")
    public String getEMail() {
        return eMail;
    }

    public void setEMail(final String eMail) {
        this.eMail = eMail;
    }

    @Size(max = 30)
    public String getFax() {
        return fax;
    }

    public void setFax(final String fax) {
        this.fax = fax;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }
}
