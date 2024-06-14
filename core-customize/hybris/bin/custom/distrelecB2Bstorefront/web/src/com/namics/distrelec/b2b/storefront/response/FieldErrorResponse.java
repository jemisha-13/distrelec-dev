package com.namics.distrelec.b2b.storefront.response;

public class FieldErrorResponse {
    public static class Builder {

        private String message;

        private String field;

        public FieldErrorResponse.Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public FieldErrorResponse.Builder withField(String field) {
            this.field = field;
            return this;
        }

        public FieldErrorResponse build() {
            FieldErrorResponse response = new FieldErrorResponse();
            response.setMessage(this.message);
            response.setField(this.field);

            return response;
        }
    }

    private String message;

    private String field;

    private FieldErrorResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
