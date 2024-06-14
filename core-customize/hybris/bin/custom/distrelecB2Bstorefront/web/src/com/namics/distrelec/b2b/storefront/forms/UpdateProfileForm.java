/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.forms;

import com.namics.distrelec.b2b.storefront.validation.annotations.AtLeastOneNotBlank;
import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Form object for updating profile.
 */
@AtLeastOneNotBlank(message = "{register.onePhone.required}", value = { "phone", "mobilePhone" })
public class UpdateProfileForm {

    private String titleCode;
    private String firstName;
    private String lastName;
    private String departmentCode;
    private String functionCode;
    private String phone;
    private String mobilePhone;
    private String fax;

    @NotNull(message = "{logindata.changeName.title.invalid}")
    public String getTitleCode() {
        return titleCode;
    }

    public void setTitleCode(final String titleCode) {
        this.titleCode = titleCode;
    }

    @NotBlank(message = "{logindata.changeName.firstName.invalid}")
    @Size(max = 40, message = "{logindata.changeName.firstName.invalid}")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    @NotBlank(message = "{logindata.changeName.lastName.invalid}")
    @Size(max = 40, message = "{logindata.changeName.lastName.invalid}")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(final String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(final String functionCode) {
        this.functionCode = functionCode;
    }

    @Phonenumber(message = "{logindata.changeName.phoneNumber.invalid}")
    @Size(max = 30, message = "{Size.phoneNumber}")
    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    @Phonenumber(message = "{logindata.changeName.mobileNumber.invalid}")
    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(final String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Phonenumber(message = "{logindata.changeName.faxNumber.invalid}")
    @Size(max = 30, message = "{Size.mobileNumber}")
    public String getFax() {
        return fax;
    }

    public void setFax(final String fax) {
        this.fax = fax;
    }

}
