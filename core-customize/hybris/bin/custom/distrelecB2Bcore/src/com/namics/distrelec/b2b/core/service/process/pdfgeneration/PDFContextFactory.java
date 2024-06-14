/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.process.pdfgeneration;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.process.strategies.DistEmailTemplateTranslationStrategy;

import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * Factory used to create the velocity context for rendering XMLs for pdf generation
 */
public class PDFContextFactory {

    private DistEmailTemplateTranslationStrategy emailTemplateTranslationStrategy;

    private ProcessContextResolutionStrategy contextResolutionStrategy;

    public PDFContext create(final BusinessProcessModel businessProcessModel, final RendererTemplateModel rendererTemplate) {

        final PDFContext pdfContext = resolvePDFContext(rendererTemplate);
        pdfContext.init(businessProcessModel);

        final String languageIso = pdfContext.getLanguage() == null ? null : pdfContext.getLanguage().getIsocode();
        pdfContext.setMessages(getEmailTemplateTranslationStrategy().translateMessagesForTemplate(rendererTemplate, languageIso,
                getContextResolutionStrategy().getCmsSite(businessProcessModel).getUid()));

        return pdfContext;
    }

    protected <T extends PDFContext> T resolvePDFContext(final RendererTemplateModel renderTemplate) {
        try {
            final Class<T> contextClass = (Class<T>) Class.forName(renderTemplate.getContextClass());
            final Map<String, T> pdfContexts = Registry.getApplicationContext().getBeansOfType(contextClass);
            if (MapUtils.isNotEmpty(pdfContexts)) {
                return pdfContexts.entrySet().iterator().next().getValue();
            } else {
                throw new JaloSystemException("Cannot find bean in application context for context class [" + contextClass + "]");
            }
        } catch (final ClassNotFoundException e) {
            throw new JaloSystemException(e, "Cannot find pdf context class", -1);
        }
    }

    public ProcessContextResolutionStrategy getContextResolutionStrategy() {
        return contextResolutionStrategy;
    }

    @Required
    public void setContextResolutionStrategy(final ProcessContextResolutionStrategy contextResolutionStrategy) {
        this.contextResolutionStrategy = contextResolutionStrategy;
    }

    public DistEmailTemplateTranslationStrategy getEmailTemplateTranslationStrategy() {
        return emailTemplateTranslationStrategy;
    }

    @Required
    public void setEmailTemplateTranslationStrategy(final DistEmailTemplateTranslationStrategy emailTemplateTranslationStrategy) {
        this.emailTemplateTranslationStrategy = emailTemplateTranslationStrategy;
    }

}
