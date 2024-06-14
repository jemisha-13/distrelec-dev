package com.namics.distrelec.b2b.storefront.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorFeedback {

    @NotEmpty(message = "Error Reason cannot be empty")
    private String ErrorReason;

    @NotEmpty(message = "Error Description cannot be empty")
    private String ErrorDescription;
    @Email(message = "Not a valid email")
    private String CustomerEmailId;

    private String CustomerName;

    private List<String> errors;

    private String Status;

    public String getErrorReason() {
        return ErrorReason;
    }

    public void setErrorReason(String errorReason) {
        ErrorReason = errorReason;
    }

    public String getErrorDescription() {
        return ErrorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        ErrorDescription = errorDescription;
    }

    public String getCustomerEmailId() {
        return CustomerEmailId;
    }

    public void setCustomerEmailId(String customerEmailId) {
        CustomerEmailId = customerEmailId;
    }

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

    @JsonCreator
    public ErrorFeedback(@JsonProperty("errorReason") String errorReason, @JsonProperty("errorDescription") String errorDescription,
        @JsonProperty("customerName") String customerName, @JsonProperty("customerEmailId") String customerEmailId)

    {
        this.ErrorDescription = errorDescription;
        this.ErrorReason = errorReason;
        this.CustomerName = customerName;
        this.CustomerEmailId = customerEmailId;
    }

    public ErrorFeedback() {
        this.Status = "New Object";
    }
}
