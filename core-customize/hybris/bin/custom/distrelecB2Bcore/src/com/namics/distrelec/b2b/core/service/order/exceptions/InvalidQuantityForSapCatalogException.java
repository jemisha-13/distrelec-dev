package com.namics.distrelec.b2b.core.service.order.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

public class InvalidQuantityForSapCatalogException extends WebserviceException {

    private static final String TYPE = "SapInvalidCatalogError";

    private static final String SUBJECT_TYPE = "cart";

    public InvalidQuantityForSapCatalogException(String message, String reason) {
        super(message, reason);
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getSubjectType() {
        return SUBJECT_TYPE;
    }
}
