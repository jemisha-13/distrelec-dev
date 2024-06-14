/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.initialization;

import de.hybris.platform.core.Initialization;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupCollector;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.cronjob.jalo.TimerTaskUtils;
import de.hybris.platform.jalo.CoreBasicDataCreator;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.util.Config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.namics.hybris.toolbox.PlaceholderUtil;
import com.namics.hybris.toolbox.spring.SpringUtil;

/**
 * <p>
 * In hybris kann man die Initialisierung des Systems auf zwei Arten erreichen:
 * <ul>
 * <li>�ber die Admin-Konsole (http://localhost:9001/admin/init.jsp?tab=init)</li>
 * <li>Mit dem ant target 'yunitinit' (nur den junit-tenant.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Hybris legt beim Aufruf des Ant-Targets 'yunitinit' die Datenbank und dessen Struktur korrekt an, ruft jedoch bei den Extensions die
 * beiden Initialisierungsmethoden <code>createEssentialData</code> und <code>createProjectData</code> NICHT auf.<br/>
 * 
 * Dies kann mit Hilfe dieser Klasse nachgeholt werden.
 * </p>
 * 
 * <p>
 * Damit beim Aufruf von yunitinit dies automatisch geschieht, muss noch folgenden Code im <code>buildcallbacks.xml</code> einer hybris
 * Extension ausgeführt werden.
 * 
 * <pre>
 * 
 * 	&lt;macrodef name="migroscore_after_yunitinit"&gt;
 * 		&lt;sequential&gt;
 * 			&lt;yrun deployname="junit"&gt;
 * 				com.namics.hybris.toolbox.initialization.ProjectInitializer.createEssentialAndProjectData();
 * 			&lt;/yrun&gt;
 * 		&lt;/sequential&gt;
 * 	&lt;/macrodef&gt;
 * 
 * </pre>
 * 
 * </p>
 * 
 * @see Extension#createEssentialData(java.util.Map, de.hybris.platform.util.JspContext)
 * @see Extension#createProjectData(java.util.Map, de.hybris.platform.util.JspContext)
 * 
 * @author Jonathan Weiss, namics ag
 * @author Markus Baumgartner, namics ag
 * @since MGB PIM 1.0
 * 
 */
public class ProjectInitializer {
    /** Used logger instance. */
    private static final Logger LOG = Logger.getLogger(ProjectInitializer.class);

    private static Set<String> keysToIgnore = new HashSet<String>();
    static {
        keysToIgnore.add("catalina.home");
    }

    /**
     * Die beiden Initialisierungsmethoden <code>createEssentialData</code> und <code>createProjectData</code> von jeder Extension auf.
     * 
     * @see Extension#createEssentialData(java.util.Map, de.hybris.platform.util.JspContext)
     * @see Extension#createProjectData(java.util.Map, de.hybris.platform.util.JspContext)
     */
    public static void createEssentialAndProjectData() throws Exception {

        Registry.setCurrentTenantByID("junit");
        final String tenantId = Registry.getCurrentTenant().getTenantID();
        LOG.debug("Performing hybris-extendsions essential and project data on tenant '" + tenantId + "'...");

        // We store the original value and turn off the timer task
        // so no jobs will be triggered all 30 seconds.
        final boolean isTimerTaskDisabled = TimerTaskUtils.getInstance().isDisabled();
        if (!isTimerTaskDisabled) {
            TimerTaskUtils.getInstance().setDisabled(true);
        }

        /*
         * Create basic data like: - createBasicC2L(); - createBasicUnits(); - createBasicUserGroups(); -
         * updateExistingRestrictionsActiveFlag(); - createBasicRestrictions(); - createBasicTypesSecurity(); - createBasicSavedQueries(); -
         * localizeOrderStatus(); - createSupportedEncodings(); - createRootMediaFolder();
         */
        final CoreBasicDataCreator coreBasicDataCreator = new CoreBasicDataCreator();
        coreBasicDataCreator.createEssentialData(java.util.Collections.EMPTY_MAP, null);

        final List<Extension> extensions = Initialization.getCreators();
        int index = 1;
        for (final Extension extension : extensions) {
            try {
                LOG.info("Extension: " + extension.getName() + "[creator=" + extension.getCreatorName() + "]" + "[disabled=" + extension.isCreatorDisabled()
                        + "]");
                if (!extension.isCreatorDisabled()) {
                    LOG.info("Essential data on tenant '" + tenantId + "' for extension '" + extension.getName() + "'.");
                    extension.createEssentialData(new HashMap<String, String>(), null);
                    createDataViaSystemSetup(extension.getName(), Type.ESSENTIAL);

                    LOG.info("Project data on tenant '" + tenantId + "' for extension '" + extension.getName() + "'.");
                    extension.createProjectData(new HashMap<String, String>(), null);
                    createDataViaSystemSetup(extension.getName(), Type.PROJECT);
                }
            } catch (final Exception e) {
                LOG.warn("Creation of data for extension '" + extension.getName() + "' runs in a error.", e);
            }

            LOG.info("Extension " + index + " of " + extensions.size() + " initialized.");
            index++;

        }

        // after initialization, the job status is reset again.
        if (!isTimerTaskDisabled) {
            LOG.info("Timer-task enabled again.");
            TimerTaskUtils.getInstance().setDisabled(false);

            // we let the timer task be disabled
            TimerTaskUtils.getInstance().setDisabled(true);
        }

        LOG.info("Hybris-extendsions essential data on tenant '" + tenantId + "' finished.");

    }

    private static void createDataViaSystemSetup(final String extensionName, final SystemSetup.Type type) {
        final SystemSetupContext systemSetupContext = new SystemSetupContext(null, type, Process.INIT, extensionName);
        final SystemSetupCollector systemSetupCollector = (SystemSetupCollector) SpringUtil.getBean(SystemSetupCollector.class);
        systemSetupCollector.executeMethods(systemSetupContext);
    }

    /**
     * Iterates through all the configuration properties of hybris (see {@link Config}) and resolve nested property references like
     * <code>This is a ${innerProperty}.</code>
     */
    public static void resolveNestedConfigurationProperties() {
        final Map<String, Object> props = new HashMap<String, Object>(Config.getAllParameters());

        // Remove keys that shouldn't be translated,
        // like ${catalina.home}
        for (final String keyToIgnore : keysToIgnore) {
            if (props.containsKey(keyToIgnore)) {
                LOG.debug("The following placeholder wont be replaced (keys to ignore):" + keyToIgnore);
                props.remove(keyToIgnore);
            }
        }

        boolean wasChanged = false;
        for (final String key : Config.getAllParameters().keySet()) {
            final String value = Config.getParameter(key);
            // Do not log property configuration, there are passwords and so on...
            // LOG.debug("Resolve property values for key-value pair: " + key + "=" + value);
            final String parsedValue = PlaceholderUtil.parseStringValue(value, props);
            if (!value.equals(parsedValue)) {
                props.put(key, parsedValue);
                Config.setParameter(key, parsedValue);
                wasChanged = true;
            }
        }
        if (wasChanged) {
            // resolve till each nested configuration was resolved (recursive).
            resolveNestedConfigurationProperties();
        }
    }

}
