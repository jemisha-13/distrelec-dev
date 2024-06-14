package com.distrelec.solrfacetsearch.indexer.cron;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.namics.distrelec.b2b.core.service.category.dao.DistCategoryDao;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.enums.IndexerOperationValues;
import de.hybris.platform.solrfacetsearch.indexer.cron.SolrIndexerJob;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.indexer.cron.SolrIndexerCronJobModel;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Runs the index job once per cms-site (DistSolrIndexerJob.cmsSites) configured ont the cron-job
 */
public class DistSolrIndexerJob extends SolrIndexerJob {
    private static final Logger LOG = LogManager.getLogger(DistSolrIndexerJob.class);

    private static final String EXPORT_SHOP = "distrelec_EX";

    @Autowired
    private DistCategoryDao distCategoryDao;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public PerformResult performIndexingJob(CronJobModel cronJob) {
        LOG.info("Started indexer cronjob.");
        if (!(cronJob instanceof SolrIndexerCronJobModel)) {
            LOG.warn("Unexpected cronjob type: {}", cronJob);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }

        SolrIndexerCronJobModel solrIndexerCronJob = (SolrIndexerCronJobModel) cronJob;
        SolrFacetSearchConfigModel facetSearchConfigModel = solrIndexerCronJob.getFacetSearchConfig();
        FacetSearchConfig facetSearchConfig = this.getFacetSearchConfig(facetSearchConfigModel);

        if (facetSearchConfig == null) {
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
        }

        if (CollectionUtils.isEmpty(solrIndexerCronJob.getCmsSites())) {
            LOG.warn("No CMSSite for indexing configured on cronjob:{}", solrIndexerCronJob.getCode());
        }

        for (final CMSSiteModel cmsSite : solrIndexerCronJob.getCmsSites()) {
            if (clearAbortRequestedIfNeeded(cronJob)) {
                return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.ABORTED);
            }

            logCMSSiteIndexingStart(solrIndexerCronJob.getIndexerOperation(), cmsSite);

            BaseStoreModel baseStore = getBaseStore(cmsSite);
            if (baseStore == null) {
                LOG.warn("No B2B Channel found for CMSSite[{}], site will be skipped from indexing", cmsSite.getUid());
                continue;
            }
            facetSearchConfig.getIndexConfig().setCurrencies(baseStore.getCurrencies());
            facetSearchConfig.getIndexConfig().setLanguages(baseStore.getLanguages());
            facetSearchConfig.getIndexConfig().setCmsSite(cmsSite);
            facetSearchConfig.getIndexConfig().setAtomicUpdate(isTrue(((SolrIndexerCronJobModel) cronJob).getUseAtomicUpdates()));
            facetSearchConfig.getIndexConfig().setAllCountries(getAllCountries(cmsSite, baseStore));

            try {
                preLoadValues(facetSearchConfig, cmsSite);
                this.indexItems(solrIndexerCronJob, facetSearchConfig);
            } catch (IndexerException e) {
                LOG.error("Error during indexer call!", e);
                return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
            }

            logCMSSiteIndexingEnd(facetSearchConfig, solrIndexerCronJob.getIndexerOperation(), cmsSite);
        }

        LOG.info("Finished indexer cronjob.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private BaseStoreModel getBaseStore(CMSSiteModel cmsSite) {
        return cmsSite.getStores()
                      .stream()
                      .filter(store -> store.getChannel().equals(SiteChannel.B2B) && store.getDefaultCurrency() != null)
                      .findAny()
                      .orElse(null);
    }

    private List<CountryModel> getAllCountries(CMSSiteModel cmsSite, BaseStoreModel baseStore) {
        return getAllCountries(baseStore.getDeliveryCountries(),
                               getSiteSettingsExtraCountries(cmsSite),
                               baseStore.getRegisterCountries(),
                               cmsSite);
    }

    private List<CountryModel> getSiteSettingsExtraCountries(CMSSiteModel cmsSite) {
        Configuration config = configurationService.getConfiguration();
        String[] extraCountries = config.getStringArray("sitesettings." + cmsSite.getUid() + ".extraCountries");
        return Stream.of(extraCountries)
                     .filter(StringUtils::isNotBlank)
                     .map(commonI18NService::getCountry)
                     .collect(Collectors.toList());
    }

    private List<CountryModel> getAllCountries(Collection<CountryModel> deliveryCountries,
                                               Collection<CountryModel> extraCountries,
                                               Collection<CountryModel> registerCountries,
                                               CMSSiteModel cmsSite) {
        if (EXPORT_SHOP.equals(cmsSite.getUid())) {
            List<CountryModel> additionalCountries = Stream.concat(deliveryCountries.stream(), extraCountries.stream())
                                                           .filter(c -> extraCountries.contains(c)
                                                                   || isEmpty(registerCountries)
                                                                   || registerCountries.contains(c))
                                                           .collect(Collectors.toList());
            additionalCountries.add(cmsSite.getCountry());
            return additionalCountries;
        } else {
            return Stream.concat(deliveryCountries.stream(), extraCountries.stream())
                         .collect(Collectors.toList());
        }
    }

    /**
     * Use it only for read-only values. FacetSearchConfig is copied for each worker (DistIndexerWorker).
     */
    private void preLoadValues(FacetSearchConfig facetSearchConfig, CMSSiteModel cmsSite) {
        long t0 = currentTimeMillis();
        preLoadForCategories(facetSearchConfig, cmsSite);
        LOG.info("Preloading of indexing data for {} took {} ms", cmsSite.getUid(), currentTimeMillis() - t0);
    }

    private void preLoadForCategories(FacetSearchConfig facetSearchConfig, CMSSiteModel cmsSite) {
        if (isCategoryTypeIndexed(facetSearchConfig)) {
            Set<String> visibleCategoryCodes = distCategoryDao.getAllVisibleCategoryCodes(cmsSite);
            facetSearchConfig.getIndexConfig().setVisibleCategoryCodes(new HashSet<>(visibleCategoryCodes));
        }
    }

    private boolean isCategoryTypeIndexed(FacetSearchConfig facetSearchConfig) {
        return facetSearchConfig.getIndexConfig().getIndexedTypes().containsKey("Category_category");
    }

    private void logCMSSiteIndexingStart(IndexerOperationValues indexerOperationValues, CMSSiteModel cmsSite) {
        LOG.info("Start {} indexing for CMS-Site:{}", indexerOperationValues.getCode(), cmsSite.getUid());
    }

    private void logCMSSiteIndexingEnd(FacetSearchConfig facetSearchConfig, IndexerOperationValues indexerOperationValues, CMSSiteModel cmsSite) {
        LocalDateTime startTime = facetSearchConfig.getIndexConfig().getStartTime();
        LocalDateTime endTime = facetSearchConfig.getIndexConfig().getEndTime();
        long timeInSeconds = ChronoUnit.SECONDS.between(startTime, endTime);
        LOG.info("Finished {} indexing for CMS-Site:{}, it took {} s", indexerOperationValues.getCode(), cmsSite.getUid(), timeInSeconds);
    }

    @Override
    public boolean isAbortable() {
        return true;
    }
}
