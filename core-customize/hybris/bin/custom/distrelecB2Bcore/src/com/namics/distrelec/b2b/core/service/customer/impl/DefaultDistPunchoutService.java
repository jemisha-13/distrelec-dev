/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.category.ProductHierarchyEntryModel;
import com.namics.distrelec.b2b.core.model.customer.CustomerPunchoutEntryModel;
import com.namics.distrelec.b2b.core.service.customer.DistPunchoutService;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.product.DistProductService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * DefaultDistPunchoutService
 */
public class DefaultDistPunchoutService implements DistPunchoutService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Autowired
    private ConfigurationService configurationService;

    private static final Logger LOG = Logger.getLogger(DefaultDistPunchoutService.class);

    @Override
    public boolean hasNotOnlyPunchedOutCategories(final CMSNavigationNodeModel node) {
        return hasNotOnlyPunchedOutEntries(node);
    }

    // TODO: CHANGE LOGIC HERE -> NOTICE FROM ADI SCHERRER
    private boolean hasNotOnlyPunchedOutEntries(final CMSNavigationNodeModel node) {
        final List<String> punchOuts = getPunchoutList();
        if (CollectionUtils.isEmpty(punchOuts) || CollectionUtils.isEmpty(node.getEntries())) {
            return true;
        }

        final CMSNavigationEntryModel entry = node.getEntries().get(0);

        if (entry.getItem() instanceof CategoryModel) {
            final CategoryModel category = (CategoryModel) entry.getItem();
            return !isCategoryPunchedout(category.getCode());
        }

        return true;
    }

    // TODO: CHANGE LOGIC HERE -> NOTICE FROM ADI SCHERRER
    @Override
    public boolean isCategoryPunchedout(final String code) {
        final List<String> punchOuts = getPunchoutList();
        if (CollectionUtils.isEmpty(punchOuts)) {
            return false;
        }

        final CategoryModel category = new CategoryModel();
        category.setCode(code);
        final CategoryModel categoryModel = flexibleSearchService.getModelByExample(category);

        if (CollectionUtils.isNotEmpty(categoryModel.getHierarchyCodes())) {
            if (punchOuts.containsAll(getHierarchyCodesFromEntries(categoryModel.getHierarchyCodes()))) {
                return true;
            }
            // CR: DISTRELEC-6467
            // for (final String hierarchyCode : getHierarchyCodesFromEntries(categoryModel.getHierarchyCodes())) {
            // if (punchOuts.contains(hierarchyCode)) {
            // containing = true;
            // break;
            // }
            // }
        }

        return false;
    }

    @Override
    public List<String> getHierarchyCodesFromEntries(final Collection<ProductHierarchyEntryModel> list) {
        List<String> productHiearchyCodes = new ArrayList<String>();
        for (ProductHierarchyEntryModel entry : list) {
            if (!productHiearchyCodes.contains(entry.getCode())) {
                productHiearchyCodes.add(entry.getCode());
            }
        }
        Collections.sort(productHiearchyCodes);
        return productHiearchyCodes;
    }

    private List<String> getHierarchyCodesFromPunchouts(final Collection<CustomerPunchoutEntryModel> list) {
        List<String> productHiearchyCodes = new ArrayList<String>();
        for (CustomerPunchoutEntryModel entry : list) {
            if (!productHiearchyCodes.contains(entry.getProductHierarchyCode())) {
                productHiearchyCodes.add(entry.getProductHierarchyCode());
            }
        }
        Collections.sort(productHiearchyCodes);
        return productHiearchyCodes;
    }

    @Override
    public List<String> getPunchoutList() {
        if (userService.isAnonymousUser(userService.getCurrentUser()) || checkoutCustomerStrategy.isAnonymousCheckout()) {
            sessionService.setAttribute(DistConstants.Session.PRODUCT_HIERARCHY_LOADED, Boolean.TRUE);
            return Collections.EMPTY_LIST;
        }

        if (configurationService.getConfiguration().getBoolean(DistConstants.PropKey.Punchout.PUNCHOUTLOGIC, false)) {
            if (!BooleanUtils.isTrue((Boolean) sessionService.getAttribute(DistConstants.Session.PRODUCT_HIERARCHY_LOADED))) {
                sessionService.setAttribute(DistConstants.Session.PRODUCT_HIERARCHY_LOADED, Boolean.TRUE);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Punchout Logic is deactivated. Switch " + DistConstants.PropKey.Punchout.PUNCHOUTLOGIC
                            + " configuration parameter to false, to activate the punchout logic.");
                }
            }
            return Collections.EMPTY_LIST;
        }

        if (userService.getCurrentUser() instanceof EmployeeModel) {
            return Collections.EMPTY_LIST;
        }

        B2BCustomerModel customer = (B2BCustomerModel) checkoutCustomerStrategy.getCurrentUserForCheckout();
        B2BUnitModel company = customer.getDefaultB2BUnit();
        if (company != null) {
            final List<String> productHierarchyList = getHierarchyCodesFromPunchouts(company.getCompanyPunchouts());
            if (BooleanUtils.isFalse((Boolean) sessionService.getAttribute(DistConstants.Session.PRODUCT_HIERARCHY_LOADED))) {
                sessionService.setAttribute(DistConstants.Session.PRODUCT_HIERARCHY_LOADED, Boolean.TRUE);
                sessionService.setAttribute(DistConstants.Session.MAIN_NAV_FLUSH, Boolean.TRUE);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.valueOf(productHierarchyList) + " punchouts for customer " + customer.getUid());
                }
            }
            Collections.sort(productHierarchyList);
            return productHierarchyList;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

}
