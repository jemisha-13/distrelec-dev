/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.forms;


import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;


import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;
import com.namics.distrelec.b2b.storefront.validation.annotations.EqualAttributes;
import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;


/**
 * Form object for registration. https://wiki.namics.com/display/distrelint/C112-RegisterAddressForm
 */
@EqualAttributes(message = "{validation.checkPwd.equals}", value = { "pwd", "checkPwd" })
public class RegisterForm implements NewsletterForm {

    private String titleCode;
    private String firstName;
    private String lastName;
    private String codiceFiscale;
    private String email;
    private String pwd;
    private String checkPwd;
    private String customerId;
    private String additionalAddress;
    private String countryCode;
    private String currencyCode;
    private String phoneNumber;
    private String mobileNumber;
    private String telephoneNumber;
    private String faxNumber;
    private String invoiceEmail;
    
    private boolean termsOfUseOption;
    private boolean marketingConsent;
    private boolean npsConsent;
    private String customerType;
    private boolean existingCustomer;
    private String vat4;
    private String legalEmail;
    private String registrationType;
    private boolean selectAllemailConsents;
    private boolean saleAndClearanceConsent;
    private boolean knowHowConsent;
    private boolean obsolescenceConsent;
    private boolean smsConsent;
    private boolean phoneConsent;
    private boolean postConsent;
    private boolean personalisationConsent;
    private boolean profilingConsent;
    private boolean newsLetterConsent;
    private boolean termsAndConditionsConsent;
    private boolean personalisedRecommendationConsent;
	private boolean customerSurveysConsent;
    
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

    @NotNull(message = "{register.checkPwd.invalid}")
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


    @Size(max = 3, message = "{register.country.invalid}")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }


    @Size(max = 3, message = "{register.currency.invalid}")
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Size(max = 30, message = "{Size.phoneNumber}")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @Phonenumber(message = "{register.faxNumber.invalid}", checkForCurrentCountry = true)
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

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public boolean getExistingCustomer() {
        return existingCustomer;
    }

    public void setExistingCustomer(boolean existingCustomer) {
        this.existingCustomer = existingCustomer;
    }
    
    @Size(max = 241, message = "{register.email.invalid}")
    @Email(message = "{register.email.invalid}")
    public String getInvoiceEmail() {
        return invoiceEmail;
    }

    public void setInvoiceEmail(String invoiceEmail) {
        this.invoiceEmail = invoiceEmail;
    }
    
    @Size(max = 7, message = "{vat.reg.label}")
    public String getVat4() {
        return vat4;
    }

    public void setVat4(String vat4) {
        this.vat4 = vat4;
    }

    @Size(max = 241, message = "{register.email.invalid}" )
    @Email(message = "{register.email.invalid}")
    public String getLegalEmail() {
        return legalEmail;
    }

    public void setLegalEmail(String legalEmail) {
        this.legalEmail = legalEmail;
    }

	public String getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}
	
	@Phonenumber(message = "{register.phoneNumber.invalid}")
    @NotBlank(message = "{register.phoneNumber.invalid}")
    public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

    public boolean isNpsConsent() {
        return npsConsent;
    }

    public void setNpsConsent(boolean npsConsent) {
        this.npsConsent = npsConsent;
    }

    public boolean isSelectAllemailConsents() {
        return selectAllemailConsents;
    }

    public void setSelectAllemailConsents(boolean selectAllemailConsents) {
        this.selectAllemailConsents = selectAllemailConsents;
    }

    public boolean isSaleAndClearanceConsent() {
        return saleAndClearanceConsent;
    }

    public void setSaleAndClearanceConsent(boolean saleAndClearanceConsent) {
        this.saleAndClearanceConsent = saleAndClearanceConsent;
    }

    public boolean isKnowHowConsent() {
        return knowHowConsent;
    }

    public void setKnowHowConsent(boolean knowHowConsent) {
        this.knowHowConsent = knowHowConsent;
    }

    public boolean isObsolescenceConsent() {
        return obsolescenceConsent;
    }

    public void setObsolescenceConsent(boolean obsolescenceConsent) {
        this.obsolescenceConsent = obsolescenceConsent;
    }

    public boolean isSmsConsent() {
        return smsConsent;
    }

    public void setSmsConsent(boolean smsConsent) {
        this.smsConsent = smsConsent;
    }

    public boolean isPhoneConsent() {
        return phoneConsent;
    }

    public void setPhoneConsent(boolean phoneConsent) {
        this.phoneConsent = phoneConsent;
    }

    public boolean isPostConsent() {
        return postConsent;
    }

    public void setPostConsent(boolean postConsent) {
        this.postConsent = postConsent;
    }

    public boolean isPersonalisationConsent() {
        return personalisationConsent;
    }

    public void setPersonalisationConsent(boolean personalisationConsent) {
        this.personalisationConsent = personalisationConsent;
    }

    public boolean isProfilingConsent() {
        return profilingConsent;
    }

    public void setProfilingConsent(boolean profilingConsent) {
        this.profilingConsent = profilingConsent;
    }

    public boolean isNewsLetterConsent() {
        return newsLetterConsent;
    }

    public void setNewsLetterConsent(boolean newsLetterConsent) {
        this.newsLetterConsent = newsLetterConsent;
    }

    public boolean isTermsAndConditionsConsent() {
        return termsAndConditionsConsent;
    }

    public void setTermsAndConditionsConsent(boolean termsAndConditionsConsent) {
        this.termsAndConditionsConsent = termsAndConditionsConsent;
    }

	public boolean isPersonalisedRecommendationConsent() {
		return personalisedRecommendationConsent;
	}

	public void setPersonalisedRecommendationConsent(boolean personalisedRecommendationConsent) {
		this.personalisedRecommendationConsent = personalisedRecommendationConsent;
	}

	public boolean isCustomerSurveysConsent() {
		return customerSurveysConsent;
	}

	public void setCustomerSurveysConsent(boolean customerSurveysConsent) {
		this.customerSurveysConsent = customerSurveysConsent;
	}
	
    
	
    
}
