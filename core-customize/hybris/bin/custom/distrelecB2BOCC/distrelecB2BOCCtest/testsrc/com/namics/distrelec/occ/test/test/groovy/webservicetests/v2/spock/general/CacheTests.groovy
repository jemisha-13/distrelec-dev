/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.general

import com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.AbstractSpockTest
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import org.apache.http.HttpHeaders
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_OK

/**
 *
 */
@Unroll
@ManualTest
class CacheTests extends AbstractSpockTest {

    def "Checking Cache-Control header during GET on product search: #format"() {

        when: "user search for products"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/search',
                contentType: format,
                query: ['query': 'canon'],
                requestContentType: URLENC
        )

        then: "Cache-Control header is in place "
        with(response) {
            status == SC_OK
            response.containsHeader(HttpHeaders.CACHE_CONTROL)
            !response.getFirstHeader(HttpHeaders.CACHE_CONTROL).getValue().contains('public')
            response.getFirstHeader(HttpHeaders.CACHE_CONTROL).getValue().contains('max-age')
        }

        where:
        format << [XML, JSON]
    }

    def "Checking Cache-Control header during HEAD on product search: #format"() {

        when: "user search for products"
        HttpResponseDecorator response = restClient.head(
                path: getBasePathWithSite() + '/products/search',
                contentType: format,
                query: ['query': 'canon'],
                requestContentType: URLENC
        )

        then: "Cache-Control header is in place "
        with(response) {
            status == SC_OK
            response.containsHeader(HttpHeaders.CACHE_CONTROL)
            !response.getFirstHeader(HttpHeaders.CACHE_CONTROL).getValue().contains('public')
            response.getFirstHeader(HttpHeaders.CACHE_CONTROL).getValue().contains('max-age')
        }

        where:
        format << [XML, JSON]
    }

    def "Checking Cache-Control header during GET on checking product by product code: #format"() {

        when: "user search for products"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/products/3429337',
                contentType: format,
                requestContentType: URLENC
        )

        then: "Cache-Control header is in place"
        with(response) {
            status == SC_OK
            response.containsHeader(HttpHeaders.CACHE_CONTROL)
            response.getFirstHeader(HttpHeaders.CACHE_CONTROL).getValue().contains('private')
            response.getFirstHeader(HttpHeaders.CACHE_CONTROL).getValue().contains('max-age')
        }

        where:
        format << [XML, JSON]
    }

}
