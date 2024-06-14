/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.jalo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.constants.Namb2bacceleratorCoreConstants;
import com.namics.distrelec.b2b.core.model.DistErpPriceConditionTypeModel;
import com.namics.distrelec.b2b.core.setup.CoreSystemSetup;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SearchResult;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.extension.ExtensionManager;
import de.hybris.platform.jalo.user.Address;
import de.hybris.platform.jalo.user.Customer;

/**
 * Don't use. User {@link CoreSystemSetup} instead.
 */
@SuppressWarnings("PMD")
public class Namb2bacceleratorCoreManager extends GeneratedNamb2bacceleratorCoreManager {
    @SuppressWarnings("unused")
    private static Logger LOG = Logger.getLogger(Namb2bacceleratorCoreManager.class.getName());

    public static final Namb2bacceleratorCoreManager getInstance() {
        final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
        return (Namb2bacceleratorCoreManager) em.getExtension(Namb2bacceleratorCoreConstants.EXTENSIONNAME);
    }

    @SuppressWarnings("deprecation")
    public DistErpPriceConditionType getErpPriceConditionTypeByCode(final String code) {
        final Map values = new HashMap();
        values.put(DistErpPriceConditionTypeModel.CODE, code);
        String query = "SELECT {" + Item.PK + "} FROM {" + DistErpPriceConditionTypeModel._TYPECODE + "} WHERE {" + DistErpPriceConditionTypeModel.CODE
                + "} =?" + DistErpPriceConditionTypeModel.CODE;
        SearchResult<DistErpPriceConditionType> res = getSession().getFlexibleSearch().search(query, values,
                Collections.singletonList(DistErpPriceConditionType.class), false, false, 0, -1);
        if (res.getResult() != null && res.getResult().size() > 0) {
            return res.getResult().get(0);
        } else {
            return null;
        }
    }
}
