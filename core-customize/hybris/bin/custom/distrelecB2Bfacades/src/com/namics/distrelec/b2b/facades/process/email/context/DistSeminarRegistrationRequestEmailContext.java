/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistSeminarRegistrationRequestProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

import java.util.Date;

/**
 * DistSeminarRegistrationRequestEmailContext
 * 
 * 
 * @author nbenothman, Distrelec
 * @since Distrelec 1.0
 */
public class DistSeminarRegistrationRequestEmailContext extends CustomerEmailContext {

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
    private Date seminarDate;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof DistSeminarRegistrationRequestProcessModel) {
            final DistSeminarRegistrationRequestProcessModel seminarRegistrationRequestProcess = (DistSeminarRegistrationRequestProcessModel) businessProcessModel;

            // Seminar Request Email
            if (!emailPageModel.getUid().equals("SeminarRegistrationRequestConfirmationEmail")) {
                put(EMAIL, getSeminarRegistrationEmail());
                put(FROM_EMAIL, seminarRegistrationRequestProcess.getCustomer().getContactEmail());
                put(FROM_DISPLAY_NAME, seminarRegistrationRequestProcess.getCustomer().getName());
            }

            setTopic(seminarRegistrationRequestProcess.getTopic());
            setSeminarDate(seminarRegistrationRequestProcess.getSeminarDate());
            setCompanyName(seminarRegistrationRequestProcess.getCompanyName());
            setCustomerNumber(seminarRegistrationRequestProcess.getCustomerNumber());
            setFirstName(seminarRegistrationRequestProcess.getFirstName());
            setLastName(seminarRegistrationRequestProcess.getLastName());
            setSalutation(seminarRegistrationRequestProcess.getSalutation());
            setStreet(seminarRegistrationRequestProcess.getStreet());
            setNumber(seminarRegistrationRequestProcess.getNumber());
            setPobox(seminarRegistrationRequestProcess.getPobox());
            setZip(seminarRegistrationRequestProcess.getZip());
            setPlace(seminarRegistrationRequestProcess.getPlace());
            setCountry(seminarRegistrationRequestProcess.getCountry());
            setDirectPhone(seminarRegistrationRequestProcess.getDirectPhone());
            setFax(seminarRegistrationRequestProcess.getFax());
            setDepartment(seminarRegistrationRequestProcess.getDepartment());
            seteMail(seminarRegistrationRequestProcess.getEMail());
            setComment(seminarRegistrationRequestProcess.getComment());
        }
    }

    private String getSeminarRegistrationEmail() {
        return getEmail(DistConstants.PropKey.Email.SEMINAR_REGISTRATION_REQUEST_EMAIL_PREFIX,
                DistConstants.PropKey.Email.SEMINAR_REGISTRATION_REQUEST_EMAIL_DEFAULT);
    }

    /* Getters & Setters */

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

    public Date getSeminarDate() {
        return seminarDate;
    }

    public void setSeminarDate(final Date date) {
        this.seminarDate = date;
    }
}
