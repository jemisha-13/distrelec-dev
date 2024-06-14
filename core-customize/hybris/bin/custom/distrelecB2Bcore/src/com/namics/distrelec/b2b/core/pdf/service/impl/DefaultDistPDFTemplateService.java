package com.namics.distrelec.b2b.core.pdf.service.impl;

import com.namics.distrelec.b2b.core.pdf.service.DistPDFTemplateService;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

public class DefaultDistPDFTemplateService implements DistPDFTemplateService, ResourceLoaderAware {

    private static final String TEMPLATE_ROOT_FOLDER = "distrelecB2Bcore/pdf-generation";

    private static final String TEMPLATE_XSL_FOLDER = TEMPLATE_ROOT_FOLDER + "/xsl";

    private static final String TEMPLATE_VM_FOLDER = TEMPLATE_ROOT_FOLDER + "/vm";

    private ResourceLoader resourceLoader;

    @Override
    public InputStream getTemplateForName(String templateName) {
        return getInputStreamForPath(TEMPLATE_VM_FOLDER + "/" + templateName);
    }

    @Override
    public InputStream getXslForName(String xslName) {
        return getInputStreamForPath(TEMPLATE_XSL_FOLDER + "/" + xslName);
    }

    private InputStream getInputStreamForPath(String path) {
        Resource resource = resourceLoader.getResource(path);
        if (resource == null) {
            return null;
        }
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            //silent catch
        }
        return null;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader =resourceLoader;
    }
}
