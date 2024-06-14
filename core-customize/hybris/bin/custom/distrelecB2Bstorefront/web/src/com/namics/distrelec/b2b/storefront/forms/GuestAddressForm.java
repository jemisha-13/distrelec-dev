package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.namics.distrelec.b2b.storefront.validation.annotations.BizContact;
import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;
import com.namics.distrelec.b2b.storefront.validation.annotations.NotBlankForCountry;
import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;

@BizContact(message = "{form.phone.error.for.country}", value = { "countryIso", "contactPhone" })
@BizContact(message = "{form.phone.error.for.country}", value = { "countryIso", "mobileNumber" })
@BizContact(message = "{form.phone.error.for.country}", value = { "countryIso", "fax" })
public class GuestAddressForm extends AbstractDistAddressForm {

    private String titleCode;

    private String firstName;

    private String lastName;

    private String contactPhone;

    private String mobileNumber;

    private String codiceFiscale;

    @NotNull(message = "{register.title.invalid}")
    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    @NotBlank(message = "{register.firstName.invalid}")
    @Size(max = 40, message = "{register.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @NotBlank(message = "{register.lastName.invalid}")
    @Size(max = 40, message = "{register.lastName.invalid}")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    @Phonenumber(message = "{form.phone.error.for.country}")
    @Size(max = 30, message = "{Size.phoneNumber}")
    @NotBlank(message = "{form.phone.error.for.country}")
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(final String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @Phonenumber(message = "{form.phone.error.for.country}")
    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @NotBlankForCountry(message = "register.codiceFiscale.invalid", countries = { "IT" })
    @Size(max = 30, message = "{register.codiceFiscale.invalid}")
    @DataFormInput(patternKey = "register.codiceFiscale.validationPattern", message = "{register.codiceFiscale.invalid}")
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }
}
