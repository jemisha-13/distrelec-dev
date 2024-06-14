package com.namics.distrelec.b2b.storefront.forms;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.Size;

public class UpdateUserProfileForm {
    private String functionCode;
    private String departmentCode;

    @Valid
    @Size(max = 4)
    @JsonProperty("departmentCode")
    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    @Valid
    @Size(max = 4)
    @JsonProperty("functionCode")
    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

}
