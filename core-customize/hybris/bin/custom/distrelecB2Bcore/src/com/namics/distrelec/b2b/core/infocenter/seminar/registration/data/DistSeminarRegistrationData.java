/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.infocenter.seminar.registration.data;

import java.util.Date;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;

@DelimitedFile(delimiter = ";")
public class DistSeminarRegistrationData {
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

    /* Getters & Setters */

    public String getTopic() {
        return topic;
    }

    @DelimitedFileMethod(position = 1, name = "Seminar Topic")
    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public Date getDate() {
        return date;
    }

    @DelimitedFileMethod(position = 18, name = "Seminar Date")
    public void setDate(final Date date) {
        this.date = date;
    }

    public String getCompanyName() {
        return companyName;
    }

    @DelimitedFileMethod(position = 2, name = "Company Name")
    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    @DelimitedFileMethod(position = 3, name = "Customer number")
    public void setCustomerNumber(final String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getSalutation() {
        return salutation;
    }

    @DelimitedFileMethod(position = 4, name = "Salutation")
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    public String getFirstName() {
        return firstName;
    }

    @DelimitedFileMethod(position = 5, name = "Firstname")
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    @DelimitedFileMethod(position = 6, name = "Lastname")
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @DelimitedFileMethod(position = 7, name = "Department")
    public void setDepartment(final String department) {
        this.department = department;
    }

    public String getStreet() {
        return street;
    }

    @DelimitedFileMethod(position = 8, name = "Street")
    public void setStreet(final String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    @DelimitedFileMethod(position = 9, name = "Number")
    public void setNumber(final String number) {
        this.number = number;
    }

    public String getPobox() {
        return pobox;
    }

    @DelimitedFileMethod(position = 10, name = "PO Box")
    public void setPobox(final String pobox) {
        this.pobox = pobox;
    }

    public String getZip() {
        return zip;
    }

    @DelimitedFileMethod(position = 11, name = "ZIP")
    public void setZip(final String zip) {
        this.zip = zip;
    }

    public String getPlace() {
        return place;
    }

    @DelimitedFileMethod(position = 12, name = "Place")
    public void setPlace(final String place) {
        this.place = place;
    }

    public String getCountry() {
        return country;
    }

    @DelimitedFileMethod(position = 13, name = "Country")
    public void setCountry(final String country) {
        this.country = country;
    }

    public String getDirectPhone() {
        return directPhone;
    }

    @DelimitedFileMethod(position = 14, name = "Directphone")
    public void setDirectPhone(final String directPhone) {
        this.directPhone = directPhone;
    }

    public String geteMail() {
        return eMail;
    }

    @DelimitedFileMethod(position = 15, name = "E-Mail")
    public void seteMail(final String eMail) {
        this.eMail = eMail;
    }

    public String getFax() {
        return fax;
    }

    @DelimitedFileMethod(position = 16, name = "Fax")
    public void setFax(final String fax) {
        this.fax = fax;
    }

    public String getComment() {
        return comment;
    }

    @DelimitedFileMethod(position = 17, name = "Comment")
    public void setComment(final String comment) {
        this.comment = comment;
    }
}
