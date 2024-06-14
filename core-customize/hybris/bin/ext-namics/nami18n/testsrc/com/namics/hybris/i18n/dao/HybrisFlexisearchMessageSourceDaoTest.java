/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.i18n.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.namics.commons.i18n.exception.DataAccessException;
import com.namics.commons.i18n.model.MessageCacheKey;
import com.namics.hybris.toolbox.items.SessionUtil;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

/**
 * Test class for {@link HybrisFlexisearchMessageSourceDao}.
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.2
 * 
 */
public class HybrisFlexisearchMessageSourceDaoTest extends ServicelayerTransactionalTest {

    protected HybrisFlexisearchMessageSourceDao messageSourceDao;

    /**
     * Setup before test case.
     * 
     * @throws Exception
     *             java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        messageSourceDao = SpringUtil.getBean("hybrisFlexisearchMessageSourceDao", HybrisFlexisearchMessageSourceDao.class);
        getOrCreateLanguage("de");
        getOrCreateLanguage("en");

        createCoreData();
        createDefaultCatalog();

        SessionUtil.setTestUserWithDefaultLanguage(SessionUtil.USER_ADMIN);

        importCsv("/nami18n/test/sampledata.impex", "utf-8");
    }

    /**
     * Test method for {@link com.namics.hybris.i18n.dao.HybrisFlexisearchMessageSourceDao#getAllMessages()} .
     * 
     * @throws Exception
     *             java.lang.Exception
     */
    @Test
    public void testGetAllMessages() throws Exception {
        final Map<MessageCacheKey, String> map = messageSourceDao.getAllMessages();
        assertTrue("At least two entries for each language were expected.", map.size() >= 4);

    }

    /**
     * Test method for {@link com.namics.hybris.i18n.dao.HybrisFlexisearchMessageSourceDao#getAvailableLanguages()} .
     * 
     * @throws Exception
     *             java.lang.Exception
     */
    @Test
    public void testGetAvailableLanguages() throws Exception {
        final List<Locale> languageList = messageSourceDao.getAvailableLanguages();
        assertTrue("More than one language were expected.", languageList.size() > 1);
    }

    /**
     * Test method for
     * {@link com.namics.hybris.i18n.dao.HybrisFlexisearchMessageSourceDao#getResourceMessage(java.lang.String, java.util.Locale)} .
     * 
     * @throws DataAccessException
     *             DataAccessException
     */
    @Test
    public void testGetResourceMessage() throws DataAccessException {
        Locale locale;
        locale = new Locale("de");
        assertEquals("Willkommen", messageSourceDao.getResourceMessage("welcome", locale));
        locale = new Locale("en");
        assertEquals("Welcome", messageSourceDao.getResourceMessage("welcome", locale));
    }

    /**
     * Test method for
     * {@link com.namics.hybris.i18n.dao.HybrisFlexisearchMessageSourceDao#getResourceMessage(java.lang.String, java.util.Locale)} .
     * 
     * @throws DataAccessException
     *             DataAccessException
     */
    @Test
    public void testGetResourceMessageWithCountry() throws DataAccessException {
        Locale locale;
        locale = new Locale("de", "CH");
        assertEquals("Willkommen", messageSourceDao.getResourceMessage("welcome", locale));
    }

    /**
     * Test method for
     * {@link com.namics.hybris.i18n.dao.HybrisFlexisearchMessageSourceDao#getResourceMessage(java.lang.String, java.util.Locale)} .
     */
    @Test
    public void testGetResourceMessageNoResult() {
        final Locale locale = new Locale("de");
        try {
            assertNull("The call with the message resource key 'quatsch' didn't end in a exception.", messageSourceDao.getResourceMessage("quatsch", locale));
        } catch (final DataAccessException e) {
            fail("The call with the message resource key 'quatsch' didn't end in a null result.");
        }
    }

}
