/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.items;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.namics.hybris.toolbox.spring.SpringUtil;

/**
 * Helper class for language operations.
 * 
 * @author pnueesch, namics ag
 * @since MGB PIM 1.0
 */

public class LanguageUtil {

    public static final Language GERMAN;
    public static final Language FRENCH;
    public static final Language ITALIAN;

    public static final LanguageModel GERMAN_MODEL;
    public static final LanguageModel FRENCH_MODEL;
    public static final LanguageModel ITALIAN_MODEL;

    public static List<Locale> LANGUAGES_TO_CHECK = new ArrayList<Locale>();

    private final static ModelService modelService = SpringUtil.getBean("modelService", ModelService.class);
    private final static CommonI18NService commonI18NService = SpringUtil.getBean("commonI18NService", CommonI18NService.class);

    static {
        GERMAN = modelService.getSource(commonI18NService.getLanguage(Locale.GERMAN.getLanguage()));
        FRENCH = modelService.getSource(commonI18NService.getLanguage(Locale.FRENCH.getLanguage()));
        ITALIAN = modelService.getSource(commonI18NService.getLanguage(Locale.ITALIAN.getLanguage()));

        GERMAN_MODEL = commonI18NService.getLanguage(Locale.GERMAN.getLanguage());
        FRENCH_MODEL = commonI18NService.getLanguage(Locale.FRENCH.getLanguage());
        ITALIAN_MODEL = commonI18NService.getLanguage(Locale.ITALIAN.getLanguage());

        LANGUAGES_TO_CHECK.add(Locale.ENGLISH);
        LANGUAGES_TO_CHECK.add(Locale.GERMAN);
        LANGUAGES_TO_CHECK.add(Locale.FRENCH);
        LANGUAGES_TO_CHECK.add(Locale.ITALIAN);
    }

    /**
     * Returns a matching <code>Language</code> for a given <code>Locale</code>.
     * 
     * @param locale
     *            The given <code>Locale</code> to convert.
     * @return The <code>Language</code> best matching the <code>Locale</code>.
     */
    public static LanguageModel locale2LanguageModel(final Locale locale) {
        if (LANGUAGES_TO_CHECK.contains(locale)) {
            return commonI18NService.getLanguage(locale.getLanguage());
        } else {
            return commonI18NService.getLanguage(Locale.GERMAN.getLanguage());
        }
    }

}
