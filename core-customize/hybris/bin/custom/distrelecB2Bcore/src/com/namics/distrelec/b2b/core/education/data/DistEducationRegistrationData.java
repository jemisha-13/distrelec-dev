/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.education.data;

import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFile;
import de.hybris.platform.acceleratorservices.dataexport.generic.output.csv.DelimitedFileMethod;

/**
 * {@code DistEducationRegistrationData}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
@DelimitedFile(delimiter = ";")
public class DistEducationRegistrationData {

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

    public String getProfileArea() {
        return profileArea;
    }

    @DelimitedFileMethod(position = 1, name = "Education Area")
    public void setProfileArea(final String profileArea) {
        this.profileArea = profileArea;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    @DelimitedFileMethod(position = 2, name = "First Name")
    public void setContactFirstName(final String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    @DelimitedFileMethod(position = 3, name = "Last Name")
    public void setContactLastName(final String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public String getContactAddress1() {
        return contactAddress1;
    }

    @DelimitedFileMethod(position = 4, name = "Contact Address 1")
    public void setContactAddress1(final String contactAddress1) {
        this.contactAddress1 = contactAddress1;
    }

    public String getContactAddress2() {
        return contactAddress2;
    }

    @DelimitedFileMethod(position = 5, name = "Contact Address 2")
    public void setContactAddress2(final String contactAddress2) {
        this.contactAddress2 = contactAddress2;
    }

    public String getContactZip() {
        return contactZip;
    }

    @DelimitedFileMethod(position = 6, name = "Contact PostalCode")
    public void setContactZip(final String contactZip) {
        this.contactZip = contactZip;
    }

    public String getContactPlace() {
        return contactPlace;
    }

    @DelimitedFileMethod(position = 7, name = "Contact City ")
    public void setContactPlace(final String contactPlace) {
        this.contactPlace = contactPlace;
    }

    public String getContactCountry() {
        return contactCountry;
    }

    @DelimitedFileMethod(position = 8, name = "Contact Country ")
    public void setContactCountry(final String contactCountry) {
        this.contactCountry = contactCountry;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    @DelimitedFileMethod(position = 9, name = "Contact Phone Number ")
    public void setContactPhoneNumber(final String contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactMobileNumber() {
        return contactMobileNumber;
    }

    @DelimitedFileMethod(position = 10, name = "Contact Mobile Number ")
    public void setContactMobileNumber(final String contactMobileNumber) {
        this.contactMobileNumber = contactMobileNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    @DelimitedFileMethod(position = 11, name = "Contact Email")
    public void setContactEmail(final String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    @DelimitedFileMethod(position = 12, name = "Institution Name")
    public void setInstitutionName(final String institutionName) {
        this.institutionName = institutionName;
    }

    public String getInstitutionAddress1() {
        return institutionAddress1;
    }

    @DelimitedFileMethod(position = 13, name = "Institution Address 1")
    public void setInstitutionAddress1(final String institutionAddress1) {
        this.institutionAddress1 = institutionAddress1;
    }

    public String getInstitutionAddress2() {
        return institutionAddress2;
    }

    @DelimitedFileMethod(position = 14, name = "Institution Address 2")
    public void setInstitutionAddress2(final String institutionAddress2) {
        this.institutionAddress2 = institutionAddress2;
    }

    public String getInstitutionZip() {
        return institutionZip;
    }

    @DelimitedFileMethod(position = 15, name = "Institution ZipCode")
    public void setInstitutionZip(final String institutionZip) {
        this.institutionZip = institutionZip;
    }

    public String getInstitutionPlace() {
        return institutionPlace;
    }

    @DelimitedFileMethod(position = 16, name = "Institution City")
    public void setInstitutionPlace(final String institutionPlace) {
        this.institutionPlace = institutionPlace;
    }

    public String getInstitutionCountry() {
        return institutionCountry;
    }

    @DelimitedFileMethod(position = 17, name = "Institution Country")
    public void setInstitutionCountry(final String institutionCountry) {
        this.institutionCountry = institutionCountry;
    }

    public String getInstitutionPhoneNumber() {
        return institutionPhoneNumber;
    }

    @DelimitedFileMethod(position = 18, name = "Institution Phone Number")
    public void setInstitutionPhoneNumber(final String institutionPhoneNumber) {
        this.institutionPhoneNumber = institutionPhoneNumber;
    }

    public String getInstitutionEmail() {
        return institutionEmail;
    }

    @DelimitedFileMethod(position = 19, name = "Institution Email")
    public void setInstitutionEmail(final String institutionEmail) {
        this.institutionEmail = institutionEmail;
    }
}
