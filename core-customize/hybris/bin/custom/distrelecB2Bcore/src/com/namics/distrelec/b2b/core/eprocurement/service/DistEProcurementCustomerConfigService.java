/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.namics.distrelec.b2b.core.model.eprocurement.DistCustomerConfigModel;
import com.namics.distrelec.b2b.core.model.eprocurement.DistFieldConfigModel;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * DistEProcurementCustomerConfigService.
 * 
 * @author pbueschi, Namics AG
 */
public interface DistEProcurementCustomerConfigService {

    DistCustomerConfigModel getCustomerConfig();

    DistCustomerConfigModel getCustomerConfigForCompany(final B2BUnitModel company);

    List<CategoryModel> getAllowedCategories();

    Set<DistFieldConfigModel> getFieldConfigs();

    Map<String, String> getFieldConfigsForProduct(final ProductModel product);

    List<String> getFieldConfigDomainNames();

    Map<String, String> getFieldsForShippingProduct();

    Map<String, String> getHeaderFields();

    boolean hasShippingProduct();

    boolean isShippingProduct(final ProductModel product);

    /**
     * Check whether the mega flyout should be disabled or not.
     * 
     * @return {@code true} if the customer has the Mega Flyout disabled, default value {@code false}
     */
    boolean hasMegaFlyOutDisabled();

    /**
     * Check whether we need to open the OCI window page in a new window or not.
     * 
     * @return {@code true} if the OCI page should be open in a new window. Default value is {@code false}
     */
    boolean openInNewWindow();

    /**
     * Return the redirect URL, if any.
     * 
     * @return the redirect URL.
     */
    String getDefaultRedirectURL();

    boolean isCustomFooter();
}
