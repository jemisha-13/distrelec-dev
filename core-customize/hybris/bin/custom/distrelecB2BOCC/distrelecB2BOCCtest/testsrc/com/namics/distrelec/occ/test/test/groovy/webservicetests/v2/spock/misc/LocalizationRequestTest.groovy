/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.misc

import com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_BAD_REQUEST
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class LocalizationRequestTest extends AbstractSpockFlowTest {
    private PRODUCT_CODE = 2053226
    private PRODUCT_DESCRIPTION_ENGLISH = "description_en"
    private PRODUCT_DESCRIPTION_GERMAN = "description_de"

    private LANGUAGE_ENGLISH = 'en'
    private LANGUAGE_GERMAN = 'de'
    private LANGUAGE_INVALID = 'abcde'
    private CURRENCY_USD = 'USD'
    private CURRENCY_JPY = 'JPY'
    private CURRENCY_INVALID = 'FGHIJ'

    def "Send a request with default currency and language parameters when request: #format"() {
        when: "a product is requested no currency or language parameter"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/' + PRODUCT_CODE,
                contentType: format,
                requestContentType: URLENC
        )

        then: "a product is returned with default language and currency"
        with(response) {
            status == SC_OK
            data.description == PRODUCT_DESCRIPTION_ENGLISH
            data.price.currencyIso == CURRENCY_USD
        }

        where:
        format << [JSON, XML]
    }

    def "Send a request with language parameter specified when request: #format"() {
        when: "a product is requested with language parameter specified"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/' + PRODUCT_CODE,
                contentType: format,
                query: ['lang': LANGUAGE_GERMAN],
                requestContentType: URLENC
        )

        then: "the product is returned with the description in the specified language"
        with(response) {
            status == SC_OK
            data.description == PRODUCT_DESCRIPTION_GERMAN
            data.price.currencyIso == CURRENCY_USD
        }

        where:
        format << [JSON, XML]
    }

    def "Send a request with currency parameter specified when request: #format"() {
        when: "a product is requested with currency parameter specified"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/' + PRODUCT_CODE,
                contentType: format,
                query: ['curr': CURRENCY_JPY],
                requestContentType: URLENC
        )

        then: "the product is returned with the price in the specified currency"
        with(response) {
            status == SC_OK
            data.description == PRODUCT_DESCRIPTION_ENGLISH
            data.price.currencyIso == CURRENCY_JPY
        }

        where:
        format << [JSON, XML]
    }

    def "Send a request with invalid language parameter specified when request: #format"() {
        when: "a product is requested with invalid language parameter specified"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/' + PRODUCT_CODE,
                contentType: format,
                query: ['lang': LANGUAGE_INVALID],
                requestContentType: URLENC
        )

        then: "an error is thrown"
        with(response) {
            status == SC_BAD_REQUEST
            isNotEmpty(data.errors)
            data.errors[0].type == 'UnsupportedLanguageError'
            data.errors[0].message == 'Language  ' + LANGUAGE_INVALID + ' is not supported'
        }

        where:
        format << [JSON, XML]
    }

    def "Send a request with invalid currency parameter specified when request: #format"() {
        when: "a product is requested with invalid currency parameter specified"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/' + PRODUCT_CODE,
                contentType: format,
                query: ['curr': CURRENCY_INVALID],
                requestContentType: URLENC
        )

        then: "an error is thrown"
        with(response) {
            status == SC_BAD_REQUEST
            isNotEmpty(data.errors)
            data.errors[0].type == 'UnsupportedCurrencyError'
            data.errors[0].message == 'Currency ' + CURRENCY_INVALID + ' is not supported'
        }

        where:
        format << [JSON, XML]
    }
}
