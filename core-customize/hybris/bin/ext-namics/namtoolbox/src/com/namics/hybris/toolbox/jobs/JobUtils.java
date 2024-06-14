/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.jobs;

import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.jalo.SearchResult;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;

/**
 * Util class to get jobs from the DB.
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class JobUtils {

    /**
     * Returns the {@link CronJob} with the given <code>code</code>. Returns <code>null</code>, if no job was found.
     * 
     * @param code
     *            Code of the cron job
     * @return Returns the {@link CronJob} with the given <code>code</code>.
     */
    @SuppressWarnings("unchecked")
    public static CronJob receiveCronJobByCode(final String code) {
        final FlexibleSearch flexibleSearch = FlexibleSearch.getInstance();
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("code", code);
        final SearchResult<CronJob> result = flexibleSearch.search("SELECT {PK} FROM {CronJob} WHERE {code}=?code", attributes, CronJob.class);
        if (result.getResult().isEmpty()) {
            return null;
        } else {
            return result.getResult().iterator().next();
        }

    }

}
