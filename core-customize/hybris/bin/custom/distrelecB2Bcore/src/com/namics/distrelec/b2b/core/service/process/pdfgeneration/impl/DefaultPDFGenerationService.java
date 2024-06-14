/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.process.pdfgeneration.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.process.pdfgeneration.PDFContext;
import com.namics.distrelec.b2b.core.service.process.pdfgeneration.PDFContextFactory;
import com.namics.distrelec.b2b.core.service.process.pdfgeneration.PDFGenerationService;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.commons.renderer.daos.RendererTemplateDao;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.util.Utilities;

public class DefaultPDFGenerationService extends AbstractBusinessService implements PDFGenerationService {

    private static final Logger LOG = Logger.getLogger(DefaultPDFGenerationService.class);

    private RendererService rendererService;
    private PDFContextFactory pdfContextFactory;
    private RendererTemplateDao rendererTemplateDao;
    private MediaService mediaService;
    private ConfigurationService configurationService;
    private I18NService i18nService;

    private String transformModelToXML(final BusinessProcessModel businessProcessModel, final String rendererTemplateCode, final BaseSiteModel baseSiteModel) {
        final List<RendererTemplateModel> results = getRendererTemplateDao().findRendererTemplatesByCode(baseSiteModel.getUid() + "-" + rendererTemplateCode);
        final RendererTemplateModel template = results.isEmpty() ? null : results.get(0);
        final PDFContext context = getPdfContextFactory().create(businessProcessModel, template);

        // print debug-statements to analyze https://jira.namics.com/browse/DISTRELEC-3465
        LOG.debug("Using template with pk: " + template.getPk());
        LOG.debug("Template code: " + template.getCode());
        LOG.debug("Current locale: " + getI18nService().getCurrentLocale());
        LOG.debug("Content of template: " + template.getContent());
        if (template.getContent() == null) {
            LOG.debug("template.getContent() returns null");
            MediaModel media = template.getContent(new Locale("de"));
            LOG.debug("Content with locale de: " + media);
            if (media != null) {
                printLogStatements(media);
            } else {
                media = getModelService().getAttributeValue(template, "content");
                LOG.debug("Getting content through modelservice: " + media);
                if (media != null) {
                    printLogStatements(media);
                }
            }
        } else {
            printLogStatements(template.getContent());
        }

        final StringWriter stringWriter = new StringWriter();
        getRendererService().render(template, context, stringWriter);

        return stringWriter.toString();
    }

    private void printLogStatements(final MediaModel media) {
        LOG.debug("Media Pk: " + media.getPk());
        LOG.debug("Media code: " + media.getCode());
    }

    private File transformXMLtoPDF(final String xml, final String xslMediaId, final BaseSiteModel baseSiteModel) {
        File tempfile = null;
        try {
            tempfile = File.createTempFile("pdffile_temp", ".pdf");

            final FopFactory fopFactory = new FopFactoryBuilder(new File(".").toURI())
                .setStrictFOValidation(false)
                .build();
            final FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            OutputStream out = null;

            try {
                out = new BufferedOutputStream(new FileOutputStream(tempfile));
                final Fop fop = fopFactory.newFop("application/pdf", foUserAgent, out);
                final TransformerFactory factory = Utilities.getTransformerFactory();

                final CatalogVersionModel catalogVersion = ((CMSSiteModel) baseSiteModel).getContentCatalogs().get(0).getActiveCatalogVersion();
                final MediaModel xslMediaModel = getMediaService().getMedia(catalogVersion, xslMediaId);
                final InputStream mediaStream = mediaService.getStreamFromMedia(xslMediaModel);

                final Transformer transformer = factory.newTransformer(new StreamSource(mediaStream));
                transformer.setParameter("versionParam", "2.0");
                final Source src = new StreamSource(new ByteArrayInputStream(xml.getBytes("UTF-8")));
                final Result res = new SAXResult(fop.getDefaultHandler());
                transformer.transform(src, res);

            } catch (final FOPException e) {
                LOG.error(e.getMessage(), e);
            } catch (final TransformerConfigurationException e) {
                LOG.error(e.getMessage(), e);
            } catch (final TransformerException e) {
                LOG.error(e.getMessage(), e);
            } finally {
                out.close();
            }

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return tempfile;
    }

    @Override
    public File transformModelToPDF(final BusinessProcessModel businessProcessModel, final String xslMediaId, final String rendererTemplateCode,
            final BaseSiteModel baseSiteModel) {
        final String xml = transformModelToXML(businessProcessModel, rendererTemplateCode, baseSiteModel);
        return transformXMLtoPDF(xml, xslMediaId, baseSiteModel);
    }

    protected RendererService getRendererService() {
        return rendererService;
    }

    @Required
    public void setRendererService(final RendererService rendererService) {
        this.rendererService = rendererService;
    }

    protected RendererTemplateDao getRendererTemplateDao() {
        return rendererTemplateDao;
    }

    @Required
    public void setRendererTemplateDao(final RendererTemplateDao rendererTemplateDao) {
        this.rendererTemplateDao = rendererTemplateDao;
    }

    public PDFContextFactory getPdfContextFactory() {
        return pdfContextFactory;
    }

    @Required
    public void setPdfContextFactory(final PDFContextFactory pdfContextFactory) {
        this.pdfContextFactory = pdfContextFactory;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    @Required
    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }
}
