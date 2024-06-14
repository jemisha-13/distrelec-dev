package com.distrelec.smartedit.cmsfacades.languages.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Environment;
import de.hybris.platform.cmsfacades.languages.impl.DefaultLanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.session.SessionService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.toList;

public class DefaultDistLanguageFacade extends DefaultLanguageFacade {

    private SessionService sessionService;
    @Override
    public List<LanguageData> getLanguages() {
        // set user as internal
        getSessionService().setAttribute(Environment.INTERNAL_USER, true);

        List<LanguageData> languages = super.getLanguages();
        Optional<LanguageData> englishLanguage = languages.stream()
                .filter(language -> language.getIsocode().equals(Locale.ENGLISH.getLanguage()))
                .findFirst();

        englishLanguage.ifPresent(english -> {
            languages.stream().forEach(language -> language.setRequired(false));
            english.setRequired(true);
        });

        return languages.stream()
                    .sorted(comparing(LanguageData::isRequired, reverseOrder()).thenComparing(LanguageData::getRank, nullsLast(reverseOrder())))
                    .collect(toList());
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
