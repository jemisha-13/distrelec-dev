/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

public class DistCatalogOrderEvent extends AbstractDistrelecCustomerEvent<BaseSiteModel> {

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
    private String mobile;
    private String directPhone;
    private String eMail;
    private String fax;
    private String comment;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(final String catalog) {
        this.catalog = catalog;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

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

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getPobox() {
        return pobox;
    }

    public void setPobox(final String pobox) {
        this.pobox = pobox;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getDirectPhone() {
        return directPhone;
    }

    public void setDirectPhone(final String directPhone) {
        this.directPhone = directPhone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(final String eMail) {
        this.eMail = eMail;
    }

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
