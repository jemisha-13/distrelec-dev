
package com.namics.distrelec.b2b.core.bmecat.export.exception;

/**
 * 
 * 
 * @author Abhinay Jadhav, Datwyler IT
 * @since 10-Dec-2017
 * 
 */
public class DistBMECatExportServiceException extends Exception {

    public DistBMECatExportServiceException() {
        super();
    }

    public DistBMECatExportServiceException(final String message) {
        super(message);
    }

    public DistBMECatExportServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DistBMECatExportServiceException(final Throwable cause) {
        super(cause);
    }

}
