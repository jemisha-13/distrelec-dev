/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.v1

import com.namics.distrelec.occ.test.test.groovy.webservicetests.markers.CollectOutputFromTest
import de.hybris.bootstrap.annotations.ManualTest
import groovy.json.JsonSlurper
import org.junit.Test

@org.junit.experimental.categories.Category(CollectOutputFromTest.class)
@ManualTest
class ErrorTests extends BaseWSTest {
    @Test
    void testRestResolverError() {
        def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();
        def con = testUtil.getSecureConnection("/stores/WRONG_ID", 'GET', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trusted_client_access_token)
        def error = con.errorStream.text;
        def response = new JsonSlurper().parseText(error);
        assert response.errors[0].type == 'UnknownIdentifierError'
        assert response.errors[0].message == 'No PointOfService with name WRONG_ID, type STORE found in baseStore wsTest'
    }

    @Test
    void testRestResolverErrorNoParam() {
        def trusted_client_access_token = testUtil.getTrustedClientCredentialsToken();
        def con = testUtil.getSecureConnection("/promotions", 'GET', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, null, null, trusted_client_access_token)
        def error = con.errorStream.text;
        def response = new JsonSlurper().parseText(error);
        assert response.errors[0].type == 'MissingServletRequestParameterError'
        assert response.errors[0].message ==~ /Required \w+ parameter 'type'.* is not present/
    }
}
