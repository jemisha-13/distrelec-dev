package com.namics.distrelec.b2b.core.pdf.service;

import java.io.InputStream;

public interface DistPDFTemplateService {

    InputStream getTemplateForName(String templateName);

    InputStream getXslForName(String xslName);
}
