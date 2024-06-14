package com.namics.distrelec.b2b.facades.common.populator.impl;

import org.junit.Test;

import java.util.Locale;

import static de.hybris.platform.testframework.Assert.assertEquals;

public class DefaultDistLocalizedPopulator_getLanguage_Test {

    final Locale locale = new Locale("en", "DE");

    DefaultDistLocalizedPopulator populator = new DefaultDistLocalizedPopulator();

    @Test
    public void testGetLanguage() {
        String language = populator.getLanguage(locale);

        assertEquals("en_DE", language);
    }
}
