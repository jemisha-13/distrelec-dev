package com.namics.distrelec.b2b.core.service.order.exceptions;

import java.util.Collection;

import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;

public class PunchoutException extends WebserviceException {

    private Collection<PunchoutFilterResult> punchoutFilterResults;

    private static final String TYPE = "PunchoutError";

    private static final String SUBJECT_TYPE = "cart";

    public PunchoutException(String message, Collection<PunchoutFilterResult> punchoutResults) {
        super(message);
        punchoutFilterResults = punchoutResults;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getSubjectType() {
        return SUBJECT_TYPE;
    }

    public Collection<PunchoutFilterResult> getPunchoutFilterResults() {
        return punchoutFilterResults;
    }
}
