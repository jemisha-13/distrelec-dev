package com.namics.distrelec.b2b.core.timestampupdate;

public class ImportException extends Exception {

    private int line;
    private ErrorSource errorSource;

    public ImportException(final String message, final ErrorSource errorSource) {
        super(message);
        this.errorSource = errorSource;
    }

    public ImportException(final String message, final int line, final ErrorSource errorSource) {
        super(message);
        this.line = line;
        this.errorSource = errorSource;
    }

    public ImportException(final String message, final Throwable cause, final ErrorSource errorSource) {
        super(message, cause);
        this.errorSource = errorSource;
    }

    public ImportException(final String message, final Throwable cause, final int line, final ErrorSource errorSource) {
        super(message, cause);
        this.line = line;
        this.errorSource = errorSource;
    }

    public ImportException(final Throwable cause, final int line, final ErrorSource errorSource) {
        super(cause);
        this.line = line;
        this.errorSource = errorSource;
    }

    public ImportException(final Throwable cause, final ErrorSource errorSource) {
        super(cause);
        this.errorSource = errorSource;
    }

    public int getLine() {
        return line;
    }

    public void setLine(final int line) {
        this.line = line;
    }

    public ErrorSource getErrorSource() {
        return errorSource;
    }

    public void setErrorSource(final ErrorSource errorSource) {
        this.errorSource = errorSource;
    }

    public enum ErrorSource {
        TOO_MANY_LINES, NO_DATA, FILE_EMPTY, IO_EXCEPTION;
    }
}
