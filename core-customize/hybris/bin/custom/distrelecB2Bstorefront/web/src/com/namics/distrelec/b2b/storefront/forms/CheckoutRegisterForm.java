package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualAttributes(message = "{validation.checkPwd.equals}", value = { "pwd", "checkPwd" })
@AtLeastOneNotBlank(message = "{register.onePhone.required}", value = { "phoneNumber", "mobileNumber" })
@BizContact(message = "{register.phoneNumber.invalid}", value = { "countryCode", "phoneNumber" })
@BizContact(message = "{register.mobileNumber.invalid}", value = { "countryCode", "mobileNumber" })
@BizContact(message = "{register.faxNumber.invalid}", value = { "countryCode", "fax" })
public class CheckoutRegisterForm implements NewsletterForm {

    private String titleCode;
    private String firstName;
    private String lastName;
    private String codiceFiscale;
    private String email;
    private String pwd;
    private String checkPwd;
    private String phoneNumber;
    private String mobileNumber;
    private String fax;
    private boolean marketingConsent;
    private boolean npsConsent;
    private boolean termsOfUseOption;
    private Boolean businessCustomer;
    private Boolean existingCustomer;
    private String customerId;
    private String currencyCode;
    private String countryCode;
    private String regionCode;
    
    @Override
    @NotNull(message = "{register.title.invalid}")
    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    @Override
    @NotBlank(message = "{register.firstName.invalid}")
    @Size(max = 35, message = "{register.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @Override
    @NotBlank(message = "{register.lastName.invalid}")
    @Size(max = 35, message = "{register.lastName.invalid}")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Size(max = 30, message = "{register.codiceFiscale.invalid}")
    @DataFormInput(patternKey = "register.codiceFiscale.validationPattern", message = "{register.codiceFiscale.invalid}")
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(final String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    @Override
    @NotBlank(message = "{register.email.invalid}")
    @Size(max = 175, message = "{register.email.invalid}")
    @Email(message = "{register.email.invalid}")
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @NotBlank(message = "{register.pwd.help.text}")
    @Size(min = 6, max = 255, message = "{register.pwd.help.text}")
    public String getPwd() {
        return pwd;
    }

    public void setPwd(final String pwd) {
        this.pwd = pwd;
    }

    @NotNull(message = "{register.pwd.help.text}")
    public String getCheckPwd() {
        return checkPwd;
    }

    public void setCheckPwd(final String checkPwd) {
        this.checkPwd = checkPwd;
    }

    @Phonenumber(message = "{register.pwd.help.text}")
    @Size(max = 30, message = "{Size.phoneNumber}")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(final boolean marketingConsent) {
        this.marketingConsent = marketingConsent;

    }

    public Boolean getBusinessCustomer() {
        return businessCustomer;
    }

    public void setBusinessCustomer(Boolean businessCustomer) {
        this.businessCustomer = businessCustomer;
    }
    
    public Boolean getExistingCustomer() {
        return existingCustomer;
    }

    public void setExistingCustomer(Boolean existingCustomer) {
        this.existingCustomer = existingCustomer;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public boolean isTermsOfUseOption() {
        return termsOfUseOption;
    }

    public void setTermsOfUseOption(boolean termsOfUseOption) {
        this.termsOfUseOption = termsOfUseOption;
    }

    @Phonenumber(message = "{register.phoneNumber.invalid}")
    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    
    @Phonenumber(message = "{register.faxNumber.invalid}")
    @Size(max = 30, message = "{Size.faxNumber}")
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
    
    @Size(max = 3, message = "{register.currency.invalid}")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    @Size(max = 3, message = "{register.country.invalid}")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }
    
    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(final String regionCode) {
        this.regionCode = regionCode;
    }

    public boolean isNpsConsent() {
        return npsConsent;
    }

    public void setNpsConsent(boolean npsConsent) {
        this.npsConsent = npsConsent;
    }
    
    
}
