/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.misc

import com.namics.distrelec.occ.test.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import de.hybris.bootstrap.annotations.ManualTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
class DeliveryCountriesTest extends AbstractSpockFlowTest {


    def "Client retrieves available delivery countries: #format"() {

        when:
        HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/deliverycountries', contentType: format)

        then:
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
            isNotEmpty(data.countries)
            data.countries.size() > 0
        }

        where:
        format << [JSON, XML]
    }
}
