package com.namics.distrelec.b2b.facades.legal;

public class DistQualityAndLegalInvalidFileUploadException extends Exception {

    private Reason reason;

    public DistQualityAndLegalInvalidFileUploadException(Reason reason) {
        super();
        this.reason = reason;
    }

    public DistQualityAndLegalInvalidFileUploadException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public DistQualityAndLegalInvalidFileUploadException(Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public DistQualityAndLegalInvalidFileUploadException(Reason reason, Throwable cause) {
        super(cause);
        this.reason = reason;
    }

    protected DistQualityAndLegalInvalidFileUploadException(Reason reason, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reason = reason;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public enum Reason {
        FILE_SIZE_LIMIT_EXCEEDED,
        FILE_EXTENSION_UNSUPPORTED,
        FILE_CONTENT_BLANK,
        FILE_LIST_LIMIT_EXCEEDED
    }

}
