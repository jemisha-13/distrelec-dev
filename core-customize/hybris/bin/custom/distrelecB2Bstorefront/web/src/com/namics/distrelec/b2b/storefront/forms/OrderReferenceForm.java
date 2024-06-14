package com.namics.distrelec.b2b.storefront.forms;

import de.hybris.platform.validation.annotations.NotBlank;

public class OrderReferenceForm {

    @NotBlank
    private String orderCode;

    @NotBlank
    private String workflowCode;

    @NotBlank
    private String orderReference;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getWorkflowCode() {
        return workflowCode;
    }

    public void setWorkflowCode(String workflowCode) {
        this.workflowCode = workflowCode;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }
}

