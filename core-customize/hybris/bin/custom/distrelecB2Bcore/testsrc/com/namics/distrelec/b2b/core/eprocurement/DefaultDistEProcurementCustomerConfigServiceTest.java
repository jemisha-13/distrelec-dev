/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementCustomerConfigService;
import com.namics.distrelec.b2b.core.model.eprocurement.DistCustomerConfigModel;
import com.namics.distrelec.b2b.core.model.eprocurement.DistFieldConfigModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Test the {@link DistEProcurementCustomerConfigService} class.
 * 
 * @author pbueschi, Namics AG
 */
@IntegrationTest
public class DefaultDistEProcurementCustomerConfigServiceTest extends ServicelayerTransactionalTest {

    @Resource
    private ProductService productService;

    @Resource
    private UserService userService;

    @Resource
    private SessionService sessionService;

    @Resource
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Resource
    private DistEProcurementCustomerConfigService distCustomerConfigService;

    private ProductModel product;
    private B2BCustomerModel b2bCustomer;

    @Before
    public void setUp() throws Exception {
        createCoreData();
        createDefaultCatalog();
        importCsv("/distrelecB2Bcore/test/testDistEProcurement.impex", "utf-8");

        product = productService.getProductForCode("14233524");
        b2bCustomer = (B2BCustomerModel) userService.getUserForUID("ociuser");
        sessionService.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, b2bCustomer);
    }

    @Test
    public void testGetCustomerConfig() {
        sessionService.removeAttribute(UserConstants.USER_SESSION_ATTR_KEY);
        DistCustomerConfigModel customerConfig = distCustomerConfigService.getCustomerConfig();
        Assert.assertNull(customerConfig);

        sessionService.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, b2bCustomer);
        customerConfig = distCustomerConfigService.getCustomerConfig();
        Assert.assertNotNull(customerConfig);
    }

    @Test
    public void testGetCustomerConfigForCompany() {
        DistCustomerConfigModel customerConfig = distCustomerConfigService.getCustomerConfigForCompany(null);
        Assert.assertNull(customerConfig);

        customerConfig = distCustomerConfigService.getCustomerConfigForCompany(b2bUnitService.getParent(b2bCustomer));
        Assert.assertNotNull(customerConfig);
    }

    @Test
    public void testGetFieldConfigs() {
        final Set<DistFieldConfigModel> fieldConfigs = distCustomerConfigService.getFieldConfigs();
        Assert.assertTrue(CollectionUtils.isNotEmpty(fieldConfigs));
    }

    @Test
    public void testGetFieldConfigsForProduct() {
        final Map<String, String> fieldConfigs = distCustomerConfigService.getFieldConfigsForProduct(product);
        Assert.assertTrue(MapUtils.isNotEmpty(fieldConfigs));
    }
}
