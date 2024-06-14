/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.category.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.regioncache.region.impl.DistCacheRegion;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.category.data.DistCategoryPageData;
import com.namics.distrelec.b2b.facades.category.dto.FactFinderFacetValueCategoryDisplayDataDto;
import com.namics.distrelec.b2b.facades.message.queue.cassandra.DistRelatedDataFacade;
import com.namics.distrelec.b2b.facades.message.queue.data.RelatedData;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.CategoryDisplayData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.core.TenantAwareThreadFactory;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import net.sf.ehcache.Element;

/**
 * Default implementation of {@link DistCategoryFacade}.
 *
 * @author sivakumaran, Namics AG
 *
 */
public class DefaultDistCategoryFacade implements DistCategoryFacade, CalculateTopCategoriesHook {

    private static final String TOP_CATEGORIES_CACHE_REGION = "topCategoriesCacheRegion";

    private static final Logger LOG = LogManager.getLogger(DefaultDistCategoryFacade.class);

    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    private DistRelatedDataFacade distRelatedDataFacade;

    @Autowired
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    @Qualifier("categoryConverter")
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @Autowired
    @Qualifier("categoryDisplayDataConverter")
    private Converter<FactFinderFacetValueCategoryDisplayDataDto, CategoryDisplayData<SearchStateData>> categoryDisplayDataConverter;

    @Autowired
    @Qualifier(TOP_CATEGORIES_CACHE_REGION)
    private DistCacheRegion topCategoriesCacheRegion;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistCommerceCommonI18NService distCommerceCommonI18NService;

    @Autowired
    private ConfigurationService configurationService;

    private final ConcurrentMap<TopCategoriesCacheKey, Object> topCategoryCalculationLocks = new ConcurrentHashMap<>();

    private final ConcurrentMap<TopCategoriesCacheKey, Object> topCategoryBackgroundRefreshLocks = new ConcurrentHashMap<>();

    @Override
    public DistCategoryPageData getCategoryPageData(final CategoryModel category) {
        final DistCategoryPageData categoryPageData = createCategoryPageData(category);
        populateCategoryPageData(category.getCategories(), categoryPageData);
        return categoryPageData;
    }

    protected void populateCategoryPageData(final List<CategoryModel> subcategories,
                                            final DistCategoryPageData categoryPageData) {
        final List<CategoryData> categoryList = new ArrayList<>();

        for (final CategoryModel category : subcategories) {
            if (!isCategoryEmptyForCurrentSite(category)) {
                categoryList.add(categoryConverter.convert(category));
            }
        }
        categoryPageData.setSubCategories(categoryList);
    }

    @Override
    public void getSubCategoryPageData(final CategoryModel category,
                                       final Collection<CategoryDisplayData<SearchStateData>> categoryDisplayDataList) {

        final List<CategoryModel> subcategories = category.getCategories();

        for (final CategoryModel cat2 : subcategories) {
            if (!isCategoryEmptyForCurrentSite(cat2)) {
                for (final CategoryDisplayData ffCategory : categoryDisplayDataList) {
                    final CategoryData level2Category = categoryConverter.convert(cat2);
                    if (level2Category.getCode() == ffCategory.getCode()) {
                        ffCategory.setUrl(level2Category.getUrl());
                    }
                    for (final CategoryModel cat3 : cat2.getCategories()) {
                        if (!isCategoryEmptyForCurrentSite(cat3)) {

                            final CategoryData level3Category = categoryConverter.convert(cat3);
                            final List<CategoryDisplayData> categorydisplayData = ffCategory
                                                                                            .getSubcategoryDisplayDataList();
                            for (final CategoryDisplayData ffCategory1 : categorydisplayData) {
                                if (level3Category.getCode() == ffCategory1.getCode()) {
                                    ffCategory1.setUrl(level3Category.getUrl());
                                }

                            }

                        }
                    }

                }
            }
        }

    }

    protected DistCategoryPageData createCategoryPageData(final CategoryModel category) {
        final DistCategoryPageData categoryPageData = new DistCategoryPageData();
        categoryPageData.setSourceCategory(categoryConverter.convert(category));
        return categoryPageData;
    }

    @Override
    public List<DistCategoryIndexData> getCategoryIndex() {
        final LanguageModel currentLang = commonI18NService.getCurrentLanguage();
        final LanguageModel baseLanguage = distCommerceCommonI18NService.getBaseLanguage(currentLang);
        final Tenant tenant = Registry.getCurrentTenant();
        final TopCategoriesCacheKey cacheKey = new TopCategoriesCacheKey(tenant, baseLanguage);
        final Element cachedCategoryDataElement = topCategoriesCacheRegion.getElement(cacheKey);
        if (cachedCategoryDataElement != null) {
            if (isExpired(cachedCategoryDataElement)) {
                refreshElementInBackground(tenant, cacheKey);
            }

            return (List<DistCategoryIndexData>) cachedCategoryDataElement.getObjectValue();
        }

        return calculateTopCategoryData(cacheKey);
    }

    protected boolean isExpired(Element cachedCategoryDataElement) {
        long ttlSeconds = configurationService.getConfiguration().getLong(DistConfigConstants.Cache.TOP_CATEGORY_TTL_SECONDS, 60L);

        boolean expired = System.currentTimeMillis() - cachedCategoryDataElement.getCreationTime() > ttlSeconds * 1000;
        return expired;
    }

    @Override
    public List<DistCategoryIndexData> calculateTopCategoryData(TopCategoriesCacheKey cacheKey) {
        Object newLangLockObject = new Object();
        Object langLockObject = topCategoryCalculationLocks.putIfAbsent(cacheKey, newLangLockObject);
        if (langLockObject == null) {
            langLockObject = newLangLockObject;
        }

        synchronized (langLockObject) {
            final Element cachedCategoryDataElement = topCategoriesCacheRegion.getElement(cacheKey);
            if (cachedCategoryDataElement != null && !isExpired(cachedCategoryDataElement)) {
                List<DistCategoryIndexData> cachedCategoryData = (List<DistCategoryIndexData>) cachedCategoryDataElement.getObjectValue();
                cachedCategoryData.sort(Comparator.comparing(CategoryData::getName));
                return cachedCategoryData;
            }

            final List<CategoryModel> categories = distCategoryService.getCategoryByLevelRange(1, 2);

            if (CollectionUtils.isNotEmpty(categories)) {
                final List<DistCategoryIndexData> datas = new ArrayList<>();
                final Map<String, DistCategoryIndexData> cat_map = new HashMap<>();

                for (final CategoryModel source : categories) {
                    final DistCategoryIndexData target = new DistCategoryIndexData();
                    target.setCode(source.getCode());
                    target.setName(source.getName());
                    target.setNameEN(source.getName(Locale.ENGLISH));
                    target.setLevel(source.getLevel());
                    target.setUrl(categoryModelUrlResolver.resolve(source));
                    if (target.getLevel() == 1 || target.getLevel() == 2) {
                        target.setChildren(new ArrayList<>());
                        cat_map.put(target.getCode(), target);
                    }

                    if (target.getLevel() == 1) {
                        datas.add(target);
                    } else {
                        for (final CategoryModel parent : source.getSupercategories()) {
                            if (parent.getClass() == CategoryModel.class && cat_map.containsKey(parent.getCode())) {
                                cat_map.get(parent.getCode()).getChildren().add(target);
                                break;
                            }
                        }
                    }
                }

                datas.sort(Comparator.comparing(CategoryData::getName));
                topCategoriesCacheRegion.put(cacheKey, datas);
                return datas;
            }
        }

        return Collections.emptyList();
    }

    protected void refreshElementInBackground(Tenant tenant, TopCategoriesCacheKey cacheKey) {
        if (topCategoryBackgroundRefreshLocks.putIfAbsent(cacheKey, new Object()) == null) {
            RefreshTopCategoriesJob refreshJob = createRefreshTopCategoriesJob(cacheKey);

            TenantAwareThreadFactory tenantAwareThreadFactory = new TenantAwareThreadFactory(tenant);
            Thread thread = tenantAwareThreadFactory.newThread(refreshJob);
            thread.start();
        }
    }

    protected RefreshTopCategoriesJob createRefreshTopCategoriesJob(TopCategoriesCacheKey cacheKey) {
        RefreshTopCategoriesJob refreshJob = new RefreshTopCategoriesJob(cacheKey, this, topCategoryBackgroundRefreshLocks, catalogVersionService,
                                                                         commonI18NService, i18NService);
        return refreshJob;
    }

    @Override
    public boolean isCategoryEmptyForCurrentSite(final CategoryModel category) {
        return distCategoryService.isCategoryEmptyForCurrentSite(category);
    }

    @Override
    public boolean isCategoryEmptyForCurrentSite(final String catCode) {
        return distCategoryService.isCategoryEmptyForCurrentSite(getDistCategoryService().getCategoryForCode(catCode));
    }

    @Override
    public List<CMSSiteModel> getAvailableCMSSitesForCategory(final CategoryModel category) {
        return distCategoryService.getAvailableCMSSitesForCategory(category);
    }

    // Getters & Setters

    public Converter<CategoryModel, CategoryData> getCategoryConverter() {
        return categoryConverter;
    }

    public void setCategoryConverter(final Converter<CategoryModel, CategoryData> categoryConverter) {
        this.categoryConverter = categoryConverter;
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }

    public DistUrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(final DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    @Override
    public Collection<CategoryDisplayData<SearchStateData>> createCategoryDisplayData(
                                                                                      final List<FactFinderFacetValueData<SearchStateData>> categoryValuesDate) {
        // Stores root Category only in the begging and then get updated
        // afterwards
        final Collection<CategoryDisplayData<SearchStateData>> categoryDisPlayDataList = new ArrayList<>();

        // key --> categoryCodePath (categoryCodePathROOT/cat-L1D_379509), value
        // --> List of CategoryDisPlayData<SearchStateData>
        final MultiValueMap<String, CategoryDisplayData<SearchStateData>> categoryCodePath2CategoryDisPlayDataMap = new LinkedMultiValueMap<>();

        // already selected category Data : used as filter at the end
        final Collection<CategoryDisplayData<SearchStateData>> selectedDisplayDataList = new ArrayList<>();
        for (final Iterator<FactFinderFacetValueData<SearchStateData>> iterator = categoryValuesDate
                                                                                                    .iterator(); iterator.hasNext();) {
            final FactFinderFacetValueData<SearchStateData> factFinderFacetValueData = iterator.next();

            try {
                final CategoryModel categoryModel = getDistCategoryService()
                                                                            .getCategoryForCode(factFinderFacetValueData.getName());

                final CategoryDisplayData<SearchStateData> categoryDisplayData = convertCategoryModelInToCategoryDisplayData(
                                                                                                                             factFinderFacetValueData,
                                                                                                                             categoryModel);
                // Stores root Category only
                if (factFinderFacetValueData.getCode()
                                            .equals(DistrelecfactfindersearchConstants.CATEGORY_CODE_ROOT_PATH_PREFIX)) {
                    categoryDisPlayDataList.add(categoryDisplayData);
                } else {
                    categoryCodePath2CategoryDisPlayDataMap.add(factFinderFacetValueData.getCode(),
                                                                categoryDisplayData);
                }
                if (factFinderFacetValueData.isSelected()) {
                    selectedDisplayDataList.add(categoryDisplayData);
                }
            } catch (final Exception e) {
                LOG.error("erroe getting catalog for id:" + factFinderFacetValueData.getName(), e);
            }
        }

        // Key -- level , value -- list of CategoryDisPlayData<SearchStateData>
        // on that level
        final MultiValueMap<Integer, CategoryDisplayData<SearchStateData>> level2CategoryDisPlayDataMap = new LinkedMultiValueMap<>();

        // updated root Category by finding there subCategory
        for (final CategoryDisplayData<SearchStateData> categoryDisPlayData : categoryDisPlayDataList) {
            final String key = DistrelecfactfindersearchConstants.SUB_CATEGORY_CODE_ROOT_PATH_PREFIX
                               + categoryDisPlayData.getCode();
            final Integer level = new Integer(1);
            // addToDisplayMap(categoryDisPlayData,
            // level2CategoryDisPlayDataMap, level);
            updateCategoryDisPlayData(key, categoryDisPlayData, categoryCodePath2CategoryDisPlayDataMap,
                                      level.intValue(), level2CategoryDisPlayDataMap);
        }

        // TODO:- ignore it for now after discussion with René
        final Integer reduce = Integer.valueOf(1);
        final Integer highestLevelValue = isNotEmpty(level2CategoryDisPlayDataMap.keySet()) ? Collections.max(level2CategoryDisPlayDataMap.keySet()) : 1;

        // DISTRELEC-11300 : means there is only category matched on that level,
        // hence we will show sub category
        // if
        // (level2CategoryDisPlayDataMap.get(Integer.valueOf(highestLevelValue.intValue()
        // - reduce.intValue())).size() == 1) {
        // reduce = Integer.valueOf(0);
        // }

        // if (LOG.isDebugEnabled()) {
        // LOG.debug(String.format("highestLevelValue=%s & reduce=%s",
        // highestLevelValue, reduce));
        // }
        if (selectedDisplayDataList.size() > 0) {
            removeAlreadySelectedCategory(level2CategoryDisPlayDataMap, selectedDisplayDataList);
        }

        // Handling of special cases for example when we have 4 levels is result
        // and top 3 are selected. so we display only last level in
        // this case
        final List<CategoryDisplayData<SearchStateData>> finalresultlist = level2CategoryDisPlayDataMap
                                                                                                       .get(Integer.valueOf(highestLevelValue.intValue()
                                                                                                                            - reduce.intValue()));
        return CollectionUtils.isEmpty(finalresultlist)
                                                        ? level2CategoryDisPlayDataMap.get(Integer.valueOf(highestLevelValue.intValue()))
                                                        : finalresultlist;
        // return categoryDisPlayDataList;
    }

    @Override
    public SearchResult<CategoryData> searchCategory(String term, int page, int pageSize) {
        SearchResult<CategoryModel> modelSearchResult = distCategoryService.searchCategories(term, page, pageSize);
        List<CategoryData> resultDataList = modelSearchResult.getResult().stream()
                                                             .map(categoryConverter::convert)
                                                             .collect(Collectors.toList());
        SearchResult<CategoryData> resultData = new SearchResultImpl<>(resultDataList, modelSearchResult.getTotalCount(), modelSearchResult.getRequestedCount(),
                                                                       modelSearchResult.getRequestedStart());
        return resultData;
    }

    @Override
    public CategoryData findCategory(String code) {
        List<CategoryModel> categoriesForCode = distCategoryService.getCategoriesForCode(Arrays.asList(code));

        if (CollectionUtils.isNotEmpty(categoriesForCode)) {
            if (categoriesForCode.size() == 1) {
                return categoryConverter.convert(categoriesForCode.get(0));
            }

            LOG.warn("More than one category found for code {}", code);
        }

        return null;
    }

    private void removeAlreadySelectedCategory(
                                               final MultiValueMap<Integer, CategoryDisplayData<SearchStateData>> level2CategoryDisplayDataMap,
                                               final Collection<CategoryDisplayData<SearchStateData>> selectedDisplayDataList) {
        for (final CategoryDisplayData<SearchStateData> selectedDisplayData : selectedDisplayDataList) {
            final Iterator<Integer> iterator = level2CategoryDisplayDataMap.keySet().iterator();
            while (iterator.hasNext()) {
                final List<CategoryDisplayData<SearchStateData>> level2CategoryDisPlaylist = level2CategoryDisplayDataMap
                                                                                                                         .get(iterator.next());
                if (level2CategoryDisPlaylist.contains(selectedDisplayData)) {
                    level2CategoryDisPlaylist.remove(selectedDisplayData);
                }
            }
        }
    }

    private CategoryDisplayData<SearchStateData> convertCategoryModelInToCategoryDisplayData(
                                                                                             final FactFinderFacetValueData<SearchStateData> factFinderFacetValueData,
                                                                                             final CategoryModel categoryModel) {
        return getCategoryDisPlayDataConverter()
                                                .convert(new FactFinderFacetValueCategoryDisplayDataDto(categoryModel, factFinderFacetValueData));
    }

    private void updateCategoryDisPlayData(final String key,
                                           final CategoryDisplayData<SearchStateData> categoryDisPlayData,
                                           final MultiValueMap<String, CategoryDisplayData<SearchStateData>> categoryCodePath2CategoryDisPlayDataMap,
                                           final int level,
                                           final MultiValueMap<Integer, CategoryDisplayData<SearchStateData>> level2CategoryDisPlayDataMap) {

        addToDisplayMap(categoryDisPlayData, level2CategoryDisPlayDataMap, new Integer(level));

        if (categoryCodePath2CategoryDisPlayDataMap.containsKey(key)) {
            final List<CategoryDisplayData<SearchStateData>> subCategoryDisPlayDatalist = categoryCodePath2CategoryDisPlayDataMap
                                                                                                                                 .get(key);
            categoryDisPlayData.getSubcategoryDisplayDataList().addAll(subCategoryDisPlayDatalist);
            for (final CategoryDisplayData<SearchStateData> subCategoryDisPlayData : subCategoryDisPlayDatalist) {
                final String subKey = key + "/" + subCategoryDisPlayData.getCode();
                final Integer subCatLevel = new Integer(level + 1);
                // addToDisplayMap(categoryDisPlayData,
                // level2CategoryDisPlayDataMap, subCatLevel);
                updateCategoryDisPlayData(subKey, subCategoryDisPlayData, categoryCodePath2CategoryDisPlayDataMap,
                                          subCatLevel.intValue(), level2CategoryDisPlayDataMap);
            }
        }
    }

    private void addToDisplayMap(final CategoryDisplayData<SearchStateData> categoryDisPlayData,
                                 final MultiValueMap<Integer, CategoryDisplayData<SearchStateData>> level2CategoryDisPlayDataMap,
                                 final Integer subCatLevel) {
        final List<CategoryDisplayData<SearchStateData>> categoryDisPlayDataList = level2CategoryDisPlayDataMap
                                                                                                               .get(subCatLevel);
        if (categoryDisPlayDataList == null || !categoryDisPlayDataList.contains(categoryDisPlayData)) {
            level2CategoryDisPlayDataMap.add(subCatLevel, categoryDisPlayData);
        }
    }

    @Override
    public DistCategoryPageData getCategoryPageDataForOCC(final String categoryCode) {
        final CategoryModel category = distCategoryService.getCategoryForCode(categoryCode);
        final DistCategoryPageData categoryPageData = getCategoryPageData(category);
        categoryPageData.setSourceCategory(categoryConverter.convert(category));
        categoryPageData.setBreadcrumbs(categoryConverter.convertAll(getBreadcrumbCategoriesInReverseOrderForCategory(category)));
        return categoryPageData;
    }

    public List<CategoryModel> getBreadcrumbCategoriesInReverseOrderForCategory(final CategoryModel category) {
        final List<CategoryModel> categories = getSupercategoriesForCategory(category, new LinkedList<>());
        Collections.reverse(categories);
        return categories;
    }

    private List<CategoryModel> getSupercategoriesForCategory(final CategoryModel currentCategory, final LinkedList<CategoryModel> categories) {
        if (nonNull(currentCategory)) {
            if (!(currentCategory instanceof ClassificationClassModel) && currentCategory.getLevel() != null && currentCategory.getLevel() != 0) {
                categories.add(currentCategory);
            }
            if (CollectionUtils.isNotEmpty(currentCategory.getSupercategories())) {
                getSupercategoriesForCategory(currentCategory.getSupercategories().iterator().next(), categories);
            }
        }
        return categories;
    }

    @Override
    public RelatedData findCategoryRelatedData(final CategoryModel category) {
        if (category.getPimCategoryType() != null) {
            return getDistRelatedDataFacade().findCategoryRelatedData(category.getCode());
        }
        return null;
    }

    public Converter<FactFinderFacetValueCategoryDisplayDataDto, CategoryDisplayData<SearchStateData>> getCategoryDisPlayDataConverter() {
        return categoryDisplayDataConverter;
    }

    public void setCategoryDisPlayDataConverter(
                                                final Converter<FactFinderFacetValueCategoryDisplayDataDto, CategoryDisplayData<SearchStateData>> categoryDisplayDataConverter) {
        this.categoryDisplayDataConverter = categoryDisplayDataConverter;
    }

    public DistRelatedDataFacade getDistRelatedDataFacade() {
        return distRelatedDataFacade;
    }

    public void setDistRelatedDataFacade(final DistRelatedDataFacade distRelatedDataFacade) {
        this.distRelatedDataFacade = distRelatedDataFacade;
    }
}
