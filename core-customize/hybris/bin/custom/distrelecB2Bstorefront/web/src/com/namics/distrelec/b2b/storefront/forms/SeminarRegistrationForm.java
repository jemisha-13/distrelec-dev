/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Future;
import javax.validation.constraints.Size;
import java.util.Date;

public class SeminarRegistrationForm {
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

    // @NotBlank(message = "{seminar.registration.seminarId.invalid}")
    public String getSeminar() {
        return seminar;
    }

    public void setSeminar(final String seminar) {
        this.seminar = seminar;
    }

    @NotBlank(message = "{seminar.registration.topic.invalid}")
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

    @NotBlank(message = "{seminar.registration.firstname.invalid}")
    @Size(max = 35, message = "{seminar.registration.firstname.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @NotBlank(message = "{seminar.registration.lastname.invalid}")
    @Size(max = 35, message = "{seminar.registration.lastname.invalid}")
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

    @NotBlank(message = "{seminar.registration.street.invalid}")
    @Size(max = 60, message = "{seminar.registration.street.invalid}")
    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @NotBlank(message = "{seminar.registration.number.invalid}")
    @Size(max = 10, message = "{seminar.registration.number.invalid}")
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

    @NotBlank(message = "{seminar.registration.zip.invalid}")
    @Size(max = 10, message = "{seminar.registration.zip.invalid}")
    public String getZip() {
        return zip;
    }

    public void setZip(final String zip) {
        this.zip = zip;
    }

    @NotBlank(message = "{seminar.registration.place.invalid}")
    @Size(max = 60, message = "{seminar.registration.place.invalid}")
    public String getPlace() {
        return place;
    }

    public void setPlace(final String place) {
        this.place = place;
    }

    @NotBlank(message = "{seminar.registration.country.invalid}")
    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    @NotBlank(message = "{seminar.registration.directphone.invalid}")
    @Size(max = 30, message = "{seminar.registration.directphone.invalid}")
    public String getDirectPhone() {
        return directPhone;
    }

    public void setDirectPhone(final String directPhone) {
        this.directPhone = directPhone;
    }

    @NotBlank(message = "{seminar.registration.email.invalid}")
    @Size(max = 255, message = "{seminar.registration.email.invalid}")
    @Email(message = "{seminar.registration.email.invalid}")
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

    @Future
    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }
}
