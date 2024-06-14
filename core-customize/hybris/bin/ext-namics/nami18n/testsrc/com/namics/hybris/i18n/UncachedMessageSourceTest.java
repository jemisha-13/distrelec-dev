/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.NoSuchMessageException;

import com.namics.hybris.toolbox.items.SessionUtil;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

/**
 * Test class for {@link UncachedDaoMessageSource}.
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.2
 * 
 */
public class UncachedMessageSourceTest extends ServicelayerTransactionalTest {

    protected UncachedDaoMessageSource uncachedMessageSource;

    /**
     * Setup before test case.
     * 
     * @throws Exception
     *             java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        uncachedMessageSource = SpringUtil.getBean("namicsI18n.uncached.messageSource", UncachedDaoMessageSource.class);
        createCoreData();
        createDefaultCatalog();
        getOrCreateLanguage("de");
        getOrCreateLanguage("en");
        importCsv("/nami18n/test/sampledata.impex", "UTF-8");
        SessionUtil.setTestUserWithDefaultLanguage(SessionUtil.USER_ADMIN);
    }

    /**
     * Test method for
     * {@link com.namics.hybris.i18n.UncachedDaoMessageSource#resolveCodeWithoutArguments(java.lang.String, java.util.Locale)} .
     */
    @Test
    public void testResolveCodeStringLocale() {

        final String[] args = new String[] { "Meine Dokumente" };
        Locale locale;
        locale = new Locale("de");
        assertEquals("Bitte Inhalte Meine Dokumente lesen.", uncachedMessageSource.getMessage("pleaseRead", args, locale));
        locale = new Locale("en");
        assertEquals("Please read content Meine Dokumente.", uncachedMessageSource.getMessage("pleaseRead", args, locale));
    }

    /**
     * Test method for
     * {@link com.namics.hybris.i18n.UncachedDaoMessageSource#resolveCodeWithoutArguments(java.lang.String, java.util.Locale)} .
     */
    @Test
    public void testResolveCodeWithoutArgumentsStringLocale() {
        Locale locale;
        locale = new Locale("de");
        assertEquals("Willkommen", uncachedMessageSource.getMessage("welcome", null, locale));
        locale = new Locale("en");
        assertEquals("Welcome", uncachedMessageSource.getMessage("welcome", null, locale));
    }

    /**
     * Test method for
     * {@link com.namics.hybris.i18n.UncachedDaoMessageSource#resolveCodeWithoutArguments(java.lang.String, java.util.Locale)} .
     */
    @Test
    public void testResolveCodeNonExistent() {

        final String[] args = new String[] { "Meine Dokumente" };
        Locale locale;
        locale = new Locale("de");
        try {
            final String message = uncachedMessageSource.getMessage("cantRead", args, locale);
            fail("Es sollte eine Exception geworfen werden. Stattdessen kam '" + message + "' zurück.");

        } catch (final NoSuchMessageException e) {
            // that was what we expected
        }
    }

}
