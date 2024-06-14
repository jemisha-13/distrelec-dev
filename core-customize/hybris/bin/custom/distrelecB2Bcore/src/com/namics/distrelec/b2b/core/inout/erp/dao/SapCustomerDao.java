/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.Dao;

public interface SapCustomerDao extends Dao {

    /**
     * Get the Address for the specified customer and SAP Addrees ID.
     * 
     * @param customer
     * @param sapAddressId
     * @return AddressModel
     */
    AddressModel getCustomerAddressForSapId(final B2BUnitModel customer, final String sapAddressId);

    /**
     * This methods give you possibility to figure out the last modified cart of a given user if it has many
     * 
     * @param currentUser
     * @return lastmodifiedcart
     */
    CartModel getlastModifiedcartForCustomer(final UserModel currentUser);

}
