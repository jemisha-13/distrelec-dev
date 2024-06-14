/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import java.util.Date;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

/**
 * DistSeminarRegistrationRequestEvent
 * 
 * 
 * @author nbenothman, Distrelec
 * @since Distrelec 3.0
 */
public class DistSeminarRegistrationRequestEvent extends AbstractDistrelecCustomerEvent<BaseSiteModel> {

    private String seminar;
    private String topic;
    private String companyName;
    private String customerNumber;
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
    private String eMail;
    private String fax;
    private String comment;
    private Date date;

    /* Getters and Setters */

    public String getSeminar() {
        return seminar;
    }

    public void setSeminar(final String seminar) {
        this.seminar = seminar;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(final String customerNumber) {
        this.customerNumber = customerNumber;
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

    public String geteMail() {
        return eMail;
    }

    public void seteMail(final String eMail) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

}
