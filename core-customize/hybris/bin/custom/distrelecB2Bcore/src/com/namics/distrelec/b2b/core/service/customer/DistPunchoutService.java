/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer;

import java.util.Collection;
import java.util.List;

import com.namics.distrelec.b2b.core.model.category.ProductHierarchyEntryModel;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;

public interface DistPunchoutService {

    /**
     * Check if the navigation node has not only punched out categories
     * 
     * @param node
     *            the navigation node
     * @return boolean
     */
    boolean hasNotOnlyPunchedOutCategories(final CMSNavigationNodeModel node);

    /**
     * Check if the navigation node has not only punched out categories
     * 
     * @param code
     *            the category code
     * @return boolean
     */
    boolean isCategoryPunchedout(final String code);

    /**
     * returns the session punchout string list
     * 
     * @return list<string>
     */
    List<String> getPunchoutList();

    /**
     * returns a list of strings with product hierarchy codes form producthierarchyentry list
     * 
     * @return list<string>
     */
    List<String> getHierarchyCodesFromEntries(Collection<ProductHierarchyEntryModel> list);

}
