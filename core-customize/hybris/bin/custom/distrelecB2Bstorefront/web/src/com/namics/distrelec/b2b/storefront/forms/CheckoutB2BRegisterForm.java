package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class CheckoutB2BRegisterForm extends CheckoutRegisterForm implements B2BNewsletterForm{

    private String company;
    private String company2;
    private String company3;
    private String functionCode;
    private String vatId;
    private String organizationalNumber;
    
    
    @NotBlank(message = "{register.company.invalid}")
    @Size(max = 35, message = "{register.company.invalid}")
    public String getCompany() {
        return company;
    }

    public void setCompany(final String company) {
        this.company = company;
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
    @Size(max = 2, message = "{register.function.invalid}")
    @NotBlank(message = "{register.function.invalid}")
    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(final String functionCode) {
        this.functionCode = functionCode;
    }

    @Size(max = 20, message = "{register.vatId.validationMessage}")
    @DataFormInput(message = "{register.vatId.validationMessage}", patternKey = "register.vatId.validationPattern")
    public String getVatId() {
        return vatId;
    }

    public void setVatId(final String vatId) {
        this.vatId = vatId;
    }

    @Size(max = 11, message = "{register.organizationalNumber.invalid}")
    @DataFormInput(message = "{register.organizationalNumber.invalid}", patternKey = "register.organizationalNumber.pattern")
    public String getOrganizationalNumber() {
        return organizationalNumber;
    }

    public void setOrganizationalNumber(final String organizationalNumber) {
        this.organizationalNumber = organizationalNumber;
    }
}
