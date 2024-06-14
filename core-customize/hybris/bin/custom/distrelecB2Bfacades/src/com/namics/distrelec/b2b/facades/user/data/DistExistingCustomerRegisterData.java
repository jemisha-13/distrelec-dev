package com.namics.distrelec.b2b.facades.user.data;

import com.namics.distrelec.b2b.core.enums.RegistrationType;

import de.hybris.platform.commerceservices.enums.CustomerType;

/**
 * Registration DTO for an existing customer
 */
public class DistExistingCustomerRegisterData {

    private String customerId;

    private String contactId;

    private String login;

    private String password;

    private String titleCode;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String mobileNumber;

    private String faxNumber;

    private String companyName;

    private String functionCode;

    private String vatId;

    private String organizationalNumber;

    private boolean newsletterOption;

    private boolean phoneMarketingOption;

    private boolean npsConsent;

    private RegistrationType registrationType;

    private CustomerType customerType;

    private String erpContactId;

    private String invoiceEmail;

    private String duns;

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

    private boolean isMarketingCookieEnabled;

    private String countryCode;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((login == null) ? 0 : login.hashCode());
        result = prime * result + ((password == null) ? 0 : login.hashCode());
        result = prime * result + ((titleCode == null) ? 0 : titleCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DistExistingCustomerRegisterData other = (DistExistingCustomerRegisterData) obj;
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        } else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!lastName.equals(other.lastName)) {
            return false;
        }
        if (login == null) {
            if (other.login != null) {
                return false;
            }
        } else if (!login.equals(other.login)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (titleCode == null) {
            if (other.titleCode != null) {
                return false;
            }
        } else if (!titleCode.equals(other.titleCode)) {
            return false;
        }
        return true;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(final String contactId) {
        this.contactId = contactId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(final String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(final String functionCode) {
        this.functionCode = functionCode;
    }

    public String getVatId() {
        return vatId;
    }

    public void setVatId(final String vatId) {
        this.vatId = vatId;
    }

    public String getOrganizationalNumber() {
        return organizationalNumber;
    }

    public void setOrganizationalNumber(final String organizationalNumber) {
        this.organizationalNumber = organizationalNumber;
    }

    public boolean isNewsletterOption() {
        return newsletterOption;
    }

    public void setNewsletterOption(final boolean newsletterOption) {
        this.newsletterOption = newsletterOption;
    }

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(final RegistrationType registrationType) {
        this.registrationType = registrationType;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(final CustomerType customerType) {
        this.customerType = customerType;
    }

    public boolean isPhoneMarketingOption() {
        return phoneMarketingOption;
    }

    public void setPhoneMarketingOption(final boolean phoneMarketingOption) {
        this.phoneMarketingOption = phoneMarketingOption;
    }

    public String getErpContactId() {
        return erpContactId;
    }

    public void setErpContactId(final String erpContactId) {
        this.erpContactId = erpContactId;
    }

    public String getInvoiceEmail() {
        return invoiceEmail;
    }

    public void setInvoiceEmail(final String invoiceEmail) {
        this.invoiceEmail = invoiceEmail;
    }

    public boolean isNpsConsent() {
        return npsConsent;
    }

    public void setNpsConsent(final boolean npsConsent) {
        this.npsConsent = npsConsent;
    }

    public boolean isSelectAllemailConsents() {
        return selectAllemailConsents;
    }

    public void setSelectAllemailConsents(final boolean selectAllemailConsents) {
        this.selectAllemailConsents = selectAllemailConsents;
    }

    public boolean isSaleAndClearanceConsent() {
        return saleAndClearanceConsent;
    }

    public void setSaleAndClearanceConsent(final boolean saleAndClearanceConsent) {
        this.saleAndClearanceConsent = saleAndClearanceConsent;
    }

    public boolean isKnowHowConsent() {
        return knowHowConsent;
    }

    public void setKnowHowConsent(final boolean knowHowConsent) {
        this.knowHowConsent = knowHowConsent;
    }

    public boolean isObsolescenceConsent() {
        return obsolescenceConsent;
    }

    public void setObsolescenceConsent(final boolean obsolescenceConsent) {
        this.obsolescenceConsent = obsolescenceConsent;
    }

    public boolean isSmsConsent() {
        return smsConsent;
    }

    public void setSmsConsent(final boolean smsConsent) {
        this.smsConsent = smsConsent;
    }

    public boolean isPhoneConsent() {
        return phoneConsent;
    }

    public void setPhoneConsent(final boolean phoneConsent) {
        this.phoneConsent = phoneConsent;
    }

    public boolean isPostConsent() {
        return postConsent;
    }

    public void setPostConsent(final boolean postConsent) {
        this.postConsent = postConsent;
    }

    public boolean isPersonalisationConsent() {
        return personalisationConsent;
    }

    public void setPersonalisationConsent(final boolean personalisationConsent) {
        this.personalisationConsent = personalisationConsent;
    }

    public boolean isProfilingConsent() {
        return profilingConsent;
    }

    public void setProfilingConsent(final boolean profilingConsent) {
        this.profilingConsent = profilingConsent;
    }

    public boolean isNewsLetterConsent() {
        return newsLetterConsent;
    }

    public void setNewsLetterConsent(final boolean newsLetterConsent) {
        this.newsLetterConsent = newsLetterConsent;
    }

    public boolean isTermsAndConditionsConsent() {
        return termsAndConditionsConsent;
    }

    public void setTermsAndConditionsConsent(final boolean termsAndConditionsConsent) {
        this.termsAndConditionsConsent = termsAndConditionsConsent;
    }

    public boolean isPersonalisedRecommendationConsent() {
        return personalisedRecommendationConsent;
    }

    public void setPersonalisedRecommendationConsent(final boolean personalisedRecommendationConsent) {
        this.personalisedRecommendationConsent = personalisedRecommendationConsent;
    }

    public boolean isCustomerSurveysConsent() {
        return customerSurveysConsent;
    }

    public void setCustomerSurveysConsent(final boolean customerSurveysConsent) {
        this.customerSurveysConsent = customerSurveysConsent;
    }

    public boolean isMarketingCookieEnabled() {
        return isMarketingCookieEnabled;
    }

    public void setMarketingCookieEnabled(final boolean marketingCookieEnabled) {
        isMarketingCookieEnabled = marketingCookieEnabled;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDuns() {
        return duns;
    }

    public void setDuns(String duns) {
        this.duns = duns;
    }
}
