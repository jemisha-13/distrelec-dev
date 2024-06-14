/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistEducationRegistrationRequestProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * {@code DistEducationRegistrationRequestEmailContext}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistEducationRegistrationRequestEmailContext extends CustomerEmailContext {

    // Personal Data
    private String educationArea;
    private String contactFirstName;
    private String contactLastName;
    private String contactAddress1;
    private String contactAddress2;
    private String contactZip;
    private String contactPlace;
    private String contactCountry;
    private String contactPhoneNumber;
    private String contactMobileNumber;
    private String contactEmail;

    // Company data
    private String institutionName;
    private String institutionAddress1;
    private String institutionAddress2;
    private String institutionZip;
    private String institutionPlace;
    private String institutionCountry;
    private String institutionPhoneNumber;
    private String institutionEmail;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof DistEducationRegistrationRequestProcessModel) {
            final DistEducationRegistrationRequestProcessModel distEducRegistRequestProcess = (DistEducationRegistrationRequestProcessModel) businessProcessModel;

            // Education Registration Request Email
            if (!emailPageModel.getUid().equals("EducationRequestConfirmationEmail")) {
                put(EMAIL, getEducationRegistrationEmail());
                put(FROM_EMAIL, distEducRegistRequestProcess.getCustomer().getContactEmail());
                put(FROM_DISPLAY_NAME, distEducRegistRequestProcess.getCustomer().getName());
            }

            setEducationArea(distEducRegistRequestProcess.getEducationArea());
            setContactFirstName(distEducRegistRequestProcess.getContactFirstName());
            setContactLastName(distEducRegistRequestProcess.getContactLastName());
            setContactAddress1(distEducRegistRequestProcess.getContactAddress1());
            setContactAddress2(distEducRegistRequestProcess.getContactAddress2());
            setContactZip(distEducRegistRequestProcess.getContactZip());
            setContactPlace(distEducRegistRequestProcess.getContactPlace());
            setContactCountry(distEducRegistRequestProcess.getContactCountry());
            setContactEmail(distEducRegistRequestProcess.getContactEmail());
            setContactPhoneNumber(distEducRegistRequestProcess.getContactPhoneNumber());
            setContactMobileNumber(distEducRegistRequestProcess.getContactMobileNumber());

            // Institution data
            setInstitutionName(distEducRegistRequestProcess.getInstitutionName());
            setInstitutionAddress1(distEducRegistRequestProcess.getInstitutionAddress1());
            setInstitutionAddress2(distEducRegistRequestProcess.getInstitutionAddress2());
            setInstitutionZip(distEducRegistRequestProcess.getInstitutionZip());
            setInstitutionPlace(distEducRegistRequestProcess.getInstitutionPlace());
            setInstitutionCountry(distEducRegistRequestProcess.getInstitutionCountry());
            setInstitutionPhoneNumber(distEducRegistRequestProcess.getInstitutionPhoneNumber());
            setInstitutionEmail(distEducRegistRequestProcess.getInstitutionEmail());

        }
    }

    protected String getEducationRegistrationEmail() {
        return getEmail(DistConstants.PropKey.Email.EDUCATION_REGISTRATION_REQUEST_EMAIL_PREFIX,
                DistConstants.PropKey.Email.EDUCATION_REGISTRATION_REQUEST_EMAIL_DEFAULT);
    }

    /* Getters & Setters */

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(final String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionAddress1() {
        return institutionAddress1;
    }

    public void setInstitutionAddress1(final String institutionAddress1) {
        this.institutionAddress1 = institutionAddress1;
    }

    public String getInstitutionAddress2() {
        return institutionAddress2;
    }

    public void setInstitutionAddress2(final String institutionAddress2) {
        this.institutionAddress2 = institutionAddress2;
    }

    public String getInstitutionZip() {
        return institutionZip;
    }

    public void setInstitutionZip(final String institutionZip) {
        this.institutionZip = institutionZip;
    }

    public String getInstitutionPlace() {
        return institutionPlace;
    }

    public void setInstitutionPlace(final String institutionPlace) {
        this.institutionPlace = institutionPlace;
    }

    public String getInstitutionCountry() {
        return institutionCountry;
    }

    public void setInstitutionCountry(final String institutionCountry) {
        this.institutionCountry = institutionCountry;
    }

    public String getInstitutionPhoneNumber() {
        return institutionPhoneNumber;
    }

    public void setInstitutionPhoneNumber(final String institutionPhoneNumber) {
        this.institutionPhoneNumber = institutionPhoneNumber;
    }

    public String getInstitutionEmail() {
        return institutionEmail;
    }

    public void setInstitutionEmail(final String institutionEmail) {
        this.institutionEmail = institutionEmail;
    }

    public String getEducationArea() {
        return educationArea;
    }

    public void setEducationArea(final String profileArea) {
        this.educationArea = profileArea;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(final String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(final String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactAddress1() {
        return contactAddress1;
    }

    public void setContactAddress1(final String contactAddress1) {
        this.contactAddress1 = contactAddress1;
    }

    public String getContactAddress2() {
        return contactAddress2;
    }

    public void setContactAddress2(final String contactAddress2) {
        this.contactAddress2 = contactAddress2;
    }

    public String getContactZip() {
        return contactZip;
    }

    public void setContactZip(final String contactZip) {
        this.contactZip = contactZip;
    }

    public String getContactPlace() {
        return contactPlace;
    }

    public void setContactPlace(final String contactPlace) {
        this.contactPlace = contactPlace;
    }

    public String getContactCountry() {
        return contactCountry;
    }

    public void setContactCountry(final String contactCountry) {
        this.contactCountry = contactCountry;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(final String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactMobileNumber() {
        return contactMobileNumber;
    }

    public void setContactMobileNumber(final String contactMobileNumber) {
        this.contactMobileNumber = contactMobileNumber;
    }

    @Override
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(final String contactEmail) {
        this.contactEmail = contactEmail;
    }

}
