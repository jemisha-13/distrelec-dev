package com.namics.distrelec.b2b.storefront.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Form object for send feedback.
 */
public class ZeroResultSearchFeedbackForm {
    private String email;
    private String manufacturer;
    private String manufacturerType;
    private String productName;
    private String manufacturerTypeOtherName;
    private String tellUsMore;
    private String searchTerm;

    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @NotEmpty
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @NotEmpty
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
