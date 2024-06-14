package com.namics.distrelec.b2b.core.pdf.messages;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.Map;

public interface DistPDFMessagesStrategy {

    Map<String, Object> getMessagesForSiteAndStream(String templateName, CMSSiteModel cmsSite, LanguageModel language);
}
