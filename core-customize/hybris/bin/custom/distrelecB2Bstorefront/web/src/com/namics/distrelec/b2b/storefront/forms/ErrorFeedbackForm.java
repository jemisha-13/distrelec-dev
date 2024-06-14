package com.namics.distrelec.b2b.storefront.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * {@code ErrorFeedbackForm}
 *
 * @author Aditya Bhavaraju
 * @since Distrelec 5.11
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorFeedbackForm {
    private String ErrorReason;

    private String ErrorDescription;

    private String CustomerEmailId;

    private String CustomerName;

    private List<String> errors;

    private String productID;

    private String Status;

    @JsonProperty("errorReason")
    @NotEmpty(message = "Error Reason cannot be empty")
    public String getErrorReason() {
        return ErrorReason;
    }

    public void setErrorReason(String errorReason) {
        ErrorReason = errorReason;
    }

    @JsonProperty("errorDescription")
    @NotEmpty(message = "Error Description cannot be empty")
    public String getErrorDescription() {
        return ErrorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        ErrorDescription = errorDescription;
    }

    @JsonProperty("customerEmailId")
    @Email(message = "Not a valid email")
    public String getCustomerEmailId() {
        return CustomerEmailId;
    }

    public void setCustomerEmailId(String customerEmailId) {
        CustomerEmailId = customerEmailId;
    }

    @JsonProperty("customerName")
    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @JsonProperty("productId")
    @NotEmpty(message = "Product Id cannot be empty")
    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

}