/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap;

import com.namics.distrelec.b2b.core.model.jobs.DistWebSitemapCronJobModel;
import com.namics.distrelec.b2b.core.service.sitemap.impl.*;
import com.namics.distrelec.b2b.core.service.sitemap.utils.WebSitemapHelper;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.admin.impl.DefaultCMSAdminSiteService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * WebSitemapJob.
 * 
 * @author csieber, Namics AG
 * @since Distrelec 1.0
 */
public class WebSitemapJob extends AbstractJobPerformable<DistWebSitemapCronJobModel> {
    private static final Logger LOG = Logger.getLogger(WebSitemapJob.class);

    private static final String SITEMAP_NEW_JOB = "sitemap.new.job";

    private static final String SITEMAP_NEW_JOB_CMS_SITES = "sitemap.new.job.cms.sites";

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DefaultCMSAdminSiteService cmsAdminSiteService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private WebSitemapHelper webSitemapHelper;

    @Autowired
    @Qualifier("webSitemapService")
    private WebSitemapService webSitemapService;

    @Autowired
    @Qualifier("distWebSitemapService")
    private WebSitemapService distWebSitemapService;

    @Override
    public PerformResult perform(final DistWebSitemapCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting WebSitemap Job.");

        try {
            performJob(cronJob.getSites());
        } catch (final InterruptedException e) {
            LOG.error("Could not sleep after removing item.", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }
        final long endTime = System.nanoTime();
        LOG.info("Finished WebSitemap Job in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    protected void performJob(final Set<CMSSiteModel> sites) throws InterruptedException {

        boolean useNewJob = configurationService.getConfiguration().getBoolean(SITEMAP_NEW_JOB, false);
        String newJobCMSSites = configurationService.getConfiguration().getString(SITEMAP_NEW_JOB_CMS_SITES, StringUtils.EMPTY);

        for (final CMSSiteModel cmsSiteModel : SetUtils.emptyIfNull(sites)) {
            if (cmsSiteModel.getDefaultLanguage() != null && CollectionUtils.isNotEmpty(cmsSiteModel.getContentCatalogs())) {
                String domain = getDomainForCMSSiteMode(cmsSiteModel);
                if (useNewJob || newJobCMSSites.contains(cmsSiteModel.getUid())) {
                    distWebSitemapService.generateSitemapForSite(cmsSiteModel, domain, getWebSitemapNewJobUrlProviders(cmsSiteModel));
                } else {
                    webSitemapService.generateSitemapForSite(cmsSiteModel, domain, getWebSitemapUrlProviders(cmsSiteModel));
                }
            }
        }
    }

    protected List<WebSitemapUrlsProvider> getWebSitemapUrlProviders(final CMSSiteModel cmsSiteModel) {
        final List<WebSitemapUrlsProvider> providerList = new ArrayList<WebSitemapUrlsProvider>();
        // Category site map URL provider
        final DistCategoryWebSitemapUrlsProvider categoryWebSitemapUrlsProvider = applicationContext.getBean(DistCategoryWebSitemapUrlsProvider.class);
        providerList.add(categoryWebSitemapUrlsProvider);
        // Manufacturer site map URL provider
        final DistManufacturerWebSitemapUrlsProvider manufacturerWebSitemapUrlsProvider = applicationContext.getBean(DistManufacturerWebSitemapUrlsProvider.class);
        providerList.add(manufacturerWebSitemapUrlsProvider);
        // Product Family site map URL provider
        final DistProductFamilyWebSitemapUrlsProvider productFamilyWebSitemapUrlsProvider = applicationContext.getBean(DistProductFamilyWebSitemapUrlsProvider.class);
        providerList.add(productFamilyWebSitemapUrlsProvider);
        // Product site map URL provider
        final DistProductWebSitemapUrlsProvider productWebSitemapUrlsProvider = applicationContext.getBean(DistProductWebSitemapUrlsProvider.class);
        providerList.add(productWebSitemapUrlsProvider);

        for (final WebSitemapUrlsProvider provider : providerList) {
            ((DistWebSitemapUrlsProvider) provider).setCmsSiteModel(cmsSiteModel);
            ((DistWebSitemapUrlsProvider) provider).setBaseSiteModel(getBaseSiteService().getBaseSiteForUID(cmsSiteModel.getUid()));
            ((DistWebSitemapUrlsProvider) provider).setCmsSiteUrlPrefix(getDomainForCMSSiteMode(cmsSiteModel));
            // Initialize the provider
            provider.init();
        }

        return providerList;
    }

    protected List<WebSitemapUrlsProvider> getWebSitemapNewJobUrlProviders(final CMSSiteModel cmsSiteModel) {
        final List<WebSitemapUrlsProvider> providerList = List.of(applicationContext.getBean(DistCategoryWebSitemapHreflangUrlsProvider.class),
                                                                  applicationContext.getBean(DistManufacturerWebSitemapHreflangUrlsProvider.class),
                                                                  applicationContext.getBean(DistProductFamilyWebSitemapHreflangUrlsProvider.class),
                                                                  applicationContext.getBean(DistProductWebSitemapHreflangUrlsProvider.class));
        for (final WebSitemapUrlsProvider provider : providerList) {
            ((DistWebSitemapHreflangUrlsProvider) provider).setCmsSiteModel(cmsSiteModel);
            ((DistWebSitemapHreflangUrlsProvider) provider).setBaseSiteModel(baseSiteService.getBaseSiteForUID(cmsSiteModel.getUid()));
            provider.setBlackList(getBlackList(cmsSiteModel.getUid(), provider));
            provider.init();
        }

        return providerList;
    }

    // DISTRELEC-11427 : Please make all instances of cross-site linking to be fully encrypted,
    protected String getDomainForCMSSiteMode(final CMSSiteModel cmsSiteModel) {
        return getConfigurationService().getConfiguration().getString("website." + cmsSiteModel.getUid() + (cmsSiteModel.isHttpsOnly() ? ".https" : ".http"));
    }

    public DefaultCMSAdminSiteService getCmsAdminSiteService() {
        return cmsAdminSiteService;
    }

    public void setCmsAdminSiteService(DefaultCMSAdminSiteService cmsAdminSiteService) {
        this.cmsAdminSiteService = cmsAdminSiteService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public WebSitemapService getWebSitemapService() {
        return webSitemapService;
    }

    public void setWebSitemapService(WebSitemapService webSitemapService) {
        this.webSitemapService = webSitemapService;
    }

    private List<String> getBlackList(final String websiteId, final WebSitemapUrlsProvider provider) {
        return webSitemapHelper.getBlacklistForSiteAndEntity(websiteId, provider.getEntityName());
    }
}
