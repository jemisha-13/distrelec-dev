/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.eprocurement;

import com.namics.distrelec.b2b.cxml.generated.CXML;
import org.springframework.http.HttpStatus;

import java.io.Writer;
import java.util.Map;

/**
 * DistAribaFacade.
 *
 * @author pbueschi, Namics AG
 */
public interface DistAribaFacade {

    /**
     * Gets Map<String, String> with credentials username and password.
     *
     * @param aribaSetupRequest
     * @return Map<String, String> credentials
     */
    Map<String, String> getCredentialsFromDistAribaOutSetupRequest(final String aribaSetupRequest);

    /**
     * Parases cXML from Ariba request which is saved on the current user
     *
     * @return DistAribaSetupRequestData
     */
    CXML parseAribaSetupRequest();

    /**
     * Gets required parsed setup request parameters as map by provided already parsed data.
     *
     * @param cXmlPunchOutSetupRequest
     * @return Map<String, String>
     */
    Map<String, String> getAribaSetupRequestParameters(final CXML cXmlPunchOutSetupRequest);

    /**
     * This method is use to determine if PunchOutSetupRequest also consist ItemOutData which is used to re-create customer cart for
     * edit/Inspect function
     *
     * @param cXmlPunchOutSetupRequest
     * @return haveItemOutData
     */
    boolean haveItemOutData(final CXML cXmlPunchOutSetupRequest);

    /**
     * Checks if the credentials are right and if they are a token will be returned.
     *
     * @param userId
     * @param password
     * @param setupRequest
     * @return the token
     */
    String getAribaToken(final String userId, final String password, final String setupRequest);

    /**
     * Writes the cXML response
     *
     * @param status
     *            HTTP Status
     * @param url
     *            the url the user should be redirected to
     * @param payloadId
     *            the payloadID send with the setup request
     * @param writer
     *            response writer
     */
    void parseAribaSetupResponse(final HttpStatus status, final String url, final String payloadId, final Writer writer);

    /**
     * Set up Ariba cart - empty or with information from the punchOutSetupRequest.
     *
     * @param cXmlPunchOutSetupRequest
     * @param useBasketFromCustomer
     * @param customerCartCode
     * @return aribaCartCode
     */
    String setUpAribaCart(final CXML cXmlPunchOutSetupRequest, final boolean useBasketFromCustomer, final String customerCartCode);

    /**
     * Set up the Ariba customer session language
     *
     * @param isocode
     *            language ISOCODE
     */
    void setUpAribaLanguage(final String isocode);

    /**
     * Check whether we need to jump out of Iframe or Not based on configuration
     *
     * @return {@code true} if customer configuration says it should be open in a new window. Default value is {@code false}
     */
    boolean openInNewWindow();
}
