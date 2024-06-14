package com.namics.distrelec.b2b.storefront.response;

public class NewsletterSubscribeResponse {

    public static class Builder {
        private boolean doubleOptIn;

        private String errorMessage;

        public Builder isDoubleOptIn(boolean isDoubleOptIn) {
            this.doubleOptIn = isDoubleOptIn;
            return this;
        }

        public Builder withErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public NewsletterSubscribeResponse build() {
            NewsletterSubscribeResponse response = new NewsletterSubscribeResponse();
            response.setDoubleOptIn(this.doubleOptIn);
            response.setErrorMessage(this.errorMessage);

            return response;
        }
    }

    private boolean doubleOptIn;

    private String errorMessage;

    private NewsletterSubscribeResponse() {}

    public boolean isDoubleOptIn() {
        return doubleOptIn;
    }

    public void setDoubleOptIn(boolean doubleOptIn) {
        this.doubleOptIn = doubleOptIn;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
