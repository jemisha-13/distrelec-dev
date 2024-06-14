/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 *
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.general

import com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.*
import static org.apache.http.HttpStatus.SC_OK

@Unroll
@ManualTest
class StateTest extends AbstractSpockFlowTest {
    def "Checking JSESSIONID cookie in V2: #format"() {

        when: "a request to any v2 controller is made"
        HttpResponseDecorator response = restClient.get(
                path: getBasePathWithSite() + '/titles',
                contentType: format,
                requestContentType: URLENC
        )

        then: "no JSESSION id is set in 'Set-Cookie' header"
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
            !containsHeader("Set-Cookie") || !getFirstHeader("Set-Cookie").getValue().contains("JSESSIONID")
        }

        where:
        format << [JSON, XML]
    }
}
