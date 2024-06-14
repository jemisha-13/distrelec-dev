/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DistXmlUtils {

    private static final Class<XmlType> ANNOTATION_CLASS_FOR_XML = XmlType.class;
    private static final Logger LOG = LogManager.getLogger(DistXmlUtils.class);

    public static <T> String soapToString(final T soapFragment, final Class<T> clazz) {
        
        if(null == soapFragment)
        {
            return "DistXmlUtils: soapToString: soapFragment: null";
        }

        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(soapFragment.getClass());
            final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

            final Writer stringWriter = new StringWriter();
            jaxbMarshaller.marshal(new JAXBElement<>(new QName("uri", soapFragment.getClass().toString()), clazz, soapFragment), stringWriter);
            // jaxbMarshaller.marshal(obj, System.out);
            return stringWriter.toString();
        } catch (final Exception ex) {
            LOG.warn(ex, ex);
            // In case of errors go ahead and do nothing
            return StringUtils.EMPTY;
        }
    }

    /**
     * Marshals an object annotated with {@code ANNOTATION_CLASS_FOR_XML} to String
     * 
     * @param soapFragment
     *            the Object to marshal (it has to be annotated with {@code ANNOTATION_CLASS_FOR_XML})
     * @return the XML String of the marshalled {@code soapFragment}, its toString() if the {@code soapFragment} is not a soap fragment,
     *         "null" String if the {@code soapFragment} is null
     */
    public static <T> String soapToString(final T soapFragment) {
        if (soapFragment == null) {
            LOG.debug("soapFragment is null");
            return "null";
        }
        final Class<T> clazz = (Class<T>) soapFragment.getClass();
        if (clazz.isAnnotationPresent(ANNOTATION_CLASS_FOR_XML)) {
            return soapToString(soapFragment, clazz);
        } else {
            LOG.debug(soapFragment + " of class: " + clazz + " not annotated with: " + ANNOTATION_CLASS_FOR_XML.getSimpleName());
            return soapFragment.toString();
        }
    }
}
