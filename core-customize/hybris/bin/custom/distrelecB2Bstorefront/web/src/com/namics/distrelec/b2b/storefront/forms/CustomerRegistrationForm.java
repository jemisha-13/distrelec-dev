package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualAttributes(message = "{validation.checkPwd.equals}", value = { "pwd", "checkPwd" })
@AtLeastOneNotBlank(message = "{register.onePhone.required}", value = { "phoneNumber", "mobileNumber" })
public class CustomerRegistrationForm implements B2BNewsletterForm {

    private String customerType;
    private boolean isExisting;
    private String company;
    private String company2;
    private String company3;
    private String functionCode;
    private String vatId;
    private String organizationalNumber;
    private String titleCode;
    private String firstName;
    private String lastName;
    private String codiceFiscale;
    private String email;
    private String pwd;
    private String checkPwd;
    private String customerId;
    private String additionalAddress;
    private String streetName;
    private String streetNumber;
    private String poBox;
    private String postalCode;
    private String town;
    private String countryCode;
    private String regionCode;
    private String currencyCode;
    private String phoneNumber;
    private String mobileNumber;
    private String faxNumber;

    private boolean termsOfUseOption;
    private boolean marketingConsent;
    private boolean npsConsent;

    @CustomerType(validCustomerTypes = { "B2B", "B2C" }, message = "Invalid customer type")
    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(final String customerType) {
        this.customerType = customerType;
    }

    public boolean isExisting() {
        return isExisting;
    }

    public void setExisting(boolean isExisting) {
        this.isExisting = isExisting;
    }

    @Size(max = 35, message = "{register.company2.invalid}")
    public String getCompany2() {
        return company2;
    }

    public void setCompany2(final String company2) {
        this.company2 = company2;
    }

    @Size(max = 35, message = "{register.company3.invalid}")
    public String getCompany3() {
        return company3;
    }

    public void setCompany3(final String company3) {
        this.company3 = company3;
    }

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
    @Size(max = 40, message = "{register.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @Override
    @NotBlank(message = "{register.lastName.invalid}")
    @Size(max = 40, message = "{register.lastName.invalid}")
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
    @Size(max = 241, message = "{register.email.invalid}")
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

    @DataFormInput(message = "{register.customerId.invalid}", patternKey = "register.customerId.validationPattern")
    @Size(max = 10, message = "{register.customerId.invalid}")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    @Size(max = 35, message = "{register.additionalAddress.invalid}")
    public String getAdditionalAddress() {
        return additionalAddress;
    }

    public void setAdditionalAddress(final String additionalAddress) {
        this.additionalAddress = additionalAddress;
    }

    @NotBlank(message = "{register.strName.invalid}")
    @Size(max = 60, message = "{Size.strName}")
    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(final String streetName) {
        this.streetName = streetName;
    }

    @NotBlank(message = "{register.strNumber.invalid}")
    @Size(max = 10, message = "{Size.strNumber}")
    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(final String streetNumber) {
        this.streetNumber = streetNumber;
    }

    @Size(max = 10, message = "{register.poBox.invalid}")
    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(final String poBox) {
        this.poBox = poBox;
    }

    @NotBlank(message = "{register.postalCode.invalid}")
    @Size(max = 10, message = "{register.postalCode.invalid}")
    @DataFormInput(message = "{register.postalCode.validationMessage}", patternKey = "register.postalCode.validationPattern")
    public String getPostalCode() {
        return postalCode != null ? postalCode.trim() : postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    @NotBlank(message = "{register.town.invalid}")
    @Size(max = 40, message = "{register.town.invalid}")
    public String getTown() {
        return town;
    }

    public void setTown(final String town) {
        this.town = town;
    }

    @Size(max = 3, message = "{register.country.invalid}")
    @NotNull(message = "{register.country.invalid}")
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

    @Size(max = 3, message = "{register.currency.invalid}")
    @NotNull(message = "{register.currency.invalid}")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Phonenumber(message = "{register.mobileNumber.invalid}")
    @Size(max = 30, message = "{Size.phoneNumber}")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Phonenumber(message = "{register.phoneNumber.invalid}")
    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Phonenumber(message = "{register.faxNumber.invalid}")
    @Size(max = 30, message = "{Size.faxNumber}")
    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String faxNumber) {
        this.faxNumber = faxNumber;
    }

    @AssertTrue(message = "{register.tOfUse.invalid}")
    public boolean isTermsOfUseOption() {
        return termsOfUseOption;
    }

    public void setTermsOfUseOption(final boolean termsOfUseOption) {
        this.termsOfUseOption = termsOfUseOption;
    }

    @Override
    public boolean isMarketingConsent() {
        return marketingConsent;
    }

    public void setMarketingConsent(final boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }

    @Size(max = 35, message = "{register.company.invalid}")
    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    @Override
    @Size(max = 2, message = "{register.function.invalid}")
    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(final String functionCode) {
        this.functionCode = functionCode;
    }

    // DISTRELEC-10999
    @Size(max = 30, message = "{register.vatId.invalid}")
    // @DataFormInput(message = "{register.vatId.validationMessage}", patternKey = "register.vatId.validationPattern")
    public String getVatId() {
        return vatId;
    }

    public void setVatId(final String vatId) {
        this.vatId = vatId;
    }

    // DISTRELEC-10999
    // @DataFormInput(message = "{register.organizationalNumber.invalid}", patternKey = "register.organizationalNumber.pattern")
    public String getOrganizationalNumber() {
        return organizationalNumber;
    }

    public void setOrganizationalNumber(final String organizationalNumber) {
        this.organizationalNumber = organizationalNumber;
    }

    public boolean isNpsConsent() {
        return npsConsent;
    }

    public void setNpsConsent(boolean npsConsent) {
        this.npsConsent = npsConsent;
    }
    
}
