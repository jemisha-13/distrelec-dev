/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.addons

import com.namics.distrelec.occ.test.test.groovy.webservicetests.v1.BaseWSTest
import com.namics.distrelec.occ.test.test.groovy.webservicetests.v1.CartTests
import com.namics.distrelec.occ.test.test.groovy.webservicetests.v1.CustomerTests
import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.util.Config
import org.junit.Before
import org.junit.Test

@ManualTest
class ExtendedCartTests extends BaseWSTest {
    static final PASSWORD = "test"
    static final STORE_NAME = "WS-Shinbashi"
    static final ANOTHER_STORE_NAME = "WS-Tokio Hotel Metropolitan Tokyo"

    @Before
    public void ignoreIf() {
        org.junit.Assume.assumeTrue(Config.getBoolean("distrelecB2BOCCtest.enableAccTest", false)
                && Config.getBoolean("distrelecB2BOCCtest.enableV1", false))
    }

    @Test
    void testGetConsolidatedPickupLocations() {
        def cartTests = new CartTests();
        def cookieNoPath, con, response
        def customerTests = new CustomerTests()
        def uid = customerTests.registerUser()
        def access_token = testUtil.getAccessToken(uid, PASSWORD)

        // add item which is out of stock in the online store
        cookieNoPath = cartTests.addToCart(2006139, 1, null, access_token, STORE_NAME)

        // add pickup item to the cart
        cartTests.addToCart(1934793, 2, cookieNoPath, access_token, ANOTHER_STORE_NAME)

        // get consolidated options
        con = testUtil.getSecureConnection('/cart/consolidate', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
        response = testUtil.verifiedJSONSlurper(con, false, false)
        assert response.pointOfServices.size() == 2
        assert response.pointOfServices[0].name == STORE_NAME
        assert response.pointOfServices[1].name == ANOTHER_STORE_NAME
    }

    @Test
    void testConsolidatePickupLocations() {
        def cartTests = new CartTests();
        def cookieNoPath, con, response
        def customerTests = new CustomerTests()
        def uid = customerTests.registerUser()
        def access_token = testUtil.getAccessToken(uid, PASSWORD)

        // add item which is out of stock in the online store
        cookieNoPath = cartTests.addToCart(2006139, 1, null, access_token, STORE_NAME)

        // add pickup item to the cart
        cartTests.addToCart(1934793, 2, cookieNoPath, access_token, ANOTHER_STORE_NAME)

        // consolidate pickup locations
        con = testUtil.getSecureConnection("/cart/consolidate?storeName=${STORE_NAME}", 'POST', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
        response = testUtil.verifiedJSONSlurper(con, false, false)
        assert response.cartModificationList != null

        // get the cart
        con = testUtil.getSecureConnection('/cart', 'GET', 'JSON', HttpURLConnection.HTTP_OK, null, cookieNoPath)
        response = testUtil.verifiedJSONSlurper(con, false, false)
        assert response.totalItems == 2
        assert response.totalUnitCount == 3
        assert response.entries[0].product.availableForPickup == true
        assert response.entries[0].deliveryPointOfService.name == STORE_NAME
        assert response.entries[1].product.availableForPickup == true
        assert response.entries[1].deliveryPointOfService.name == STORE_NAME
        assert response.pickupOrderGroups[0].entries[0].product.availableForPickup == true
        assert response.pickupOrderGroups[0].entries[0].deliveryPointOfService.name == STORE_NAME
        assert response.pickupOrderGroups[0].entries[1].product.availableForPickup == true
        assert response.pickupOrderGroups[0].entries[1].deliveryPointOfService.name == STORE_NAME
        assert response.pickupOrderGroups[0].deliveryPointOfService.name == STORE_NAME
    }
}
