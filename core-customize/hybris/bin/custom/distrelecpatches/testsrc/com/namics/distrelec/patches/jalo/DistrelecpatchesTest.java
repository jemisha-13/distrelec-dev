/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.jalo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JUnit Tests for the Distrelecpatches extension.
 */
public class DistrelecpatchesTest extends HybrisJUnit4TransactionalTest {
    /**
     * Edit the local|project.properties to change logging behaviour (properties log4j2.logger.*).
     */
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DistrelecpatchesTest.class);

    @Before
    public void setUp() {
        // implement here code executed before each test
    }

    @After
    public void tearDown() {
        // implement here code executed after each test
    }

    /**
     * This is a sample test method.
     */
    @Test
    public void testDistrelecpatches() {
        final boolean testTrue = true;
        assertThat(testTrue).isTrue();
    }
}
