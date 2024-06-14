package com.distrelec.smartedit.webservices.strategy.impl;

import com.distrelec.smartedit.webservices.strategy.DistrelecBaseLocaleStrategy;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.Optional;

public class DefaultDistrelecBaseLocaleStrategy implements DistrelecBaseLocaleStrategy {

    private static Logger LOG = LoggerFactory.getLogger(DefaultDistrelecBaseLocaleStrategy.class);

    @Autowired
    private I18NService i18NService;

    @Override
    public Locale setLocaleToBase(Locale locale) {
        Locale baseLocale = Optional.ofNullable(locale)
                .map(Locale::getLanguage)
                .map(Locale::new)
                .orElse(Locale.ENGLISH);
        LOG.debug("Setting base locale as {}", baseLocale);
        i18NService.setCurrentLocale(baseLocale);
        return locale != null ? locale : baseLocale;
    }

    @Override
    public void revertLocale(Locale locale) {
        LOG.debug("Reverting locale to {}", locale);
        Optional.ofNullable(locale)
                .ifPresent(i18NService::setCurrentLocale);
    }
}
