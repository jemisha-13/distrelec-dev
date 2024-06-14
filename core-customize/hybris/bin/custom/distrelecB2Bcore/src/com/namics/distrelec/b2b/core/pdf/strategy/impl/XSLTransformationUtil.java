package com.namics.distrelec.b2b.core.pdf.strategy.impl;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class XSLTransformationUtil {

    public static void transform(TransformerFactory factory, InputStream inputStream, String xml, Fop fop) throws TransformerException, FOPException {
        final Transformer transformer = factory.newTransformer(new StreamSource(inputStream));
        transformer.setParameter("versionParam", "2.0");
        final Source src = new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        final Result res = new SAXResult(fop.getDefaultHandler());
        transformer.transform(src, res);
    }
}
