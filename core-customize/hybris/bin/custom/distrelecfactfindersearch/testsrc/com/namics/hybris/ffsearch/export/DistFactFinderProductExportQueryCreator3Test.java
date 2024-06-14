/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.hybris.ffsearch.export.query.DistFactFinderProductExportQueryCreator3;
import com.namics.hybris.ffsearch.export.query.DistFactFinderProductExportQueryCreatorFromFile;

import junit.framework.Assert;

/**
 * Test to generate the export queries for the different environments. The values are hardcoded!
 * 
 * @author ascherrer, rlehmann, Namics AG
 * @since Distrelec 2.0.20
 * 
 */
public class DistFactFinderProductExportQueryCreator3Test {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderProductExportQueryCreator3Test.class);

    private static final String CHANNEL_7310_CH_DE = "7310_CH_DE";
    private static final String CHANNEL_7790_EE_RU = "7790_EE_RU";

    private static final String ENV_DEV_ADRIAN = "DEV ADRIAN";
    private static final String ENV_ATOS_D = "ATOS_D";
    private static final String ENV_HP_Q = "HP_Q";
    private static final String ENV_HP_P = "HP_P";

    @Test
    public void createFactFinderExportQuery() {
        final DistFactFinderProductExportQueryCreator3 queryCreator = new DistFactFinderProductExportQueryCreator3();

        runTest(queryCreator.createQuery());

        // dummy test
        Assert.assertTrue(true);
    }

    @Test
    public void createFactFinderExportQueryFromFile() {
        final DistFactFinderProductExportQueryCreatorFromFile queryCreatorFromFile = new DistFactFinderProductExportQueryCreatorFromFile();

        runTest(queryCreatorFromFile.createQuery());

        // dummy test
        Assert.assertTrue(true);
    }

    /**
     * tests that the Initial Capacity of the StringBuilder for the query is big enough, so the StringBuilder will not be resized)
     */
    @Test
    public void testQueryStringBuilderInitialCapacity() {
        final DistFactFinderProductExportQueryCreator3 queryCreator = new DistFactFinderProductExportQueryCreator3();
        final int queryLength = queryCreator.createQuery().length();
        assertTrue("Increase the value of Query StringBuilder initial capacity to at least: " + queryLength,
                DistFactFinderProductExportQueryCreator3.QUERY_LENGTH >= queryLength);
    }

    private void runTest(final String originalQuery) {
        logQuery("GENERIC", "GENERIC", originalQuery);

        final List<String> channels = new ArrayList<>();
        channels.add(CHANNEL_7310_CH_DE);
        channels.add(CHANNEL_7790_EE_RU);

        final List<String> environments = new ArrayList<>();
        environments.add(ENV_DEV_ADRIAN);
        environments.add(ENV_ATOS_D);
        environments.add(ENV_HP_Q);
        environments.add(ENV_HP_P);

        for (final String channel : channels) {
            for (final String env : environments) {
                String query = originalQuery;

                final Map<String, String> params = getParams(channel, env);

                for (final Entry<String, String> entry : params.entrySet()) {
                    query = query.replaceAll(entry.getKey(), entry.getValue());
                }

                // Escape quotes to use the query in Groovy
                // query = query.replaceAll("\\\"", "\\\\\\\"");

                logQuery(channel, env, query);
            }
        }
    }

    private void logQuery(final String channel, final String env, final String query) {
        LOG.info(Strings.padEnd(channel + " / " + env + ":", 28, ' ') + query);
    }

    private Map<String, String> getParams(final String channel, final String env) {
        final Map<String, String> params = new HashMap<>();
        //params.put("\\?Date", now());
        params.put("\\?ExcludedProducts", "0");

        if (channel.equals(CHANNEL_7310_CH_DE)) {
            params.putAll(getParamsFor7310ChDe(env));
        } else if (channel.equals(CHANNEL_7790_EE_RU)) {
            params.putAll(getParamsFor7790EeRu(env));
        } else {
            Assert.fail();
        }

        return params;
    }

    private Map<String, String> getParamsFor7310ChDe(final String env) {
        final Map<String, String> params = new HashMap<>();
        params.put("\\?ErpSystem", "'" + DistErpSystem.SAP.getCode() + "'");
        params.put("\\?LanguageIsocode", "'de'");
        params.put("\\?Language", "8796125823008"); // de - ADRIAN, ATOS_D, HP_Q, HP_P
        params.put("\\?Country", "8796094464034"); // CH - ADRIAN, ATOS_D, HP_Q, HP_P
        params.put("\\?DeliveryCountries", "8796094464034, 8796097249314"); // CH+LI - ADRIAN, ATOS_D, HP_Q
        params.put("\\?NumberOfDeliveryCountries", "2");

        if (env.equals(ENV_DEV_ADRIAN)) {
            params.put("\\?CMSSite", "8796094235688"); // distrelec_CH
            params.put("\\?DistSalesOrg", "8796094650841"); // 7310
        } else if (env.equals(ENV_ATOS_D)) {
            params.put("\\?CMSSite", "8796093187112");
            params.put("\\?DistSalesOrg", "8796094650841");
        } else if (env.equals(ENV_HP_Q)) {
            params.put("\\?CMSSite", "8796093056040");
            params.put("\\?DistSalesOrg", "8796094650841");
        } else if (env.equals(ENV_HP_P)) {
            params.put("\\?CMSSite", "8796093187112");
            params.put("\\?DistSalesOrg", "8796094650841");
        } else {
            Assert.fail();
        }
        return params;
    }

    private Map<String, String> getParamsFor7790EeRu(final String env) {
        final Map<String, String> params = new HashMap<>();
        params.put("\\?ErpSystem", "'" + DistErpSystem.SAP.getCode() + "'");
        params.put("\\?LanguageIsocode", "'ru'");
        params.put("\\?Language", "8796126380064"); // ru
        params.put("\\?NumberOfDeliveryCountries", "1");

        if (env.equals(ENV_DEV_ADRIAN)) {
            params.put("\\?Country", "8796095119394"); // EE
            params.put("\\?DeliveryCountries", "8796095119394"); // EE
            params.put("\\?CMSSite", "8796093318184"); // distrelec_EE
            params.put("\\?DistSalesOrg", "8796095109593"); // 7790
        } else if (env.equals(ENV_ATOS_D)) {
            params.put("\\?Country", ""); // EE
            params.put("\\?DeliveryCountries", ""); // EE
            params.put("\\?CMSSite", ""); // distrelec_EE
            params.put("\\?DistSalesOrg", ""); // 7790
        } else if (env.equals(ENV_HP_Q)) {
            params.put("\\?Country", "8796095119394"); // EE
            params.put("\\?DeliveryCountries", "8796095119394"); // EE
            params.put("\\?CMSSite", "8796158788648"); // distrelec_EE
            params.put("\\?DistSalesOrg", "8796095109593"); // 7790
        } else if (env.equals(ENV_HP_P)) {
            params.put("\\?Country", ""); // EE
            params.put("\\?DeliveryCountries", ""); // EE
            params.put("\\?CMSSite", ""); // distrelec_EE
            params.put("\\?DistSalesOrg", "8796095109593"); // 7790
        } else {
            Assert.fail();
        }
        return params;
    }

}
