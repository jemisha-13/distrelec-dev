/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.customer;

import java.util.Collection;

import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;

public interface DistCustomerRegistrationService {

	/**
	 * To get list of countries for registration
	 * 
	 */
	Collection<CountryModel> getCountriesForRegistration(SiteChannel channel);
}
