package com.namics.distrelec.b2b.facades.category.job;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.category.CategoryCMSSiteEntryModel;
import com.namics.distrelec.b2b.core.model.jobs.CheckNonEmptyCategoriesCronJobModel;
import com.namics.distrelec.b2b.core.util.DistLogUtils;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class CheckDeactivatedCategoriesJob extends AbstractJobPerformable<CheckNonEmptyCategoriesCronJobModel> {

    private static final Logger LOG = LogManager.getLogger(CheckDeactivatedCategoriesJob.class);

    private static final int ORA_SQL_LIMIT = 1000;

    private static final String CMS_SITE_QUERY_PARAM = "cmsSite";

    private static final String SALESORG_PUNCHOUT_QUERY = "SELECT {dsopf.product} FROM {DistSalesOrgPunchOutFilter! AS dsopf" //
                                                          + "           JOIN DistSalesOrg AS so ON {so.pk}={dsopf.salesOrg}" //
                                                          + "    } " //
                                                          + "    WHERE {so.pk}= ?salesOrg";

    private static final String COUNTRY_PUNCHOUT_QUERY = "SELECT {dcopf.product} FROM {DistCOPunchOutFilter! AS dcopf " //
                                                         + "           JOIN DistSalesOrg AS so ON {so.pk}={dcopf.salesOrg}" //
                                                         + "           JOIN CMSSite AS site ON {site.salesOrg}={so.pk}" //
                                                         + "    } " //
                                                         + "    WHERE {so.pk}= ?salesOrg " //
                                                         + "       AND {site.country}={dcopf.country} " //
                                                         + "       AND {site.pk}= ?cmsSite";

    private static final String CATEGORIES_WITH_PRODUCTS_QUERY = "SELECT DISTINCT {ca.pk} FROM { Product AS p" //
                                                                 + "           JOIN CategoryProductRelation! AS cpr ON {p.pk}={cpr.target} " //
                                                                 + "           JOIN Category! AS ca ON {ca.pk}={cpr.source}"//
                                                                 + "           JOIN DistSalesOrgProduct AS dsop ON {dsop.product} = {p.pk}" //
                                                                 + "           JOIN DistSalesStatus AS st ON {dsop.salesStatus} = {st.pk}" //
                                                                 + "           JOIN CatalogVersion AS cv ON {p.catalogVersion} = {cv.pk}" //
                                                                 + "           JOIN Catalog AS c ON {cv.catalog} = {c.pk}" //
                                                                 + "           JOIN DistSalesOrg AS dso ON {dso.pk}={dsop.salesOrg}" //
                                                                 + "    }" //
                                                                 + "    WHERE {dso.pk} = ?salesOrg AND {st.buyableInShop} = 1 " //
                                                                 + "       AND {c.id} = 'distrelecProductCatalog' AND {cv.version} = 'Online'" //
                                                                 + "       AND {p.pk} NOT IN ({{ " + SALESORG_PUNCHOUT_QUERY + " }})" //
                                                                 + "       AND {p.pk} NOT IN ({{ " + COUNTRY_PUNCHOUT_QUERY + "  }})";

    private static final String FIND_CATEGORY_CMS_SITE_ENTRIES_FOR_CMSSITE = "SELECT {pk} FROM {CategoryCMSSiteEntry} WHERE {cmsSite} =?cmsSite ";

    private static final String FIND_CATEGORY_CMS_SITE_ENTRIES_FOR_CMSSITE_AND_CATEGORY = FIND_CATEGORY_CMS_SITE_ENTRIES_FOR_CMSSITE
                                                                                          + " AND {category} =?category";

    private static final String FIND_CATEGORY_CMS_SITE_ENTRIES_WITH_EMPTY_CATEGORIES_FOR_CMSSITE_AND_ENTRIES = FIND_CATEGORY_CMS_SITE_ENTRIES_FOR_CMSSITE
                                                                                                               + " AND {pk} NOT IN (?entries)";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public PerformResult perform(final CheckNonEmptyCategoriesCronJobModel cronjob) {
        long startTime = System.currentTimeMillis();
        LOG.info("Start checking non empty categories");

        cmsSiteService.getSites().forEach(cmsSite -> {
            try {
                long siteTime = System.currentTimeMillis();
                processCMSSite(cmsSite);
                LOG.info("Processing CMS Site {} took {} ms.", cmsSite.getUid(), System.currentTimeMillis() - siteTime);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        });
        LOG.info("\n\nTotal job time: {} ms.", System.currentTimeMillis() - startTime);
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void processCMSSite(final CMSSiteModel cmsSite) {
        LOG.info("Processing CMSSite: {}", cmsSite.getUid());

        List<CategoryModel> categoriesWithProducts = getCategoriesWithProducts(cmsSite);

        Map<String, CategoryCMSSiteEntryModel> categoryCMSSiteEntriesCategoriesWithProducts = new HashMap<>();
        categoriesWithProducts.forEach(category -> addCategoriesWithProductsToMap(category, cmsSite, categoryCMSSiteEntriesCategoriesWithProducts));

        for (CategoryCMSSiteEntryModel entry : categoryCMSSiteEntriesCategoriesWithProducts.values()) {
            try {
                SearchResult<CategoryCMSSiteEntryModel> existingCategoryCMSSiteEntryResult = getExistingCategoryCMSSiteEntry(cmsSite, entry.getCategory());
                saveNewCategoryCMSSiteEntriesAndAddToMap(existingCategoryCMSSiteEntryResult, entry, categoryCMSSiteEntriesCategoriesWithProducts);
            } catch (Exception exp) {
                DistLogUtils.logError(LOG,
                                      "ERROR while processing the entry [CMSSite: {}, Category: {}]", exp, entry.getCmsSite().getUid(),
                                      entry.getCategory().getCode());
            }
        }
        getCategoryCMSSiteEntriesWithEmptyCategories(categoryCMSSiteEntriesCategoriesWithProducts, cmsSite).forEach(this::removeCategoryCMSSiteEntry);
    }

    private void saveNewCategoryCMSSiteEntriesAndAddToMap(SearchResult<CategoryCMSSiteEntryModel> existingCategoryCMSSiteEntryResult,
                                                          CategoryCMSSiteEntryModel entry,
                                                          Map<String, CategoryCMSSiteEntryModel> categoryCMSSiteEntriesCategoriesWithProducts) {
        if (existingCategoryCMSSiteEntryResult.getCount() == 0) {
            modelService.save(entry);
            modelService.refresh(entry);
        } else {
            categoryCMSSiteEntriesCategoriesWithProducts.put(entry.getCategory().getCode(), existingCategoryCMSSiteEntryResult.getResult().get(0));
        }
    }

    private List<CategoryCMSSiteEntryModel> getCategoryCMSSiteEntriesWithEmptyCategories(Map<String, CategoryCMSSiteEntryModel> catSiteEntriesCategoriesWithProducts,
                                                                                         CMSSiteModel cmsSite) {
        FlexibleSearchQuery catSiteEntriesWithEmptyCategoriesQuery = getQueryForCategoryCMSSiteEntriesWithEmptyCategories(catSiteEntriesCategoriesWithProducts.values(),
                                                                                                                          cmsSite);

        return flexibleSearchService.<CategoryCMSSiteEntryModel> search(catSiteEntriesWithEmptyCategoriesQuery).getResult();
    }

    private SearchResult<CategoryCMSSiteEntryModel> getExistingCategoryCMSSiteEntry(CMSSiteModel cmsSite, CategoryModel category) {
        FlexibleSearchQuery entryQuery = new FlexibleSearchQuery(FIND_CATEGORY_CMS_SITE_ENTRIES_FOR_CMSSITE_AND_CATEGORY);
        entryQuery.addQueryParameter(CMS_SITE_QUERY_PARAM, cmsSite);
        entryQuery.addQueryParameter("category", category);
        return flexibleSearchService.search(entryQuery);
    }

    private List<CategoryModel> getCategoriesWithProducts(CMSSiteModel cmsSite) {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(CATEGORIES_WITH_PRODUCTS_QUERY);
        searchQuery.addQueryParameter(CMS_SITE_QUERY_PARAM, cmsSite);
        searchQuery.addQueryParameter("salesOrg", cmsSite.getSalesOrg());
        return flexibleSearchService.<CategoryModel> search(searchQuery).getResult();
    }

    private void removeCategoryCMSSiteEntry(CategoryCMSSiteEntryModel categoryCMSSiteEntry) {
        try {
            modelService.remove(categoryCMSSiteEntry);
        } catch (Exception e) {
            DistLogUtils.logError(LOG,
                                  "ERROR while removing the entry [CMSSite: {}, Category: {}]",
                                  e,
                                  categoryCMSSiteEntry.getCmsSite().getUid(),
                                  categoryCMSSiteEntry.getCategory().getCode());
        }
    }

    private void addCategoriesWithProductsToMap(final CategoryModel category, final CMSSiteModel cmsSite, final Map<String, CategoryCMSSiteEntryModel> map) {
        if (category == null || map.containsKey(category.getCode()) || category.getLevel() == null || category.getLevel() <= 0) {
            return;
        }
        CategoryCMSSiteEntryModel entry = modelService.create(CategoryCMSSiteEntryModel.class);
        entry.setCategory(category);
        entry.setCmsSite(cmsSite);

        map.put(category.getCode(), entry);
        if (isNotEmpty(category.getSupercategories())) {
            category.getSupercategories().stream()
                    .filter(supCat -> supCat.getClass() == CategoryModel.class)
                    .findFirst()
                    .ifPresent(supCat -> addCategoriesWithProductsToMap(supCat, cmsSite, map));
        }
    }

    private FlexibleSearchQuery getQueryForCategoryCMSSiteEntriesWithEmptyCategories(Collection<CategoryCMSSiteEntryModel> entries,
                                                                                     CMSSiteModel cmsSite) {
        final FlexibleSearchQuery searchQuery;
        if (entries.size() <= ORA_SQL_LIMIT) {
            searchQuery = new FlexibleSearchQuery(FIND_CATEGORY_CMS_SITE_ENTRIES_WITH_EMPTY_CATEGORIES_FOR_CMSSITE_AND_ENTRIES);
            searchQuery.addQueryParameter("entries", entries);
        } else {
            final Map<String, Object> queryParams = new HashMap<>();
            final String IN_CLOSE = " AND {pk} NOT IN (?";
            final StringBuilder sb = new StringBuilder(FIND_CATEGORY_CMS_SITE_ENTRIES_FOR_CMSSITE);
            final Object[] array = entries.toArray();
            final int n = (array.length / ORA_SQL_LIMIT) + ((array.length % ORA_SQL_LIMIT == 0) ? 0 : 1);

            int offset = 0;
            for (int i = 0; i < n; i++) {
                sb.append(IN_CLOSE)
                  .append("entries_")
                  .append(i)
                  .append(") ");
                final int to = offset + Math.min(ORA_SQL_LIMIT, array.length - offset);
                queryParams.put("entries_" + i, Arrays.asList(Arrays.copyOfRange(array, offset, to)));
                offset = to;
            }
            searchQuery = new FlexibleSearchQuery(sb.toString(), queryParams);
        }
        searchQuery.addQueryParameter(CMS_SITE_QUERY_PARAM, cmsSite);
        return searchQuery;
    }
}
