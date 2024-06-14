/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.category.impl;

import com.namics.distrelec.b2b.core.model.CategoryCountryModel;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import com.namics.distrelec.b2b.core.model.category.DeactivatedCategoryEntryModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.category.dao.DistCategoryDao;
import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.impl.DefaultCategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.converter.impl.DefaultLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static java.util.Objects.nonNull;

/**
 * Default implementation of {@link DistCategoryService}
 * 
 * @author pnueesch, Namics AG
 * @since Namics Extensions 1.0
 */
public class DefaultDistCategoryService extends DefaultCategoryService implements DistCategoryService {

    private static final Logger LOG = LogManager.getLogger(DefaultDistCategoryService.class);

    private static final String CATEGORY_ENTRY_FOR_CMSSITE_QUERY = "SELECT COUNT({pk}) FROM {CategoryCMSSiteEntry} WHERE {cmsSite} =?cmsSite AND {category} =?category";

    private static final String CMSSITE_CHANNEL_CATEGORY_QUERY = "SELECT DISTINCT({pk}) FROM {" + CMSSiteModel._TYPECODE + "} WHERE {pk} NOT IN " //
            + "({{SELECT DISTINCT({st.pk}) FROM {" + CMSSiteModel._TYPECODE + " AS st " //
            + "JOIN " + DistFactFinderExportChannelModel._TYPECODE + " AS ffc ON {st.pk}={ffc.cmsSite} " //
            + "JOIN " + DeactivatedCategoryEntryModel._TYPECODE + " AS dce ON {dce.channelCode}={ffc.code} " //
            + "JOIN " + CategoryModel._TYPECODE + " AS c ON {dce.category}={c.PK}} " //
            + "WHERE {c.pk}=?category}})";

    private static final String SEARCH_CATEGORIES_QUERY = "SELECT {c.pk} FROM {Category AS c} WHERE {c.code} LIKE ?term OR {c.name} LIKE ?term";

    @Autowired
    private DistCategoryDao distCategoryDao;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public CategoryCountryModel getCountrySpecificCategoryInformation(final CategoryModel category) {
        final CMSSiteModel currentCmsSite = getCmsSiteService().getCurrentSite();
        if (currentCmsSite != null && currentCmsSite.getCountry() != null) {
            return getCountrySpecificCategoryInformation(category, currentCmsSite.getCountry());
        } else {
            return null;
        }
    }

    @Override
    public CategoryCountryModel getCountrySpecificCategoryInformation(final CategoryModel category, final CountryModel country) {
        return getDistCategoryDao().findCountrySpecificCategoryInformation(category, country);
    }

    @Override
    public List<CategoryModel> getProductSuperCategories(final CategoryModel category) {
        final List<CategoryModel> productSuperCategories = new ArrayList<>();

        final List<CategoryModel> superCategories = category.getSupercategories();
        if (superCategories != null) {
            for (final CategoryModel superCategory : superCategories) {
                if (superCategory != null && category.getCatalogVersion().equals(superCategory.getCatalogVersion())
                        && !(superCategory instanceof ClassificationClassModel)) {
                    productSuperCategories.add(superCategory);
                }
            }
        }

        return productSuperCategories;
    }

    @Override
    public List<CategoryModel> getEmptyCategories() {
        return getDistCategoryDao().findEmptyCategories();
    }

    @Override
    public List<CategoryModel> getCategoryByLevelRange(final int from, final int to) {
        return getDistCategoryDao().getCategoryByLevelRange(from, to);
    }

    @Override
    public boolean isCategoryEmptyForCurrentSite(final String catCode) {
        return isCategoryEmptyForCurrentSite(getCategoryForCode(catCode));
    }

    @Override
    public boolean isCategoryEmptyForCurrentSite(final CategoryModel category) {
        return isCategoryEmptyForCMSSite(category, getCmsSiteService().getCurrentSite());
    }

    @Override
    public boolean isCategoryEmptyForCMSSite(final CategoryModel category, final CMSSiteModel cmsSite) {
        if (category == null || cmsSite == null) {
            LOG.warn("isCategoryEmptyForCMSSite called with category: {} and cmsSite: {}", category, cmsSite);
            return true;
        }
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(CATEGORY_ENTRY_FOR_CMSSITE_QUERY);
        searchQuery.addQueryParameter("category", category);
        searchQuery.addQueryParameter("cmsSite", cmsSite);
        searchQuery.setResultClassList(Arrays.asList(Long.class));
        return getFlexibleSearchService().<Long> search(searchQuery).getResult().get(0).longValue() == 0;
    }

    @Override
    public List<CategoryModel> getCategoriesForName(final String name) {
        final CategoryModel example = new CategoryModel();
        final LocaleProvider localeProvider = new DefaultLocaleProvider(i18nService);
        // example.setLocaleProvider(localeProvider);
        example.setName(name, localeProvider.getCurrentDataLocale());
        example.setLevel(3);
        try {
            return flexibleSearchService.getModelsByExample(example);
        } catch (final RuntimeException ex) {
            return null;
        }
    }

    @Override
    public List<CategoryModel> getCategoriesForCode(final List<String> codes) {
        validateParameterNotNull(codes, "Parameter 'code' was null.");
        return getDistCategoryDao().findCategoriesByCodes(codes);
    }

    @Override
    public List<CMSSiteModel> getAvailableCMSSitesForCategory(final CategoryModel category) {
        if (CollectionUtils.isEmpty(category.getDeactivatedForChannels())) {
            return new ArrayList<>(getCmsSiteService().getSites());
        }

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(CMSSITE_CHANNEL_CATEGORY_QUERY);
        searchQuery.addQueryParameter("category", category.getPk());

        return getFlexibleSearchService().<CMSSiteModel> search(searchQuery).getResult();
    }

    @Override
    public CategoryModel findSuccessor(final String code) {
        return getDistCategoryDao().findSuccessor(code);
    }

    @Override
    public boolean hasSuccessor(final String code) {
        return findSuccessor(code) != null;
    }

    @Override
    public boolean hasProductsOrSubCategories(final CategoryModel category) {
        return getDistCategoryDao().hasProductsOrSubCategories(category);
    }

    @Override
    public Collection<CategoryModel> getCategoriesInTree(final CategoryModel subTreeRoot) {
        final Collection<CategoryModel> subCat = new HashSet(getAllSubcategoriesForCategory(subTreeRoot));
        subCat.add(subTreeRoot);
        final Collection<CategoryModel> result = subCat.stream()
                .filter(c -> c.getCatalogVersion().equals(subTreeRoot.getCatalogVersion()) && !isCategoryEmptyForCurrentSite(c))
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(result)) {
            LOG.warn("getCategoriesInTree for category: {} returns an empty collection", subTreeRoot.getCode());
        }
        return result;
    }

    @Override
    public Collection<CategoryModel> getCategoriesInTree(final String subTreeRootCode) {
        return getCategoriesInTree(getCategoryForCode(subTreeRootCode));
    }

    @Override
    public boolean isProductLine(final CategoryModel category) {
        return category.getPimCategoryType() != null && BooleanUtils.isTrue(category.getPimCategoryType().getForceProductList());
    }

    @Override
    public SearchResult<CategoryModel> searchCategories(String term, int page, int pageSize) {
        Map<String,Object> queryParamerters = new HashMap<>();
        queryParamerters.put("term", "%"+term+"%");
        FlexibleSearchQuery query = new FlexibleSearchQuery(SEARCH_CATEGORIES_QUERY, queryParamerters);
        query.setNeedTotal(true);
        query.setStart(page * pageSize);
        query.setCount(pageSize);

        return flexibleSearchService.search(query);
    }

    @Override
    public Optional<CategoryModel> findProductFamily(String code) {
        return distCategoryDao.findProductFamily(code);
    }

    @Override
    public boolean hasMultipleProductsInFamily(final String code) {
        return distCategoryDao.hasMultipleProductsInFamily(code);
    }

    @Override
    public boolean isProductFamily(CategoryModel category) {
        return Optional.ofNullable(category)
                       .map(CategoryModel::getPimCategoryType)
                       .map(DistPimCategoryTypeModel::getCode)
                       .filter("Familie"::equals)
                       .isPresent();
    }
    
    @Override
    public boolean categoryHasVisibleProduct(CategoryModel category, CMSSiteModel cmsSite)
    {
        return distCategoryDao.categoryHasVisibleProduct(category,cmsSite);
    }

    @Override
    public Set<String> getAllVisibleCategoryCodes(CMSSiteModel cmsSite) {
        return distCategoryDao.getAllVisibleCategoryCodes(cmsSite);
    }

    @Override
    public List<CategoryModel> getBreadcrumbCategoriesInReverseOrderForCategory(final CategoryModel category)
    {
        final List<CategoryModel> categories = getSupercategoriesForCategory(category, new LinkedList<>());
        Collections.reverse(categories);
        return categories;
    }

    private List<CategoryModel> getSupercategoriesForCategory(final CategoryModel currentCategory, final LinkedList<CategoryModel> categories)
    {
        if (nonNull(currentCategory))
        {
            if (!(currentCategory instanceof ClassificationClassModel))
            {
                categories.add(currentCategory);
            }
            if (CollectionUtils.isNotEmpty(currentCategory.getSupercategories()))
            {
                getSupercategoriesForCategory(currentCategory.getSupercategories().iterator().next(), categories);
            }
        }
        return categories;
    }


    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistCategoryDao getDistCategoryDao() {
        return distCategoryDao;
    }

    public void setDistCategoryDao(final DistCategoryDao distCategoryDao) {
        this.distCategoryDao = distCategoryDao;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }
}
