package com.namics.distrelec.occ.core.v2.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class ZeroResultSearchFeedbackForm {

    @Email
    @NotEmpty
    private String email;

    @NotEmpty
    private String manufacturer;

    @NotEmpty
    private String manufacturerType;

    private String productName;

    private String manufacturerTypeOtherName;

    private String tellUsMore;

    private String searchTerm;

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturerType() {
        return manufacturerType;
    }

    public void setManufacturerType(String manufacturerType) {
        this.manufacturerType = manufacturerType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getManufacturerTypeOtherName() {
        return manufacturerTypeOtherName;
    }

    public void setManufacturerTypeOtherName(String manufacturerTypeOtherName) {
        this.manufacturerTypeOtherName = manufacturerTypeOtherName;
    }

    public String getTellUsMore() {
        return tellUsMore;
    }

    public void setTellUsMore(String tellUsMore) {
        this.tellUsMore = tellUsMore;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
