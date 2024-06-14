/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * {@code EducationRegistrationForm}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class EducationRegistrationForm {

    // Company data
    private String institutionName;
    private String institutionAddress1;
    private String institutionAddress2;
    private String institutionZip;
    private String institutionPlace;
    private String institutionCountry;
    private String institutionPhoneNumber;
    private String institutionEmail;
    private String institutionEmailRepeat;

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
    private String contactEmailRepeat;

    // Files
    private CommonsMultipartFile eTalent2014File;
    private CommonsMultipartFile studyExchangeFile;
    private CommonsMultipartFile motivationFile;

    @NotBlank
    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(final String institutionName) {
        this.institutionName = institutionName;
    }

    @NotBlank
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

    @NotBlank
    @DataFormInput(patternKey = "register.postalCode.validationPattern", message = "{register.postalCode.validationMessage}")
    public String getInstitutionZip() {
        return institutionZip;
    }

    public void setInstitutionZip(final String institutionZip) {
        this.institutionZip = institutionZip;
    }

    @NotBlank
    public String getInstitutionPlace() {
        return institutionPlace;
    }

    public void setInstitutionPlace(final String institutionPlace) {
        this.institutionPlace = institutionPlace;
    }

    @NotBlank
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

    @Email
    @NotBlank
    public String getInstitutionEmail() {
        return institutionEmail;
    }

    public void setInstitutionEmail(final String institutionEmail) {
        this.institutionEmail = institutionEmail;
    }

    @Email
    @NotBlank
    public String getInstitutionEmailRepeat() {
        return institutionEmailRepeat;
    }

    public void setInstitutionEmailRepeat(final String institutionEmailRepeat) {
        this.institutionEmailRepeat = institutionEmailRepeat;
    }

    @NotBlank
    public String getProfileArea() {
        return profileArea;
    }

    public void setProfileArea(final String profileArea) {
        this.profileArea = profileArea;
    }

    @NotBlank
    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(final String firstName) {
        this.contactFirstName = firstName;
    }

    @NotBlank
    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(final String lastName) {
        this.contactLastName = lastName;
    }

    @NotBlank
    public String getContactAddress1() {
        return contactAddress1;
    }

    public void setContactAddress1(final String addressLine1) {
        this.contactAddress1 = addressLine1;
    }

    public String getContactAddress2() {
        return contactAddress2;
    }

    public void setContactAddress2(final String addressLine2) {
        this.contactAddress2 = addressLine2;
    }

    @NotBlank
    public String getContactPlace() {
        return contactPlace;
    }

    public void setContactPlace(final String city) {
        this.contactPlace = city;
    }

    @NotBlank
    public String getContactCountry() {
        return contactCountry;
    }

    public void setContactCountry(final String country) {
        this.contactCountry = country;
    }

    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(final String phone) {
        this.contactPhoneNumber = phone;
    }

    public String getContactMobileNumber() {
        return contactMobileNumber;
    }

    public void setContactMobileNumber(final String mobilePhone) {
        this.contactMobileNumber = mobilePhone;
    }

    @Email
    @NotBlank
    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(final String email) {
        this.contactEmail = email;
    }

    @Email
    @NotBlank
    public String getContactEmailRepeat() {
        return contactEmailRepeat;
    }

    public void setContactEmailRepeat(final String confirmEmail) {
        this.contactEmailRepeat = confirmEmail;
    }

    @NotBlank
    @DataFormInput(patternKey = "register.postalCode.validationPattern", message = "{register.postalCode.validationMessage}")
    public String getContactZip() {
        return contactZip;
    }

    public void setContactZip(final String contactZip) {
        this.contactZip = contactZip;
    }

    public CommonsMultipartFile geteTalent2014File() {
        return eTalent2014File;
    }

    public void seteTalent2014File(final CommonsMultipartFile eTalent2014File) {
        this.eTalent2014File = eTalent2014File;
    }

    public CommonsMultipartFile getStudyExchangeFile() {
        return studyExchangeFile;
    }

    public void setStudyExchangeFile(final CommonsMultipartFile studyExchangeFile) {
        this.studyExchangeFile = studyExchangeFile;
    }

    public CommonsMultipartFile getMotivationFile() {
        return motivationFile;
    }

    public void setMotivationFile(final CommonsMultipartFile motivationFile) {
        this.motivationFile = motivationFile;
    }
}
