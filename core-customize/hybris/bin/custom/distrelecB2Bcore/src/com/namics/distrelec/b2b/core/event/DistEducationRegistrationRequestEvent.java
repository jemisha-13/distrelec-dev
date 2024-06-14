/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

/**
 * {@code DistEducationRegistrationRequestEvent}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 2.0
 */
public class DistEducationRegistrationRequestEvent extends AbstractDistrelecCustomerEvent<BaseSiteModel> {

    // Company data
    private String institutionName;
    private String institutionAddress1;
    private String institutionAddress2;
    private String institutionZip;
    private String institutionPlace;
    private String institutionCountry;
    private String institutionPhoneNumber;
    private String institutionEmail;

    // Personal Data
    private String profileArea;
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

    // Getters & Setters

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionAddress1() {
        return institutionAddress1;
    }

    public void setInstitutionAddress1(String institutionAddress1) {
        this.institutionAddress1 = institutionAddress1;
    }

    public String getInstitutionAddress2() {
        return institutionAddress2;
    }

    public void setInstitutionAddress2(String institutionAddress2) {
        this.institutionAddress2 = institutionAddress2;
    }

    public String getInstitutionZip() {
        return institutionZip;
    }

    public void setInstitutionZip(String institutionZip) {
        this.institutionZip = institutionZip;
    }

    public String getInstitutionPlace() {
        return institutionPlace;
    }

    public void setInstitutionPlace(String institutionPlace) {
        this.institutionPlace = institutionPlace;
    }

    public String getInstitutionCountry() {
        return institutionCountry;
    }

    public void setInstitutionCountry(String institutionCountry) {
        this.institutionCountry = institutionCountry;
    }

    public String getInstitutionPhoneNumber() {
        return institutionPhoneNumber;
    }

    public void setInstitutionPhoneNumber(String institutionPhoneNumber) {
        this.institutionPhoneNumber = institutionPhoneNumber;
    }

    public String getInstitutionEmail() {
        return institutionEmail;
    }

    public void setInstitutionEmail(String institutionEmail) {
        this.institutionEmail = institutionEmail;
    }

    public String getProfileArea() {
        return profileArea;
    }

    public void setProfileArea(String profileArea) {
        this.profileArea = profileArea;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactAddress1() {
        return contactAddress1;
    }

    public void setContactAddress1(String contactAddress1) {
        this.contactAddress1 = contactAddress1;
    }

    public String getContactAddress2() {
        return contactAddress2;
    }

    public void setContactAddress2(String contactAddress2) {
        this.contactAddress2 = contactAddress2;
    }

    public String getContactZip() {
        return contactZip;
    }

    public void setContactZip(String contactZip) {
        this.contactZip = contactZip;
    }

    public String getContactPlace() {
        return contactPlace;
    }

    public void setContactPlace(String contactPlace) {
        this.contactPlace = contactPlace;
    }

    public String getContactCountry() {
        return contactCountry;
    }

    public void setContactCountry(String contactCountry) {
        this.contactCountry = contactCountry;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactMobileNumber() {
        return contactMobileNumber;
    }

    public void setContactMobileNumber(String contactMobileNumber) {
        this.contactMobileNumber = contactMobileNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
}
