/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * AbstractDistAddressForm
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public abstract class AbstractB2BAddressForm extends AbstractDistAddressForm {

    private String companyName;
    private String companyName2;
    private String companyName3;
    
    @NotBlank(message = "{address.companyName.invalid}")
    @Size(max = 35, message = "{address.companyName.invalid}")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    @Size(max = 35, message = "{address.companyName2.invalid}")
    public String getCompanyName2() {
        return companyName2;
    }

    public void setCompanyName2(final String companyName2) {
        this.companyName2 = companyName2;
    }

    @Size(max = 35, message = "{address.companyName3.invalid}")
    public String getCompanyName3() {
        return companyName3;
    }

    public void setCompanyName3(final String companyName3) {
        this.companyName3 = companyName3;
    }
}
