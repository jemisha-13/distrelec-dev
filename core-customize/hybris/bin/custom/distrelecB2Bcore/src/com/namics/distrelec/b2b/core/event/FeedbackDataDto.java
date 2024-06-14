package com.namics.distrelec.b2b.core.event;

/**
 * @author nshandilya
 */
public class FeedbackDataDto {
    public FeedbackDataDto(String email, String manufacturer, String manufacturerType, String productName, String tellUsMore, String searchTerm) {
        super();
        this.email = email;
        this.manufacturer = manufacturer;
        this.manufacturerType = manufacturerType;
        this.productName = productName;
        this.tellUsMore = tellUsMore;
        this.searchTerm = searchTerm;
    }

    public FeedbackDataDto(String email, String manufacturer, String otherManufacturerName, String manufacturerType, String productName, String tellUsMore, String searchTerm) {
        super();
        this.email = email;
        this.manufacturer = manufacturer;
        this.otherManufacturerName = otherManufacturerName;
        this.manufacturerType = manufacturerType;
        this.productName = productName;
        this.tellUsMore = tellUsMore;
        this.searchTerm = searchTerm;
    }


    private String email;
    private String manufacturer;
    private String otherManufacturerName;
    private String manufacturerType;
    private String productName;
    private String tellUsMore;
    private String searchTerm;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getOtherManufacturerName() {
        return otherManufacturerName;
    }

    public void setOtherManufacturerName(String otherManufacturerName) {
        this.otherManufacturerName = otherManufacturerName;
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
