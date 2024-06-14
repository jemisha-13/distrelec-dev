package com.namics.distrelec.b2b.facades.bloomreach;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.namics.distrelec.b2b.core.bloomreach.enums.BloomreachTouchpoint;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachExportException;
import com.namics.distrelec.b2b.facades.bloomreach.data.DistBloomreachConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistMarketingConsentData;

public interface DistBloomreachFacade {

    void sendBatchRequestToBloomreach(final String requestString) throws DistBloomreachBatchException;

    DistBloomreachConsentData exportCustomerConsentsFromBloomreach(final String email) throws IOException, DistBloomreachExportException;

    String createBloomreachRegistrationRequest(final DistConsentData consentData) throws IOException;

    String createBloomreachSubscriptionRequest(final DistConsentData consentData) throws IOException;

    String getPreferenceCenterUpdates(final DistMarketingConsentData updatedconsents, final DistConsentData consentData,
                                      final BloomreachTouchpoint touchpoint) throws IOException, DistBloomreachExportException;

    void updateCustomerInBloomreach(final DistConsentData consentData) throws DistBloomreachBatchException, JsonProcessingException;
}
