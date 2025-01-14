/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.carts

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.basecommerce.enums.InStockStatus
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.*

@Unroll
@ManualTest
class OrderPlacementTest extends AbstractCartTest {

    def "Customer places order: #format"() {
        given: "a customer with cart ready for ordering"
        def val = createAndAuthorizeCustomerWithCart(restClient, format)
        def customer = val[0]
        def cart = val[1]
        addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480)
        addProductToCartPickup(restClient, customer, cart.code, PRODUCT_M340, STORE_SHINBASHI)
        def address = createAddress(restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
        setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_STANDARD, format)
        createPaymentInfo(restClient, customer, cart.code)

        when: "customer tries to place order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
                query: [
                        'cartId'      : cart.code,
                        'securityCode': '123',
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: format,
                requestContentType: URLENC
        )

        then: "order is properly placed"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
            isNotEmpty(data.created)
            data.guestCustomer == false
            data.totalTax.value.toDouble() > 0.0
        }
        where:
        format << [XML, JSON]
    }

    def "Customer places order without cart security code : #format"() {
        given: "a customer with cart ready for ordering"
        def val = createAndAuthorizeCustomerWithCart(restClient, format)
        def customer = val[0]
        def cart = val[1]
        addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480)
        addProductToCartPickup(restClient, customer, cart.code, PRODUCT_M340, STORE_SHINBASHI)
        def address = createAddress(restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
        setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_STANDARD, format)
        createPaymentInfo(restClient, customer, cart.code)

        when: "customer tries to place order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
                query: [
                        'cartId': cart.code,
                        'fields': FIELD_SET_LEVEL_FULL
                ],
                contentType: format,
                requestContentType: URLENC
        )

        then: "order is properly placed"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
            isNotEmpty(data.created)
            data.guestCustomer == false
        }
        where:
        format << [XML, JSON]
    }

    def "Customer places pickup order: #format"() {
        given: "a customer with cart ready for ordering and pickup delivery mode"
        def val = createAndAuthorizeCustomerWithCart(restClient, format)
        def customer = val[0]
        def cart = val[1]
        addProductToCartPickup(restClient, customer, cart.code, PRODUCT_M340, STORE_SHINBASHI)
        def address = createAddress(restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
        setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_PICKUP, format)
        createPaymentInfo(restClient, customer, cart.code)

        when: "customer tries to place order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
                query: [
                        'cartId'      : cart.code,
                        'securityCode': '123',
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: format,
                requestContentType: URLENC
        )

        then: "order is properly placed"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
            isNotEmpty(data.created)
            data.guestCustomer == false
        }
        where:
        format << [XML, JSON]
    }

    def "Customer places order missing cart details: #format"() {
        given: "a customer with cart that does not have delivery mode or payment info set"
        def val = createAndAuthorizeCustomerWithCart(restClient, format)
        def customer = val[0]
        def cart = val[1]
        addProductToCartPickup(restClient, customer, cart.code, PRODUCT_M340, STORE_SHINBASHI)
        def address = createAddress(restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)

        when: "customer tries to place order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
                query: [
                        'cartId'      : cart.code,
                        'securityCode': '123',
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: format,
                requestContentType: URLENC
        )

        then: "order is rejected properly placed"
        with(response) {
            status == SC_BAD_REQUEST
            isNotEmpty(data.errors)
            data.errors[0].message == 'Delivery mode is not set'
            data.errors[1].message == 'Payment info is not set'
        }
        where:
        format << [XML, JSON]
    }

    def "Customer places order with out of stock product: #format"() {
        given: "a customer with cart ready for ordering"
        def val = createAndAuthorizeCustomerWithCart(restClient, format)
        def customer = val[0]
        def cart = val[1]
        addProductToCartOnline(restClient, customer, cart.code, PRODUCT_REMOTE_CONTROL_TRIPOD)
        def address = createAddress(restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
        setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_STANDARD, format)
        createPaymentInfo(restClient, customer, cart.code)

        and: "product has gone out of stock"
        setStockStatus(restClient, InStockStatus.FORCEOUTOFSTOCK, PRODUCT_REMOTE_CONTROL_TRIPOD)

        when: "customer tries to place order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
                query: [
                        'cartId'      : cart.code,
                        'securityCode': '123',
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: format,
                requestContentType: URLENC
        )

        then: "order is rejected due to missing stock"
        with(response) {
            status == SC_BAD_REQUEST
            isNotEmpty(data.errors)
            data.errors[0].reason == 'noStock'
        }

        cleanup: "restore stock status"
        setStockStatus(restClient, InStockStatus.NOTSPECIFIED, PRODUCT_REMOTE_CONTROL_TRIPOD)

        where:
        format << [XML, JSON]
    }

    def "Customer tries to place an order when response: #format and a B2B base-site is used"() {
        given: "a customer with cart ready for ordering"
        def val = createAndAuthorizeCustomerWithCart(restClient, format)
        def customer = val[0]
        def cart = val[1]
        addProductToCartOnline(restClient, customer, cart.code, PRODUCT_POWER_SHOT_A480)
        addProductToCartPickup(restClient, customer, cart.code, PRODUCT_M340, STORE_SHINBASHI)
        def address = createAddress(restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart.code, address.id, format)
        setDeliveryModeForCart(restClient, customer, cart.code, DELIVERY_STANDARD, format)
        createPaymentInfo(restClient, customer, cart.code)

        when: "customer tries to place order"
        HttpResponseDecorator response = restClient.post(
                path: getBasePathWithB2BSite() + '/users/' + customer.id + '/orders',
                query: [
                        'cartId'      : cart.code,
                        'securityCode': '123',
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: format,
                requestContentType: URLENC
        )

        then: "request fails because a restricted B2C API endpoint is used from a B2B base-site"
        with(response) {
            status == SC_UNAUTHORIZED
            data.errors.any { it.type == 'AccessDeniedError' }
        }
        where:
        format << [XML, JSON]
    }

    def "Customer with two carts places separate order for each cart: #format"() {
        given: "authorized customer with two carts with different products in them"
        def val = createAndAuthorizeCustomerWithCart(restClient, format)
        def customer = val[0]
        def cart1 = val[1]
        addProductToCartOnline(restClient, customer, cart1.code, PRODUCT_REMOTE_CONTROL_TRIPOD)
        def address1 = createAddress(restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart1.code, address1.id, format)
        setDeliveryModeForCart(restClient, customer, cart1.code, DELIVERY_STANDARD, format)
        createPaymentInfo(restClient, customer, cart1.code)

        def cart2 = createCart(restClient, customer, format)
        addProductToCartPickup(restClient, customer, cart2.code, PRODUCT_POWER_SHOT_A480_2, STORE_SHINBASHI)
        def address2 = createAddress(GOOD_ADDRESS_US, restClient, customer)
        setDeliveryAddressForCart(restClient, customer, cart2.code, address2.id, format)
        setDeliveryModeForCart(restClient, customer, cart2.code, DELIVERY_PICKUP, format)
        createPaymentInfo(restClient, customer, cart2.code)

        when: "customer places order for each of the carts separately"
        HttpResponseDecorator response1 = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
                query: [
                        'cartId'      : cart1.code,
                        'securityCode': '123',
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: format,
                requestContentType: URLENC
        )

        HttpResponseDecorator response2 = restClient.post(
                path: getBasePathWithSite() + '/users/' + customer.id + '/orders',
                query: [
                        'cartId'      : cart2.code,
                        'securityCode': '123',
                        'fields'      : FIELD_SET_LEVEL_FULL
                ],
                contentType: format,
                requestContentType: URLENC
        )

        then: "orders are placed and one does not affect another"
        with(response1) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
            isNotEmpty(data.created)
            data.deliveryCost.value.toDouble() != 0.0
        }

        response1.data.code != response2.data.code

        with(response2) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_CREATED
            isNotEmpty(data.created)
            data.deliveryCost.value.toDouble() == 0.0
        }

        where:
        format << [XML, JSON]
    }
}
