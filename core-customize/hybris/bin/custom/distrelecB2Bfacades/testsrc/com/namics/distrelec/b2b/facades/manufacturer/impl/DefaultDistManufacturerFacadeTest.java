/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.manufacturer.impl;

import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistMiniManufacturerData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Test class for {@link DefaultDistManufacturerFacade}.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DefaultDistManufacturerFacadeTest extends ServicelayerTransactionalTest {

    @Resource
    private DistManufacturerFacade distManufacturerFacade;

    @Resource
    private CMSSiteService cmsSiteService;

    @Before
    public void setUp() throws ImpExException, CMSItemNotFoundException {
        importCsv("/distrelecB2Bcore/test/testDistManufacturers.impex", "utf-8");
        cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());

    }

    @Test
    public void testGetManufacturers() {
        final Map<String, List<DistMiniManufacturerData>> distManufacturers = distManufacturerFacade.getManufactures();
        Assert.assertTrue(MapUtils.isNotEmpty(distManufacturers));
        Assert.assertFalse(distManufacturers.get("A").equals(ListUtils.EMPTY_LIST));
        Assert.assertNull(distManufacturers.get("Z"));
    }
}
