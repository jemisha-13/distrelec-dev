/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Form object for b2b customer registration.
 *
 * @author rmeier, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class RegisterB2BForm extends RegisterForm implements B2BNewsletterForm {

    private String company;
    private String company2;
    private String company3;
    private String functionCode;
    private String groupVatId;
    private String vatId;
    private String organizationalNumber;
    private String duns;
    private String vatIdMessage;
    private String companyMessage;
    private String companyMatch;
    
    @NotBlank(message = "{register.company.invalid}")
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
    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(final String functionCode) {
        this.functionCode = functionCode;
    }

    public String getGroupVatId() {
        return groupVatId;
    }

    public void setGroupVatId(String groupVatId) {
        this.groupVatId = groupVatId;
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
    @DataFormInput(message = "{checkoutregister.org.error}", patternKey = "orgNumber.validation.pattern")
    public String getOrganizationalNumber() {
        return organizationalNumber;
    }

    public void setOrganizationalNumber(final String organizationalNumber) {
        this.organizationalNumber = organizationalNumber;
    }
    
    public String getDuns() {
        return duns;
    }

    public void setDuns(String duns) {
        this.duns = duns;
    }

	public String getVatIdMessage() {
		return vatIdMessage;
	}

	public void setVatIdMessage(String vatIdMessage) {
		this.vatIdMessage = vatIdMessage;
	}
	
    public String getCompanyMessage() {
		return companyMessage;
	}

	public void setCompanyMessage(String companyMessage) {
		this.companyMessage = companyMessage;
	}

	public String getCompanyMatch() {
        return companyMatch;
    }

    public void setCompanyMatch(final String companyMatch) {
        this.companyMatch = companyMatch;
    }
}
