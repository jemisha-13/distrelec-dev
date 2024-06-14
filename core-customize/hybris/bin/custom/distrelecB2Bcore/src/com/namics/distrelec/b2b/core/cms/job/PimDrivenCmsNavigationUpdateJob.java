package com.namics.distrelec.b2b.core.cms.job;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Taxonomy.CATEGORY_MAX_LEVEL_KEY;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Taxonomy.DELETE_UNUSED_NAVNODES;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Taxonomy.ROOT_CATEGORY_CODE_KEY;
import static com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Import.MASTER_IMPORT_LANGUAGE;
import static java.util.Collections.emptyList;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.cms.DistCMSNavigationService;
import com.namics.distrelec.b2b.core.setup.DistSetupSyncJobService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

public class PimDrivenCmsNavigationUpdateJob<T extends CronJobModel> extends AbstractJobPerformable<T> {

    private static final Logger LOG = LogManager.getLogger(PimDrivenCmsNavigationUpdateJob.class);

    private static final String PHANTOM_NAV_NODE_UID = "PhantomNavNode";

    private static final String MAIN_CATEGORY_NAV_NODE_UID = "MainCategoryNavNode";

    private static final String NAV_NODE_UID_PREFIX = "NavNode_";

    private static final String CATEGORY_CODES_TO_SKIP = "distrelec.taxonomy.skip.pim.sorting.number.update";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistCMSNavigationService cmsNavigationService;

    @Autowired
    private DistCategoryService categoryService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    @Qualifier("b2bSetupSyncJobService")
    private DistSetupSyncJobService setupSyncJobService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Override
    public PerformResult perform(final T cronJob) {
        try {
            Collection<CMSSiteModel> cmsSites = getCmsSites();
            cmsSites.forEach(site -> this.processSite(site));
            performFullSync(cmsSites);
        } catch (Exception e) {
            LOG.error(String.format("Error occur while updating the CMS Navigation from PIM: '%s'", e.getMessage()), e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void processSite(CMSSiteModel cmsSite) {
        Set<LanguageModel> languages = cmsSite.getStores().get(0).getLanguages();
        long t0 = System.currentTimeMillis();
        Set<String> allVisibleCategoryCodes = categoryService.getAllVisibleCategoryCodes(cmsSite);
        LOG.info("Loading all visible categories for site {} took {} ms", cmsSite.getUid(), System.currentTimeMillis() - t0);
        if (isNotEmpty(languages)) {
            String masterLangIso = configurationService.getConfiguration().getString(MASTER_IMPORT_LANGUAGE, "en");
            List<LanguageModel> sortedLanguages = sortLanguages(languages.stream().toList(), masterLangIso);
            sortedLanguages.forEach(language -> process(cmsSite,
                                                        language,
                                                        language.getIsocode().equals(masterLangIso),
                                                        getRootCategory().getCategories(),
                                                        allVisibleCategoryCodes));
        }
    }

    protected void process(CMSSiteModel cmsSite,
                           LanguageModel language,
                           boolean isMasterLang,
                           List<CategoryModel> levelOneCategories,
                           Set<String> allVisibleCategoryCodes) {
        if (cmsSite == null
                || language == null
                || isEmpty(levelOneCategories)) {
            return;
        }
        if (isMasterLang) {
            LOG.info("Starting with Master Language: {}", language.getIsocode());
        }
        try {
            preformCMSSiteUpdate(cmsSite, language, levelOneCategories, isMasterLang, allVisibleCategoryCodes);
            language.setLastCMSNavUpdateDate(new Date());
            modelService.save(language);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    protected void preformCMSSiteUpdate(CMSSiteModel cmsSite,
                                        LanguageModel language,
                                        List<CategoryModel> levelOneCategories,
                                        boolean isMasterLang,
                                        Set<String> allVisibleCategoryCodes) {
        try {
            updateCMSSite(cmsSite, language, levelOneCategories, isMasterLang, allVisibleCategoryCodes);
        } catch (Exception e) {
            LOG.error(String.format("An error occur while updating CMSSite: %s for language: %s .", cmsSite.getUid(), language.getIsocode()), e);
        }
    }

    protected void updateCMSSite(CMSSiteModel cmsSite,
                                 LanguageModel language,
                                 List<CategoryModel> levelOneCategories,
                                 boolean isMasterLang,
                                 Set<String> allVisibleCategoryCodes) {

        LOG.info("Start updating navigation-nodes for cms-site:{} and language:{}", cmsSite.getUid(), language.getIsocode());
        final AtomicBoolean updated = new AtomicBoolean(false);
        if (isMasterLang) {
            updateNavigationStructure(cmsSite, cmsSite.getCountryContentCatalog(), levelOneCategories, language, allVisibleCategoryCodes);
            updated.set(true);
        }

        if (isEmpty(cmsSite.getStores()) || isEmpty(cmsSite.getStores().get(0).getLanguages())) {
            LOG.error("Either no store available for CMS site {} or no supported language", cmsSite.getUid());
            return;
        }

        cmsSite.getStores().get(0).getLanguages().stream()
               .filter(lang -> lang.equals(language))
               .findFirst()
               .ifPresent(lang -> {
                   levelOneCategories.forEach(levelOneCategory -> updateDataOnNavigationNode(getStagedCatalogVersion(cmsSite.getCountryContentCatalog()),
                                                                                             levelOneCategory,
                                                                                             commonI18NService.getLocaleForLanguage(language),
                                                                                             levelOneCategory.getPimSortingNumber()));
                   updated.set(true);
               });

        if (updated.get()) {
            syncCMSSite(cmsSite, false);
        }

    }

    protected void updateNavigationStructure(CMSSiteModel cmsSite,
                                             ContentCatalogModel contentCatalog,
                                             List<CategoryModel> levelOneCategories,
                                             LanguageModel language,
                                             Set<String> allVisibleCategoryCodes) {
        CatalogVersionModel stagedCatalogVersion = getStagedCatalogVersion(contentCatalog);
        CMSNavigationNodeModel mainCategoryNavNode = getMainNavigationNode(stagedCatalogVersion);
        CategoryModel rootCategory = getRootCategory();

        if (isEmpty(mainCategoryNavNode.getEntries())
                || mainCategoryNavNode.getEntries().size() > 1
                || mainCategoryNavNode.getEntries().get(0).getItem() == null
                || !mainCategoryNavNode.getEntries().get(0).getItem().equals(rootCategory)) {

            if (isNotEmpty(mainCategoryNavNode.getEntries())) {
                modelService.removeAll(mainCategoryNavNode.getEntries());
                modelService.refresh(mainCategoryNavNode);
            }
            cmsNavigationService.createCmsNavigationEntry(mainCategoryNavNode,
                                                          "NavEntry for " + rootCategory.getName(commonI18NService.getLocaleForLanguage(language)),
                                                          rootCategory);
        }

        CMSNavigationNodeModel phantomNavNode = cmsNavigationService.getNavigationNodeForId(PHANTOM_NAV_NODE_UID, stagedCatalogVersion);
        if (phantomNavNode == null) {
            throw new IllegalStateException("No phantom navigation node found for content catalog: " + contentCatalog.getId());
        }

        Locale locale = commonI18NService.getLocaleForLanguage(language);

        for (CategoryModel category : levelOneCategories) {
            updateOrCreateNavNodeForCategory(category, cmsSite, stagedCatalogVersion, mainCategoryNavNode, locale);
        }

        checkStructureHealth(mainCategoryNavNode, phantomNavNode, allVisibleCategoryCodes);
        cleanUpPhantom(phantomNavNode);
    }

    protected void updateDataOnNavigationNode(CatalogVersionModel catalogVersion,
                                              CategoryModel category,
                                              Locale locale,
                                              String sortingNumber) {
        String nodeUid = NAV_NODE_UID_PREFIX + category.getCode();
        CMSNavigationNodeModel categoryNavNode = cmsNavigationService.getNavigationNodeForId(nodeUid, catalogVersion);
        if (categoryNavNode == null) {
            LOG.debug("No Navigation Node found for category: {}", category.getCode());
            return;
        }
        boolean changed = false;

        // Update only if required.
        if (!StringUtils.equals(category.getName(locale), categoryNavNode.getTitle(locale))) {
            categoryNavNode.setTitle(category.getName(locale), locale);
            changed = true;
        }

        // Update only if required.
        if (!StringUtils.equals(sortingNumber, categoryNavNode.getSortingNumber(locale))
                && allowSortingNumberUpdate(category.getCode())) {
            categoryNavNode.setSortingNumber(sortingNumber, locale);
            changed = true;
        }

        if (changed) {
            modelService.save(categoryNavNode);
        }

        long t4 = System.currentTimeMillis();
        int maxLevel = configurationService.getConfiguration().getInt(CATEGORY_MAX_LEVEL_KEY, 4);
        // Update children.
        if (category.getLevel() != null
                && category.getLevel() < maxLevel
                && isNotEmpty(category.getCategories())) {
            final List<CategoryModel> subCategories = getAlphabeticallySortedCategories(category.getCategories(), locale);
            final AtomicInteger sortingIndex = new AtomicInteger(100);
            LOG.info("Start updating child category node structure, time taken:{}", (System.currentTimeMillis() - t4) / 1000 / 60);
            subCategories.forEach(child -> updateDataOnNavigationNode(catalogVersion, child, locale, String.valueOf(sortingIndex.getAndAdd(10))));
            LOG.info("Finish updating child category node structure, time taken:{}", (System.currentTimeMillis() - t4) / 1000 / 60);
        }
    }

    private CMSNavigationNodeModel getMainNavigationNode(CatalogVersionModel stagedCatalogVersion) {
        CMSNavigationNodeModel mainCategoryNavNode = cmsNavigationService.getNavigationNodeForId(MAIN_CATEGORY_NAV_NODE_UID, stagedCatalogVersion);
        if (mainCategoryNavNode == null) {
            throw new IllegalStateException("No Main category navigation node found for content catalog: " + stagedCatalogVersion.getCatalog().getId());
        }
        return mainCategoryNavNode;
    }

    private CatalogVersionModel getStagedCatalogVersion(ContentCatalogModel contentCatalog) {
        CatalogVersionModel stagedCatalogVersion = contentCatalog.getCatalogVersions()
                                                                 .stream().filter(cv -> "Staged".equalsIgnoreCase(cv.getVersion())).findFirst()
                                                                 .orElse(null);
        if (stagedCatalogVersion == null) {
            throw new IllegalStateException("No Staged catalog version found for content catalog: " + contentCatalog.getId());
        }
        return stagedCatalogVersion;
    }

    private List<CategoryModel> getAlphabeticallySortedCategories(List<CategoryModel> categories, Locale locale) {
        List<CategoryModel> subCategories = new ArrayList<>(categories);
        subCategories.sort((c1, c2) -> {
            String nameC1 = c1.getName(locale);
            String nameC2 = c2.getName(locale);
            if (nameC1 == null && nameC2 == null) {
                return 0;
            }
            if (isBlank(nameC1) && isNotBlank(nameC2)) {
                return -1;
            }
            if (isNotBlank(nameC1) && isBlank(nameC2)) {
                return 1;
            }
            return defaultString(nameC1, EMPTY).toUpperCase().compareTo(defaultString(nameC2, EMPTY).toUpperCase());
        });
        return subCategories;
    }

    protected void checkStructureHealth(CMSNavigationNodeModel root, CMSNavigationNodeModel phantom, Set<String> allVisibleCategoryCodes) {
        if (isNotEmpty(root.getChildren())) {
            checkNodeStructure(root, phantom, allVisibleCategoryCodes);
        }
    }

    /**
     * Verify the node structure. If the node should be removed the move its children to the phantom node and remove it.
     * 
     * @param node
     *            the node to verify.
     */
    protected void checkNodeStructure(CMSNavigationNodeModel node,
                                      CMSNavigationNodeModel phantom,
                                      Set<String> allVisibleCategoryCodes) {
        emptyIfNull(node.getChildren()).forEach(child -> checkNodeStructure(child, phantom, allVisibleCategoryCodes));
        modelService.refresh(node);
        if (canRemoveCategoryNavNode(node, allVisibleCategoryCodes)) {
            if (isNotEmpty(node.getChildren())) {
                moveNodeChildrenToPhantomNode(phantom, node);
            }
            removeNavigationNodeAndEntries(node);
        }
    }

    private void moveNodeChildrenToPhantomNode(CMSNavigationNodeModel phantom,
                                               CMSNavigationNodeModel node) {
        List<CMSNavigationNodeModel> phantomChildren = new ArrayList<>(phantom.getChildren() == null ? emptyList()
                                                                                                     : phantom.getChildren());
        phantomChildren.addAll(node.getChildren());
        for (CMSNavigationNodeModel ch : node.getChildren()) {
            ch.setParent(phantom);
            modelService.save(ch);
        }
        phantom.setChildren(phantomChildren);
        modelService.save(phantom);
    }

    protected boolean canRemoveCategoryNavNode(CMSNavigationNodeModel node, Set<String> visibleCategoryCodes) {

        boolean deleteNodes = configurationService.getConfiguration().getBoolean(DELETE_UNUSED_NAVNODES);
        if (!deleteNodes
                || (node != null && MAIN_CATEGORY_NAV_NODE_UID.equals(node.getUid()))) {
            return false;
        }
        if (isEmptyNode(node) || node.getParent() == null) { // If the node is empty or it is orphan.
            return true;
        }
        // If the node is not category navigation node, we keep it.
        final ItemModel item = node.getEntries().get(0).getItem();
        if (!CategoryModel._TYPECODE.equals(item.getItemtype())) {
            return false;
        }

        // If the category does not have any sub-category nor sub-product
        // with a for level deep category structure, this check causes massive performance problems, hence we skip delete for now
        final CategoryModel category = (CategoryModel) item;

        boolean hasVisibleProduct = visibleCategoryCodes.contains(category.getCode());
        if (!hasVisibleProduct) {
            return true;
        }

        // If the parent is not category node or if the category linked to the parent is not the direct super category of the category
        // linked to the target node.
        final CMSNavigationNodeModel parent = node.getParent();
        return !isCategoryNode(parent) || !isSubCategoryOf(category, (CategoryModel) parent.getEntries().get(0).getItem());
    }

    private boolean isCategoryNode(final CMSNavigationNodeModel node) {
        return !isEmptyNode(node)
                && CategoryModel._TYPECODE.equals(node.getEntries().get(0).getItem().getItemtype());
    }

    private boolean isEmptyNode(CMSNavigationNodeModel node) {
        return node == null
                || isEmpty(node.getEntries())
                || node.getEntries().get(0).getItem() == null;
    }

    protected boolean isSubCategoryOf(CategoryModel category, CategoryModel superCategory) {
        if (category == null
                || superCategory == null
                || isEmpty(category.getSupercategories())) {
            return false;
        }
        return category.getSupercategories().stream()
                       .anyMatch(supCat -> supCat.equals(superCategory));
    }

    protected void cleanUpPhantom(CMSNavigationNodeModel phantom) {
        if (phantom == null
                || isEmpty(phantom.getChildren())) {
            LOG.info("Nothing to cleanup from the phantom CMS Navigation Node in the content catalog.");
            return;
        }
        final List<CMSNavigationNodeModel> garbageList = new ArrayList<>();
        for (final CMSNavigationNodeModel child : phantom.getChildren()) {
            collectGarbageNodes(child, garbageList);
        }
        remove(garbageList);
    }

    protected void remove(final List<CMSNavigationNodeModel> nodes) {
        if (isEmpty(nodes)) {
            return;
        }
        nodes.forEach(this::removeNavigationNodeAndEntries);
    }

    protected void removeNavigationNodeAndEntries(final CMSNavigationNodeModel node) {
        if (isNotEmpty(node.getEntries())) {
            modelService.removeAll(node.getEntries());
        }
        modelService.refresh(node);
        modelService.remove(node);
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
        if (isEmpty(node.getEntries())
                || node.getEntries().get(0).getItem() == null
                || CategoryModel._TYPECODE.equals(node.getEntries().get(0).getItem().getItemtype())) {
            garbageList.add(0, node); // Children needs to be removed first.
        }

        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            for (final CMSNavigationNodeModel child : node.getChildren()) {
                collectGarbageNodes(child, garbageList);
            }
        }
    }

    protected void updateOrCreateNavNodeForCategory(CategoryModel category,
                                                    CMSSiteModel cmsSite,
                                                    CatalogVersionModel catalogVersion,
                                                    CMSNavigationNodeModel parent,
                                                    Locale locale) {
        String nodeUid = NAV_NODE_UID_PREFIX + category.getCode();

        // We don't create navigation nodes for empty categories nor for categories with level greater than the max allowed
        if (isEmptyCategory(category, cmsSite)
                || category.getLevel() == null
                || category.getLevel() > configurationService.getConfiguration().getInt(CATEGORY_MAX_LEVEL_KEY, 4)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Category " + category.getCode() + " [Level: " + category.getLevel() + ", Max allowed: "
                          + configurationService.getConfiguration().getInt(CATEGORY_MAX_LEVEL_KEY, 4) + "]");
            }
            // Removing the navigation node if it is already created.
            CMSNavigationNodeModel categoryNavNode = cmsNavigationService.getNavigationNodeForId(nodeUid, catalogVersion);
            if (categoryNavNode != null) {
                LOG.info("Removing navigation node for category: {}", category.getCode());
                removeNavigationNodeAndEntries(categoryNavNode);
            }
            return;
        }

        CMSNavigationNodeModel categoryNavNode = getOrCreateNavigationNode(nodeUid, parent, catalogVersion, category, locale);
        if (category.getLevel() == 1
                && allowSortingNumberUpdate(category.getCode())
                && !StringUtils.equals(category.getPimSortingNumber(), categoryNavNode.getSortingNumber(locale))) {
            categoryNavNode.setSortingNumber(category.getPimSortingNumber(), locale);
            modelService.save(categoryNavNode);
        }

        if (isEmpty(categoryNavNode.getEntries())
                || categoryNavNode.getEntries().size() > 1
                || categoryNavNode.getEntries().get(0).getItem() == null
                || !categoryNavNode.getEntries().get(0).getItem().equals(category)) {
            if (isNotEmpty(categoryNavNode.getEntries())) {
                modelService.removeAll(categoryNavNode.getEntries());
            }
            cmsNavigationService.createCmsNavigationEntry(categoryNavNode, "NavEntry_" + category.getName(locale), category);
        }

        if (!parent.equals(categoryNavNode.getParent())) {
            categoryNavNode.setParent(parent);
            final List<CMSNavigationNodeModel> children = new ArrayList<>(parent.getChildren());
            children.add(categoryNavNode);
            parent.setChildren(children);
            modelService.saveAll(categoryNavNode, parent);
        }

        emptyIfNull(category.getCategories())
                                             .forEach(subCategory -> updateOrCreateNavNodeForCategory(subCategory, cmsSite, catalogVersion, categoryNavNode,
                                                                                                      locale));
    }

    private CMSNavigationNodeModel getOrCreateNavigationNode(String nodeUid,
                                                             CMSNavigationNodeModel parent,
                                                             CatalogVersionModel catalogVersion,
                                                             CategoryModel category,
                                                             Locale locale) {
        CMSNavigationNodeModel categoryNavNode = cmsNavigationService.getNavigationNodeForId(nodeUid, catalogVersion);
        if (categoryNavNode != null) {
            return categoryNavNode;
        }
        LOG.info("Creating new navigation node for category: {}", category.getCode());
        CMSNavigationNodeModel newNavigationNode = modelService.create(CMSNavigationNodeModel.class);
        newNavigationNode.setUid(nodeUid);
        newNavigationNode.setParent(parent);
        newNavigationNode.setCatalogVersion(catalogVersion);
        newNavigationNode.setVisible(true);
        newNavigationNode.setTitle(category.getName(locale), locale);
        newNavigationNode.setName("Category Navigation node for " + category.getCode());
        modelService.save(newNavigationNode);
        return newNavigationNode;
    }

    private boolean allowSortingNumberUpdate(String categoryCode) {
        String[] skipSortingNumberUpdateArray = configurationService.getConfiguration().getString(CATEGORY_CODES_TO_SKIP).split(",");
        return !Arrays.asList(skipSortingNumberUpdateArray).contains(categoryCode);
    }

    private Collection<CMSSiteModel> getCmsSites() {
        Collection<CMSSiteModel> cmsSites = cmsSiteService.getSites().stream()
                                                          .filter(cmsSite -> cmsSite.getCountryContentCatalog() != null)
                                                          .collect(Collectors.toList());
        if (isEmpty(cmsSites)) {
            throw new UnknownIdentifierException("No CMSSite found");
        }
        return cmsSites;
    }

    private CategoryModel getRootCategory() {
        String rootCategoryCode = configurationService.getConfiguration().getString(ROOT_CATEGORY_CODE_KEY, "");
        if (isBlank(rootCategoryCode)) {
            throw new IllegalStateException("No Root Category Code defined");
        }
        CategoryModel rootCategory = categoryService.getCategoryForCode(rootCategoryCode);
        if (rootCategory == null || isEmpty(rootCategory.getCategories())) {
            throw new UnknownIdentifierException("No category found");
        }
        return rootCategory;
    }

    private List<LanguageModel> sortLanguages(List<LanguageModel> languages, String masterLangIso) {
        // We should always start with the Master if it is in the list.
        List<LanguageModel> languageList = new ArrayList<>(languages);
        languageList.sort((o1, o2) -> {
            if (o1.getIsocode().equals(masterLangIso)) {
                return -1;
            } else if (o2.getIsocode().equals(masterLangIso)) {
                return 1;
            }
            return 0;
        });
        return languageList;
    }

    private void performFullSync(Collection<CMSSiteModel> cmsSites) {
        for (CMSSiteModel cmsSite : cmsSites) {
            syncCMSSite(cmsSite, true);
        }
    }

    private void syncCMSSite(CMSSiteModel cmsSite, boolean fullSync) {
        PerformResult syncResult = syncContentCatalog(cmsSite.getCountryContentCatalog(), fullSync);
        if (!CronJobResult.SUCCESS.equals(syncResult.getResult())) {
            LOG.error("The synchronization CronJob of the content catalog {} was not finished successfully!",
                      cmsSite.getCountryContentCatalog().getId());
        }
    }

    private PerformResult syncContentCatalog(ContentCatalogModel contentCatalog, boolean fullSync) {
        return setupSyncJobService.executeCatalogSyncJob(contentCatalog.getId(), fullSync);
    }

    /**
     * Checks whether a category is empty or not. A category is considered as empty if:
     * <ul>
     * <li>The category is null</li>
     * <li>OR: The category products list and sub-categories list are both empty.</li>
     * </ul>
     * In addition to the emptiness check, a category is also considered empty if it has a successor.
     *
     * @param category
     *            the category to check
     * @param cmsSite
     *            the cmsSite to check
     * @return {@code true} if the category is empty, {@code false} otherwise.
     */
    protected boolean isEmptyCategory(CategoryModel category, CMSSiteModel cmsSite) {
        return category == null
                || categoryService.isCategoryEmptyForCMSSite(category, cmsSite)
                || categoryService.hasSuccessor(category.getCode());
    }
}
