/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user.daos;

import de.hybris.platform.core.model.user.CustomerModel;

public interface DistCustomerDao {

    CustomerModel getCustomerByContactId(String erpContactId);

}
