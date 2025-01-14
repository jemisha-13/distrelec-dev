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
class LanguagesTest extends AbstractSpockFlowTest {

    private languageList = ['ja', 'en', 'de', 'zh']


    def "Client retrieves supported languages: #format"() {

        when:
        HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/languages', contentType: format)

        then:
        with(response) {
            if (isNotEmpty(data) && isNotEmpty(data.errors)) println(data)
            status == SC_OK
            isNotEmpty(data.languages)
            data.languages.size() == languageList.size()
            data.languages.findAll { language ->
                language.isocode in languageList
            }.size() == languageList.size()
        }

        where:
        format << [JSON, XML]
    }
}
