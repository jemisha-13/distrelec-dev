package com.namics.distrelec.b2b.core.rma;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

public class CreateRMARequestForm {

    private String orderId;
    private Date orderDate;
    private List<CreateRMAOrderEntryDataForm> orderItems;


    @NotBlank(message = "{lightboxreturnrequest.code.invalid}")
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    @Valid
    @NotEmpty
    public List<CreateRMAOrderEntryDataForm> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<CreateRMAOrderEntryDataForm> orderItems) {
        this.orderItems = orderItems;
    }

}
