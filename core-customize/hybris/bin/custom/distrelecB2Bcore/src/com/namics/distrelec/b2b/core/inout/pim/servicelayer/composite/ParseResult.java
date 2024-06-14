package com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite;

public class ParseResult {

    private final boolean success;

    private final String message;

    public ParseResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
