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
class CurrenciesTest extends AbstractSpockFlowTest {

    private currencyList = ['USD', 'JPY']

    def "Client retrieves supported currencies: #format"() {

        when:
        HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/currencies', contentType: format)

        then:
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
            isNotEmpty(data.currencies)
            data.currencies.size() == currencyList.size()
            data.currencies.findAll { currency ->
                currency.isocode in currencyList
            }.size() == currencyList.size()
        }

        where:
        format << [JSON, XML]
    }
}
