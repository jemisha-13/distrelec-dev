/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.v1

import com.namics.distrelec.occ.test.test.groovy.webservicetests.markers.CollectOutputFromTest
import de.hybris.bootstrap.annotations.ManualTest
import org.junit.Test
import org.junit.experimental.categories.Category

@Category(CollectOutputFromTest.class)
@ManualTest
class LogoutTests extends BaseWSTest {

    final password = "test"

    @Test
    void testLogout() {
        def customerTests = new CustomerTests()
        def uid = customerTests.registerUser()
        def access_token = testUtil.getAccessToken(uid, password);

        //get customer profile
        def con = testUtil.getSecureConnection("/customers/current", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, null, access_token)
        def response = testUtil.verifiedXMLSlurper(con)
        assert response

        //get cookie
        def cookie = con.getHeaderField('Set-Cookie')
        assert cookie: 'No cookie present!'
        def cookieNoPath = cookie.split(';')[0]

        //logout
        con = testUtil.getSecureConnection(config.DEFAULT_HTTPS_URI + config.BASE_PATH + "/customers/current/logout", 'POST', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
        response = testUtil.verifiedXMLSlurper(con)

        assert response.success == true

        //after logout, using the same cookie than before must fail!
        con = testUtil.getSecureConnection("/customers/current", 'GET', 'XML', HttpURLConnection.HTTP_UNAUTHORIZED, null, cookieNoPath)
    }

    @Test
    void testLogoutJSON() {
        def customerTests = new CustomerTests()
        def uid = customerTests.registerUser()
        def access_token = testUtil.getAccessToken(uid, password);

        //get customer profile
        def con = testUtil.getSecureConnection("/customers/current", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, null, access_token)
        def response = testUtil.verifiedJSONSlurper(con)
        assert response

        //get cookie
        def cookie = con.getHeaderField('Set-Cookie')
        assert cookie: 'No cookie present!'
        def cookieNoPath = cookie.split(';')[0]

        //logout
        con = testUtil.getSecureConnection(config.DEFAULT_HTTPS_URI + config.BASE_PATH + "/customers/current/logout", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
        response = testUtil.verifiedJSONSlurper(con)

        assert response.success == true

        //after logout, using the same cookie than before must fail!
        con = testUtil.getSecureConnection("/customers/current", 'GET', 'JSON', HttpURLConnection.HTTP_UNAUTHORIZED, null, cookieNoPath)
    }
}
