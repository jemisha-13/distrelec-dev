/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.addons

import com.namics.distrelec.occ.test.test.groovy.webservicetests.markers.CollectOutputFromTest
import com.namics.distrelec.occ.test.test.groovy.webservicetests.v1.BaseWSTest
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.core.Registry
import de.hybris.platform.storelocator.GeoWebServiceWrapper
import de.hybris.platform.util.Config
import groovy.json.JsonSlurper
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.springframework.beans.factory.NoSuchBeanDefinitionException

import java.lang.reflect.Method

@org.junit.experimental.categories.Category(CollectOutputFromTest.class)
@ManualTest
class ExtendedCustomersTests extends BaseWSTest {

    final firstName = "Sven"
    final lastName = "Haiges"
    final titleCode = "dr"
    final public static password = "test"

    private static GeoWebServiceWrapper originalServiceWrapper;

    @BeforeClass
    public static void initGeoServiceWrapper() {

        if (Config.getBoolean("distrelecB2BOCCtest.enableAccTest", false)
                && Config.getBoolean("distrelecB2BOCCtest.enableV1", false)) {
            final GeoWebServiceWrapper webServiceWrapper = Registry.getApplicationContext().getBean("mockedGeoServiceWrapper", GeoWebServiceWrapper.class)
            try {
                final Object customerLocationService = Registry.getApplicationContext().getBean("defaultCustomerLocationService")
                final Method getterMethod = customerLocationService.getClass().getDeclaredMethod("getGeoWebServiceWrapper")
                getterMethod.setAccessible(true)
                originalServiceWrapper = (GeoWebServiceWrapper) getterMethod.invoke(customerLocationService)

                // set mock
                final Method setterMethod = customerLocationService.getClass().getMethod("setGeoWebServiceWrapper", GeoWebServiceWrapper.class)
                setterMethod.invoke(customerLocationService, webServiceWrapper)
            } catch (final NoSuchBeanDefinitionException ex) {
                // acceleratorservices not present: no need to replace GeoWebServiceWrapper
            }
        }
    }

    @AfterClass
    public static void restoreGeoServiceWrapper() {
        if (originalServiceWrapper != null) {
            final Object customerLocationService = Registry.getApplicationContext().getBean("customerLocationService")
            final Method setterMethod = customerLocationService.getClass().getMethod("setGeoWebServiceWrapper", GeoWebServiceWrapper.class)
            setterMethod.invoke(customerLocationService, originalServiceWrapper)
        }
    }

    @Before
    public void ignoreIf() {
        org.junit.Assume.assumeTrue(Config.getBoolean("distrelecB2BOCCtest.enableAccTest", false)
                && Config.getBoolean("distrelecB2BOCCtest.enableV1", false))
    }

    /**
     * helper method to register user
     * @return generated userId
     */
    def registerUser(useSecureConnection = true, status = HttpURLConnection.HTTP_CREATED) {
        def client_credentials_token = testUtil.getClientCredentialsToken()
        def randomUID = System.currentTimeMillis()
        def body = "login=${randomUID}@sven.de&password=${password}&firstName=${firstName}&lastName=${lastName}&titleCode=${titleCode}"

        def con = null
        if (useSecureConnection) {
            con = testUtil.getSecureConnection("/customers", 'POST', 'XML', status, body, null, client_credentials_token)
        } else {
            con = testUtil.getSecureConnection("/customers", 'POST', 'XML', status, body, null, client_credentials_token)
        }

        return status == HttpURLConnection.HTTP_CREATED ? "${randomUID}@sven.de" : null
    }


    def registerUserJSON() {
        def client_credentials_token = testUtil.getClientCredentialsToken()
        def randomUID = System.currentTimeMillis()
        def body = "login=${randomUID}@sven.de&password=${password}&firstName=${firstName}&lastName=${lastName}&titleCode=${titleCode}"
        def con = testUtil.getSecureConnection("/customers", 'POST', 'JSON', HttpURLConnection.HTTP_CREATED, body, null, client_credentials_token)
        return "${randomUID}@sven.de"
    }

    @Test
    void testSetUserLocationByGeolocationJSON() {
        def uid = registerUserJSON()
        def access_token = testUtil.getAccessToken(uid, password);
        def postBody = "latitude=35.65&longitude=139.69"

        def con = testUtil.getSecureConnection("/customers/current/locationLatLong", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
        def cookie = con.getHeaderField('Set-Cookie')

        assert cookie: 'No cookie present, cannot keep session'

        def cookieNoPath = cookie.split(';')[0]

        con = testUtil.getSecureConnection("/customers/current/location", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
        def response = testUtil.verifiedJSONSlurper(con)

        assert response.point.longitude == 139.69
        assert response.point.latitude == 35.65
        assert response.searchTerm == ""
    }

    @Test
    void testSetUserLocationByGeolocationXML() {
        def uid = registerUser()
        def access_token = testUtil.getAccessToken(uid, password);
        def postBody = "latitude=35.65&longitude=139.69"

        def con = testUtil.getSecureConnection("/customers/current/locationLatLong", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
        def cookie = con.getHeaderField('Set-Cookie')

        assert cookie: 'No cookie present, cannot keep session'

        def cookieNoPath = cookie.split(';')[0]

        con = testUtil.getSecureConnection("/customers/current/location", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
        def response = testUtil.verifiedXMLSlurper(con)

        assert response.point.longitude == 139.69
        assert response.point.latitude == 35.65
        assert response.searchTerm == ""
    }

    @Test
    void testSetUserLocationByValidTermJSON() {
        def uid = registerUserJSON()
        def access_token = testUtil.getAccessToken(uid, password);
        def postBody = "location=tokio"

        def con = testUtil.getSecureConnection("/customers/current/location", 'PUT', 'JSON', HttpURLConnection.HTTP_OK, postBody, null, access_token)
        def cookie = con.getHeaderField('Set-Cookie')

        assert cookie: 'No cookie present, cannot keep session'

        def cookieNoPath = cookie.split(';')[0]

        con = testUtil.getSecureConnection("/customers/current/location", 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
        def response = testUtil.verifiedJSONSlurper(con)

        assert response.searchTerm == "tokio"
        assert response.point.latitude == 35.6894875
        assert response.point.longitude == 139.6917064
    }

    @Test
    void testSetUserLocationByValidTermXML() {
        def uid = registerUser()
        def access_token = testUtil.getAccessToken(uid, password);
        def postBody = "location=tokio"

        def con = testUtil.getSecureConnection("/customers/current/location", 'PUT', 'XML', HttpURLConnection.HTTP_OK, postBody, null, access_token)
        def cookie = con.getHeaderField('Set-Cookie')

        assert cookie: 'No cookie present, cannot keep session'

        def cookieNoPath = cookie.split(';')[0]

        con = testUtil.getSecureConnection("/customers/current/location", 'GET', 'XML', HttpURLConnection.HTTP_OK, null, cookieNoPath, access_token)
        def response = testUtil.verifiedXMLSlurper(con)

        assert response.searchTerm == "tokio"
        assert response.point.latitude == 35.6894875
        assert response.point.longitude == 139.6917064
    }

    @Test
    void testSetUserLocationByInValidTermJSON() {
        def uid = registerUserJSON()
        def access_token = testUtil.getAccessToken(uid, password);
        def location = "457231589321y5sdhkguisdgh"
        def postBody = "location=${location}"

        def con = testUtil.getSecureConnection("/customers/current/location", 'PUT', 'JSON', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
        def error = con.errorStream.text;
        def response = new JsonSlurper().parseText(error)
        assert response.errors[0].type == 'NoLocationFoundError'
        assert response.errors[0].message == "Location: ${location} could not be found"
    }

    @Test
    void testSetUserLocationByInValidTermXML() {
        def uid = registerUser()
        def access_token = testUtil.getAccessToken(uid, password);
        def location = "457231589321y5sdhkguisdgh"
        def postBody = "location=${location}"

        def con = testUtil.getSecureConnection("/customers/current/location", 'PUT', 'XML', HttpURLConnection.HTTP_BAD_REQUEST, postBody, null, access_token)
        def response = new XmlSlurper().parseText(con.errorStream.text)
        assert response.error.type == 'NoLocationFoundError'
        assert response.error.message == "Location: ${location} could not be found"
    }
}
