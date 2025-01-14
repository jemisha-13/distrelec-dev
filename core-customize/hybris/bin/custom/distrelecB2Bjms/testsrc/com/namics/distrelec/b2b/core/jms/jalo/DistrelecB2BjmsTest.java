/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.namics.distrelec.b2b.core.jms.jalo;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Tests for the DistrelecB2Bjms extension.
 */
public class DistrelecB2BjmsTest extends HybrisJUnit4TransactionalTest {
    /** Edit the local|project.properties to change logging behaviour (properties log4j2.logger.*). */
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DistrelecB2BjmsTest.class);

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
    public void testDistrelecB2Bjms() {
        final boolean testTrue = true;
        assertThat(testTrue).isTrue();
    }
}
