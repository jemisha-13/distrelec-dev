package com.namics.distrelec.b2b.storefront.response;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.EMPTY;

/**
 * Response for file upload on environmental documentation download page.
 * <p>
 * productCodes - list of product codes that were parsed successfully and that will end up in the reports when downloading
 * errorMessage - contains the Error message in case of an error. Empty when no error.
 * </p>
 */
public class EnvironmentalDocumentationFileUploadResponse {

    private List<String> productCodes;

    private List<String> invalidProductsCodes;

    private String errorMessage;

    public EnvironmentalDocumentationFileUploadResponse() {
        this.productCodes = new ArrayList<>();
        this.invalidProductsCodes = new ArrayList<>();
        this.errorMessage = EMPTY;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getProductCodes() {
        return productCodes;
    }

    public void setProductCodes(List<String> productCodes) {
        this.productCodes = productCodes;
    }

    public List<String> getInvalidProductsCodes() {
        return invalidProductsCodes;
    }

    public void setInvalidProductsCodes(List<String> invalidProductsCodes) {
        this.invalidProductsCodes = invalidProductsCodes;
    }

    public static class Builder {
        private List<String> productCodes = new ArrayList<>();
        private List<String> invalidProductCodes = new ArrayList<>();
        private String errorMessage = EMPTY;

        public Builder withProductCodes(List<String> productCodes, List<String> invalidProductCodes) {
            this.productCodes = productCodes;
            this.invalidProductCodes = invalidProductCodes;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public EnvironmentalDocumentationFileUploadResponse build() {
            EnvironmentalDocumentationFileUploadResponse response = new EnvironmentalDocumentationFileUploadResponse();
            response.setProductCodes(this.productCodes);
            response.setInvalidProductsCodes(this.invalidProductCodes);
            response.setErrorMessage(this.errorMessage);

            return response;
        }
    }

}
