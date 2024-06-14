package com.namics.distrelec.b2b.storefront.response;

public class GlobalMessageResponse {

    public enum MessageType {

        WARNING("warning"),
        ERROR("error"),
        INFORMATION("information"),
        SUCCESS("success");

        private String type;

        MessageType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public static class Builder {

        private String message;

        private MessageType type;

        public GlobalMessageResponse.Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public GlobalMessageResponse.Builder withType(MessageType type) {
            this.type = type;
            return this;
        }

        public GlobalMessageResponse build() {
            GlobalMessageResponse response = new GlobalMessageResponse();
            response.setMessage(this.message);
            response.setType(this.type.getType());

            return response;
        }
    }

    private String message;

    private String type;

    private GlobalMessageResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
