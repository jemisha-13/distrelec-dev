package com.namics.distrelec.b2b.core.pdf.strategy.impl;

import com.namics.distrelec.b2b.core.pdf.service.DistPDFTemplateService;
import com.namics.distrelec.b2b.core.pdf.strategy.DistXslTransformationStrategy;
import de.hybris.platform.util.Utilities;
import org.apache.fop.apps.*;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.ConfigurationException;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.*;

public class DefaultDistXslTransformationStrategy implements DistXslTransformationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistXslTransformationStrategy.class);

    @Value("classpath:distrelecB2Bcore/pdf-generation/fop/")
    private Resource fopFolder;

    @Value("classpath:distrelecB2Bcore/pdf-generation/fop/dist-config.xml")
    private Resource fopConfig;

    @Autowired
    private DistPDFTemplateService distPDFTemplateService;

    @Override
    public ByteArrayOutputStream transformXslToPdf(String xml, String xslName) {
        InputStream xslInputStream = distPDFTemplateService.getXslForName(xslName);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            FopFactory fopFactory = new FopFactoryBuilder(fopFolder.getURI()).setConfiguration(getConfiguration())
                                                                             .setStrictFOValidation(false)
                                                                             .build();
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
            try (OutputStream out = new BufferedOutputStream(byteArrayOutputStream)) {
                final Fop fop = fopFactory.newFop("application/pdf", foUserAgent, out);
                final TransformerFactory factory = Utilities.getTransformerFactory();
                XSLTransformationUtil.transform(factory, xslInputStream, xml, fop);
            } catch (final FOPException | TransformerException e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        } catch (final IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
            return null;
        }
        return byteArrayOutputStream;
    }

    private Configuration getConfiguration() {
        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
        try {
            return cfgBuilder.build(fopConfig.getInputStream());
        } catch (IOException | ConfigurationException e) {
            LOG.warn("Error getting configuration file");
            return null;
        }
    }

}
