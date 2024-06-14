package com.namics.distrelec.b2b.core.service.order.exceptions;

import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

import java.util.Collection;

public class BlockedProductException extends WebserviceException {

    private static final String TYPE = "BlockedProductError";

    private static final String SUBJECT_TYPE = "cart";

    public BlockedProductException(final String message, final String reason) {
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
