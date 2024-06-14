package com.namics.distrelec.b2b.core.pdf.strategy;

import java.io.ByteArrayOutputStream;

public interface DistXslTransformationStrategy {

    ByteArrayOutputStream transformXslToPdf(String xml, String xslName);
}
