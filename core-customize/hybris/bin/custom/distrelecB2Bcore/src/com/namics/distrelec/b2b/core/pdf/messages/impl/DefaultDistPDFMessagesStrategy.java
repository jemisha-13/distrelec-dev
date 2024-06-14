package com.namics.distrelec.b2b.core.pdf.messages.impl;

import com.namics.distrelec.b2b.core.pdf.messages.DistPDFMessagesStrategy;
import com.namics.distrelec.b2b.core.pdf.service.DistPDFTemplateService;
import com.namics.distrelec.b2b.core.service.process.strategies.impl.DefaultDistEmailTemplateTranslationStrategy;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commons.renderer.exceptions.RendererException;
import de.hybris.platform.core.model.c2l.LanguageModel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultDistPDFMessagesStrategy extends DefaultDistEmailTemplateTranslationStrategy implements DistPDFMessagesStrategy{

    @Autowired
    private DistPDFTemplateService distPDFTemplateService;

    @Override
    public Map<String, Object> getMessagesForSiteAndStream(String templateName, CMSSiteModel cmsSite, LanguageModel language) {
        InputStream inputStream = distPDFTemplateService.getTemplateForName(templateName);
        List<String> propertyFiles = getListOfPropertyFiles(inputStream, cmsSite, language);
        return getPropertiesFromRootPaths(propertyFiles);
    }

    private List<String> getListOfPropertyFiles(InputStream inputStream, CMSSiteModel cmsSite, LanguageModel language) {
        List<String> messageSources = new ArrayList<>();
        String siteUid = cmsSite.getUid();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = reader.readLine();
            if (StringUtils.isNotEmpty(line)) {
                String messageSource = null;

                if (line.contains("## messageSource=")) {
                    messageSource = StringUtils.substring(line, line.indexOf("## messageSource=") + 17);
                } else if (line.contains("##messageSource=")) {
                    messageSource = StringUtils.substring(line, line.indexOf("##messageSource=") + 16);
                }

                if (StringUtils.isNotEmpty(messageSource)) {
                    if (messageSource.contains(LANG_ATTR)) {
                        // needs to all message sources for all fallback languages
                        addMessageSourcesPerLanguages(messageSources, messageSource, siteUid, language);
                    } else {
                        addMessageSource(messageSources, messageSource, siteUid);
                    }
                }
            }
            return messageSources;
        } catch (final IOException e) {
            throw new RendererException("Problem during rendering", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
