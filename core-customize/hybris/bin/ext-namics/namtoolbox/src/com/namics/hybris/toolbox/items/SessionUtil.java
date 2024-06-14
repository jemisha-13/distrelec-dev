/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.items;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Utilities;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.namics.hybris.toolbox.spring.SpringUtil;

/**
 * Helper class for session operations.
 * 
 * @author pnueesch, namics ag
 * @since MGB PIM 1.0
 */
public class SessionUtil {

    /** username for the admin. */
    public static final String USER_ADMIN = "admin";

    /** Used logger instance. */
    private static final Logger LOG = Logger.getLogger(SessionUtil.class.getName());

    private static final ModelService MODEL_SERVICE = SpringUtil.getBean("modelService", ModelService.class);
    private static final CommonI18NService COMMON_I18N_SERVICE = SpringUtil.getBean("commonI18NService", CommonI18NService.class);
    private static final UserService USER_SERVICE = SpringUtil.getBean("userService", UserService.class);

    /**
     * Set user for the current jalo session with the default language. WARNING: Don't use it for test cases! Use
     * <code>setTestUserWithDefaultLanguage()</code>
     * 
     * @param name
     *            the name of the user
     */
    public static void setUserWithDefaultLanguage(final String name) {
        activateMasterSession();
        setUser(name);
        setDefaultLanguage();
    }

    /**
     * Set user for the test session with the default language.
     * 
     * @param name
     *            the name of the user
     */
    public static void setTestUserWithDefaultLanguage(final String name) {
        activateTestSession();
        setUser(name);
        setDefaultLanguage();
    }

    /**
     * Set the default language GERMAN for the current jalo session.
     */
    public static void setDefaultLanguage() {
        setLanguage(Locale.GERMAN);
    }

    /**
     * Set the language for the current jalo session with a <code>Locale</code>.
     * 
     * @param locale
     *            the locale
     */
    public static void setLanguage(final Locale locale) {
        Language language = null;
        try {
            language = MODEL_SERVICE.getSource(COMMON_I18N_SERVICE.getLanguage(locale.getLanguage()));
        } catch (final JaloItemNotFoundException ejinfe) {
            language = MODEL_SERVICE.getSource(COMMON_I18N_SERVICE.getLanguage(Locale.GERMAN.getLanguage()));
        }
        final JaloSession jSession = getCurrentSession();
        jSession.getSessionContext().setLanguage(language);
        LOG.debug("Set language " + language + ". Language is in session " + jSession.getSessionContext().getLanguage() + " and Locale is set to "
                + jSession.getSessionContext().getLocale() + ". Locale over language is " + jSession.getSessionContext().getLanguage());
    }

    /**
     * Set the locale for the current jalo session with a <code>Locale</code>.
     * 
     * @param locale
     *            the locale
     */
    public static void setLocale(final Locale locale) {
        final JaloSession jaloSession = JaloSession.getCurrentSession();
        jaloSession.getSessionContext().setLocale(locale);
        jaloSession.getSessionContext().setAttribute("userSetLocale", Boolean.TRUE);
        LOG.debug("Set locale " + locale + "Language is in session " + jaloSession.getSessionContext().getLanguage() + " and Locale is set to "
                + jaloSession.getSessionContext().getLocale() + ". Locale over language is " + jaloSession.getSessionContext().getLanguage());
    }

    private static void setUser(final String name) {
        final JaloSession jSession = getCurrentSession();
        final User user = MODEL_SERVICE.getSource(USER_SERVICE.getUserForUID(name));
        jSession.setUser(user);
        LOG.debug("Set user to " + name);
    }

    private static void activateMasterSession() {
        Registry.activateMasterTenant();
    }

    private static void activateTestSession() {

        if (!"junit".equalsIgnoreCase(Registry.getCurrentTenant().getTenantID())) {
            Utilities.setJUnitTenant();
        }
    }

    /**
     * Returns the current session and create a new one if it is closed.
     * 
     * @return The current jalo session
     */
    public static JaloSession getCurrentSession() {
        JaloSession jSession = JaloSession.getCurrentSession();
        if (jSession.isClosed()) {
            try {
                jSession = JaloConnection.getInstance().createAnonymousCustomerSession();
            } catch (final JaloSecurityException e) {
                LOG.error("Error during session creation.", e);
            }
        }
        return jSession;
    }

    /**
     * get the {@link Language} from the current session.
     * 
     * @return current language
     */
    public static Language getLanguage() {
        final JaloSession jSession = getCurrentSession();
        return jSession.getSessionContext().getLanguage();
    }

    /**
     * get the iso code of the language from the current session.
     * 
     * @return current iso code
     */
    public static String getLanguageIsoCode() {
        return ((LanguageModel) MODEL_SERVICE.get(getLanguage())).getIsocode();
    }

    /**
     * Get the current session locale.
     * 
     * @return The current session locale
     */
    public static Locale getLocale() {
        final JaloSession jaloSession = JaloSession.getCurrentSession();

        if (jaloSession.getSessionContext().getAttribute("userSetLocale") != null) {
            Locale locale = jaloSession.getSessionContext().getLocale();
            // FIXME the country is sometimes empty, not sure why. This is a quick fix
            if (StringUtils.isEmpty(locale.getCountry())) {
                locale = new Locale(locale.getLanguage(), "CH");
                setLocale(locale);
            }
            return jaloSession.getSessionContext().getLocale();
        }
        return null;

    }

    /**
     * set the default catalog id.
     * 
     * @param id
     *            the id
     */
    public static void setDefaultCatalogId(final String id) {
        final JaloSession jSession = getCurrentSession();
        jSession.setAttribute("defaultCatalogId", id);
    }

    /**
     * return the default catalog id.
     * 
     * @return default catalog id
     */
    public static String getDefaultCatalogId() {
        final JaloSession jSession = getCurrentSession();
        return (String) jSession.getAttribute("defaultCatalogId");
    }

    public static boolean isFirstRequest() {
        final JaloSession jSession = getCurrentSession();
        return (jSession.getAttribute("firstRequestDone") != null ? false : true);
    }

    public static void setFirstRequestDone() {
        final JaloSession jSession = getCurrentSession();
        jSession.setAttribute("firstRequestDone", Boolean.TRUE);
    }
    
    public static boolean isShippingModeSelectable(String deliveryMode) {
        final JaloSession jSession = getCurrentSession();
        final boolean deMode = null != jSession.getAttribute("deliveryMode#" + deliveryMode.replace("SAP_", ""))
                ? (boolean) jSession.getAttribute("deliveryMode#" + deliveryMode.replace("SAP_", ""))
                : false;
        return  deMode;
    }
}
