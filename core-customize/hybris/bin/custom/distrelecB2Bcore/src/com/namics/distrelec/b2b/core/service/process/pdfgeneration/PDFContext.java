/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.process.pdfgeneration;

import java.util.Map;

import org.apache.velocity.VelocityContext;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.util.MediaUtil;

/**
 * velocity context for generation XML files for PDF generation.
 */
public abstract class PDFContext extends VelocityContext {

    public static final String LANGUAGE = "language";

    private Map<String, Object> messages;

    public Map<String, Object> getMessages() {
        return messages;
    }

    public void setMessages(final Map<String, Object> messages) {
        this.messages = messages;
    }

    public LanguageModel getLanguage() {
        return (LanguageModel) get(LANGUAGE);
    }

    public void init(final BusinessProcessModel businessProcessModel) {
        final LanguageModel language = getPDFLanguage(businessProcessModel);
        if (language != null) {
            put(LANGUAGE, language);
        }
    }

    protected String getMediaFilePath(final MediaModel mediaModel) {
        final StringBuilder builder = new StringBuilder("file:///");
        builder.append(MediaUtil.getTenantMediaReadDir()).append("/").append(mediaModel.getSubFolderPath())
                // FIXME
                // .append(MediaUtil.extractDataPkFromResourcePath(mediaModel.getURL())).append(".")
                .append(MediaUtil.getFileExtension(mediaModel.getRealFileName()));
        return builder.toString();

    }

    protected abstract LanguageModel getPDFLanguage(final BusinessProcessModel businessProcessModel);

}
