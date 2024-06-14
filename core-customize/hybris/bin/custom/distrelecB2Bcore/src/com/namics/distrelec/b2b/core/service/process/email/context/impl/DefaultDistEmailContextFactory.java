/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.process.email.context.impl;

import java.io.StringWriter;
import java.util.List;

import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import com.namics.distrelec.b2b.core.service.process.strategies.DistEmailTemplateTranslationStrategy;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.impl.DefaultEmailContextFactory;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * Extended factory used to create the velocity context for rendering emails
 */
public class DefaultDistEmailContextFactory extends DefaultEmailContextFactory {

    private static final String ABSTRACT_EMAIL_PAGE_TEMPLATE = "AbstractEmailPageTemplate";
    private static final String EMAIL_FOOTER_TEMPLATE = "Email_Footer";
    private static final String EMAIL_HEADER_TEMPLATE = "Email_Header";


    private ProcessContextResolutionStrategy b2bProcessContextStrategy;

    private CMSEmailPageService cmsEmailPageService;

    private DistEmailTemplateTranslationStrategy distEmailTemplateTranslationStrategy;

    @Autowired
    private DistrelecCMSSiteService distrelecCMSSiteService;

    public DistEmailTemplateTranslationStrategy getDistEmailTemplateTranslationStrategy() {
        return distEmailTemplateTranslationStrategy;
    }

    @Required
    public void setDistEmailTemplateTranslationStrategy(final DistEmailTemplateTranslationStrategy distEmailTemplateTranslationStrategy) {
        this.distEmailTemplateTranslationStrategy = distEmailTemplateTranslationStrategy;
    }

    @Override
    public AbstractDistEmailContext create(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel,
            final RendererTemplateModel renderTemplate) throws RuntimeException {

        final AbstractDistEmailContext emailContext = resolveEmailContext(renderTemplate);
        emailContext.init(businessProcessModel, emailPageModel);
        renderCMSSlotsIntoEmailContext(emailContext, emailPageModel, businessProcessModel);

        // parse and populate the variable at the end
        parseVariablesIntoEmailContext(emailContext);

        // Render translated messages from the email message resource bundles into the email context.
        final String languageIso = emailContext.getEmailLanguage() == null ? null : emailContext.getEmailLanguage().getIsocode();
        emailContext.setMessages(getDistEmailTemplateTranslationStrategy().translateMessagesForTemplate(renderTemplate, languageIso,
                getContextResolutionStrategy().getCmsSite(businessProcessModel).getUid()));

        final BaseSiteModel site = getContextResolutionStrategy().getCmsSite(businessProcessModel);

        // render header template and store it to header attribute of email context
        emailContext.setHeader(renderTemplateByCode(businessProcessModel, emailPageModel, site.getUid() + "-" + EMAIL_HEADER_TEMPLATE));
        // render footer template and store it to footer attribute of email context
        emailContext.setFooter(renderTemplateByCode(businessProcessModel, emailPageModel, site.getUid() + "-" + EMAIL_FOOTER_TEMPLATE));

        return emailContext;
    }

    protected String renderTemplateByCode(final BusinessProcessModel businessProcessModel, final EmailPageModel originEmailPageModel, final String templateCode) {

        EmailPageModel emailPageModel;
        ContentCatalogModel originContentCatalog = (ContentCatalogModel) originEmailPageModel.getCatalogVersion().getCatalog();
        if (originContentCatalog.getSuperCatalog() != null) {
            emailPageModel = originEmailPageModel;
        } else {
            // international content catalog, so get site abstract email page
            CatalogVersionModel contentCatalogModel = getB2bProcessContextStrategy().getContentCatalogVersion(businessProcessModel);
            emailPageModel = getCmsEmailPageService().getEmailPageForFrontendTemplate(ABSTRACT_EMAIL_PAGE_TEMPLATE, contentCatalogModel);
        }

        // get render template from code
        final List<RendererTemplateModel> results = getRendererTemplateDao().findRendererTemplatesByCode(templateCode);
        final RendererTemplateModel renderTemplate = results.isEmpty() ? null : results.get(0);

        // get context and set properties
        final AbstractDistEmailContext context = resolveEmailContext(renderTemplate);
        context.init(businessProcessModel, emailPageModel);
        renderCMSSlotsIntoEmailContext(context, emailPageModel, businessProcessModel);
        parseVariablesIntoEmailContext(context);
        final String languageIso = context.getEmailLanguage() == null ? null : context.getEmailLanguage().getIsocode();
        context.setMessages(getDistEmailTemplateTranslationStrategy().translateMessagesForTemplate(renderTemplate, languageIso,
                getContextResolutionStrategy().getCmsSite(businessProcessModel).getUid()));

        // render and return as string
        final StringWriter text = new StringWriter();
        getRendererService().render(renderTemplate, context, text);
        return text.toString();
    }

    public ProcessContextResolutionStrategy getB2bProcessContextStrategy() {
        return b2bProcessContextStrategy;
    }

    @Required
    public void setB2bProcessContextStrategy(final ProcessContextResolutionStrategy b2bProcessContextStrategy) {
        this.b2bProcessContextStrategy = b2bProcessContextStrategy;
    }

    public CMSEmailPageService getCmsEmailPageService() {
        return cmsEmailPageService;
    }

    @Required
    public void setCmsEmailPageService(final CMSEmailPageService cmsEmailPageService) {
        this.cmsEmailPageService = cmsEmailPageService;
    }

    public DistrelecCMSSiteService getDistrelecCMSSiteService() {
        return distrelecCMSSiteService;
    }

    public void setDistrelecCMSSiteService(final DistrelecCMSSiteService distrelecCMSSiteService) {
        this.distrelecCMSSiteService = distrelecCMSSiteService;
    }
}
