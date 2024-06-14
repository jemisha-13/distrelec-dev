/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class TestSoapUtils {

    public static <T> T unmarshall(final InputStream resourceStream, final Class objectFactoryClass) {

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(objectFactoryClass);

            // final JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceSearchResponse.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            final JAXBElement<T> responseInvoice = (JAXBElement<T>) jaxbUnmarshaller.unmarshal(resourceStream);
            return responseInvoice.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
