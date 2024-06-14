/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.manufacturer.job;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.jobs.ManufacturerFlagUpdateCronJobModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * {@code ManufacturerFlagUpdateJob}
 */
public class ManufacturerFlagUpdateJob extends AbstractJobPerformable<ManufacturerFlagUpdateCronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(ManufacturerFlagUpdateJob.class);

    private static final String INTERNATIONAL_SITE_UID = "distrelec";

    @Autowired
    private DistManufacturerService manufacturerService;

    @Autowired
    private CMSSiteService cmsSiteService;

    /*
     * (non-Javadoc)
     *
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(final ManufacturerFlagUpdateCronJobModel cronJob) {
        Instant startTime = Instant.now();
        LOG.info("Starting Update Manufacturer Flag CronJob at {}", startTime);

        final Collection<CMSSiteModel> cmsSites = cmsSiteService.getSites().stream()
                .filter(cmsSite -> !cmsSite.getUid().equals(INTERNATIONAL_SITE_UID))
                .collect(Collectors.toList());

        boolean success = true;

        for(CMSSiteModel cmsSite : cmsSites){
            CountryModel country = cmsSite.getCountry();
            DistSalesOrgModel salesOrg = cmsSite.getSalesOrg();

            if (country == null || salesOrg == null) {
                LOG.info("Skipping cms site: {} because country or sales org are not assigned to it", cmsSite.getUid());
                continue;
            }

            LOG.info("Manufacturer update started. Country Name: {}, Sales Org Name: {}", country.getIsocode(), salesOrg.getCode());

            try {
                manufacturerService.updateManufacturerIndexList(country, salesOrg);
                LOG.info("Manufacturer update finished. Country Name: {}, Sales Org Name: {}", country.getIsocode(), salesOrg.getCode());
            } catch (Exception e) {
                LOG.info("Manufacturer update failed. Country Name: {}, Sales Org Name: {}, Error: {}", country.getIsocode(), salesOrg.getCode(), e.getMessage());
                success = false;
            }
        }

        Instant endTime = Instant.now();
        long executionTime = Duration.between(startTime, endTime).getSeconds();
        LOG.info("Finished Updating the Manufacturer Flag Job in {} seconds.", executionTime);
        return new PerformResult(success ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

}
