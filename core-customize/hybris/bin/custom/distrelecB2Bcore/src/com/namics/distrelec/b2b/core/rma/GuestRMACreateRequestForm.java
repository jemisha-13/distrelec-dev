package com.namics.distrelec.b2b.core.rma;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class GuestRMACreateRequestForm {

    private String customerName;
    private String orderNumber;
    private String articleNumber;
    private Long quantity;
    private String returnReason;
    private String returnSubReason;
    private String emailAddress;
    private String phoneNumber;
    private String customerText;

    @NotBlank(message = "{lightboxreturnrequest.code.invalid}")
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @NotBlank(message = "{lightboxreturnrequest.code.invalid}")
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @NotBlank(message = "{lightboxreturnrequest.code.invalid}")
    @Size(min = 8)
    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @NotBlank(message = "{lightboxreturnrequest.code.invalid}")
    @Email
    public String getEmailAddress() {
        return emailAddress;
    }

    @NotBlank(message = "{lightboxreturnrequest.code.invalid}")
    public String getReturnReason() {
        return returnReason;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Size(max = 50)
    public String getCustomerText() {
        return customerText;
    }

    public void setCustomerText(String customerText) {
        this.customerText = customerText;
    }

    public String getReturnSubReason() {
        return returnSubReason;
    }

    public void setReturnSubReason(String returnSubReason) {
        this.returnSubReason = returnSubReason;
    }
}
