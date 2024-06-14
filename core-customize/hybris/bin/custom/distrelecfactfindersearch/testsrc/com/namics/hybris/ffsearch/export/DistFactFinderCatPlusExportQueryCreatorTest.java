/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.namics.hybris.ffsearch.export.query.DistFactFinderCatPlusExportQueryCreator;

/**
 * Test to generate the export queries for the different environments.
 * the values are hardcoded!
 * 
 * @author ascherrer, rlehmann, Namics AG
 * @since Distrelec 2.0.20
 * 
 */
public class DistFactFinderCatPlusExportQueryCreatorTest {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderCatPlusExportQueryCreatorTest.class);
    private final DistFactFinderCatPlusExportQueryCreator queryCreator = new DistFactFinderCatPlusExportQueryCreator();

    @Test
    public void createFactFinderExportQuery() {
        final String ENV_LOCALHOST = "LOCALHOST";
        final String ENV_ATOS_D = "ATOS_D";
        final String ENV_HP_Q = "HP_Q";
        final String ENV_HP_P = "HP_P";

        final String originalQuery = queryCreator.createQuery();

        LOG.info("ORIGINAL: " + originalQuery);

        final List<String> environments = new ArrayList<String>();
        environments.add(ENV_LOCALHOST);
        environments.add(ENV_ATOS_D);
        environments.add(ENV_HP_Q);
        environments.add(ENV_HP_P);

        for (final String env : environments) {
            String query = originalQuery;

            final Map<String, String> params = new HashMap<String, String>();
            params.put("\\?LanguageIsocode", "'de'");
            //params.put("\\?Date", now());
            params.put("\\?Country", "8796094464034"); // CH - LOCALHOST, ATOS_D, HP_Q, HP_P
            params.put("\\?ExcludedProducts", "0");
            params.put("\\?NumberOfDeliveryCountries", "2");
            params.put("\\?Language", "8796125823008"); // LOCALHOST, ATOS_D, HP_Q, HP_P
            params.put("\\?DistSalesOrg", "8796094650841"); // LOCALHOST, ATOS_D, HP_Q, HP_P
            params.put("\\?DeliveryCountries", "8796094464034, 8796097249314"); // ATOS_D, HP_Q

            if (env.equals(ENV_LOCALHOST)) {
                params.put("\\?CMSSite", "8796093219880");
            } else if (env.equals(ENV_ATOS_D)) {
                params.put("\\?CMSSite", "8796093187112");
            } else if (env.equals(ENV_HP_Q)) {
                params.put("\\?CMSSite", "8796093056040");
            } else if (env.equals(ENV_HP_P)) {
                params.put("\\?CMSSite", "8796093187112");
            }

            for (final Entry<String, String> entry : params.entrySet()) {
                query = query.replaceAll(entry.getKey(), entry.getValue());
            }

            // Escape quotes to use the query in Groovy
            // query = query.replaceAll("\\\"", "\\\\\\\"");

            LOG.info(env + ":   " + query);
        }

        // dummy test
        Assert.assertTrue(true);
    }

}
