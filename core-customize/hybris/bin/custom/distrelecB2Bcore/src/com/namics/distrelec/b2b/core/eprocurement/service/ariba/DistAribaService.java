/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.ariba;

import java.io.Writer;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.namics.distrelec.b2b.cxml.generated.CXML;

import de.hybris.platform.core.model.user.UserModel;

/**
 * DistAribaService.
 * 
 * @author pbueschi, Namics AG
 */
public interface DistAribaService {

    /**
     * Checks if current customer is member of ARIBACUSTOMERGROUP
     * 
     * @return is Ariba customer
     */
    boolean isAribaCustomer();

    /**
     * Parses cXML from Ariba request to DistAribaSetupRequestData object
     * 
     * @param aribaSetupRequest
     * @return DistAribaSetupRequestData
     */
    CXML parseAribaSetupRequest(final String aribaSetupRequest);

    /**
     * Parases cXML from Ariba request which is saved on the current user
     * 
     * @return DistAribaSetupRequestData
     */
    CXML parseAribaSetupRequest();

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
     * check credentials. this method does not set the user on the current session.
     * 
     * @param token
     *            the token
     * @return the UserModel
     */

    UserModel checkCredentials(final String token);

    /**
     * login the user identified with the token. login means the credentials are checked and the user is being set as current user on the
     * session (see UserService.setCurrentUser(UserModel)) Implementation note: The default implementation checks against the Jalo user UID
     * and the password.
     * 
     * @param token
     *            the token
     * @return the same usermodel that is also set as current user in the session
     */
    UserModel aribaLogin(final String token);

    /**
     * log out the current user. this closes the underlaying session
     */
    void logout();

    /**
     * Gets required parsed setup request parameters as map by provided already parsed data.
     * 
     * @param cXmlPunchOutSetupRequest
     * @return Map<String, String>
     */
    Map<String, String> getAribaSetupRequestParameters(final CXML cXmlPunchOutSetupRequest);

    /**
     * Parses current session cart to cXML.
     * 
     * @return cXML
     */
    String parseAribaOrderMessage();

    /**
     * Set up empty Ariba cart.
     * 
     * @return aribaCartCode
     */
    String setUpAribaCart();

    /**
     * Set up Ariba cart with provided already parsed product and address information.
     * 
     * @param cXmlPunchOutSetupRequest
     * @return aribaCartCode
     */
    String setUpAribaCart(final CXML cXmlPunchOutSetupRequest);

    /**
     * Update Ariba cart and define if the cart should be editable or not.
     * 
     * @param cXmlPunchOutSetupRequest
     */
    void updateAribaCart(final CXML cXmlPunchOutSetupRequest);

    /**
     * Checks whether the custom footer should be enabled for the OCI customer
     * 
     * @return {@code true} if and only if custom footer is enabled.
     */
    boolean isCustomFooterEnabled();

    /**
     * Set up the Ariba customer session language
     * 
     * @param isocode
     *            language ISOCODE
     */
    void setUpAribaLanguage(final String isocode);
    
    
    /**
     * This method is use to determine if PunchOutSetupRequest also consist ItemOutData which is used to re-create customer cart for
     * edit/Inspect function
     *
     * @param cXmlPunchOutSetupRequest
     * @return haveItemOutData
     */
    boolean haveItemOutData(final CXML cXmlPunchOutSetupRequest);
    
    /**
     * Check whether we need to jump out of Iframe or Not based on configuration
     * 
     * @return {@code true} if customer configuration says it should be open in a new window. Default value is {@code false}
     */
     boolean openInNewWindow();

}
