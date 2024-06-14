package com.namics.distrelec.b2b.storefront.response;

import java.util.List;

public class ErrorFeedbackResponse {

    private String status;
    private List<String> statusDescription;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(List<String> statusDescription) {
        this.statusDescription = statusDescription;
    }

}
