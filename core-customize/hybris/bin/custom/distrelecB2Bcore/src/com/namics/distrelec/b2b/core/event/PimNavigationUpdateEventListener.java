/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Import;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.cms.DistCMSNavigationService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.setup.SetupSyncJobService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * {@code PimNavigationUpdateEventListener}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.12
 */
public class PimNavigationUpdateEventListener extends AbstractEventListener<PimNavigationUpdateEvent> {

    private static final Logger LOG = Logger.getLogger(PimNavigationUpdateEventListener.class);
    private static final String ROOT_CATEGORY_CODE = "cat-L0D_324785";
    private static final String PHANTOM_NAV_NODE_UID = "PhantomNavNode";
    private static final String MAIN_CATEGORY_NAV_NODE_UID = "MainCategoryNavNode";

    private JaloSession session;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.servicelayer.event.impl.AbstractEventListener#onEvent(de.hybris.platform.servicelayer.event.events.AbstractEvent)
     */
    @Override
    protected void onEvent(final PimNavigationUpdateEvent event) {
        if (event == null || !event.isSuccessful() || StringUtils.isBlank(event.getLanguage())) {
            return;
        }

        // Initialize the session.
        init();

        LanguageModel language = null;
        try {
            language = getCommonI18NService().getLanguage(event.getLanguage());
            if (language == null) {
                throw new NullPointerException("No language found for isocode " + event.getLanguage());
            }
        } catch (final Exception exp) {
            LOG.error("ERROR:" + exp.getMessage(), exp);
            return;
        }

        final CategoryModel root = getCategoryService().getCategoryForCode(ROOT_CATEGORY_CODE);
        final List<CategoryModel> l1_categories = root.getCategories();
        if (CollectionUtils.isEmpty(l1_categories)) {
            LOG.info("No L1 category found");
            return;
        }

        final Collection<CMSSiteModel> cmsSites = getCmsSiteService().getSites();
        if (CollectionUtils.isEmpty(cmsSites)) {
            LOG.info("No CMSSite found");
            return;
        }

        for (final CMSSiteModel cmsSite : cmsSites) {
            try {
                updateCMSSite(cmsSite, language, l1_categories, event.isMasterImport());
            } catch (final Exception exp) {
                LOG.error("An error occur while updating CMSSite: " + cmsSite.getUid() + " for language: " + language.getIsocode(), exp);
            }
        }

        // Destroy the session.
        destroy();
    }

    /**
     * Update the CMS site with the newly imported language.
     * 
     * @param cmsSite
     *            the CMS site to update
     * @param language
     *            the language newly imported
     * @param l1_categories
     *            the list of level 1 categories.
     * @param master
     *            a boolean indicates whether the last import was a Master import.
     */
    protected void updateCMSSite(final CMSSiteModel cmsSite, final LanguageModel language, final List<CategoryModel> l1_categories, final boolean master) {
        boolean updated = false;
        if (master) {
            updateNavigationStructure(cmsSite.getCountryContentCatalog(), l1_categories, language);
            updated = true;
        }

        for (final LanguageModel lang : cmsSite.getStores().get(0).getLanguages()) {
            if (lang.equals(language)) { // Update translations only if the language is supported by the CMS Site.
                updateTranslation(cmsSite.getCountryContentCatalog(), l1_categories, language);
                updated = true;
                break;
            }
        }

        if (updated) {
            // Synchronize the content catalog
            final PerformResult syncResult = syncContentCatalog(cmsSite.getCountryContentCatalog());
            if (!CronJobResult.SUCCESS.equals(syncResult.getResult())) {
                LOG.error("The synchronization CronJob of the content catalog " + cmsSite.getCountryContentCatalog().getId()
                        + " was not finished successfully!");
            }
        }
    }

    /**
     * Update the navigation node structure.
     * 
     * @param contentCatalog
     *            the target content catalog.
     * @param l1_categories
     *            the list of level 1 categories.
     * @param language
     *            the master language.
     */
    protected void updateNavigationStructure(final ContentCatalogModel contentCatalog, final List<CategoryModel> l1_categories, final LanguageModel language) {

        // Look for the staged catalog version.
        CatalogVersionModel stagedCatalogVersion = null;

        for (final CatalogVersionModel catalogVersion : contentCatalog.getCatalogVersions()) {
            if ("Staged".equalsIgnoreCase(catalogVersion.getVersion())) {
                stagedCatalogVersion = catalogVersion;
                break;
            }
        }

        if (stagedCatalogVersion == null) {
            throw new IllegalStateException("No Staged catalog version found for content catalog: " + contentCatalog.getId());
        }

        final CMSNavigationNodeModel mainCategoryNavNode = getCmsNavigationService().getNavigationNodeForId(MAIN_CATEGORY_NAV_NODE_UID, stagedCatalogVersion);
        if (mainCategoryNavNode == null) {
            throw new IllegalStateException("No Main category navigation node found for content catalog: " + contentCatalog.getId());
        }

        // Look for the phantom navigation node.
        final CMSNavigationNodeModel phantomNavNode = getCmsNavigationService().getNavigationNodeForId(PHANTOM_NAV_NODE_UID, stagedCatalogVersion);
        if (phantomNavNode == null) {
            throw new IllegalStateException("No phantom navigation node found for content catalog: " + contentCatalog.getId());
        }

        final Locale locale = getCommonI18NService().getLocaleForLanguage(language);

        for (final CategoryModel category : l1_categories) {
            getOrCreateNavNodeForCategory(category, stagedCatalogVersion, mainCategoryNavNode, locale);
        }

        // check navigation structure.
        checkStructureHealth(mainCategoryNavNode, l1_categories, phantomNavNode);
        // Cleanup phantom node.
        cleanUpPhantom(phantomNavNode);
    }

    /**
     * Update the translation of navigation nodes with localized category names.
     * 
     * @param contentCatalog
     *            the target content catalog to update.
     * @param l1_categories
     *            the list of level 1 categories.
     * @param language
     *            the target locale.
     */
    protected void updateTranslation(final ContentCatalogModel contentCatalog, final List<CategoryModel> l1_categories, final LanguageModel language) {
        // Look for the staged catalog version.
        CatalogVersionModel stagedCatalogVersion = null;

        for (final CatalogVersionModel catalogVersion : contentCatalog.getCatalogVersions()) {
            if ("Staged".equalsIgnoreCase(catalogVersion.getVersion())) {
                stagedCatalogVersion = catalogVersion;
                break;
            }
        }

        if (stagedCatalogVersion == null) {
            throw new IllegalStateException("No Staged catalog version found for content catalog: " + contentCatalog.getId());
        }

        final CMSNavigationNodeModel mainCategoryNavNode = getCmsNavigationService().getNavigationNodeForId(MAIN_CATEGORY_NAV_NODE_UID, stagedCatalogVersion);
        if (mainCategoryNavNode == null) {
            throw new IllegalStateException("No Main category navigation node found for content catalog: " + contentCatalog.getId());
        }

        final Locale locale = getCommonI18NService().getLocaleForLanguage(language);

        for (final CategoryModel category : l1_categories) {
            updateTranslation(stagedCatalogVersion, category, locale);
        }
    }

    /**
     * Update translation for the navigation node of the specified category.
     * 
     * @param catalogVersion
     *            the target catalog version
     * @param category
     *            the target category
     * @param locale
     *            the target locale.
     */
    protected void updateTranslation(final CatalogVersionModel catalogVersion, final CategoryModel category, final Locale locale) {
        final String NODE_UID = "NavNode_" + category.getCode();
        CMSNavigationNodeModel categoryNavNode = getCmsNavigationService().getNavigationNodeForId(NODE_UID, catalogVersion);
        if (categoryNavNode == null) {
            LOG.error("No Navigation Node found for category: " + category.getCode());
        } else if (!StringUtils.equals(category.getName(locale), categoryNavNode.getTitle(locale))) { // Update only if required.
            categoryNavNode.setTitle(category.getName(locale), locale);
            getModelService().save(categoryNavNode);
        }

        // Update children.
        if (category.getLevel() != null && category.getLevel().intValue() <= 1 && CollectionUtils.isNotEmpty(category.getCategories())) {
            for (final CategoryModel child : category.getCategories()) {
                updateTranslation(catalogVersion, child, locale);
            }
        }
    }

    /**
     * Check the structure healthiness.
     * 
     * @param root
     *            the root CMS navigation node from where to start the check.
     * @param l1_categories
     *            the list of level 1 categories.
     */
    protected void checkStructureHealth(final CMSNavigationNodeModel root, final List<CategoryModel> l1_categories, final CMSNavigationNodeModel phantom) {
        if (CollectionUtils.isEmpty(root.getChildren())) {
            return;
        }

        for (final CMSNavigationNodeModel child : root.getChildren()) {
            checkNodeStructure(child, phantom);
        }
    }

    /**
     * 
     * @param node
     */
    protected void checkNodeStructure(final CMSNavigationNodeModel node, final CMSNavigationNodeModel phantom) {
        // Start checking children first.
        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            for (final CMSNavigationNodeModel child : node.getChildren()) {
                checkNodeStructure(child, phantom);
            }
        }

        // Refresh the node to ensure having the latest data from the database.
        getModelService().refresh(node);

        if (CollectionUtils.isEmpty(node.getEntries()) || node.getEntries().get(0).getItem() == null || //
                (CategoryModel._TYPECODE.equals(node.getEntries().get(0).getItem().getItemtype())
                        && isEmptyCategory((CategoryModel) node.getEntries().get(0).getItem()))) {
            // Should be removed
            if (CollectionUtils.isNotEmpty(node.getChildren())) {
                // Move node children to the phantom navigation node.
                final List<CMSNavigationNodeModel> phantomChildren = new ArrayList<CMSNavigationNodeModel>(
                        phantom.getChildren() == null ? Collections.EMPTY_LIST : phantom.getChildren());
                phantomChildren.addAll(node.getChildren());
                for (final CMSNavigationNodeModel ch : node.getChildren()) {
                    ch.setParent(phantom);
                    getModelService().save(ch);
                }
                phantom.setChildren(phantomChildren);
                getModelService().save(phantom);
            }
            // We can delete the node safely.
            remove(node);
        }
    }

    /**
     * Cleanup the phantom node from empty CMS Navigation nodes and category navigation nodes.
     * 
     * @param phantom
     *            the phantom root node.
     */
    protected void cleanUpPhantom(final CMSNavigationNodeModel phantom) {
        if (phantom == null || CollectionUtils.isEmpty(phantom.getChildren())) {
            LOG.info("Nothing to cleanup from the phantom CMS Navigation Node in the content catalog: " + phantom.getCatalogVersion().getCatalog().getId() + ":"
                    + phantom.getCatalogVersion().getVersion());
            return;
        }

        final List<CMSNavigationNodeModel> garbageList = new ArrayList<CMSNavigationNodeModel>();
        for (final CMSNavigationNodeModel child : phantom.getChildren()) {
            collectGarbageNodes(child, garbageList);
        }

        // Remove all garbage nodes.
        remove(garbageList);
    }

    /**
     * Remove all nodes from the database.
     * 
     * @param nodes
     *            the nodes to remove
     * @see #remove(CMSNavigationNodeModel)
     */
    protected void remove(final List<CMSNavigationNodeModel> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (final CMSNavigationNodeModel node : nodes) {
            remove(node);
        }
    }

    /**
     * Remove the specified CMS navigation node and its entries, if any, from the database.
     * 
     * @param node
     *            the node the delete.
     */
    protected void remove(final CMSNavigationNodeModel node) {
        if (CollectionUtils.isNotEmpty(node.getEntries())) {
            getModelService().removeAll(node.getEntries());
        }
        getModelService().remove(node);
    }

    /**
     * <p>
     * Collect CMS navigation nodes linked to the phantom node that needs to be removed. A node is a candidate for removal if it has no
     * entries, its first entry has no item or the item of the first entry is of type {@link CategoryModel}
     * </p>
     * 
     * @param node
     *            the CMS navigation node.
     * @param garbageList
     *            the list in which the candidate nodes are collected.
     */
    protected void collectGarbageNodes(final CMSNavigationNodeModel node, final List<CMSNavigationNodeModel> garbageList) {
        if (CollectionUtils.isEmpty(node.getEntries()) || node.getEntries().get(0).getItem() == null || //
                CategoryModel._TYPECODE.equals(node.getEntries().get(0).getItem().getItemtype())) {
            garbageList.add(0, node); // Children needs to be removed first.
        }

        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            for (final CMSNavigationNodeModel child : node.getChildren()) {
                collectGarbageNodes(child, garbageList);
            }
        }
    }

    /**
     * Look for the CMS Navigation node referencing the specified category. If no navigation node found, a new node is created and populated
     * with required data.
     * 
     * @param category
     *            the target category.
     * @param catalogVersion
     *            the catalog version used to fetch the navigation node.
     * @param parent
     *            the parent navigation node.
     * @param locale
     *            the locale used to set the translation data.
     * @return an instance of {@code CMSNavigationNodeModel}
     */
    protected CMSNavigationNodeModel getOrCreateNavNodeForCategory(final CategoryModel category, final CatalogVersionModel catalogVersion,
            final CMSNavigationNodeModel parent, final Locale locale) {
        if (isEmptyCategory(category)) { // We don't create navigation nodes for empty categories.
            return null;
        }

        final String NODE_UID = "NavNode_" + category.getCode();
        CMSNavigationNodeModel categoryNavNode = getCmsNavigationService().getNavigationNodeForId(NODE_UID, catalogVersion);
        if (categoryNavNode == null) {
            LOG.info("Creating new navigation node for category: " + category.getCode());
            // CMS Navigation Node
            categoryNavNode = getModelService().create(CMSNavigationNodeModel.class);
            categoryNavNode.setUid(NODE_UID);
            categoryNavNode.setParent(parent);
            categoryNavNode.setCatalogVersion(catalogVersion);
            categoryNavNode.setVisible(true);
            categoryNavNode.setTitle(category.getName(locale), locale);
            categoryNavNode.setName("Category Navigation node for " + category.getCode());
            // CMS Navigation Entry
            final CMSNavigationEntryModel navEntry = getModelService().create(CMSNavigationEntryModel.class);
            navEntry.setItem(category);
            navEntry.setUid("NavEntry_" + category.getCode());
            navEntry.setNavigationNode(categoryNavNode);
            navEntry.setName("Navigation Entry for Category " + category.getCode());
            navEntry.setCatalogVersion(catalogVersion);
            categoryNavNode.setEntries(Collections.singletonList(navEntry));
            getModelService().save(categoryNavNode);
        }

        if (!parent.equals(categoryNavNode.getParent())) {
            categoryNavNode.setParent(parent);
            final List<CMSNavigationNodeModel> children = new ArrayList<CMSNavigationNodeModel>(parent.getChildren());
            children.add(categoryNavNode);
            parent.setChildren(children);
            getModelService().saveAll(categoryNavNode, parent);
        }

        if (category.getLevel() != null && category.getLevel().intValue() <= 1 && CollectionUtils.isNotEmpty(category.getCategories())) {
            for (final CategoryModel subCategory : category.getCategories()) {
                getOrCreateNavNodeForCategory(subCategory, catalogVersion, categoryNavNode, locale);
            }
        }

        return categoryNavNode;
    }

    /**
     * Synchronize the content catalog
     * 
     * @param contentCatalog
     *            the content catalog to synchronize.
     */
    protected PerformResult syncContentCatalog(final ContentCatalogModel contentCatalog) {
        return getSetupSyncJobService().executeCatalogSyncJob(contentCatalog.getId());
    }

    /**
     * Checks whether a category is empty or not. A category is considered as empty if:
     * <ul>
     * <li>The category is null</li>
     * <li>OR: The category products list and sub-categories list are both empty.</li>
     * </ul>
     * 
     * @param category
     *            the category to check
     * @return {@code true} if the category is empty, {@code false} otherwise.
     */
    protected boolean isEmptyCategory(final CategoryModel category) {
        return category == null || (CollectionUtils.isEmpty(category.getProducts()) && CollectionUtils.isEmpty(category.getCategories()));
    }

    /**
     * Initialize the Session with all its dependencies.
     */
    protected void init() {
        LOG.info("Initializing PIM Navigation update Listener");
        session = setupSession();
        final Configuration configuration = getConfigurationService().getConfiguration();
        getCatalogVersionService().setSessionCatalogVersion(configuration.getString(Import.PRODUCT_CATALOG_ID),
                configuration.getString(Import.PRODUCT_CATALOG_VERSION));

        UserModel user = getUserService().getUserForUID(getConfigurationService().getConfiguration().getString("import.pim.user"));
        getUserService().setCurrentUser(user);
        LOG.info("PIM Navigation update listener initialized");
    }

    /**
     * Close the session and remove its dependencies.
     */
    protected void destroy() {
        LOG.info("Destroying the PIM navigation update listener and its Jalo Session");
        if (session != null && !session.isClosed()) {
            session.close();
        }
    }

    /**
     * Ensure we use a proper JaloSession having no rubbish (like httpSessionID) attached during last usage. <br/>
     * Important: Disable timeout to ensure session does not expire.
     * 
     * @return a proper JaloSession
     */
    private JaloSession setupSession() {
        if (!Registry.hasCurrentTenant()) {
            Registry.activateMasterTenant();
        }

        if (JaloSession.hasCurrentSession()) {
            JaloSession currentSession = JaloSession.getCurrentSession();
            LOG.info("Current session exists: sessionID [" + currentSession.getSessionID() + "], httpSessionId [" + currentSession.getHttpSessionId()
                    + "], user [" + currentSession.getUser() + "]. Deactivate current session and create a new one.");
            JaloSession.deactivate();
        }

        JaloSession newSession = JaloSession.getCurrentSession();
        newSession.setTimeout(-1);
        LOG.info("New session: sessionID [" + newSession.getSessionID() + "], httpSessionId [" + newSession.getHttpSessionId() + "], user ["
                + newSession.getUser() + "]. Deactivate current session and create a new one.");
        return newSession;
    }

    // Getters & Setters

    protected <T> T getBean(final String beanId) {
        return (T) Registry.getApplicationContext().getBean(beanId);
    }

    public UserService getUserService() {
        return getBean("userService");
    }

    public CatalogVersionService getCatalogVersionService() {
        return getBean("catalogVersionService");
    }

    public SetupSyncJobService getSetupSyncJobService() {
        return getBean("b2bSetupSyncJobService");
    }

    public DistUrlResolver<CategoryModel> getCategoryModelResolver() {
        return getBean("categoryModelUrlResolver");
    }

    public ConfigurationService getConfigurationService() {
        return getBean("configurationService");
    }

    public CMSSiteService getCmsSiteService() {
        return getBean("cmsSiteService");
    }

    public ModelService getModelService() {
        return getBean("modelService");
    }

    public CommonI18NService getCommonI18NService() {
        return getBean("commonI18NService");
    }

    public DistCategoryService getCategoryService() {
        return getBean("categoryService");
    }

    public DistCMSNavigationService getCmsNavigationService() {
        return getBean("cmsNavigationService");
    }

    public SessionService getSessionService() {
        return getBean("sessionService");
    }
}
